CREATE OR REPLACE FUNCTION sign_up(email VARCHAR, hashed_password VARCHAR)
RETURNS BOOLEAN AS $$  --Delimiter for functions and strings
DECLARE followers INTEGER;
        conv      INTEGER;
        conv_id   INTEGER;
        userID    INTEGER;
BEGIN
    INSERT user_id
    INTO Member
    VALUES ( )
END; $$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION sign_up(email VARCHAR, hashed_password VARCHAR)
RETURNS BOOLEAN AS $$  --Delimiter for functions and strings
DECLARE followers INTEGER;
        conv      INTEGER;
        conv_id   INTEGER;
        userID    INTEGER;
BEGIN
    INSERT user_id
    INTO Member
    VALUES ( )
END; $$
LANGUAGE PLPGSQL;
