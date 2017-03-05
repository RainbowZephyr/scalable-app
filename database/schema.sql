DROP TABLE IF EXISTS users CASCADE;


CREATE TABLE member (
    id serial PRIMARY KEY NOT NULL,
    username varchar(30) UNIQUE NOT NULL,
    email varchar(100) UNIQUE NOT NULL,
    password varchar(150) NOT NULL,
    first_name varchar(100) NOT NULL,
    last_name varchar(100) NOT NULL,
    date_of_birth datetime,
    creation_time datetime,
    last_login datetime
);

CREATE TABLE user (
    user_id int PRIMARY KEY,
    FOREIGN KEY(user_id) references member(id)
);

CREATE TABLE admin (
    admin_id int PRIMARY KEY,
    FOREIGN KEY(user_id) references member(id)
);

--False indicates request sent but not yet approved
CREATE TABLE friends (
    +*user_1 int,
    +*user_2 int,
    accepted boolean,
    PRIMARY KEY(user_1,user_2),
    FOREIGN KEY(user_1) references member(id),
    FOREIGN KEY(user_2) references member(id)
);

--user_1 blocks user_2
CREATE TABLE blocks (
    +*user_1 int,
    +*user_2 int,
    PRIMARY KEY(user_1,user_2),
    FOREIGN KEY(user_1) references member(id),
    FOREIGN KEY(user_2) references member(id)
);


CREATE TABLE post (
    id serial PRIMARY KEY NOT NULL,
    user_id int,
    post_text varchar(400) NOT NULL,
    hashtags
    creation_time datetime,
    update_time datetime,
    FOREIGN KEY user_id references member(id)
);

CREATE TABLE post_tags (
    post_id int,
    user_id int,
    PRIMARY KEY (post_id,user_id),
    FOREIGN Key (post_id) references post(id),
    FOREIGN Key (user_id) references member(id)
);

CReATE TABLE post_hashtags (
    post_id int,
    user_id int,
    hashtag varchar(50),
    PRIMARY KEY (post_id, user_id,hashtag),
    FOREIGN Key (post_id) references post(id),
    FOREIGN Key (user_id) references member(id)
);

CREATE TABLE comment (
    id serial PRIMARY KEY NOT NULL,
    user_id int,
    comment_text varchar(400) NOT NULL,
    creation_time datetime,
    update_time datetime,
    FOREIGN KEY user_id references member(id)
);

CREATE TABLE comment_tags (
    comment_id int,
    user_id int,
    PRIMARY KEY (comment_id,user_id),
    FOREIGN Key (comment_id) references comment(id),
    FOREIGN Key (user_id) references member(id)
);

CREATE TABLE Message (
id int PRIMARY KEY NOT NULL,
thread_id int NOT NULL,
message_text varchar(255) NOT NULL,
creation_time datetime,
FOREIGN KEY thread_id references thread(id)
);

CREATE TABLE Message_Thread (
id int PRIMARY KEY NOT NULL,
message_users int NOT NULL,
FOREIGN KEY message_user references member(id)
);

CREATE TABLE Message_Thread_Users (
thread_id int NOT NULL,
user_id int NOT NULL,
FOREIGN KEY thread_id references thread(id)
FOREIGN KEY user_id references member(id)
);
