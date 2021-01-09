CREATE TABLE database (
    id                 BIGINT                                    NOT NULL,
    created_date       TIMESTAMP WITHOUT TIME ZONE DEFAULT now() NOT NULL,
    last_modified_date TIMESTAMP WITHOUT TIME ZONE DEFAULT now() NOT NULL,
    version            BIGINT                      DEFAULT 0     NOT NULL,

    name               CHARACTER VARYING(255)                    NOT NULL,
    host               CHARACTER VARYING(255)                    NOT NULL,
    port               INTEGER                                   NOT NULL,
    username           CHARACTER VARYING(255)                    NOT NULL,
    password           CHARACTER VARYING(255)                    NOT NULL,
    dbname             CHARACTER VARYING(255)                    NOT NULL,
    environment        CHARACTER VARYING(255)                    NOT NULL,
    description        CHARACTER VARYING(255)                    NOT NULL,
    main_database_id   BIGINT
);

ALTER TABLE database
    ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
        SEQUENCE NAME database_id_seq
        START WITH 1
        INCREMENT BY 1
        NO MINVALUE
        NO MAXVALUE
        CACHE 1
        );

ALTER TABLE database
    ADD CONSTRAINT database_pkey PRIMARY KEY (id);

ALTER TABLE database
    ADD CONSTRAINT fk_main_database_id FOREIGN KEY (main_database_id) REFERENCES database(id) NOT VALID;

ALTER TABLE database
    VALIDATE CONSTRAINT fk_main_database_id;
