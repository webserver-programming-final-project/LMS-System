package com.example.lmssystem.entity;

public class Lecture {
    private Long id;
    private String title;
    private String description;
    private String classroom;
    private Long professorId;
    private String professorName;   
    private int homeworkCount;      

    public Lecture() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getClassroom() { return classroom; }
    public void setClassroom(String classroom) { this.classroom = classroom; }

    public Long getProfessorId() { return professorId; }
    public void setProfessorId(Long professorId) { this.professorId = professorId; }

    public String getProfessorName() { return professorName; }
    public void setProfessorName(String professorName) { this.professorName = professorName; }

    public int getHomeworkCount() { return homeworkCount; }
    public void setHomeworkCount(int homeworkCount) { this.homeworkCount = homeworkCount; }
}
