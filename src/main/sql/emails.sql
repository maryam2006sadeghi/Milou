use Milou;
create table emails (
    id int primary key auto_increment,
    sender_id int not null,
    subject varchar(1000) not null,
    body longtext not null ,
    date datetime not null ,
    code varchar(6) not null unique ,
    is_reply boolean default false,
    is_forward boolean default false,
    parent_email_id int unique ,
    foreign key (sender_id) references users(id),
    foreign key (parent_email_id) references emails(id)
);
