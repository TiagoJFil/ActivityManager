begin transaction ;

insert into "User" values (
                           default,
                           'abc'
                          );
insert into Email values (1,'abc@gmail.com');

insert into Email values (2,'abdc@gmail.com');
commit;

select distinct * from (SELECT "User".id ,"User".name, Email.email FROM Activity
    JOIN "User" ON (Activity.user = "User".id)
    JOIN Email ON (Email.user = "User".id)
WHERE Activity.sport = 1 AND Activity.route = 1 ORDER BY Activity.duration DESC LIMIT 10 OFFSET 0) as t
