CREATE TABLE category (
    id                 bigint                                    NOT NULL,
    created_date       timestamp without time zone DEFAULT now() NOT NULL,
    last_modified_date timestamp without time zone DEFAULT now() NOT NULL,
    version            bigint                      DEFAULT 0     NOT NULL,

    path               text                                      NOT NULL,
    title              character varying(255)                    NOT NULL,
    description        text,
    parent_id          bigint,
    sort_order         integer                     DEFAULT 1     NOT NULL,
    visible            boolean                     DEFAULT true  NOT NULL
);


ALTER TABLE category
    ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
        SEQUENCE NAME category_id_seq
        START
            WITH 1
        INCREMENT BY 1
        NO MINVALUE
        NO MAXVALUE
        CACHE 1
        );


SELECT pg_catalog.setval('category_id_seq', 1, true);


ALTER TABLE ONLY category
    ADD CONSTRAINT category_pkey PRIMARY KEY (id);


CREATE TRIGGER increase_version
    BEFORE UPDATE
    ON category
    FOR EACH ROW
EXECUTE FUNCTION increase_version();

CREATE TRIGGER update_last_modified_date
    BEFORE UPDATE
    ON category
    FOR EACH ROW
EXECUTE FUNCTION update_last_modified_date();