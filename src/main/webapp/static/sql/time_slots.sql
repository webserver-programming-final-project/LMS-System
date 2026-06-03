create schema if not exists lms_system;
use lms_system;
create table if not exists time_slots(
    id bigint auto_increment not null,
    slot_no int not null,
    start_time TIME not null,
    end_time TIME not null,
    primary key (id),
    unique (slot_no)
);
-- 서경대학교 시간표 기준으로 구성하였습니다.
INSERT INTO time_slots (slot_no, start_time, end_time) VALUES
-- 일반 교시
(1,  '09:00:00', '09:50:00'),
(2,  '10:00:00', '10:50:00'),
(3,  '11:00:00', '11:50:00'),
(4,  '12:00:00', '12:50:00'),
(5,  '13:00:00', '13:50:00'),
(6,  '14:00:00', '14:50:00'),
(7,  '15:00:00', '15:50:00'),
(8,  '16:00:00', '16:50:00'),
(9,  '17:00:00', '17:50:00'),

-- 75분제 교시
(21, '09:00:00', '10:15:00'),
(22, '10:30:00', '11:45:00'),
(23, '12:00:00', '13:15:00'),
(24, '13:30:00', '14:45:00'),
(25, '15:00:00', '16:15:00'),
(26, '16:30:00', '17:45:00');