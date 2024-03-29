CREATE TABLE post (
    id                 bigint                                    NOT NULL,
    created_date       timestamp without time zone DEFAULT now() NOT NULL,
    last_modified_date timestamp without time zone DEFAULT now() NOT NULL,
    version            bigint                      DEFAULT 0     NOT NULL,

    title              text,
    content            text                                      NOT NULL,
    author_id          bigint                                    NOT NULL,
    topic_id           bigint                                    NOT NULL,
    parent_id          bigint,
    visible            boolean                     DEFAULT true  NOT NULL
);


ALTER TABLE post
    ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
        SEQUENCE NAME post_id_seq
        START
            WITH 1
        INCREMENT BY 1
        NO MINVALUE
        NO MAXVALUE
        CACHE 1
        );


SELECT pg_catalog.setval('post_id_seq', 1, false);


ALTER TABLE ONLY post
    ADD CONSTRAINT post_pkey PRIMARY KEY (id);


CREATE INDEX ix_post_topic_id ON post USING btree(topic_id);


CREATE TRIGGER increase_version
    BEFORE UPDATE
    ON post
    FOR EACH ROW
EXECUTE FUNCTION increase_version();

CREATE TRIGGER update_last_modified_date
    BEFORE UPDATE
    ON post
    FOR EACH ROW
EXECUTE FUNCTION update_last_modified_date();
