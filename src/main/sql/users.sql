use milou;
create table users (
    id int primary key auto_increment,
    name varchar(100) not null ,
    email varchar(100) unique not null ,
    password varchar(255) not null check ( length(password) >= 8 )
);