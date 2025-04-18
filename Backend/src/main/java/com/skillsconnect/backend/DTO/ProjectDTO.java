package com.skillsconnect.backend.DTO;

import com.skillsconnect.backend.models.Project;

import java.time.LocalDateTime;

public class ProjectDTO {
    private Long id;
    private String name;
    private String detail;
    private String category;
    private Long duration;
    private Long uid;
    private String creater;
    private LocalDateTime deadline;
    private String status;
    private String filestatus;
    private String location;

    public ProjectDTO(){}

    public ProjectDTO(Long id,String name,String detail,Long duration,Long uid)
    {
        this.id = id;
        this.name = name;
        this.detail = detail;
        this.duration = duration;
        this.uid = uid;
    }

    public ProjectDTO(Project p)
    {
        this.id = p.getId();
        this.name = p.getName();
        this.detail = p.getDetail();
        this.uid = p.getCreator().getId();
        this.creater = p.getCreator().getName();
        this.deadline = p.getDeadline();
        this.status = p.getStatus().toString();
        this.location = p.getFileLocation();
        this.category = p.getCategories();
        if(p.getFileStatus() != null)
        {
            this.filestatus = p.getFileStatus().toString();
        }
    }

    public String getFilestatus() {
        return filestatus;
    }
    public void setFilestatus(String filestatus) {}

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public String getLocation() {
        return location;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setLocation(String loaction) {
        this.location = loaction;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public Long getId() {
        return id;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDetail() {
        return detail;
    }

    public Long getDuration() {
        return duration;
    }

    public Long getUid() {
        return uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }
}
