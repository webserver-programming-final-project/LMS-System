create schema if not exists lms_system;
use lms_system;
create table if not exists time_table(
    id bigint auto_increment not null,
    class_id bigint not null,
    slot_id bigint not null,
    days enum('월','화','수','목','금','토','일') not null,
    primary key (id),
    foreign key (class_id) references classes(id),
    foreign key (slot_id) references time_slots(id)
);