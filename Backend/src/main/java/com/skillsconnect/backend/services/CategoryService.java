package com.skillsconnect.backend.services;


import com.skillsconnect.backend.DTO.CategoryDTO;
import com.skillsconnect.backend.DTO.SkillDTO;
import com.skillsconnect.backend.models.Category;
import com.skillsconnect.backend.models.Project;
import com.skillsconnect.backend.models.Skill;
import com.skillsconnect.backend.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategory(){
        return categoryRepository.findAll();
    }

    public List<CategoryDTO> getallCategoryDTOs()
    {
        List<Category> skillList = getAllCategory();
        List<CategoryDTO> skillDTOS = new ArrayList<>();
        for(Category s: skillList)
        {
            CategoryDTO skillDTO = new CategoryDTO(s.getId(),s.getName());
            skillDTOS.add(skillDTO);
        }
        return skillDTOS;
    }

    public Category addNewCategory(String name)
    {
        Category c = new Category();
        c.setName(name);
        return categoryRepository.save(c);
    }

    /*public List<Project> getProjectWithCategory(Long id)
    {
        return categoryRepository.getProjectsByCategoryId(id);
    }

    public List<String> getCategoryOfProject(Long id)
    {
        return categoryRepository.getCategoriesByProjectId(id);
    }

    */
    public Category getCategoryById(Long id)
    {
        return categoryRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Cant Find Category with ID : "+id));
    }

    public Category getCategoryByName(String name)
    {
        return categoryRepository.getByName(name);
    }
}
