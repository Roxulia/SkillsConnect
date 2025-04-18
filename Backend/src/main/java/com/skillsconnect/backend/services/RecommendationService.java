package com.skillsconnect.backend.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.skillsconnect.backend.DTO.ProjectDTO;
import com.skillsconnect.backend.DTO.ProjectRecommendationDTO;
import com.skillsconnect.backend.models.*;
import jdk.jfr.SettingDefinition;
import jnr.ffi.annotations.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class RecommendationService {
    @Value("${python.api.link}")
    private String apiLink;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BidService bidService;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Project> getProjectRecommendation(Long uid)
    {
        User u = userService.getUserById(uid);
        List<Project> projectList = projectService.getByStatusNotUser(Projectstatus.HIRING,uid);
        if(projectList.isEmpty())
        {
            return null;
        }
        List<ProjectDTO> projects = new ArrayList<ProjectDTO>();
        projectList.forEach(p -> projects.add(new ProjectDTO(p) ));
        List<Project> finishedList = bidService.getProjectFinishedByUser(uid);
        List<ProjectDTO> finished = new ArrayList<ProjectDTO>();
        finishedList.forEach(p -> finished.add(new ProjectDTO(p)));
        List<String> skills = new ArrayList<String>();
        Set<Skill> skillList = u.getSkills();
        skillList.forEach( s-> skills.add(s.getName()));
        ProjectRecommendationDTO projectRecommendationDTO = new ProjectRecommendationDTO(projects,finished,skills);
        String link = apiLink + "/project";
        List<Long> response = sendRequest(projectRecommendationDTO,link);
        if (response == null)
        {
            return  null;
        }
        List<Project> result = new ArrayList<Project>();
        response.forEach(r-> result.add(projectService.getById(r)));
        return result;
    }

    /*public List<Bid> getUserRecommendation(Long pid)
    {
        Project p = projectService.getById(pid);
        String description = p.getDetail();
        List<String> categories = categoryService.getCategoryOfProject(pid);
        String category = categories.iterator().next();
        List<User> userList = bidService.getBiddersByProjectId(pid);

    }*/

    private List<Long> sendRequest(Object object,String link)
    {
        RestTemplate restTemplate = new RestTemplate();
        try {
            // Send POST request to Python API
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    link, object, Map.class);

            // Extract predicted price
            Map<String,Object> responseBody = response.getBody();
            List<Integer> result = (List<Integer>) responseBody.get("result");
            List<Long> idList = new ArrayList<>();
            for(Integer i : result)
            {
                idList.add(i.longValue());
            }
            return idList;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
