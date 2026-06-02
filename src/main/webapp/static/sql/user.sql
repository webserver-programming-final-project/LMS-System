create schema if not exists lms_system;
use lms_system;
create table if not exists user(
    id bigint AUTO_INCREMENT not null,
    email varchar(100) not null ,
    password varchar(100) not null,
    name varchar(20) not null,
    is_professor boolean default false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    primary key (id)
)