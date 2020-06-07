AP_assign2

/* Database Script
drop table user;
drop table job;
drop table sale;
drop table event;
drop table reply;
drop table post;

create table USER(
    USER_ID char(8) not null,
    USER_NAME varchar(20) not null,
    USER_PASSWORD varchar(20),
    primary key (USER_ID)
);

create table POST(
    POST_ID char(6) not null,
    TITLE varchar(80) not null,
    DESCRIPTION varchar(80) not null,
    STATUS varchar(6) DEFAULT 'OPEN' not null,
    USER_ID char(8) not null,
    foreign key (USER_ID) REFERENCES USER,
    DATE timestamp DEFAULT CURRENT_TIMESTAMP not null,
    IMAGE varchar(40),
    PRIMARY KEY (POST_ID)
);

create table REPLY(
    POST_ID char(6) not null,
    USER_ID char(8) not null,
    foreign key (POST_ID) REFERENCES POST,
    foreign key (USER_ID) REFERENCES USER,
    REPLY double not null ,
    PRIMARY KEY (POST_ID,USER_ID,REPLY)
);


create table EVENT(
    POST_ID char(6) not null,
    foreign key (POST_ID) REFERENCES POST,
    VENUE varchar(20) not null,
    DATE date not null,
    CAPACITY integer not null,
    --ATTENDEES_COUNT integer DEFAULT 0 not null,
    PRIMARY KEY (POST_ID)
);

create table SALE(
    POST_ID char(6) not null,
    foreign key (POST_ID) REFERENCES POST,
    ASKING_PRICE double not null,
    MINIMUM_RAISE double not null,
    --HIGHEST_OFFER double,
    PRIMARY KEY (POST_ID)
);

create table JOB(
    POST_ID char(6) not null,
    foreign key (POST_ID) REFERENCES POST,
    PROPOSED_PRICE double not null,
    --LOWEST_OFFER double,
    PRIMARY KEY (POST_ID)
);
*/
