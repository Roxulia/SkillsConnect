package com.skillsconnect.backend.DTO;

import com.skillsconnect.backend.models.Skill;

public class SkillDTO {
    private Long id;
    private String name;

    public SkillDTO(){}

    public SkillDTO(Long id,String name)
    {
        this.id = id;
        this.name = name;
    }

    public SkillDTO(Skill s)
    {
        this.name = s.getName();
        this.id = s.getId();
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
