DROP TABLE IF EXISTS member CASCADE;
CREATE TABLE member (
    id serial PRIMARY KEY,
    email varchar UNIQUE NOT NULL,
    password_hash varchar NOT NULL,
    first_name varchar NOT NULL,
    last_name varchar NOT NULL,
    date_of_birth date,
    creation_time TIMESTAMP,
    last_login TIMESTAMP
);

DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users (
    user_id int PRIMARY KEY,
    FOREIGN KEY(user_id) references member(id)
);

DROP TABLE IF EXISTS admin CASCADE;
CREATE TABLE admin (
    admin_id int PRIMARY KEY,
    FOREIGN KEY(admin_id) references member(id)
);

DROP TABLE IF EXISTS friends CASCADE;
CREATE TABLE friends (
    user_1 int,
    user_2 int,
    accepted boolean,
    PRIMARY KEY(user_1,user_2),
    FOREIGN KEY(user_1) references member(id),
    FOREIGN KEY(user_2) references member(id)
);

DROP TABLE IF EXISTS blocks CASCADE;
CREATE TABLE blocks (
    user_1 int,
    user_2 int,
    PRIMARY KEY (user_1,user_2),
    FOREIGN KEY (user_1) references member(id),
    FOREIGN KEY (user_2) references member(id)
);

DROP TABLE IF EXISTS post CASCADE;
CREATE TABLE post (
    id SERIAL PRIMARY KEY,
    user1_id int,
    user2_id int,
    post_text varchar NOT NULL,
    creation_time TIMESTAMP,
    update_time TIMESTAMP,
    FOREIGN KEY (user1_id) references member(id),
    FOREIGN KEY (user2_id) references member(id)

);

DROP TABLE IF EXISTS post_tags CASCADE;
CREATE TABLE post_tags (
    post_id int,
    user_id int,
    PRIMARY KEY (post_id,user_id),
    FOREIGN Key (post_id) references post(id),
    FOREIGN Key (user_id) references member(id)
);

DROP TABLE IF EXISTS post_hashtags CASCADE;
CREATE TABLE post_hashtags (
    post_id int,
    user_id int,
    hashtag varchar,
    PRIMARY KEY (post_id, user_id,hashtag),
    FOREIGN Key (post_id) references post(id),
    FOREIGN Key (user_id) references member(id)
);

DROP TABLE IF EXISTS post_reactions CASCADE;
CREATE TABLE post_reactions (
    post_id int,
    user_id int,
    reaction_type int,
    PRIMARY KEY (post_id, user_id),
    FOREIGN Key (post_id) references post(id),
    FOREIGN Key (user_id) references member(id)
);

DROP TABLE IF EXISTS comment CASCADE;
CREATE TABLE comment (
    id int PRIMARY KEY,
    post_id int,
    user_id int,
    comment_text varchar,
    creation_time TIMESTAMP,
    update_time TIMESTAMP,
    FOREIGN Key (user_id) references member(id),
    FOREIGN Key (post_id) references post(id)

);

DROP TABLE IF EXISTS comment_tags CASCADE;
CREATE TABLE comment_tags (
    comment_id int,
    user_id int,
    PRIMARY KEY (comment_id, user_id),
    FOREIGN Key (comment_id) references comment(id),
    FOREIGN Key (user_id) references member(id)
);

DROP TABLE IF EXISTS comment_likes CASCADE;
CREATE TABLE comment_likes (
    comment_id int,
    user_id int,
    PRIMARY KEY (comment_id, user_id),
    FOREIGN Key (comment_id) references comment(id),
    FOREIGN Key (user_id) references member(id)
);

DROP TABLE IF EXISTS message_thread CASCADE;
CREATE TABLE message_thread (
    id int PRIMARY KEY,
    created_at TIMESTAMP
);

DROP TABLE IF EXISTS message CASCADE;
CREATE TABLE message (
    id int PRIMARY KEY,
    thread_id int,
    message_text varchar,
    creation_time TIMESTAMP,
    FOREIGN Key (thread_id) references message_thread(id)
);

DROP TABLE IF EXISTS message_thread_users CASCADE;
CREATE TABLE message_thread_users (
    thread_id int,
    user_id int,
    PRIMARY KEY (thread_id, user_id),
    FOREIGN Key (thread_id) references message_thread(id),
    FOREIGN Key (user_id) references member(id)
);

DROP TABLE IF EXISTS event CASCADE;
CREATE TABLE event (
    id int PRIMARY KEY,
    title varchar,
    description varchar,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    creator_id int,
    created_at TIMESTAMP,
    FOREIGN Key (creator_id) references member(id)
);

