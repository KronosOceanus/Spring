DROP TABLE IF EXISTS t_employee;
DROP TABLE IF EXISTS t_employee_task;
DROP TABLE IF EXISTS t_female_health_form;
DROP TABLE IF EXISTS t_male_health_form;
DROP TABLE IF EXISTS t_task;
DROP TABLE IF EXISTS t_work_card;

/* TABLE: t_employee */
CREATE TABLE t_employee
(
	id INT(12) NOT NULL AUTO_INCREMENT,
	real_name VARCHAR(60) NOT NULL,
	sex INT(2) NOT NULL COMMENT '1-男 0-女',
	birthday DATE NOT NULL,
	mobile VARCHAR(20) NOT NULL,
	email VARCHAR(60) NOT NULL,
	position VARCHAR(20) NOT NULL,
	note VARCHAR(256),
	PRIMARY KEY(id)
);

/* TABLE: t_employee_task */
CREATE TABLE t_employee_task
(
	id INT(12) NOT NULL,
	emp_id INT(12) NOT NULL,
	task_id INT(12) NOT NULL,
	task_name VARCHAR(60) NOT NULL,
	note VARCHAR(256),
	PRIMARY KEY(id)
);

/* TABLE: t_female_health_form */
CREATE TABLE t_female_health_form
(
	id INT(12) NOT NULL AUTO_INCREMENT,
	emp_id INT(12) NOT NULL,
	heart VARCHAR(64) NOT NULL,
	liver VARCHAR(64) NOT NULL,
	spleen VARCHAR(64) NOT NULL,
	lung VARCHAR(64) NOT NULL,
	kidney VARCHAR(64) NOT NULL,
	uterus VARCHAR(64) NOT NULL,
	note VARCHAR(256),
	PRIMARY KEY(id)
);

/* TABLE: t_male_health_form  */
CREATE TABLE t_male_health_form
(
	id INT(12) NOT NULL AUTO_INCREMENT,
	emp_id INT(12) NOT NULL,
	heart VARCHAR(64) NOT NULL,
	liver VARCHAR(64) NOT NULL,
	spleen VARCHAR(64) NOT NULL,
	lung VARCHAR(64) NOT NULL,
	kidney VARCHAR(64) NOT NULL,
	prostate VARCHAR(64) NOT NULL,
	note VARCHAR(256),
	PRIMARY KEY(id)
);

/* TABLE: t_task */
CREATE TABLE t_task
(
	id INT(12) NOT NULL,
	title VARCHAR(60) NOT NULL,
	context VARCHAR(256) NOT NULL,
	note VARCHAR(256),
	PRIMARY KEY(id)
);

/* TABLE: t_work_card */
CREATE TABLE t_work_card
(
	id INT(12) NOT NULL AUTO_INCREMENT,
	emp_id INT(12) NOT NULL,
	real_name VARCHAR(60) NOT NULL,
	department VARCHAR(20) NOT NULL,
	mobile VARCHAR(20) NOT NULL,
	position VARCHAR(30) NOT NULL,
	note VARCHAR(256),
	PRIMARY KEY(id)
);


ALTER TABLE t_employee_task ADD CONSTRAINT FK_Reference_4 FOREIGN KEY(emp_id)
	REFERENCES t_employee(id) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE t_employee_task ADD CONSTRAINT FK_Reference_8 FOREIGN KEY(task_id)
	REFERENCES t_task(id) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE t_female_health_form ADD CONSTRAINT FK_Reference_5 FOREIGN KEY(emp_id)
	REFERENCES t_employee(id) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE t_male_health_form ADD CONSTRAINT FK_Reference_6 FOREIGN KEY(emp_id)
	REFERENCES t_employee(id) ON DELETE RESTRICT ON UPDATE RESTRICT;
ALTER TABLE t_work_card ADD CONSTRAINT FK_Reference_7 FOREIGN KEY(emp_id)
	REFERENCES t_employee(id) ON DELETE RESTRICT ON UPDATE RESTRICT;