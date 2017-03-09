CREATE OR REPLACE FUNCTION sign_up(email VARCHAR, hashed_password VARCHAR)
RETURNS BOOLEAN AS $$  --Delimiter for functions and strings
DECLARE userID    INTEGER;
BEGIN
INSERT INTO Member (member_id, email, password_hash, creation_time, last_login)
    VALUES (userID, email, hashed_password, creation_time, last_login);

END; $$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION login(member_id VARCHAR)
RETURNS BOOLEAN AS $$  --Delimiter for functions and strings
BEGIN
    UPDATE last_login
    INTO Member
    VALUES (now() :: TIMESTAMP)
    WHERE id = member_id
END; $$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION edit_user(member_id VARCHAR,
                                       email VARCHAR,
                                       password VARCHAR,
                                       first_name VARCHAR,
                                       last_name VARCHAR,
                                       date_of_birth DATE,
                                       )
    RETURNS SETOF users AS $$
BEGIN
    INSERT INTO Member(email, password_hash, first_name, last_name, date_of_birth)
    VALUES (email, password, first_name, last_name, date_of_birth)
    WHERE id = member_id
END; $$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION delete_user(member_id VARCHAR)
    RETURNS VOID AS $$
BEGIN
    DELETE FROM Member
    WHERE id = member_id
END; $$
LANGUAGE PLPGSQL;
