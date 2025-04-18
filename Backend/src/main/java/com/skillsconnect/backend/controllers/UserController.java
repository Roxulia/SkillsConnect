package com.skillsconnect.backend.controllers;


import com.skillsconnect.backend.DTO.LoginDTO;
import com.skillsconnect.backend.DTO.ProjectDTO;
import com.skillsconnect.backend.DTO.SkillDTO;
import com.skillsconnect.backend.DTO.UserDTO;
import com.skillsconnect.backend.models.Bid;
import com.skillsconnect.backend.models.Project;
import com.skillsconnect.backend.models.Skill;
import com.skillsconnect.backend.models.User;
import com.skillsconnect.backend.services.*;
import jakarta.servlet.http.HttpSession;
import org.hibernate.annotations.Array;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private BidService bidService;

    @GetMapping
    public ResponseEntity<Page<User>> getAll(@PageableDefault(size = 10,sort = "id")Pageable p)
    {
        return ResponseEntity.ok(userService.getAllUser(p));
    }

    @GetMapping("/{uid}")
    public ResponseEntity<UserDTO> getById(@PathVariable Long uid)
    {
        User u = userService.getUserById(uid);
        List<Project> finishedList = bidService.getProjectFinishedByUser(u.getId());
        List<ProjectDTO> finished = new ArrayList<ProjectDTO>();
        finishedList.forEach(p -> finished.add(new ProjectDTO(p)));
        List<SkillDTO> skills = new ArrayList<>();
        Set<Skill> skillList = u.getSkills();
        skillList.forEach( s-> skills.add(new SkillDTO(s)));
        Float rating =  ratingService.getAverageRating(u.getId());
        UserDTO user = new UserDTO(u.getId(),u.getName(),u.getWallet(),u.getEmail(),u.getPh_number(),finished,skills,rating);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/name")
    public ResponseEntity<?> updateName(@PathVariable Long id,@RequestParam String name,HttpSession session)
    {
        User u = (User)session.getAttribute("user");
        if(u == null)
        {
            return ResponseEntity.status(401).body("Not Logged in");
        }
        if(adminService.isAdmin(u.getWallet())||(Objects.equals(u.getId(),id)))
        {
            userService.updateName(id,name);
            return ResponseEntity.ok("Name Changed successfully");
        }
        return ResponseEntity.status(401).body("Not Authorized");
    }

    @PutMapping("/{id}/email")
    public ResponseEntity<String> updateEmail(@PathVariable Long id,@RequestParam String email,HttpSession session)
    {
        User u = (User)session.getAttribute("user");
        if(u == null)
        {
            return ResponseEntity.status(401).body("Not Logged in");
        }
        if(adminService.isAdmin(u.getWallet()) || (Objects.equals(u.getId(), id)))
        {
            userService.updateEmail(id,email);
            return ResponseEntity.ok("Email changed successfully");
        }
        return ResponseEntity.status(401).body("Not Authorized");
    }

    @PutMapping("/{id}/phone")
    public ResponseEntity<?> updatePhone(@PathVariable Long id,@RequestParam String phone,HttpSession session)
    {
        User u = (User)session.getAttribute("user");
        if(u == null)
        {
            return ResponseEntity.status(401).body("Not Logged in");
        }
        if(adminService.isAdmin(u.getWallet()) || (Objects.equals(u.getId(), id)))
        {
            userService.updatePhone(id,phone);
            return ResponseEntity.ok("Phone changed successfully");
        }
        return ResponseEntity.status(401).body("Not Authorized");
    }

    @PutMapping("/{id}/rating")
    public ResponseEntity<?> getRating(@PathVariable Long id,HttpSession session)
    {
        User u = (User)session.getAttribute("user");
        if(u == null)
        {
            return ResponseEntity.status(401).body("Not Logged in");
        }
        return ResponseEntity.ok(ratingService.getAverageRating(id));
    }

    @PostMapping("/{id}/skills")
    public ResponseEntity<?> updateSkills(@PathVariable Long id, @RequestBody List<Long> skills, HttpSession session)
    {
        User u = (User)session.getAttribute("user");
        if(u == null)
        {
            return ResponseEntity.status(401).body("Not Logged in");
        }
        if(!adminService.isAdmin(u.getWallet()) && !(Objects.equals(u.getId(),id)))
        {
            return ResponseEntity.status(401).body("Not Authorized");
        }
        System.out.println(skills);
        userService.updateSkills(id,skills);
        return ResponseEntity.ok("Skill assign successfully");
    }

    @PostMapping("/{id}/profile-image")
    public ResponseEntity<?> updateProfileImage(@PathVariable Long id,@RequestParam("image") MultipartFile file,HttpSession session)
    {
        User u = (User)session.getAttribute("user");
        if(u == null)
        {
            return ResponseEntity.status(401).body("Not Logged in");
        }
        if(!adminService.isAdmin(u.getWallet()) && !(Objects.equals(u.getId(),id)))
        {
            return ResponseEntity.status(401).body("Not Authorized");
        }
        try {
            // Validate file
            if (file.isEmpty()) {
                throw new IllegalArgumentException("File is empty");
            }

            // Simulate file processing
            if (file.getContentType().equals("image/jpeg") && file.getContentType().equals("image/png")) {
                throw new IllegalArgumentException("Only JPEG and PNG are allowed");
            }

            // File upload logic
            String filename = fileUploadService.uploadProfileImage(file);
            if(Objects.equals(filename,"Fail"))
            {
                throw new Exception("Something Wrong in Uploading");
            }
            userService.updateProfileImage(id,filename);
            String message = "File uploaded successfully: " + filename;
            return ResponseEntity.ok(message);

        } catch (IllegalArgumentException e) {
            // Handle client-side errors
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("File upload failed: " + e.getMessage());

        } catch (Exception e) {
            // Handle unexpected server-side errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: An unexpected error occurred");
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO, HttpSession session)
    {
        System.out.println(loginDTO.getWallet());
        User u = userService.userLogin(loginDTO.getWallet());
        if(u != null)
        {
            session.setAttribute("user",u);List<Project> finishedList = bidService.getProjectFinishedByUser(u.getId());
            List<ProjectDTO> finished = new ArrayList<ProjectDTO>();
            finishedList.forEach(p -> finished.add(new ProjectDTO(p)));
            List<SkillDTO> skills = new ArrayList<>();
            Set<Skill> skillList = u.getSkills();
            skillList.forEach( s-> skills.add(new SkillDTO(s)));
            Float rating =  ratingService.getAverageRating(u.getId());
            UserDTO user = new UserDTO(u.getId(),u.getName(),u.getWallet(),u.getEmail(),u.getPh_number(),finished,skills,rating);
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(401).body("Something wrong");
    }

    @GetMapping("/me")
    public ResponseEntity<?> userProfile(HttpSession session)
    {
        User u = (User)session.getAttribute("user");
        if(u == null)
        {
            return ResponseEntity.status(401).body("Not Logged in");
        }

        List<Project> finishedList = bidService.getProjectFinishedByUser(u.getId());
        List<ProjectDTO> finished = new ArrayList<ProjectDTO>();
        finishedList.forEach(p -> finished.add(new ProjectDTO(p)));
        List<SkillDTO> skills = new ArrayList<>();
        Set<Skill> skillList = u.getSkills();
        skillList.forEach( s-> skills.add(new SkillDTO(s)));
        Float rating =  ratingService.getAverageRating(u.getId());
        UserDTO user = new UserDTO(u.getId(),u.getName(),u.getWallet(),u.getEmail(),u.getPh_number(),finished,skills,rating);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.removeAttribute("user");
        session.invalidate();
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/accepted-projects")
    public ResponseEntity<?> getAcceptedProjects(HttpSession session)
    {
        User u = (User)session.getAttribute("user");
        if(u == null)
        {
            return ResponseEntity.status(401).body("Not Logged in");
        }
        List<Project> projects = bidService.getProjectsAcceptedByUser(u.getId());
        List<ProjectDTO> projectDTOS = new ArrayList<>();
        for(Project p : projects)
        {
            projectDTOS.add(new ProjectDTO(p));
        }

        return ResponseEntity.ok(projectDTOS);

    }

    @GetMapping("/bidded-projects")
    public ResponseEntity<?> getProjectsBiddedByUser(HttpSession session)
    {
        User u = (User)session.getAttribute("user");
        if(u == null)
        {
            return ResponseEntity.status(401).body("Not Logged in");
        }
        List<Project> projects = bidService.getProjectsBiddedFromUser(u.getId());
        List<ProjectDTO> projectDTOS = new ArrayList<>();
        for(Project p : projects)
        {
            projectDTOS.add(new ProjectDTO(p));
        }

        return ResponseEntity.ok(projectDTOS);
    }

}
