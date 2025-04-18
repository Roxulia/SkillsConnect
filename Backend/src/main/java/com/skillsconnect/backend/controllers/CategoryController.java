package com.skillsconnect.backend.controllers;


import com.skillsconnect.backend.DTO.CategoryDTO;
import com.skillsconnect.backend.DTO.SkillDTO;
import com.skillsconnect.backend.models.Category;
import com.skillsconnect.backend.models.Project;
import com.skillsconnect.backend.models.User;
import com.skillsconnect.backend.services.AdminService;
import com.skillsconnect.backend.services.CategoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/category")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private AdminService adminService;

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAll()
    {
        return ResponseEntity.ok(categoryService.getallCategoryDTOs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getById(@PathVariable Long id)
    {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<?> addNewCategory(@RequestParam("name") String name, HttpSession session)
    {
        User u = (User)session.getAttribute("user");
        if(u == null)
        {
            return ResponseEntity.status(401).body("Not Logged in");
        }
        if(!adminService.isAdmin(u.getWallet()))
        {
            System.out.println(adminService.getAdminWallet());
            System.out.println(u.getWallet());
            return ResponseEntity.status(401).body("Not Authorized");
        }
        Category c = categoryService.addNewCategory(name);
        return ResponseEntity.ok("Category Added");
    }
/*
    @PutMapping("/{id}/projects")
    public ResponseEntity<List<Project>> getProjectinCategory(@PathVariable Long id)
    {
        return ResponseEntity.ok(categoryService.getProjectWithCategory(id));
    }

    @PutMapping("/{id}/getcategory")
    public ResponseEntity<List<String>> getCategoryOfProject(@PathVariable Long id)
    {
        return ResponseEntity.ok(categoryService.getCategoryOfProject(id));
    }*/
}
