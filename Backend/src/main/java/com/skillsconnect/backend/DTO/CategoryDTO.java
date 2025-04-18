package com.skillsconnect.backend.DTO;

public class CategoryDTO {
    private Long id;
    private String name;

    public CategoryDTO(){}

    public CategoryDTO(Long id,String name)
    {
        this.id = id;
        this.name = name;
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
