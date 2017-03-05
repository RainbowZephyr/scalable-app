CREATE OR REPLACE FUNCTION sign_up(email VARCHAR, hashed_password VARCHAR)
RETURNS BOOLEAN AS $$  --Delimiter for functions and strings
DECLARE userID    INTEGER;
        creation_time DATETIME;
        last_login DATETIME;
BEGIN
INSERT INTO Member (member_id, email, password_hash, creation_time, last_login)
    VALUES (userID, email, hashed_password, creation_time, last_login)
END; $$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION login(member_id VARCHAR)
RETURNS BOOLEAN AS $$  --Delimiter for functions and strings
BEGIN
    UPDATE last_login
    INTO Member
    VALUES (now() :: TIMESTAMP)
    WHERE member_id = member_id
END; $$
LANGUAGE PLPGSQL;
