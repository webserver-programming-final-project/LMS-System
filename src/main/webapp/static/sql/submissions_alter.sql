-- F08 과제 제출 기능에 필요한 컬럼·제약 추가
-- 기존 submissions 테이블 (submissions.sql) 생성 후 실행
use lms_system;

alter table submissions
    add column submitted_at TIMESTAMP not null default CURRENT_TIMESTAMP,
    add unique key uk_submissions_homework_student (homework_id, student_id);
