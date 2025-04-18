package com.skillsconnect.backend.DTO;

import com.skillsconnect.backend.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserDTO {
    private Long id;
    private String wallet;
    private String name;
    private String email;
    private String phone;
    private List<ProjectDTO> finishedProjects;
    private List<SkillDTO> skillDTOS;
    private List<String> skills;
    private List<Long> skillsId;
    private Float rating;

    // Constructors
    public UserDTO() {}

    public UserDTO(Long id,String name,String wallet,String email,String ph, List<ProjectDTO> finishedProjects, List<SkillDTO> skills, Float rating) {
        this.id = id;
        this.finishedProjects = finishedProjects;
        this.skillDTOS = skills;
        List<String> skillnames = new ArrayList<>();
        List<Long> skillids = new ArrayList<>();
        for(SkillDTO s : skills)
        {
            skillnames.add(s.getName());
            skillids.add(s.getId());
        }
        this.skills = skillnames;
        this.skillsId = skillids;
        this.rating = rating;
        this.name = name;
        this.wallet = wallet;
        this.email = email;
        this.phone = ph;
    }

    public UserDTO(User user)
    {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.phone = user.getPh_number();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public List<Long> getSkillsId() {
        return skillsId;
    }

    public List<SkillDTO> getSkillDTOS() {
        return skillDTOS;
    }

    public void setSkillDTOS(List<SkillDTO> skillDTOS) {
        this.skillDTOS = skillDTOS;
    }

    public void setSkillsId(List<Long> skillsId) {
        this.skillsId = skillsId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getWallet() {
        return wallet;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ProjectDTO> getFinishedProjects() {
        return finishedProjects;
    }

    public void setFinishedProjects(List<ProjectDTO> finishedProjects) {
        this.finishedProjects = finishedProjects;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }
}
