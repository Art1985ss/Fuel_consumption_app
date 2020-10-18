create table if not exists records
(
    id        integer primary key auto_increment,
    fuel_type varchar(10),
    price     decimal(10, 2),
    volume    decimal(10, 2),
    date      date,
    driver_id integer
);

truncate table records;

insert into records values ( 0, '95', 1.25, 20, (DATE '2020-10-20'), 1 );
insert into records values ( 1, '95', 1.25, 20, (DATE '2020-10-21'), 1 );
insert into records values ( 2, '95', 1.25, 20, (DATE '2020-10-20'), 1 );
insert into records values ( 3, '95', 1.25, 20, (DATE '2020-09-20'), 1 );
insert into records values ( 4, '95', 1.25, 20, (DATE '2020-09-20'), 2 );