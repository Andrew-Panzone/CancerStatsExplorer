-- ********************************************
-- CREATE THE CANCER DATABASE
-- *******************************************

-- create the database
DROP DATABASE IF EXISTS cancerstatisticdb;
CREATE DATABASE cancerstatisticdb;

-- select the database
USE cancerstatisticdb;

-- create the tables

CREATE TABLE demographicgroup
(
  groupID INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  gender VARCHAR(50),
  race VARCHAR(50),
  description VARCHAR(64)
);

CREATE TABLE state
(
	s_name VARCHAR(64) PRIMARY KEY NOT NULL,
	population INT NOT NULL
);

CREATE TABLE users 
(
    username VARCHAR(50) PRIMARY KEY,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE user_action
(
	a_type INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
	role_requirement INT,
	action_description VARCHAR(200) NOT NULL
);


CREATE TABLE activitylog 
(
    logid INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    time_stamp TIMESTAMP NOT NULL,
    username VARCHAR(50) NOT NULL,
    log_action INT NOT NULL,
    log_description VARCHAR(200) NOT NULL,
    FOREIGN KEY (username) REFERENCES users(username) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (log_action) REFERENCES user_action(a_type) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE patient
(
	id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL,
    age INT NOT NULL,
    gender INT NOT NULL,
    race INT NOT NULL,
    FOREIGN KEY (gender) REFERENCES demographicgroup(groupID) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (race) REFERENCES demographicgroup(groupID) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE professional 
(
	id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL,
    age INT NOT NULL,
    specialization VARCHAR(64) NOT NULL,
	gender INT NOT NULL,
    FOREIGN KEY (gender) REFERENCES demographicgroup(groupID) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE report 
(
	id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    description VARCHAR(200) NOT NULL,
    time_stamp TIMESTAMP NOT NULL,
    professional INT NOT NULL,
    patient INT NOT NULL,
    FOREIGN KEY (professional) REFERENCES professional(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (patient) REFERENCES patient(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE cancertype
(
	name VARCHAR(64) PRIMARY KEY NOT NULL,
	description VARCHAR(64) NOT NULL
);

CREATE TABLE incidencerate
(
	id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    state VARCHAR(64) NOT NULL,
    cancertype VARCHAR(64) NOT NULL,
    gender INT NOT NULL,
    race INT NOT NULL,
	year INT NOT NULL,
	i_rate FLOAT,
	case_count INT,
    population INT REFERENCES state(population) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (state) REFERENCES state(s_name) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (cancertype) REFERENCES cancertype(name) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (gender) REFERENCES demographicgroup(groupID) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (race) REFERENCES demographicgroup(groupID) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE deathrate
(
	id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    state VARCHAR(64) NOT NULL,
    cancertype VARCHAR(64) NOT NULL,
    gender INT NOT NULL,
    race INT NOT NULL,
	year INT NOT NULL,
	d_rate FLOAT,
	death_count INT,
    population INT REFERENCES state(population) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (state) REFERENCES state(s_name) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (cancertype) REFERENCES cancertype(name) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (gender) REFERENCES demographicgroup(groupID) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (race) REFERENCES demographicgroup(groupID) ON UPDATE CASCADE ON DELETE CASCADE
);