DROP TABLE IF EXISTS event_subscribers CASCADE;
CREATE TABLE event_subscribers (
    event_id int,
    user_id int,
    attending boolean DEFAULT NULL,
    PRIMARY KEY (event_id, user_id),
    FOREIGN Key (event_id) references event(id),
    FOREIGN Key (user_id) references member(id)
);

DROP TABLE IF EXISTS image CASCADE;
CREATE TABLE image (
    id serial PRIMARY KEY,
    image_path varchar
);

DROP TABLE IF EXISTS saved_posts CASCADE;
CREATE TABLE saved_posts (
    post_id int,
    user_id int,
    PRIMARY KEY (post_id, user_id),
    FOREIGN Key (post_id) references post(id),
    FOREIGN Key (user_id) references member(id)
);

DROP TABLE IF EXISTS facebook_group CASCADE;
CREATE TABLE facebook_group (
    id int PRIMARY KEY,
    name varchar,
    description varchar,
    creator_id int,
    created_at TIMESTAMP
);

DROP TABLE IF EXISTS group_members CASCADE;
CREATE TABLE group_members (
    group_id int,
    user_id int,
    admin boolean,
    PRIMARY KEY (group_id, user_id),
    FOREIGN Key (group_id) references facebook_group(id),
    FOREIGN Key (user_id) references member(id)
);

DROP TABLE IF EXISTS group_post CASCADE;
CREATE TABLE group_post (
    id serial PRIMARY KEY,
    group_id int,
    user_id int,
    post_text varchar,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN Key (group_id) references facebook_group(id),
    FOREIGN Key (user_id) references member(id)
);

DROP TABLE IF EXISTS group_post_comments CASCADE;
CREATE TABLE group_post_comments (
    id serial PRIMARY KEY,
    group_id int,
    user_id int,
    comment_text varchar,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN Key (group_id) references facebook_group(id),
    FOREIGN Key (user_id) references member(id)
);

DROP TABLE IF EXISTS follows CASCADE;
CREATE TABLE follows (
    user1_id int,
    user2_id int,
    PRIMARY Key (user1_id, user2_id),
    FOREIGN Key (user1_id) references member(id),
    FOREIGN Key (user2_id) references member(id)
);

DROP TABLE IF EXISTS comment_replies CASCADE;
CREATE TABLE comment_replies (
    id serial PRIMARY KEY,
    comment_id int,
    user_id int,
    reply_text varchar,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN Key (comment_id) references comment(id),
    FOREIGN Key (user_id) references member(id)
);

DROP TABLE IF EXISTS report_user CASCADE;
CREATE TABLE report_user (
    user1_id int,
    user2_id int,
    PRIMARY KEY (user1_id, user2_id),
    FOREIGN Key (user1_id) references member(id),
    FOREIGN Key (user2_id) references member(id)
);

DROP TABLE IF EXISTS report_post CASCADE;
CREATE TABLE report_post (
    user_id int,
    post_id int,
    PRIMARY KEY (user_id, post_id),
    FOREIGN Key (user_id) references member(id),
    FOREIGN Key (post_id) references post(id)
);

DROP TABLE IF EXISTS report_comment CASCADE;
CREATE TABLE report_comment (
    user_id int,
    comment_id int,
    PRIMARY KEY (user_id, comment_id),
    FOREIGN Key (user_id) references member(id),
    FOREIGN Key (comment_id) references comment(id)
);

DROP TABLE IF EXISTS report_group CASCADE;
CREATE TABLE report_group (
    user_id int,
    group_id int,
    PRIMARY KEY (user_id, group_id),
    FOREIGN Key (user_id) references member(id),
    FOREIGN Key (group_id) references facebook_group(id)
);

DROP TABLE IF EXISTS user_album CASCADE;
CREATE TABLE user_album (
    user_id int,
    picture_id int,
    PRIMARY KEY (user_id, picture_id),
    FOREIGN Key (user_id) references member(id),
    FOREIGN Key (picture_id) references image(id)
);

DROP TABLE IF EXISTS post_images CASCADE;
CREATE TABLE post_images (
    post_id int,
    picture_id int,
    PRIMARY KEY (post_id, picture_id),
    FOREIGN Key (post_id) references post(id),
    FOREIGN Key (picture_id) references image(id)
);

DROP TABLE IF EXISTS comment_image CASCADE;
CREATE TABLE comment_image (
    comment_id int,
    picture_id int,
    PRIMARY KEY (comment_id, picture_id),
    FOREIGN Key (comment_id) references comment(id),
    FOREIGN Key (picture_id) references image(id)
);

DROP TABLE IF EXISTS reply_image CASCADE;
CREATE TABLE reply_image (
    reply_id int,
    picture_id int,
    PRIMARY KEY (reply_id, picture_id),
    FOREIGN Key (reply_id) references comment_replies(id),
    FOREIGN Key (picture_id) references image(id)
);
