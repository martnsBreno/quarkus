CREATE DATABASE quarkussocial;

CREATE TABLE USERS (
	id serial not null primary key,
    name varchar(100) not null,
    age integer not null
);

CREATE TABLE POSTS (
    id integer not null primary key auto_increment,
    post_text varchar(150) not null,
    dateTime timestamp,
    user_id integer not null references USERS(id)
);

CREATE TABLE FOLLOWERS (
id integer not null primary key auto_increment,
id_user integer not null references USERS(id),
follower_id integer not null references USERS(id)
);