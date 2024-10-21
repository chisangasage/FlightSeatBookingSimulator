-- Active: 1728917734193@@127.0.0.1@3306@seat_booking
CREATE DATABASE seat_booking;

USE seat_booking;

CREATE TABLE seats (
    id INT AUTO_INCREMENT PRIMARY KEY,
    seat_row INT NOT NULL,
    seat_column CHAR(1) NOT NULL,
    status VARCHAR(10) NOT NULL
);
DESCRIBE seats;