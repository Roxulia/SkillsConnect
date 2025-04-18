package com.skillsconnect.backend.controllers;

import com.skillsconnect.backend.DTO.ProjectDTO;
import com.skillsconnect.backend.models.Bid;
import com.skillsconnect.backend.models.Project;
import com.skillsconnect.backend.models.Projectstatus;
import com.skillsconnect.backend.models.User;
import com.skillsconnect.backend.services.*;
import jakarta.servlet.http.HttpSession;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

@RestController
@RequestMapping("api/project")
@CrossOrigin(origins = "localhost:3000" , allowCredentials = "true")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private BidService bidService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping
    public ResponseEntity<Page<Project>> getAll(@PageableDefault(size = 10,page = 0)Pageable pageable)
    {
        return ResponseEntity.ok(projectService.getAllProject(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> getById(@PathVariable Long id)
    {
        return ResponseEntity.ok(new ProjectDTO(projectService.getById(id)));
    }

    @GetMapping("/{status}")
    public ResponseEntity<List<ProjectDTO>> getByStatus(@PathVariable Projectstatus projectstatus)
    {
        List<Project> projects =projectService.getByStatus(projectstatus);
        List<ProjectDTO> projectDTOS = new ArrayList<>();
        for(Project p : projects)
        {
            projectDTOS.add(new ProjectDTO(p));
        }
        return ResponseEntity.ok(projectDTOS);
    }

    @PutMapping("/{id}/projects")
    public ResponseEntity<List<ProjectDTO>> getProjectFromCreator(@PathVariable Long id)
    {
        List<Project> projects =projectService.getByCreator(id);
        List<ProjectDTO> projectDTOS = new ArrayList<>();
        for(Project p : projects)
        {
            projectDTOS.add(new ProjectDTO(p));
        }
        return ResponseEntity.ok(projectDTOS);
    }

    @PutMapping("/{id}/ongoings")
    public ResponseEntity<List<ProjectDTO>> getProjectOngoingFromCreator(@PathVariable Long id)
    {
        List<Project> projects =projectService.getOngoingByCreator(id);
        List<ProjectDTO> projectDTOS = new ArrayList<>();
        for(Project p : projects)
        {
            projectDTOS.add(new ProjectDTO(p));
        }
        return ResponseEntity.ok(projectDTOS);
    }

    @PutMapping("/{id}/checkings")
    public ResponseEntity<List<ProjectDTO>> getProjectCheckingFromCreator(@PathVariable Long id)
    {
        List<Project> projects =projectService.getCheckingByCreator(id);
        List<ProjectDTO> projectDTOS = new ArrayList<>();
        for(Project p : projects)
        {
            projectDTOS.add(new ProjectDTO(p));
        }
        return ResponseEntity.ok(projectDTOS);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createProject(@RequestBody ProjectDTO projectDTO, HttpSession httpSession)
    {
        User u = (User)httpSession.getAttribute("user");
        if(u == null)
        {
            return ResponseEntity.status(401).body("Not Logged in");
        }
        projectDTO.setUid(u.getId());
        String response = projectService.createProject(projectDTO);
        if(Objects.equals(response,"Fail"))
        {
            return ResponseEntity.status(401).body("Something went wrong");
        }
        return ResponseEntity.ok("Created successfully");
    }

    @PutMapping("/{id}/name")
    public ResponseEntity<?> changeName(@PathVariable Long id,@RequestParam("name") String name,HttpSession session)
    {
        User u = (User)session.getAttribute("user");
        if(u == null)
        {
            return ResponseEntity.status(401).body("Not Logged in");
        }
        Project p = projectService.getById(id);
        if(!Objects.equals(u.getId(),p.getCreator().getId()))
        {
            return ResponseEntity.status(401).body("Not Authorized");
        }
        projectService.updateName(id, name);
        return ResponseEntity.ok("Changed successfully");
    }

    @PutMapping("/{id}/detail")
    public ResponseEntity<?> changeDetail(@PathVariable Long id,@RequestParam("detail") String detail,HttpSession session)
    {
        User u = (User)session.getAttribute("user");
        if(u == null)
        {
            return ResponseEntity.status(401).body("Not Logged in");
        }
        Project p = projectService.getById(id);
        if(!Objects.equals(u.getId(),p.getCreator().getId()))
        {
            return ResponseEntity.status(401).body("Not Authorized");
        }
        projectService.updateDetail(id, detail);
        return ResponseEntity.ok("Changed successfully");
    }

    @PutMapping("/{id}/extend")
    public ResponseEntity<?> changeDeadline(@PathVariable Long id,@RequestParam Long duration,HttpSession session)
    {
        User u = (User)session.getAttribute("user");
        if(u == null)
        {
            return ResponseEntity.status(401).body("Not Logged in");
        }
        Project p = projectService.getById(id);
        if(!Objects.equals(u.getId(),p.getCreator().getId()))
        {
            return ResponseEntity.status(401).body("Not Authorized");
        }
        projectService.extendDuration(id, duration);
        return ResponseEntity.ok("Changed successfully");
    }

    @PostMapping("/{id}/filesubmit")
    public ResponseEntity<?> uploadFile(@PathVariable Long id, @RequestParam("file")MultipartFile file,HttpSession session)
    {
        User u = (User)session.getAttribute("user");
        List<Bid> bids = bidService.getAcceptedFromProject(id);
        User bidder = bids.get(0).getBidder();
        if(u == null)
        {
            return ResponseEntity.status(401).body("Not Logged in");
        }
        if(!(Objects.equals(u.getId(),bidder.getId())))
        {
            return ResponseEntity.status(401).body("Not Authorized");
        }
        try {
            // Validate file
            if (file.isEmpty()) {
                System.out.println("File empty");
                throw new IllegalArgumentException("File is empty");
            }

            // Simulate file processing
            if (file.getContentType().equals("application/zip") && file.getContentType().equals("application/x-rar-compressed")) {
                throw new IllegalArgumentException("Only Compressed Files are allowed");
            }

            // File upload logic
            String filename = fileUploadService.uploadFile(file);
            if(Objects.equals(filename,"Fail"))
            {
                throw new Exception("Something Wrong in Uploading");
            }
            String response = projectService.updateFileLocation(id,filename);
            if(Objects.equals(response,"Fail"))
            {
                throw new Exception("Something went wrong");
            }
            String message = "File uploaded successfully: " + filename;
            System.out.println(message);
            return ResponseEntity.ok(filename);

        } catch (IllegalArgumentException e) {
            // Handle client-side errors
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("File upload failed: " + e.getMessage());

        } catch (Exception e) {
            // Handle unexpected server-side errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: "+e.getMessage());
        }
    }

    @PutMapping("/{id}/getFile")
    public ResponseEntity<?> getFile(@PathVariable Long id,HttpSession session)
    {
        User u = (User)session.getAttribute("user");
        if(u == null)
        {
            return ResponseEntity.status(401).body("Not Logged in");
        }
        Project p = projectService.getById(id);
        if(!Objects.equals(u.getId(),p.getCreator().getId()))
        {
            return ResponseEntity.status(401).body("Not Authorized");
        }
        File f = projectService.getProjectFile(id);
        if (!f.exists()) {
            return ResponseEntity.status(404).body("File not found");
        }

        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(f));

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + f.getName() + "\"")
                    .body(resource);
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(500).body("Error reading file");
        }
    }

    @PutMapping("/{id}/finished")
    public ResponseEntity<?> setFinished(@PathVariable Long id, @NotNull HttpSession session)
    {
        User u = (User)session.getAttribute("user");
        if(u == null)
        {
            return ResponseEntity.status(401).body("Not Logged in");
        }
        System.out.println(u.getId());
        Project p = projectService.getById(id);
        System.out.println(p.getCreator().getId());
        if(!Objects.equals(u.getId(),p.getCreator().getId()))
        {
            return ResponseEntity.status(401).body("Not Authorized");
        }
        String response = projectService.setFinished(id);
        if(Objects.equals(response,"Fail"))
        {
            return ResponseEntity.status(401).body("Something went wrong");
        }
        return ResponseEntity.ok("Changed Successfully");
    }

    @PutMapping("/{id}/file-reject")
    public ResponseEntity<?> setFileRejected(@PathVariable Long id, @NotNull HttpSession session)
    {
        User u = (User)session.getAttribute("user");
        if(u == null)
        {
            return ResponseEntity.status(401).body("Not Logged in");
        }
        System.out.println(u.getId());
        Project p = projectService.getById(id);
        System.out.println(p.getCreator().getId());
        if(!Objects.equals(u.getId(),p.getCreator().getId()))
        {
            return ResponseEntity.status(401).body("Not Authorized");
        }
        String response = projectService.setFileReject(id);
        if(Objects.equals(response,"Fail"))
        {
            return ResponseEntity.status(401).body("Something went wrong");
        }
        return ResponseEntity.ok("Changed Successfully");
    }

    @PutMapping("/{id}/refunded")
    public ResponseEntity<?> setRefunded(@PathVariable Long id,HttpSession session)
    {
        User u = (User)session.getAttribute("user");
        if(u == null)
        {
            return ResponseEntity.status(401).body("Not Logged in");
        }
        Project p = projectService.getById(id);
        if(!Objects.equals(u.getId(),p.getCreator().getId()))
        {
            return ResponseEntity.status(401).body("Not Authorized");
        }
        String response = projectService.setRefunded(id);
        if(Objects.equals(response,"Fail"))
        {
            return ResponseEntity.status(401).body("Something went wrong");
        }
        return ResponseEntity.ok("Changed Successfully");
    }

    @PutMapping("/{id}/category")
    public ResponseEntity<?> assignCategory(@PathVariable Long id,@RequestParam("category") String category,HttpSession session)
    {
        User u = (User)session.getAttribute("user");
        if(u == null)
        {
            return ResponseEntity.status(401).body("Not Logged in");
        }
        Project p = projectService.getById(id);
        if(!Objects.equals(u.getId(),p.getCreator().getId()))
        {
            return ResponseEntity.status(401).body("Not Authorized");
        }
        projectService.assignCategory(id, category);
        return ResponseEntity.ok("Changed successfully");
    }

    @GetMapping("/hiring-projects")
    public ResponseEntity<?> getHiringProjects(HttpSession session)
    {
        User u = (User)session.getAttribute("user");
        if(u == null)
        {
            return ResponseEntity.status(401).body("Not Logged in");
        }
        Map<String, Object> response = new HashMap<>();
        List<Project> hiring = projectService.getByStatusNotUser(Projectstatus.HIRING,u.getId());
        List<ProjectDTO> projectDTOS = new ArrayList<>();
        for(Project p: hiring)
        {
            projectDTOS.add(new ProjectDTO(p));
        }
        response.put("hiring", projectDTOS);
        List<Project> recommended = recommendationService.getProjectRecommendation(u.getId());
        if(recommended != null)
        {
            projectDTOS = new ArrayList<>();
            for(Project p: recommended)
            {
                projectDTOS.add(new ProjectDTO(p));
            }
            response.put("recommended", projectDTOS);
        }
        return ResponseEntity.ok(response);

    }

}
