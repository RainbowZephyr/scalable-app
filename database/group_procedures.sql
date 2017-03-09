CREATE OR REPLACE FUNCTION create_group(name VARCHAR, description VARCHAR, creator_id INTEGER)
RETURNS BOOLEAN AS $$
DECLARE groupID  INTEGER;

BEGIN
INSERT INTO facebook_group (id, name, description, creator_id, created_at)
Values(DEFAULT, name, description, creator_id, CURRENT_TIMESTAMP);
INSERT INTO group_members values (CURRVAL(pg_get_serial_sequence('facebook_group','id')), creator_id, TRUE);
END; $$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION edit_group(groupID INTEGER, new_name VARCHAR, new_description VARCHAR)
RETURNS BOOLEAN AS $$
BEGIN
UPDATE facebook_group SET name = new_name, description = new_description where id = groupID;
END; $$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION delete_group(groupID INTEGER)
RETURNS BOOLEAN AS $$
BEGIN
DELETE FROM facebook_group
where id = groupID;
END; $$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION add_user_group(groupID INTEGER, userID INTEGER, isAdmin BOOLEAN)
RETURNS BOOLEAN AS $$
BEGIN
INSERT INTO group_members(group_id, user_id, admin)
VALUES(groupID, userID, isAdmin);
END; $$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION make_group_admin(groupID INTEGER, userID INTEGER)
RETURNS BOOLEAN AS $$
BEGIN
UPDATE group_members SET admin = TRUE where group_id = groupID and user_id = userID;
END; $$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION remove_group_admin(groupID INTEGER, userID INTEGER)
RETURNS BOOLEAN AS $$
BEGIN
UPDATE group_members SET admin = FALSE where group_id = groupID and user_id = userID;
END; $$
LANGUAGE PLPGSQL:

CREATE OR REPLACE FUNCTION remove_group_user(groupID INTEGER, userID INTEGER)
RETURNS BOOLEAN AS $$
BEGIN
DELETE FROM group_members
where group_id = groupID and user_id = userID;
END; $$
LANGUAGE PLPGSQL:

CREATE OR REPLACE FUNCTION create_group_post(groupID INTEGER, userID INTEGER, postTxt text)
RETURNS BOOLEAN AS $$
BEGIN
INSERT INTO group_post VALUES(DEFAULT,  groupID,  userID, postTxt, now()::TIMESTAMP, now()::TIMESTAMP);
END; $$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION edit_group_post(postID INTEGER,
    groupID INTEGER,
    userID INTEGER,
    postTxt text)
    RETURNS BOOLEAN AS $$
    BEGIN
    UPDATE group_post SET post_text = postTxt, updated_at = now()::TIMESTAMP where id = postID and group_id = groupID and user_id = userID;
    END; $$
    LANGUAGE PLPGSQL;

    CREATE OR REPLACE FUNCTION delete_post_group(postID INTEGER,
        groupID INTEGER,
        userID INTEGER)
        RETURNS BOOLEAN AS $$
        BEGIN
        DELETE FROM group_post
        where id = postID and group_id = groupID and user_id = userID
        END; $$
        LANGUAGE PLPGSQL;

        CREATE OR REPLACE FUNCTION make_comment_post_group(postID INTEGER,
            groupID INTEGER,
            userID INTEGER,
            commentTxt VARCHAR)
            RETURNS BOOLEAN AS $$
            DECLARE commentID  INTEGER;
            creation_time CURRENT_TIMESTAMP;
            BEGIN
            INSERT INTO group_post_comments(id, post_id, group_id, user_id, comment_text, created_at)
            Values(commentID, postID, groupID, userID, commentTxt, creation_time)
            END; $$
            LANGUAGE PLPGSQL;

            CREATE OR REPLACE FUNCTION edit_comment_post_group(commentID INTEGER,
                postID INTEGER,
                groupID INTEGER,
                userID INTEGER,
                commentTxt VARCHAR)
                RETURNS BOOLEAN AS $$
                DECLARE updating_time CURRENT_TIMESTAMP;
                BEGIN
                INSERT INTO group_post_comments(comment_text, updated_at)
                Values(commentTxt, updating_time)
                where id = commentID and post_id = postID and group_id = groupID and user_id = userID
                END; $$
                LANGUAGE PLPGSQL;

                CREATE OR REPLACE FUNCTION delete_comment_post_group(commentID INTEGER,
                    postID INTEGER,
                    groupID INTEGER,
                    userID INTEGER,
                    RETURNS BOOLEAN AS $$
                    BEGIN
                    DELETE FROM group_post_comments
                    where id = comment and post_id = postID and group_id = groupID and user_id = userID
                    END; $$
                    LANGUAGE PLPGSQL;
