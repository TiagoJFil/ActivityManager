begin transaction ;

insert into "User" values (
                           default,
                           'abc'
                          );
insert into Email values (1,'abc@gmail.com');

insert into Email values (2,'abdc@gmail.com');
commit;