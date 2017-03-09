CREATE OR REPLACE FUNCTION sign_up(email VARCHAR, hashed_password VARCHAR, fName VARCHAR, lName VARCHAR, dob DATE)
  RETURNS BOOLEAN AS $$  --Delimiter for functions and strings
DECLARE userID INTEGER;
BEGIN
  INSERT INTO member VALUES (DEFAULT, email, hashed_password, fName, lName, dob, now() :: TIMESTAMP);
  INSERT INTO users VALUES (CURRVAL(pg_get_serial_sequence('member', 'id')));

END; $$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION login(member_id VARCHAR, session VARCHAR, address VARCHAR)
  RETURNS BOOLEAN AS $$  --Delimiter for functions and strings
DECLARE current_time TIMESTAMP := now() :: TIMESTAMP;

BEGIN
  INSERT INTO logins
  VALUES (DEFAULT, member_id, current_time, address, FALSE);

  INSERT INTO sessions
  VALUES (DEFAULT, member_id, current_time, current_time, address, FALSE);

END; $$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION edit_user(member_id         VARCHAR,
                                     new_email         VARCHAR,
                                     new_password      VARCHAR,
                                     new_first_name    VARCHAR,
                                     new_last_name     VARCHAR,
                                     new_date_of_birth DATE)
  RETURNS SETOF users AS $$
BEGIN
  UPDATE member
  SET email = new_email, first_name = new_first_name, last_name = new_last_name, date_of_birth = new_date_of_birth
  WHERE id = member_id;
END; $$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION delete_user(member_id VARCHAR)
  RETURNS VOID AS $$
BEGIN
  DELETE FROM Member
  WHERE id = member_id;
END; $$
LANGUAGE PLPGSQL;
