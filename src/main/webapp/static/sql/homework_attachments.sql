create schema if not exists lms_system;
use lms_system;
create table if not exists homework_attachments(
    id bigint AUTO_INCREMENT not null,
    homework_id bigint not null,
    file_id bigint not null,
    primary key (id),
    foreign key (homework_id) references homeworks(id),
    foreign key (file_id) references files(id)
);