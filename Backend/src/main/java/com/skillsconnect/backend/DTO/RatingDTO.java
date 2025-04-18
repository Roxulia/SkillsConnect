package com.skillsconnect.backend.DTO;

public class RatingDTO {
    private Long rater_id;
    private Long rated_id;
    private Float rating;
    private String comment;
    private Long project_id;

    public Long getProject_id() {
        return project_id;
    }

    public Float getRating() {
        return rating;
    }

    public Long getRated_id() {
        return rated_id;
    }

    public Long getRater_id() {
        return rater_id;
    }

    public String getComment() {
        return comment;
    }

    public void setProject_id(Long project_id) {
        this.project_id = project_id;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setRated_id(Long rated_id) {
        this.rated_id = rated_id;
    }

    public void setRater_id(Long rater_id) {
        this.rater_id = rater_id;
    }
}
