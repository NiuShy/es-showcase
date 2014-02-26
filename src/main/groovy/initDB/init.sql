create database es;

create table PRODUCTS
(
  id int primary key auto_increment,
  product_name varchar(50),
  product_category varchar(30),
  brand_name varchar(30),
  model_name varchar(30),
  tags varchar(50),
  price double);


create unique index unique_product on PRODUCTS (product_name);