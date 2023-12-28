-- new table dish_items

CREATE TABLE public.dish_items
(
    dish_item_id bigint NOT NULL,
    dish_id      bigint NOT NULL,
    tag_id       bigint NOT NULL
);

CREATE SEQUENCE public.dish_item_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;


-- units
CREATE TABLE units
(
    unit_id      BIGINT NOT NULL,
    type         VARCHAR(255),
    subtype VARCHAR(255),
    name VARCHAR(255),
    is_liquid    BOOLEAN not null default false,
    is_list_unit BOOLEAN not null default false,
    is_dish_unit BOOLEAN not null default false,
    is_weight    BOOLEAN not null default false,
    is_volume    BOOLEAN not null default false,
    CONSTRAINT pk_units PRIMARY KEY (unit_id)
);

CREATE SEQUENCE public.unit_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;


CREATE TABLE factors
(
    factor_id BIGINT NOT NULL,
    factor    DOUBLE PRECISION,
    to_unit   BIGINT,
    from_unit BIGINT,
    CONSTRAINT pk_factors PRIMARY KEY (factor_id)
);

ALTER TABLE factors
    ADD CONSTRAINT FK_FACTORS_ON_FROM_UNIT FOREIGN KEY (from_unit) REFERENCES units (unit_id);

ALTER TABLE factors
    ADD CONSTRAINT FK_FACTORS_ON_TO_UNIT FOREIGN KEY (to_unit) REFERENCES units (unit_id);

CREATE SEQUENCE public.factor_sequence
    START WITH 1000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 1;