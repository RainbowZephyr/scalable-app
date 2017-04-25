DROP TABLE IF EXISTS member CASCADE;
CREATE TABLE member (
  id            SERIAL PRIMARY KEY ,
  email         VARCHAR UNIQUE NOT NULL,
  password_hash VARCHAR        NOT NULL,
  first_name    VARCHAR        NOT NULL,
  last_name     VARCHAR        NOT NULL,
  date_of_birth DATE,
  created_at    TIMESTAMP
);

DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users (
  user_id INT PRIMARY KEY,
  FOREIGN KEY (user_id) REFERENCES member (id)
);

DROP TABLE IF EXISTS admin CASCADE;
CREATE TABLE admin (
  admin_id INT PRIMARY KEY,
  FOREIGN KEY (admin_id) REFERENCES member (id)
);

DROP TABLE IF EXISTS sessions CASCADE;
CREATE TABLE IF NOT EXISTS sessions (
  id            SERIAL PRIMARY KEY,
  user_id       INT       NOT NULL,
  session_start TIMESTAMP NOT NULL,
  last_access   TIMESTAMP NOT NULL,
  address       VARCHAR,
  FOREIGN KEY (user_id) REFERENCES member (ID)
);

DROP TABLE IF EXISTS logins CASCADE;
CREATE TABLE IF NOT EXISTS logins (
  id         SERIAL PRIMARY KEY,
  user_id    INT       NOT NULL,
  last_login TIMESTAMP NOT NULL,
  address    VARCHAR,
  logout     BOOLEAN,
  FOREIGN KEY (user_id) REFERENCES member (id)
);

DROP TABLE IF EXISTS login_failures CASCADE;
CREATE TABLE IF NOT EXISTS login_failures (
  id            SERIAL PRIMARY KEY,
  user_id       INT       NOT NULL,
  login_attempt TIMESTAMP NOT NULL,
  address       VARCHAR,
  FOREIGN KEY (userID) REFERENCES member (id)
);

DROP TABLE IF EXISTS friends CASCADE;
CREATE TABLE friends (
  user_1   INT,
  user_2   INT,
  accepted BOOLEAN,
  PRIMARY KEY (user_1, user_2),
  FOREIGN KEY (user_1) REFERENCES member (id),
  FOREIGN KEY (user_2) REFERENCES member (id)
);

DROP TABLE IF EXISTS blocks CASCADE;
CREATE TABLE blocks (
  user_1 INT,
  user_2 INT,
  PRIMARY KEY (user_1, user_2),
  FOREIGN KEY (user_1) REFERENCES member (id),
  FOREIGN KEY (user_2) REFERENCES member (id)
);

DROP TABLE IF EXISTS post CASCADE;
CREATE TABLE post (
  id          SERIAL PRIMARY KEY,
  user1_id    INT,
  user2_id    INT,
  post_text   VARCHAR NOT NULL,
  created_at  TIMESTAMP,
  update_time TIMESTAMP,
  FOREIGN KEY (user1_id) REFERENCES member (id),
  FOREIGN KEY (user2_id) REFERENCES member (id)

);

DROP TABLE IF EXISTS post_tags CASCADE;
CREATE TABLE post_tags (
  post_id INT,
  user_id INT,
  PRIMARY KEY (post_id, user_id),
  FOREIGN KEY (post_id) REFERENCES post (id),
  FOREIGN KEY (user_id) REFERENCES member (id)
);

DROP TABLE IF EXISTS post_hashtags CASCADE;
CREATE TABLE post_hashtags (
  post_id INT,
  user_id INT,
  hashtag VARCHAR,
  PRIMARY KEY (post_id, user_id, hashtag),
  FOREIGN KEY (post_id) REFERENCES post (id),
  FOREIGN KEY (user_id) REFERENCES member (id)
);

DROP TABLE IF EXISTS post_reactions CASCADE;
CREATE TABLE post_reactions (
  post_id       INT,
  user_id       INT,
  reaction_type INT,
  PRIMARY KEY (post_id, user_id),
  FOREIGN KEY (post_id) REFERENCES post (id),
  FOREIGN KEY (user_id) REFERENCES member (id)
);

DROP TABLE IF EXISTS comment CASCADE;
CREATE TABLE comment (
  id           INT PRIMARY KEY,
  post_id      INT,
  user_id      INT,
  comment_text VARCHAR,
  created_at   TIMESTAMP,
  update_time  TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES member (id),
  FOREIGN KEY (post_id) REFERENCES post (id)

);

DROP TABLE IF EXISTS comment_tags CASCADE;
CREATE TABLE comment_tags (
  comment_id INT,
  user1_id   INT,
  user2_id   INT,
  PRIMARY KEY (comment_id, user1_id, user2_id),
  FOREIGN KEY (comment_id) REFERENCES comment (id),
  FOREIGN KEY (user1_id) REFERENCES member (id),
  FOREIGN KEY (user2_id) REFERENCES member (id)
);

DROP TABLE IF EXISTS comment_likes CASCADE;
CREATE TABLE comment_likes (
  comment_id INT,
  user_id    INT,
  PRIMARY KEY (comment_id, user_id),
  FOREIGN KEY (comment_id) REFERENCES comment (id),
  FOREIGN KEY (user_id) REFERENCES member (id)
);

