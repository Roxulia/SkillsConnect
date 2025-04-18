package com.skillsconnect.backend.DTO;

import java.util.List;

public class ProjectRecommendationDTO {
    private List<ProjectDTO> projects;
    private List<ProjectDTO> finished;
    private List<String> skills;

    public ProjectRecommendationDTO(){}

    public ProjectRecommendationDTO(List<ProjectDTO> projects,List<ProjectDTO> finished,List<String> skills)
    {
        this.finished = finished;
        this.projects = projects;
        this.skills = skills;
    }

    public List<ProjectDTO> getFinished() {
        return finished;
    }

    public List<ProjectDTO> getProjects() {
        return projects;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public void setProjects(List<ProjectDTO> projects) {
        this.projects = projects;
    }

    public void setFinished(List<ProjectDTO> finished) {
        this.finished = finished;
    }
}
