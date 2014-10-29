use locationData;

/* more information on phone?*/
create table if not exists app_users (
	user_id integer unsigned primary key,
	user_lat float not null,
	user_lng float not null
	) ENGINE=InnoDB;

create table if not exists markers (
    marker_id integer unsigned auto_increment primary key,
    lat float not null,
    lng float not null,
    user_id integer unsigned not null,
    finishHour integer unsigned not null,
    finishMinute integer unsigned not null,
    activePlayers integer unsigned not null,
    neededPlayers integer unsigned not null,
    customActivity varchar(55), /*only not null if activity_id ==0*/
    teamName varchar(55) not null default "team",
    activityNum integer not null default 0,/*0-8*/
    userName varchar(55)

    user_name varchar(55)
    ) ENGINE=InnoDB;

create table if not exists user_profile(
    user_id integer unsigned auto_increment primary key,
    user_name varchar(55),
    user_image blob
    ) engine=InnoDB;



CREATE TABLE if not exists SEQ (
    NAME VARCHAR(30) PRIMARY KEY,
    CURRENT_VALUE INTEGER NOT NULL 
)ENGINE=InnoDB;
-- The use of LAST_INSERT_ID is a MySQL-specific trick to
-- eliminate the need for an explicit transaction here.

-- From: Zaitsev, Peter. "Stored function to generate sequences". MySQL
--   Performance Blog. Pleasanton, Calif.: Percona LLC, 2008 Apr 2.
--   URL: http://www.mysqlperformanceblog.com/2008/04/02/stored-function-to-generate-sequences/

delimiter //
CREATE FUNCTION NEXT_SEQ_VALUE(SEQ_NAME VARCHAR(30))
    RETURNS INT
    MODIFIES SQL DATA
BEGIN
    UPDATE SEQ
        SET
            CURRENT_VALUE = LAST_INSERT_ID(CURRENT_VALUE+1)
        WHERE NAME = SEQ_NAME;
    RETURN LAST_INSERT_ID();
END
//
delimiter ;


INSERT INTO SEQ SELECT 'APP_USERS', MAX(USER_ID) FROM APP_USERS;
