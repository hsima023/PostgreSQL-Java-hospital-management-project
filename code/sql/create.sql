DROP TABLE IF EXISTS Hospital CASCADE;--OK
DROP TABLE IF EXISTS Patient CASCADE;--OK
DROP TABLE IF EXISTS Department CASCADE;--OK
DROP TABLE IF EXISTS Appointment CASCADE;--OK
DROP TABLE IF EXISTS Doctor CASCADE;--OK
DROP TABLE IF EXISTS Staff CASCADE;--OK
DROP TABLE IF EXISTS has_appointment CASCADE;--OK
DROP TABLE IF EXISTS request_maintenance CASCADE;--OK
DROP TABLE IF EXISTS searches CASCADE;--OK
DROP TABLE IF EXISTS schedules CASCADE;--OK


-------------
---DOMAINS---
-------------
CREATE DOMAIN _GENDER VARCHAR(1) CHECK (VALUE IN ( 'F' , 'M' ) );
CREATE DOMAIN _PINTEGER AS int4 CHECK(VALUE > 0);
CREATE DOMAIN _PZEROINTEGER AS int4 CHECK(VALUE >= 0);
CREATE DOMAIN _STATUS VARCHAR(2) CHECK (VALUE IN ('PA', 'AC', 'AV', 'WL')); --Past, Active, Available, Waitlisted

------------
---TABLES---
------------
CREATE TABLE Patient
(
	patient_ID INTEGER NOT NULL,
	name VARCHAR(128) NOT NULL,	
	gtype _GENDER NOT NULL,
	age INTEGER NOT NULL,
	address VARCHAR(256),
	number_of_appts INTEGER,
	PRIMARY KEY (patient_ID)
);

CREATE TABLE Hospital
(
	hospital_ID INTEGER NOT NULL,
	name VARCHAR(64) NOT NULL,	
	PRIMARY KEY (hospital_ID)
);

CREATE TABLE Department
(
	dept_ID INTEGER NOT NULL,
	name VARCHAR(32) NOT NULL,
	hid INTEGER NOT NULL,
	PRIMARY KEY (dept_ID),
	FOREIGN KEY (hid) REFERENCES Hospital(hospital_ID)
);

CREATE TABLE Staff
(
	staff_ID INTEGER NOT NULL,
	name VARCHAR(128) NOT NULL,	
	hid INTEGER NOT NULL,
	PRIMARY KEY (staff_ID),
	FOREIGN KEY (hid) REFERENCES Hospital(hospital_ID)
);

CREATE TABLE Doctor
(
	doctor_ID INTEGER NOT NULL,
	name VARCHAR(128),
	specialty VARCHAR(24),
	did INTEGER NOT NULL,
	PRIMARY KEY (doctor_ID),
	FOREIGN KEY (did) REFERENCES Department(dept_ID)
);


CREATE TABLE Appointment
(	
	appnt_ID INTEGER NOT NULL,	
	adate DATE NOT NULL,
	time_slot VARCHAR(11),
	status _STATUS,
	PRIMARY KEY (appnt_ID)
);



---------------
---RELATIONS---
---------------

CREATE TABLE request_maintenance
(
	patient_per_hour INTEGER NOT NULL,
	dept_name VARCHAR(32) NOT NULL,
	time_slot VARCHAR(11) NOT NULL,
	did INTEGER NOT NULL,	
	sid INTEGER NOT NULL,	
	PRIMARY KEY (did,sid),
	FOREIGN KEY (did) REFERENCES Doctor(doctor_ID),
	FOREIGN KEY (sid) REFERENCES Staff(staff_ID)	
);

CREATE TABLE searches
(
	hid INTEGER NOT NULL,	
	pid INTEGER NOT NULL,
	aid INTEGER NOT NULL,
	PRIMARY KEY (hid,pid,aid),
	FOREIGN KEY (hid) REFERENCES Hospital(hospital_ID),
	FOREIGN KEY (pid) REFERENCES Patient(patient_ID),
	FOREIGN KEY (aid) REFERENCES Appointment(appnt_ID)
);

