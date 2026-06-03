create schema if not exists lms_system;
use lms_system;
create table if not exists homeworks(
    id bigint AUTO_INCREMENT not null,
    class_id bigint not null,
    title varchar(50) not null,
    description TEXT not null,
    created_at TIMESTAMP not null default CURRENT_TIMESTAMP,
    start_from DATETIME not null,
    end_to DATETIME not null,
    foreign key (class_id) references classes(id),
    primary key (id)
);