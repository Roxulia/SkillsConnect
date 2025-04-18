package com.skillsconnect.backend.repositories;

import com.skillsconnect.backend.models.Category;
import com.skillsconnect.backend.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category,Long> {
    /*
    @Query("SELECT c.name FROM Category c JOIN c.projects p WHERE p.id = :pid")
    List<String> getCategoriesByProjectId(@Param("pid") Long id);

    @Query("SELECT c.projects FROM Category c WHERE c.id = :cid")
    List<Project> getProjectsByCategoryId(@Param("cid") Long id);*/

    @Query("SELECT c FROM Category c WHERE c.name = :cname")
    Category getByName(@Param("cname") String name);
}
