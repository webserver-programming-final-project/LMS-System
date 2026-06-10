package com.example.lmssystem.service;

import com.example.lmssystem.entity.Lecture;
import com.example.lmssystem.entity.TimeTable;
import com.example.lmssystem.repository.LectureRepository;

import java.sql.SQLException;
import java.util.List;

public class LectureService {

    private final LectureRepository lectureRepository = new LectureRepository();

    public void addLecture(Lecture lecture, List<TimeTable> timeTables) throws SQLException {
        if (lecture.getTitle() == null || lecture.getTitle().trim().isEmpty())
            throw new IllegalArgumentException("강의명을 입력해주세요.");
        if (lecture.getClassroom() == null || lecture.getClassroom().trim().isEmpty())
            throw new IllegalArgumentException("강의실을 입력해주세요.");
        if (timeTables == null || timeTables.isEmpty())
            throw new IllegalArgumentException("요일과 교시를 최소 하나 이상 선택해주세요.");
        for (TimeTable tt : timeTables) {
            boolean conflict = lectureRepository.hasTimeConflict(tt.getDays(), tt.getSlotId(), null);
            if (conflict)
                throw new IllegalArgumentException(
                    tt.getDays() + "요일 " + tt.getSlotId() + "교시는 이미 다른 강의가 등록되어 있습니다.");
        }

        lectureRepository.save(lecture, timeTables);
    }
    public List<Lecture> getAllLectures() throws SQLException {
        return lectureRepository.findAll();
    }
    public Lecture getLectureById(Long id) throws SQLException {
        return lectureRepository.findById(id);
    }
    public List<TimeTable> getTimeTableByClassId(Long classId) throws SQLException {
        return lectureRepository.findTimeTableByClassId(classId);
    }
    public List<Lecture> getLecturesByProfessor(Long professorId) throws SQLException {
        return lectureRepository.findByProfessorId(professorId);
    }
    public List<Lecture> getLecturesByStudent(Long userId) throws SQLException {
        return lectureRepository.findByStudentId(userId);
    }
}
