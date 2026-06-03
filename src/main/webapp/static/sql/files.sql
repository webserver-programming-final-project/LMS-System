create schema if not exists lms_system;
use lms_system;
create table if not exists files(
    id bigint auto_increment not null,
    path VARCHAR(500) not null,
    uploaded_at TIMESTAMP not null default CURRENT_TIMESTAMP,
    primary key (id)
);