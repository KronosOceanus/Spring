DROP TABLE IF EXISTS t_user_role;

/* TABLE: t_user_role */
CREATE TABLE t_user_role
(
  id int(12) NOT NULL AUTO_INCREMENT,
  user_id int(12) NOT NULL,
  role_id int(12) NOT NULL,
  PRIMARY KEY (id),
  KEY user_id(user_id),
  KEY role_id(role_id),
  CONSTRAINT t_user_role_ibfk_1
  FOREIGN KEY (user_id) REFERENCES t_user(id) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT t_user_role_ibfk_2
  FOREIGN KEY (role_id) REFERENCES t_role(id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci