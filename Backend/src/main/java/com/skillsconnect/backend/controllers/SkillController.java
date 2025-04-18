package com.skillsconnect.backend.controllers;


import com.skillsconnect.backend.DTO.SkillDTO;
import com.skillsconnect.backend.models.Skill;
import com.skillsconnect.backend.models.User;
import com.skillsconnect.backend.services.AdminService;
import com.skillsconnect.backend.services.SkillService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("api/skill")
@CrossOrigin(origins = "*")
public class SkillController {

    @Autowired
    private SkillService skillService;

    @Autowired
    private AdminService adminService;

    @GetMapping
    public ResponseEntity<List<SkillDTO>> getAll()
    {
       return ResponseEntity.ok(skillService.getallSkillDTOs());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Skill> getById(@PathVariable Long id)
    {
        return ResponseEntity.ok(skillService.getSkillById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createSkill(@RequestParam("name") String name, HttpSession session)
    {
        User u = (User)session.getAttribute("user");
        if(u == null)
        {
            return ResponseEntity.status(401).body("Not Logged in");
        }
        if(!adminService.isAdmin(u.getWallet()))
        {
            return ResponseEntity.status(401).body("Not Authorized");
        }
        Skill s = skillService.addNewSkill(name);
        return ResponseEntity.ok("Skill added");
    }

    @PutMapping("/{id}/users")
    public ResponseEntity<List<User>> getUserbySkill(@PathVariable Long id)
    {
        return ResponseEntity.ok(skillService.getUserWithSkill(id));
    }

    @PutMapping("/{id}/getskills")
    public ResponseEntity<List<String>> getSkillsofUser(@PathVariable Long id)
    {
        return ResponseEntity.ok(skillService.getUserSkills(id));
    }
}
