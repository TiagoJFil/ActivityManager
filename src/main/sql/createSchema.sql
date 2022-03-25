drop table if exists activity;
drop table if exists users;
drop table if exists route;


--may not be user because its a reserved word
create table users (
   id serial primary key,
   email varchar(100) check(email like '%@%.%' ),
   name varchar(35) not null,
   CONSTRAINT email_unique UNIQUE (email)
);

create table activity (
    id serial primary key,
    name varchar(50) not null,
    description varchar(200),
    userID serial not null,
    foreign key (userID) references USERS(id)
);

create table route (
    id serial primary key,
    startLocation varchar(200) not null,
    endLocation varchar(200) not null,
    distance real not null,
    userID serial not null,
    foreign key (userID) references USERS(id)
);
