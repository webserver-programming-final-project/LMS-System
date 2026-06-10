create schema if not exists lms_system;
use lms_system;
create table if not exists access_logs(
    id         bigint auto_increment not null,
    user_id    bigint,
    method     varchar(10)  not null,
    path       varchar(255) not null,
    ip         varchar(45),
    created_at TIMESTAMP    not null default CURRENT_TIMESTAMP,
    primary key (id),
    foreign key (user_id) references user(id)
);
