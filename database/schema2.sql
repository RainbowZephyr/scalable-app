title 20,
);

DROP TABLE IF EXISTS member CASCADE;
CREATE TABLE member (
    id int PRIMARY KEY,
    email varchar,
    password_hash varchar,
    first_name varchar,
    last_name varchar,
    date_of_birth date,
    profile_picture varchar,
    cover_picture varchar,
    creation_time TIMESTAMP,
    last_login TIMESTAMP,
);

DROP TABLE IF EXISTS user CASCADE;
CREATE TABLE user (
    user_id int PRIMARY KEY,
);

DROP TABLE IF EXISTS admin CASCADE;
CREATE TABLE admin (
    admin_id int PRIMARY KEY,
);

DROP TABLE IF EXISTS friends CASCADE;
CREATE TABLE friends (
    user_1 int PRIMARY KEY,
    user_2 int PRIMARY KEY,
    accepted boolean,
);

DROP TABLE IF EXISTS blocks CASCADE;
CREATE TABLE blocks (
    user_1 int PRIMARY KEY,
    user_2 int PRIMARY KEY,
);

DROP TABLE IF EXISTS post CASCADE;
CREATE TABLE post (
    id int PRIMARY KEY,
    user1_id int,
    user2_id int,
    post_text varchar,
    creation_time TIMESTAMP,
    update_time TIMESTAMP,
);

DROP TABLE IF EXISTS post_tags CASCADE;
CREATE TABLE post_tags (
    post_id int PRIMARY KEY,
    user1_id int PRIMARY KEY,
    user2_id int PRIMARY KEY,
);

DROP TABLE IF EXISTS post_hashtags CASCADE;
CREATE TABLE post_hashtags (
    post_id int PRIMARY KEY,
    user_id int PRIMARY KEY,
    hashtag varchar PRIMARY KEY,
);

DROP TABLE IF EXISTS post_reactions CASCADE;
CREATE TABLE post_reactions (
    post_id int PRIMARY KEY,
    user_id int PRIMARY KEY,
    reaction_type int,
);

DROP TABLE IF EXISTS comment CASCADE;
CREATE TABLE comment (
    id int PRIMARY KEY,
    user_id int,
    comment_text varchar,
    creation_time TIMESTAMP,
    update_time TIMESTAMP,
);

DROP TABLE IF EXISTS comment_tags CASCADE;
CREATE TABLE comment_tags (
    comment_id int PRIMARY KEY,
    user1_id int PRIMARY KEY,
    user2_id int PRIMARY KEY,
);

DROP TABLE IF EXISTS comment_likes CASCADE;
CREATE TABLE comment_likes (
    comment_id int PRIMARY KEY,
    user_id int PRIMARY KEY,
);

DROP TABLE IF EXISTS message_thread CASCADE;
CREATE TABLE message_thread (
    id int PRIMARY KEY,
    created_at TIMESTAMP,
);

DROP TABLE IF EXISTS message CASCADE;
CREATE TABLE message (
    id int PRIMARY KEY,
    thread_id int,
    message_text varchar,
    creation_time TIMESTAMP,
);

DROP TABLE IF EXISTS message_thread_users CASCADE;
CREATE TABLE message_thread_users (
    thread_id int PRIMARY KEY,
    user_id int PRIMARY KEY,
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
);

DROP TABLE IF EXISTS event_subscribers CASCADE;
CREATE TABLE event_subscribers (
    event_id int PRIMARY KEY,
    user_id int PRIMARY KEY,
    attending boolean,
);

DROP TABLE IF EXISTS image CASCADE;
CREATE TABLE image (
    post_id int PRIMARY KEY,
    image_path varchar,
);

DROP TABLE IF EXISTS saved_posts CASCADE;
CREATE TABLE saved_posts (
    post_id int PRIMARY KEY,
    user_id int PRIMARY KEY,
);

DROP TABLE IF EXISTS facebook_group CASCADE;
CREATE TABLE facebook_group (
    id int PRIMARY KEY,
    name varchar,
    description varchar,
    creator_id int,
    created_at TIMESTAMP,
);

DROP TABLE IF EXISTS group_members CASCADE;
CREATE TABLE group_members (
    group_id int PRIMARY KEY,
    user_id int PRIMARY KEY,
    admin boolean,
);

DROP TABLE IF EXISTS group_post CASCADE;
CREATE TABLE group_post (
    group_id int PRIMARY KEY,
    user_id int PRIMARY KEY,
    text varchar,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
);

DROP TABLE IF EXISTS group_post_comments CASCADE;
CREATE TABLE group_post_comments (
    group_post_id int PRIMARY KEY,
    user_id int PRIMARY KEY,
    text varchar,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
);

DROP TABLE IF EXISTS follows CASCADE;
CREATE TABLE follows (
    user1_id int PRIMARY KEY,
    user2_id int PRIMARY KEY,
);

DROP TABLE IF EXISTS comment_replies CASCADE;
CREATE TABLE comment_replies (
    comment_id int PRIMARY KEY,
    user_id int PRIMARY KEY,
    text varchar,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
);

DROP TABLE IF EXISTS report_user CASCADE;
CREATE TABLE report_user (
    user1_id int PRIMARY KEY,
    user2_id int PRIMARY KEY,
);

DROP TABLE IF EXISTS report_post CASCADE;
CREATE TABLE report_post (
    user_id int PRIMARY KEY,
    post_id int PRIMARY KEY,
);

DROP TABLE IF EXISTS report_comment CASCADE;
CREATE TABLE report_comment (
    user_id int PRIMARY KEY,
    comment_id int PRIMARY KEY,
);

DROP TABLE IF EXISTS report_group CASCADE;
CREATE TABLE report_group (
    user_id int PRIMARY KEY,
    group_id int PRIMARY KEY,
);

DROP TABLE IF EXISTS user_album CASCADE;
CREATE TABLE user_album (
    user_id int PRIMARY KEY,
    picture varchar PRIMARY KEY,
);

DROP TABLE IF EXISTS post_images CASCADE;
CREATE TABLE post_images (
    post_id int PRIMARY KEY,
    picture_id int PRIMARY KEY,
);

DROP TABLE IF EXISTS comment_image CASCADE;
CREATE TABLE comment_image (
    comment_id int PRIMARY KEY,
    picture_id int PRIMARY KEY,
);

DROP TABLE IF EXISTS reply_image CASCADE;
CREATE TABLE reply_image (
    reply_id int PRIMARY KEY,
    picture_id int PRIMARY KEY,
