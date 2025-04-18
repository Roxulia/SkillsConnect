package com.skillsconnect.backend.services;


import com.skillsconnect.backend.DTO.ProjectDTO;
import com.skillsconnect.backend.models.*;
import com.skillsconnect.backend.repositories.CategoryRepository;
import com.skillsconnect.backend.repositories.ProjectRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private FileUploadService fileUploadService;

    private BidService bidService;

    @Autowired
    private UserService userService;

    public ProjectService(@Lazy BidService bidService)
    {
        this.bidService = bidService;
    }

    public Page<Project> getAllProject(Pageable number)
    {
        return projectRepository.findAll(number);
    }

    public Project getById(Long id)
    {
        return projectRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Cant Find Project with ID : "+id));
    }

    public List<Project> getByCreator(Long id)
    {
        return projectRepository.getByCreatorId(id);
    }

    public String createProject(ProjectDTO projectDTO)
    {
        User creator = userService.getUserById(projectDTO.getUid());
        LocalDateTime created_at = LocalDateTime.now();
        Duration duration1 = Duration.ofDays(projectDTO.getDuration());
        LocalDateTime deadline = created_at.plus(duration1);
        Project p = new Project();
        p.setCreator(creator);
        p.setCreated_at(created_at);
        p.setName(projectDTO.getName());
        p.setDetail(projectDTO.getDetail());
        p.setDeadline(deadline);
        p.setStatus(Projectstatus.HIRING);
        p.setCategories(projectDTO.getCategory());
        projectRepository.save(p);
        return "Success";
    }

    @Transactional
    public void assignCategory(Long id,String category)
    {
        Project p = getById(id);
        p.setCategories(category);
        projectRepository.save(p);
    }

    public void updateName(Long id,String name)
    {
        projectRepository.updateName(id,name);
    }

    public void updateDetail(Long id,String detail)
    {
        projectRepository.updateDetail(id,detail);
    }

    public void extendDuration(Long id,Long day)
    {
        Project p = getById(id);
        LocalDateTime create_at = p.getDeadline();
        Duration duration = Duration.ofDays(day);
        LocalDateTime updatedDeadline = create_at.plus(duration);
        projectRepository.updateDeadline(id,updatedDeadline);
    }

    public List<Project> getByStatus(Projectstatus projectstatus)
    {
        return projectRepository.getProjectByStatus(projectstatus);
    }

    public List<Project> getByStatusNotUser(Projectstatus projectstatus,Long uid)
    {
        return projectRepository.getProjectByStatusAndUID(projectstatus,uid);
    }

    public boolean isStatus(Long id,Projectstatus projectstatus)
    {
        return getById(id).getStatus().equals(projectstatus);
    }

    public String setFinished(Long id)
    {
        Project p = getById(id);
        if(!isStatus(id,Projectstatus.CHECKING))
        {
            return "Fail";
        }
        String trxResult = transactionService.succeedTransaction(id);
        if(Objects.equals(trxResult,"Success"))
        {
            p.setStatus(Projectstatus.FINISHED);
            p.setFileStatus(FileStatus.ACCEPTED);
            projectRepository.save(p);
            return "Success";
        }

        return "Fail";
    }

    @Transactional
    public String setOngoing(Long id, Bid bid,String trxHash)
    {

        Project p = getById(id);
        Transaction response = transactionService.createTransaction(p,bid,trxHash);
        p.setTransaction(response);
        p.setStatus(Projectstatus.ONGOING);
        projectRepository.save(p);
        return "Success";
    }

    public File getProjectFile(Long id)
    {
        Project p = getById(id);
        String file = p.getFileLocation();
        return fileUploadService.getFile(file);
    }

    public void setExpired(Long id)
    {
        projectRepository.updateStatus(id,Projectstatus.EXPIRED);
    }

    public boolean isDeadlineMet(Long id)
    {
        Project p = getById(id);
        System.out.println(p.getDeadline());
        System.out.println(LocalDateTime.now());
        return !p.getDeadline().isAfter(LocalDateTime.now());
    }

    @Scheduled(fixedRate = 3600000)
    public void checkDeadline()
    {
        List<Project> actives = projectRepository.getProjectByStatus(Projectstatus.ONGOING);
        actives.addAll(projectRepository.getProjectByStatus(Projectstatus.HIRING));

        for(Project p : actives)
        {
            if(isDeadlineMet(p.getId()))
            {
                setExpired(p.getId());
            }
        }
    }

    public String setRefunded(Long id)
    {
        Project p = getById(id);
        if(isStatus(id,Projectstatus.EXPIRED))
        {
            String trxresult = transactionService.refundTransaction(id);
            if(Objects.equals(trxresult,"Success"))
            {
                projectRepository.updateStatus(id,Projectstatus.REFUNDED);
                return "Success";
            }
            return "Fail";

        }
        return "Fail";
    }

    public String setFileReject(Long id)
    {
        try{
            Project p = getById(id);
            p.setFileStatus(FileStatus.REJECTED);
            projectRepository.save(p);
            return "Success";
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
            return "Fail";
        }

    }

    public String updateFileLocation(Long id,String url)
    {
        Project p = getById(id);
        if(!isStatus(id,Projectstatus.ONGOING) && !isStatus(id,Projectstatus.CHECKING))
        {
            return "Fail";
        }
        try{
            p.setFileLocation(url);
            p.setFileStatus(FileStatus.PENDING);
            p.setStatus(Projectstatus.CHECKING);
            projectRepository.save(p);
            return "Success";
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Fail";
        }
    }

    public List<Project> getOngoingByCreator(Long id) {
        return projectRepository.getOngoingByCreator(id);
    }

    public List<Project> getCheckingByCreator(Long id) {
        return projectRepository.getCheckingByCreator(id);
    }
}