CREATE TABLE schedules
(
	appt_id INTEGER NOT NULL,
	staff_id INTEGER NOT NULL,	
	PRIMARY KEY (appt_id,staff_id),
	FOREIGN KEY (appt_id) REFERENCES Appointment(appnt_ID),
	FOREIGN KEY (staff_id) REFERENCES Staff(staff_ID)
);

CREATE TABLE has_appointment
(
	appt_id INTEGER NOT NULL,
	doctor_id INTEGER NOT NULL,	
	PRIMARY KEY (appt_id,doctor_id),
	FOREIGN KEY (appt_id) REFERENCES Appointment(appnt_ID),
	FOREIGN KEY (doctor_id) REFERENCES Doctor(doctor_ID)
);

----------------------------
-- INSERT DATA STATEMENTS --
----------------------------

COPY Patient (
	patient_ID ,
	name ,	
	gtype ,
	age ,
	address ,
	number_of_appts
)
FROM 'patient.csv'
WITH DELIMITER ',';


COPY Hospital (
	hospital_ID,
	name
)
FROM 'hospital.csv'
WITH DELIMITER ',';


COPY Department (
	dept_ID,
	name,
	hid
)
FROM 'department.csv'
WITH DELIMITER ',';


COPY Staff (
	staff_ID,
	name,
	hid
)
FROM 'staff.csv'
WITH DELIMITER ',';


COPY Doctor (
	doctor_ID,
	name,
	specialty,
	did
)
FROM 'doctor.csv'
WITH DELIMITER ',';


COPY Appointment (
	appnt_ID,
	adate,
	time_slot,
	status
)
FROM 'appointment.csv'
WITH DELIMITER ',';


COPY request_maintenance (
	patient_per_hour,
	dept_name,
	time_slot,
	did,
	sid
)
FROM 'request_maintenance.csv'
WITH DELIMITER ',';


COPY searches (
	hid,
	pid,
	aid
)
FROM 'searches.csv'
WITH DELIMITER ',';


COPY schedules (
	appt_id,
	staff_id
)
FROM 'schedules.csv'
WITH DELIMITER ',';


COPY has_appointment (
	appt_id,
	doctor_id
)
FROM 'has_appointment.csv'
WITH DELIMITER ',';

---------------
----INDEXES----
---------------
DROP INDEX IF EXISTS doctor_name;
DROP INDEX IF EXISTS doctor_id;
DROP INDEX IF EXISTS doctor_specialty;
DROP INDEX IF EXISTS doctor_did;
DROP INDEX IF EXISTS patient_name;
DROP INDEX IF EXISTS patient_id;
DROP INDEX IF EXISTS patient_gender;
DROP INDEX IF EXISTS patient_age;
DROP INDEX IF EXISTS patient_address;
DROP INDEX IF EXISTS patient_num_appt;
DROP INDEX IF EXISTS appt_date;
DROP INDEX IF EXISTS appt_id;
DROP INDEX IF EXISTS appt_status;
DROP INDEX IF EXISTS appt_timeslot;
DROP INDEX IF EXISTS dept_name;

CREATE INDEX doctor_name ON Doctor USING BTREE (name);
CREATE INDEX doctor_id ON Doctor USING BTREE (doctor_ID);
CREATE INDEX doctor_specialty ON Doctor USING BTREE (specialty);
CREATE INDEX doctor_did ON Doctor USING BTREE (did);
CREATE INDEX patient_name ON Patient USING BTREE (name);
CREATE INDEX patient_id ON Patient USING BTREE (patient_ID);
CREATE INDEX patient_gender ON Patient USING BTREE (gtype);
CREATE INDEX patient_age ON Patient USING BTREE (age);
CREATE INDEX patient_address ON Patient USING BTREE (address);
CREATE INDEX patient_num_appt ON Patient USING BTREE (number_of_appts);
CREATE INDEX appt_date ON Appointment USING BTREE (adate);
CREATE INDEX appt_id ON Appointment USING BTREE (appnt_id);
CREATE INDEX appt_status ON Appointment USING BTREE (status);
CREATE INDEX appt_timeslot ON Appointment USING BTREE (time_slot);
CREATE INDEX dept_name ON Department USING BTREE (name);

