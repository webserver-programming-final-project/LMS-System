create schema if not exists lms_system;
use lms_system;
create table if not exists classes(
    id bigint AUTO_INCREMENT not null,
    classroom VARCHAR(50) not null default '',
    title VARCHAR(50) not null,
    description TEXT not null,
    professor_id bigint not null,
    created_at timestamp not null default CURRENT_TIMESTAMP,
    foreign key (professor_id) references lms_system.user(id),
    primary key(id)
);