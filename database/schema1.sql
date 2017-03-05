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
    FOREIGN KEY(user_id) references member(id)
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
    PRIMARY KEY(user_1,user_2),
    FOREIGN KEY(user_1) references member(id),
    FOREIGN KEY(user_2) references member(id)
);

DROP TABLE IF EXISTS post CASCADE;
CREATE TABLE post (
    id int PRIMARY KEY,
    user_id int,
    post_text varchar NOT NULL,
    creation_time TIMESTAMP,
    update_time TIMESTAMP,
    FOREIGN KEY user_id references member(id)
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
    user_id int,
    comment_text varchar,
    creation_time TIMESTAMP,
    update_time TIMESTAMP,
    FOREIGN Key (user_id) references member(id)
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
    created_at TIMESTAMP,
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
    event_id int PRIMARY KEY,
    user_id int PRIMARY KEY,
    attending boolean DEFAULT NULL,
    PRIMARY KEY (event_id, user_id),
    FOREIGN Key (event_id) references event(id),
    FOREIGN Key (user_id) references member(id)
);

DROP TABLE IF EXISTS image CASCADE;
CREATE TABLE image (
    path varchar PRIMARY KEY,
    name varchar,
);

DROP TABLE IF EXISTS saved_posts CASCADE;
CREATE TABLE saved_posts (
    post_id int PRIMARY KEY,
    user_id int PRIMARY KEY,
    PRIMARY KEY (post_id, user_id),
    FOREIGN Key (post_id) references post(id),
    FOREIGN Key (user_id) references member(id)
);

DROP TABLE IF EXISTS group CASCADE;
CREATE TABLE group (
    id int PRIMARY KEY,
    name varchar,
    description varchar,
    creator_id int,
    created_at TIMESTAMP,
);

DROP TABLE IF EXISTS group_members CASCADE;
CREATE TABLE group_members (
    group_id int,
    user_id int
    admin boolean,
    PRIMARY KEY (group_id, user_id),
    FOREIGN Key (group_id) references group(id),
    FOREIGN Key (user_id) references member(id)
);
