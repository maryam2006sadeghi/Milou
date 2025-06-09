use Milou;
create table email_recipients(
    email_id int not null,
    recipient_id int not null,
    is_read boolean default false,
    primary key (email_id, recipient_id),
    foreign key (email_id) references emails(id),
    foreign key (recipient_id) references users(id)
);