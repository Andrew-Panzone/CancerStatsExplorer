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
  sex VARCHAR(50) NOT NULL,
  race VARCHAR(50) NOT NULL,
  UNIQUE(sex, race)
);

CREATE TABLE state
(
	sname VARCHAR(64) PRIMARY KEY NOT NULL
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
    groupID INT NOT NULL,
    FOREIGN KEY (groupID) REFERENCES demographicgroup(groupID) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE professional
(
	id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL,
    age INT NOT NULL,
    specialization VARCHAR(64) NOT NULL,
	gender VARCHAR(64) NOT NULL
);

CREATE TABLE cancertype
(
	name VARCHAR(64) PRIMARY KEY NOT NULL,
	description VARCHAR(200) NOT NULL
);

CREATE TABLE report
(
	id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    time_stamp TIMESTAMP NOT NULL,
    professional INT NOT NULL,
    patient INT NOT NULL,
    cancer_type VARCHAR(64),
    FOREIGN KEY (professional) REFERENCES professional(id) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (cancer_type) REFERENCES cancertype(name) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (patient) REFERENCES patient(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE incidencerate
(
	id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    state VARCHAR(64) NOT NULL,
    cancertype VARCHAR(64) NOT NULL,
    sex VARCHAR(50) NOT NULL,
    race VARCHAR(50) NOT NULL,
	year INT NOT NULL,
	i_rate FLOAT,
	case_count INT,
    population INT,
    FOREIGN KEY (state) REFERENCES state(sname) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (cancertype) REFERENCES cancertype(name) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE deathrate
(
	id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    state VARCHAR(64) NOT NULL,
    cancertype VARCHAR(64) NOT NULL,
    sex VARCHAR(50) NOT NULL,
    race VARCHAR(50) NOT NULL,
	year INT NOT NULL,
	d_rate FLOAT,
	death_count INT,
    population INT,
    FOREIGN KEY (state) REFERENCES state(sname) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (cancertype) REFERENCES cancertype(name) ON UPDATE CASCADE ON DELETE CASCADE
);

INSERT INTO state VALUES
('Alabama'),
('Alaska'),
('Arizona'),
('Arkansas'),
('California'),
('Colorado'),
('Connecticut'),
('Delaware'),
('District of Columbia'),
('Florida'),
('Georgia'),
('Hawaii'),
('Idaho'),
('Illinois'),
('Indiana'),
('Iowa'),
('Kansas'),
('Kentucky'),
('Louisiana'),
('Maine'),
('Maryland'),
('Massachusetts'),
('Michigan'),
('Minnesota'),
('Mississippi'),
('Missouri'),
('Montana'),
('Nebraska'),
('Nevada'),
('New Hampshire'),
('New Jersey'),
('New Mexico'),
('New York'),
('North Carolina'),
('North Dakota'),
('Ohio'),
('Oklahoma'),
('Oregon'),
('Pennsylvania'),
('Puerto Rico'),
('Rhode Island'),
('South Carolina'),
('South Dakota'),
('Tennessee'),
('Texas'),
('Utah'),
('Vermont'),
('Virginia'),
('Washington'),
('West Virginia'),
('Wisconsin'),
('Wyoming');




