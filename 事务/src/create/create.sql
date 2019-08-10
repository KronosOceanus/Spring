CREATE TABLE account
(
	id INT PRIMARY KEY AUTO_INCREMENT,
	username VARCHAR(50),
	money INT
);

INSERT INTO account(username, money) VALUES('jack','10000');
INSERT INTO account(username,money) VALUES('rose','10000');