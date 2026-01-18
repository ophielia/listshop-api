CREATE ROLE postgres WITH
    LOGIN encrypted password 'postgres'
    SUPERUSER
    INHERIT
    NOCREATEDB
    NOCREATEROLE
    NOREPLICATION;


CREATE ROLE listshopstarter WITH
    LOGIN encrypted password 'postgres'
    SUPERUSER
    INHERIT
    NOCREATEDB
    NOCREATEROLE
    NOREPLICATION;
