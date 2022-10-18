drop database if exists `customerdb`;
create database `customerdb`;
use `customerdb`;

drop table customer;


select * from customer;

create table customer(
customerNumber char(6) not null primary key,
customerName char(5) not null,
firstHalf tinyint not null,
secondHalf tinyint not null,
totalPurchase tinyint not null,
avgPurchase smallint not null,
grade varchar(10) not null
);

select * from customer;

-- drop table if exists customer;
-- drop database if exists customerdb;
-------------------------------------------
drop table if exists customer;


drop table if exists update_customer;

CREATE TABLE update_customer (
customerNumber char(6) not null,
customerName char(5) not null,
firstHalf tinyint not null,
secondHalf tinyint not null,
totalPurchase tinyint not null,
avgPurchase smallint not null,
grade varchar(10) not null
);

DROP TABLE IF EXISTS delete_customer;


CREATE TABLE delete_customer (
customerNumber char(6) not null,
customerName char(5) not null,
firstHalf tinyint not null,
secondHalf tinyint not null,
totalPurchase tinyint not null,
avgPurchase smallint not null,
grade varchar(10) not null,
deleteDate datetime
);

DROP TRIGGER IF EXISTS delete_trigger_customer;
DELIMITER $$
CREATE TRIGGER delete_trigger_customer
AFTER DELETE ON customer
FOR EACH ROW
BEGIN
INSERT INTO delete_customer VALUES (OLD.customerNumber, OLD.customerName, OLD.firstHalf,
OLD.secondHalf, OLD.totalPurchase, OLD.avgPurchase, OLD.grade, now());
END$$ 
DELIMITER ;



#입력 프로시저 생성
DROP PROCEDURE IF EXISTS insert_customer;
DELIMITER $$
CREATE PROCEDURE insert_customer(
IN in_customerNumber CHAR(6),
IN in_customerName VARCHAR(10),
IN in_firstHalf INT,
IN in_secondHalf INT
)
BEGIN
DECLARE in_totalPurchase INT;
DECLARE in_avr DOUBLE;
DECLARE in_grade VARCHAR(2);
SET in_totalPurchase = (in_firstHalf + in_secondHalf);
SET in_avgPurchase = (in_totalPurchase / 2.0);
SET in_grade = 
CASE
WHEN in_avr >= 70.0 THEN 'VVIP'
WHEN in_avr >= 60.0 THEN 'VIP'
WHEN in_avr >= 50.0 THEN 'GOLD'
WHEN in_avr >= 40.0 THEN 'SILVER'
ELSE
'BRONZE'
END;
INSERT INTO customer(customerNumber, customerName, firstHalf, secondHalf, totalPurchase, avgPurchase, grade)
VALUES(in_customerNumber, in_customerName, in_firstHalf, in_secondHalf, in_totalPurchase, in_avgPurchase, in_grade)
DELIMITER ;
