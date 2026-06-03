create schema if not exists lms_system;
use lms_system;
create table if not exists submission_attachments(
   id bigint AUTO_INCREMENT not null,
   submission_id bigint not null,
   file_id bigint not null,
   primary key (id),
   foreign key (submission_id) references submissions(id),
   foreign key (file_id) references files(id)
);