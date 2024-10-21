# FlightSeatBookingSimulator
A simple java flight seating booking simulator GUI with a MYSQL database 

# Flight Seat Booking Simulator

A simple Java-based flight seat booking simulator using a GUI built with Swing and MySQL for database integration. Users can select and book seats in a flight, with seat statuses updated in real time.

## Features
- **Seat Selection**: Users can select available seats (grey).
- **Seat Booking**: Once a seat is booked, it is marked as red and cannot be modified.
- **Clear Selected Seats**: Users can clear selected (green) seats before booking them.
- **Database Integration**: Seats' booking statuses are stored in a MySQL database.

## Technologies
- **Java** (JDK 8+)
- **Swing** for GUI
- **MySQL** for database (using XAMPP)
- **JDBC** for database connectivity

## Requirements
- Java Development Kit (JDK 8+)
- MySQL installed and running (can be part of [XAMPP](https://www.apachefriends.org/index.html))
- MySQL Connector for Java (ensure it's added to your classpath)

## Database Setup

1. Start MySQL using XAMPP.
2. Create a new database named `seat_booking` with the following table structure:

```sql
CREATE DATABASE seat_booking;

USE seat_booking;

CREATE TABLE seats (
    row INT,
    col INT,
    status ENUM('available', 'booked'),
    PRIMARY KEY (row, col)
);
```
3. Have created a flight.sql to help you run the SQL commands
4. make sure to compile and run the code together with JDBC in order to the code to work 
