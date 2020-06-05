-- add new table user_devices
CREATE TABLE user_devices
(
    user_device_id   bigint NOT NULL,
    user_id          bigint NOT NULL,
    name             character varying(255),
    model            character varying(255),
    os               character varying(255),
    os_version       character varying(255),
    client_type      character varying(15),
    os_version       character varying(255),
    build_number     character varying(255),
    client_device_id character varying(255),
    token            text,
    last_login       timestamp with time zone
);

-- add new sequence user_device_sequence
CREATE SEQUENCE user_device_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

alter table users
    add column last_login timestamp with time zone;

---- rollback

-- add new table user_devices
-- DROP TABLE user_devices;

-- add new sequence user_device_sequence
--DROP SEQUENCE user_device_sequence;

--alter table users drop column last_login;
