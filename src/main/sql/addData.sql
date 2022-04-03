begin transaction ;

insert into "user" values (
                           default,
                           'abc'
                          );
insert into email values (1,'abc@gmail.com');

insert into email values (2,'abdc@gmail.com');
commit;