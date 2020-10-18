create table if not exists records
(
    id        integer primary key auto_increment,
    fuel_type varchar(10),
    price     decimal(10, 2),
    volume    decimal(10, 2),
    date      date,
    driver_id integer
);