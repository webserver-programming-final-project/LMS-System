package com.example.lmssystem.entity;

public class Homework {
    private Long id;            // 과제 고유 번호
    private String title;       // 과제 제목
    private String content;     // 과제 내용
    private String dueDate;     // 마감 기한

    // 기본 생성자
    public Homework() {}

    // 모든 필드를 포함하는 생성자
    public Homework(Long id, String title, String content, String dueDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.dueDate = dueDate;
    }

    // Getter 및 Setter 메서드
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
}