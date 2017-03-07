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

CREATE OR REPLACE FUNCTION user_to_admin_group(groupID INTEGER, userID INTEGER)
RETURNS BOOLEAN AS $$
BEGIN
      INSERT INTO group_members(admin)
                         VALUES(true)
      where group_id = groupID and user_id = userID
END; $$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION remove_admin_group(groupID INTEGER, userID INTEGER)
RETURNS BOOLEAN AS $$
BEGIN
      DELETE FROM group_members
      where group_id = groupID and user_id = userID and admin = true
END; $$
LANGUAGE PLPGSQL:

CREATE OR REPLACE FUNCTION remove_user_group(groupID INTEGER, userID INTEGER)
RETURNS BOOLEAN AS $$
BEGIN
      DELETE FROM group_members
      where group_id = groupID and user_id = userID and admin = false
END; $$
LANGUAGE PLPGSQL:

CREATE OR REPLACE FUNCTION add_post_group(groupID INTEGER, userID INTEGER, postTxt text)
RETURNS BOOLEAN AS $$
DECLARE postID INTEGER;
        posting_time CURRENT_TIMESTAMP;
BEGIN
      INSERT INTO group_post(post_id, group_id, user_id, post_text, created_at)
                       VALUES(postID,  groupID,  userID, postTxt, posting_time)
END; $$
LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION edit_post_group(postID INTEGER,
                                           groupID INTEGER,
                                           userID INTEGER,
                                           postTxt text)
RETURNS BOOLEAN AS $$
DECLARE updating_time CURRENT_TIMESTAMP;
BEGIN
      INSERT INTO group_post(post_text, updated_at)
                       VALUES(postTxt, updating_time)
      where id = postID and group_id = groupID and user_id = userID
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
