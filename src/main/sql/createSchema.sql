begin transaction ;

drop table if exists activity cascade;
drop table if exists sport cascade;
drop table if exists route cascade;
drop table if exists tokens cascade ;
drop table if exists email cascade;
drop table if exists "user" cascade;

create table "user" (
                        id serial primary key,
                        name varchar(35) not null
);
create table email(
                      "user" int,
                      email varchar(100) check(email ~* '^[A-Z0-9._%-]+@[A-Z0-9.-]+\.[A-Z]{2,4}$') primary key,
                      foreign key ("user") references "user"(id)

);

create table tokens(
                       token char(36) primary key,
                       "user" int not null,
                       foreign key ("user") references "user"(id)
);


create table route (
                       id serial primary key,
                       startLocation varchar(200) not null,
                       endLocation varchar(200) not null,
                       distance real not null check(distance > 0),
                       "user" int not null,
                       foreign key ("user") references "user"(id)
);

create table sport(
                      id serial primary key,
                      name varchar(50) not null,
                      description varchar(200) DEFAULT null,
                      "user" int not null,
                      foreign key ("user") references "user"(id)
);

create table activity (
                          id serial primary key,
                          date date not null,
                          duration bigint not null check ( duration  > 0),
                          sport int not null,
                          route int DEFAULT null,
                          "user" int not null,
                          foreign key ("user") references "user"(id),
                          foreign key (sport) references SPORT(id),
                          foreign key (route) references ROUTE(id)
);
commit;