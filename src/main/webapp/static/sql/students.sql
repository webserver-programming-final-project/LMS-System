create schema if not exists lms_system;
use lms_system;
create table if not exists students(
    id bigint auto_increment not null,
    class_id bigint not null,
    user_id bigint not null,
    primary key(id),
    foreign key (class_id) references classes(id),
    foreign key (user_id) references user(id)
);