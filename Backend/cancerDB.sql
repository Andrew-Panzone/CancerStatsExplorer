-- ********************************************
-- CREATE THE CANCER DATABASE
-- *******************************************

-- create the database
DROP DATABASE IF EXISTS cancerstatisticdb;
CREATE DATABASE cancerstatisticdb;

-- select the database
USE cancerstatisticdb;

-- create the tables
-- lol testing
CREATE TABLE demographicgroup # DO THIS FIRST -- Insert these via created csv
(
  groupID INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  sex VARCHAR(50) NOT NULL,
  race VARCHAR(50) NOT NULL,
  UNIQUE(sex),
  UNIQUE(race)
);

CREATE TABLE state # DO THIS SECOND -- Insert these via created csv
(
	s_name VARCHAR(64) PRIMARY KEY NOT NULL
);

CREATE TABLE users # Leave empty for now, will be populated by our frontend
(
    username VARCHAR(50) PRIMARY KEY,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE user_action # Further discussion required (what actions can a user take?)
(
	a_type INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
	role_requirement INT,
	action_description VARCHAR(200) NOT NULL
);


CREATE TABLE activitylog # Leave empty for now, will be populated by our frontend
(
    logid INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    time_stamp TIMESTAMP NOT NULL,
    username VARCHAR(50) NOT NULL,
    log_action INT NOT NULL,
    log_description VARCHAR(200) NOT NULL,
    FOREIGN KEY (username) REFERENCES users(username) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (log_action) REFERENCES user_action(a_type) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE patient # Fabricate these values
(
	id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL,
    age INT NOT NULL,
    groupID INT NOT NULL,
    FOREIGN KEY (groupID) REFERENCES demographicgroup(groupID) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE professional # Fabricate these values
(
	id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL,
    age INT NOT NULL,
    specialization VARCHAR(64) NOT NULL,
	gender VARCHAR(64) NOT NULL
);

CREATE TABLE cancertype # DO THIS THIRD -- Insert these via created csv
(
	name VARCHAR(64) PRIMARY KEY NOT NULL,
	description VARCHAR(64) NOT NULL
);

CREATE TABLE report # Fabricate these values
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

CREATE TABLE incidencerate # Insert via csv files
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
    FOREIGN KEY (state) REFERENCES state(s_name) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (cancertype) REFERENCES cancertype(name) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (sex) REFERENCES demographicgroup(sex) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (race) REFERENCES demographicgroup(race) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE deathrate # Insert via csv files
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
    FOREIGN KEY (state) REFERENCES state(s_name) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (cancertype) REFERENCES cancertype(name) ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (sex) REFERENCES demographicgroup(sex) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (race) REFERENCES demographicgroup(race) ON UPDATE CASCADE ON DELETE CASCADE
);