DROP TABLE IF EXISTS message_thread CASCADE;
CREATE TABLE message_thread (
  id         SERIAL PRIMARY KEY,
  created_at TIMESTAMP
);

DROP TABLE IF EXISTS message CASCADE;
CREATE TABLE message (
  id           SERIAL PRIMARY KEY,
  thread_id    INT,
  user_id      INT,
  message_text VARCHAR,
  created_at   TIMESTAMP,
  FOREIGN KEY (thread_id) REFERENCES message_thread (id),
  FOREIGN KEY (user_id) REFERENCES member (id)
);

DROP TABLE IF EXISTS message_thread_users CASCADE;
CREATE TABLE message_thread_users (
  thread_id INT,
  user_id   INT,
  PRIMARY KEY (thread_id, user_id),
  FOREIGN KEY (thread_id) REFERENCES message_thread (id),
  FOREIGN KEY (user_id) REFERENCES member (id)
);

DROP TABLE IF EXISTS event CASCADE;
CREATE TABLE event (
  id          INT PRIMARY KEY,
  title       VARCHAR,
  description VARCHAR,
  start_date  TIMESTAMP,
  end_date    TIMESTAMP,
  creator_id  INT,
  created_at  TIMESTAMP,
  FOREIGN KEY (creator_id) REFERENCES member (id)
);

DROP TABLE IF EXISTS event_subscribers CASCADE;
CREATE TABLE event_subscribers (
  event_id  INT,
  user_id   INT,
  attending BOOLEAN DEFAULT NULL,
  PRIMARY KEY (event_id, user_id),
  FOREIGN KEY (event_id) REFERENCES event (id),
  FOREIGN KEY (user_id) REFERENCES member (id)
);

DROP TABLE IF EXISTS image CASCADE;
CREATE TABLE image (
  id         SERIAL PRIMARY KEY,
  image_path VARCHAR
);

DROP TABLE IF EXISTS saved_posts CASCADE;
CREATE TABLE saved_posts (
  post_id INT,
  user_id INT,
  PRIMARY KEY (post_id, user_id),
  FOREIGN KEY (post_id) REFERENCES post (id),
  FOREIGN KEY (user_id) REFERENCES member (id)
);


DROP TABLE IF EXISTS follows CASCADE;
CREATE TABLE follows (
  user1_id INT,
  user2_id INT,
  PRIMARY KEY (user1_id, user2_id),
  FOREIGN KEY (user1_id) REFERENCES member (id),
  FOREIGN KEY (user2_id) REFERENCES member (id)
);

DROP TABLE IF EXISTS comment_replies CASCADE;
CREATE TABLE comment_replies (
  id         SERIAL PRIMARY KEY,
  comment_id INT,
  user_id    INT,
  reply_text VARCHAR,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  FOREIGN KEY (comment_id) REFERENCES comment (id),
  FOREIGN KEY (user_id) REFERENCES member (id)
);

DROP TABLE IF EXISTS report_user CASCADE;
CREATE TABLE report_user (
  user1_id INT,
  user2_id INT,
  PRIMARY KEY (user1_id, user2_id),
  FOREIGN KEY (user1_id) REFERENCES member (id),
  FOREIGN KEY (user2_id) REFERENCES member (id)
);

DROP TABLE IF EXISTS report_post CASCADE;
CREATE TABLE report_post (
  user_id INT,
  post_id INT,
  PRIMARY KEY (user_id, post_id),
  FOREIGN KEY (user_id) REFERENCES member (id),
  FOREIGN KEY (post_id) REFERENCES post (id)
);

DROP TABLE IF EXISTS report_comment CASCADE;
CREATE TABLE report_comment (
  user_id    INT,
  comment_id INT,
  PRIMARY KEY (user_id, comment_id),
  FOREIGN KEY (user_id) REFERENCES member (id),
  FOREIGN KEY (comment_id) REFERENCES comment (id)
);

DROP TABLE IF EXISTS user_album CASCADE;
CREATE TABLE user_album (
  user_id    INT,
  picture_id INT,
  PRIMARY KEY (user_id, picture_id),
  FOREIGN KEY (user_id) REFERENCES member (id),
  FOREIGN KEY (picture_id) REFERENCES image (id)
);

DROP TABLE IF EXISTS post_images CASCADE;
CREATE TABLE post_images (
  post_id    INT,
  picture_id INT,
  PRIMARY KEY (post_id, picture_id),
  FOREIGN KEY (post_id) REFERENCES post (id),
  FOREIGN KEY (picture_id) REFERENCES image (id)
);

DROP TABLE IF EXISTS comment_image CASCADE;
CREATE TABLE comment_image (
  comment_id INT,
  picture_id INT,
  PRIMARY KEY (comment_id, picture_id),
  FOREIGN KEY (comment_id) REFERENCES comment (id),
  FOREIGN KEY (picture_id) REFERENCES image (id)
);

DROP TABLE IF EXISTS reply_image CASCADE;
CREATE TABLE reply_image (
  reply_id   INT,
  picture_id INT,
  PRIMARY KEY (reply_id, picture_id),
  FOREIGN KEY (reply_id) REFERENCES comment_replies (id),
  FOREIGN KEY (picture_id) REFERENCES image (id)
);
