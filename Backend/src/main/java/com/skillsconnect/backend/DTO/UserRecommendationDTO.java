package com.skillsconnect.backend.DTO;

import java.util.List;

public class UserRecommendationDTO {
    private String description;
    private String Category;
    private List<UserDTO> users;

    public UserRecommendationDTO(){}

    public UserRecommendationDTO(String description, String category, List<UserDTO> users)
    {
        this.Category=category;
        this.users=users;
        this.description = description;
    }

    public String getCategory() {
        return Category;
    }

    public String getDescription() {
        return description;
    }

    public List<UserDTO> getUsers() {
        return users;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUsers(List<UserDTO> users) {
        this.users = users;
    }
}
