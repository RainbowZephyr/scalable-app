CREATE OR REPLACE FUNCTION create_group(name VARCHAR, description VARCHAR, creator_id INTEGER)
RETURNS BOOLEAN AS $$
DECLARE groupID  INTEGER;
        creation_time CURRENT_TIMESTAMP;
BEGIN
     INSERT INTO facebook_group (id, name, description, creator_id, created_at)
                          Values(groupID, name, description, creator_id, creation_time)
END; $$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION edit_group(groupID INTEGER, name VARCHAR, description VARCHAR)
RETURNS BOOLEAN AS $$
BEGIN
      INSERT INTO facebook_group(name, description)
                          values(name, description)
where id = groupID;
END; $$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION delete_group(groupID INTEGER)
RETURNS BOOLEAN AS $$
BEGIN
     DELETE FROM facebook_group
     where id = groupID
END; $$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION add_user_group(groupID INTEGER, userID INTEGER, isAdmin BOOLEAN)
RETURNS BOOLEAN AS $$
BEGIN
      INSERT INTO group_members(group_id, user_id, admin)
                         VALUES(groupID, userID, isAdmin)
END; $$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION add_post_group(groupID INTEGER, userID INTEGER, postTxt text)
RETURNS BOOLEAN AS $$
DECLARE postID INTEGER;
        posting_time CURRENT_TIMESTAMP;
BEGIN
      INSERT INTO group_posts(post_id, group_id, user_id, post_text, posted_at)
                       VALUES(postID,  groupID,  userID, postTxt, posting_time)
END; $$
LANGUAGE PLPGSQL;
