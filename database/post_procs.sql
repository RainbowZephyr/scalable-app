CREATE OR REPLACE FUNCTION create_post(post_text VARCHAR , reciepient_id INTEGER)
RETURNS BOOLEAN AS $$
DECLARE ID INTEGER;
        userID INTEGER;

BEGIN

SELECT user_id
INTO userID
FROM users;

INSERT INTO Post(id, user1_id,user2_id,post_text,creation_time,update_time)
VALUES(ID,userID,reciepient_id,post_text,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);
END; $$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION edit_post(ID INTEGER, newText VARCHAR)
RETURNS BOOLEAN AS $$
BEGIN
UPDATE post_text,update_time
INTO Post P
VALUES(newText, now () :: TIMESTAMP)
WHERE ID=P.id;
END; $$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION delete_post(ID INTEGER)
RETURNS VOID AS $$
BEGIN
DELETE FROM Post P
WHERE P.id=ID;
END; $$
LANGUAGE PLPSQL;
