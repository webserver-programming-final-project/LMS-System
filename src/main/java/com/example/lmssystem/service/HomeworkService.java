package com.example.lmssystem.service;

import com.example.lmssystem.entity.Homework;
import com.example.lmssystem.entity.User;
import com.example.lmssystem.repository.HomeworkRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public class HomeworkService {

    private final HomeworkRepository homeworkRepository = new HomeworkRepository();

    public List<Homework> getHomeworksByClass(User user, Long classId) throws SQLException {
        if (!canViewHomeworks(user, classId)) {
            throw new SecurityException("해당 강의의 과제 목록을 조회할 권한이 없습니다.");
        }
        return homeworkRepository.findByClassId(classId);
    }

    public void addHomework(User user, Homework homework) throws SQLException {
        if (!canAddHomework(user, homework.getClassId())) {
            throw new SecurityException("해당 강의에 과제를 등록할 권한이 없습니다.");
        }
        validateHomework(homework);
        homeworkRepository.save(homework);
    }

    public boolean canAddHomework(User user, Long classId) throws SQLException {
        return user != null
                && user.isProfessor()
                && homeworkRepository.isProfessorOfClass(user.getId(), classId);
    }

    private boolean canViewHomeworks(User user, Long classId) throws SQLException {
        if (user == null || classId == null) {
            return false;
        }
        if (user.isProfessor()) {
            return homeworkRepository.isProfessorOfClass(user.getId(), classId);
        }
        return homeworkRepository.isStudentOfClass(user.getId(), classId);
    }

    private void validateHomework(Homework homework) {
        if (homework.getClassId() == null) {
            throw new IllegalArgumentException("강의 ID가 필요합니다.");
        }
        if (isBlank(homework.getTitle())) {
            throw new IllegalArgumentException("과제 제목을 입력해주세요.");
        }
        if (isBlank(homework.getDescription())) {
            throw new IllegalArgumentException("과제 설명을 입력해주세요.");
        }
        if (isBlank(homework.getStartFrom()) || isBlank(homework.getEndTo())) {
            throw new IllegalArgumentException("과제 시작일과 마감일을 입력해주세요.");
        }

        LocalDateTime startFrom = LocalDateTime.parse(homework.getStartFrom());
        LocalDateTime endTo = LocalDateTime.parse(homework.getEndTo());
        if (endTo.isBefore(startFrom)) {
            throw new IllegalArgumentException("마감일은 시작일보다 빠를 수 없습니다.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
