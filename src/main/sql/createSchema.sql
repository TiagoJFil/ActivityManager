
create table user_prod (
    id serial primary key,
    name varchar(20) not null
);
create table email_prod(
    "user" int,
    email varchar(100) constraint email_invalid check(email ~* '^[A-Z0-9._%-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,4}$') primary key,
    foreign key ("user") references user_prod(id)
);

create table token_prod(
    token char(36) primary key,
    "user" int not null,
    foreign key ("user") references user_prod(id)
);

create table route_prod (
    id serial primary key,
    startLocation varchar(150) not null,
    endLocation varchar(150) not null,
    distance real not null check(distance > 0),
    "user" int not null,
    foreign key ("user") references user_prod(id)
);

create table sport_prod(
    id serial primary key,
    name varchar(20) not null,
    description varchar(200) DEFAULT null,
    "user" int not null,
    foreign key ("user") references user_prod(id)
);

create table activity_prod (
    id serial primary key,
    date date not null,
    duration bigint not null check ( duration  > 0),
    sport int not null,
    route int DEFAULT null,
    "user" int not null,
    foreign key ("user") references user_prod(id),
    foreign key (sport) references SPORT_prod(id),
    foreign key (route) references ROUTE_prod(id)
);
