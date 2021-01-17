CREATE TABLE account (
    id                 BIGINT                                    NOT NULL,
    created_date       TIMESTAMP WITHOUT TIME ZONE DEFAULT now() NOT NULL,
    last_modified_date TIMESTAMP WITHOUT TIME ZONE DEFAULT now() NOT NULL,
    version            BIGINT                      DEFAULT 0     NOT NULL,

    username           CHARACTER VARYING(255)                    NOT NULL,
    password           CHARACTER VARYING(255)                    NOT NULL,
    email              CHARACTER VARYING(255)                    NOT NULL,
    first_name         CHARACTER VARYING(255)                    NOT NULL,
    last_name          CHARACTER VARYING(255)                    NOT NULL
);

ALTER TABLE account
    ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
        SEQUENCE NAME account_id_seq
        START WITH 1
        INCREMENT BY 1
        NO MINVALUE
        NO MAXVALUE
        CACHE 1
        );

ALTER TABLE account
    ADD CONSTRAINT account_pkey PRIMARY KEY (id);

ALTER TABLE account
    ADD CONSTRAINT uk_account_username UNIQUE (username);

ALTER TABLE account
    ADD CONSTRAINT uk_account_email UNIQUE (email);