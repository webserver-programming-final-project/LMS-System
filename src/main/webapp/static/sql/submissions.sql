create schema if not exists lms_system;
use lms_system;
create table if not exists submissions(
    id bigint AUTO_INCREMENT not null,
    homework_id bigint not null,
    student_id bigint not null,
    primary key (id),
    foreign key (homework_id) references homeworks(id),
    foreign key (student_id) references students(id)
);