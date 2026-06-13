package com.example.lmssystem.entity;

public class Homework {
    private Long id;
    private Long classId;
    private String classTitle;
    private String professorName;
    private String title;
    private String description;
    private String createdAt;
    private String startFrom;
    private String endTo;

    public Homework() {}

    public Homework(Long id, Long classId, String title, String description, String startFrom, String endTo) {
        this.id = id;
        this.classId = classId;
        this.title = title;
        this.description = description;
        this.startFrom = startFrom;
        this.endTo = endTo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }

    public String getClassTitle() { return classTitle; }
    public void setClassTitle(String classTitle) { this.classTitle = classTitle; }

    public String getProfessorName() { return professorName; }
    public void setProfessorName(String professorName) { this.professorName = professorName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getStartFrom() { return startFrom; }
    public void setStartFrom(String startFrom) { this.startFrom = startFrom; }

    public String getEndTo() { return endTo; }
    public void setEndTo(String endTo) { this.endTo = endTo; }
}
