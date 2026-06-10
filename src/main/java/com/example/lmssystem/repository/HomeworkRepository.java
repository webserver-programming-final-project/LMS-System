package com.example.lmssystem.repository;

import com.example.lmssystem.entity.Homework;
import java.util.List;

public interface HomeworkRepository {
    // 1. 과제 등록 기능 (DB에 저장)
    void save(Homework homework);

    // 2. 과제 현황 조회 기능 (DB에서 전체 목록 가져오기)
    List<Homework> findAll();
}