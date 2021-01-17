CREATE TYPE EXPORT_ITEM_REASON AS ENUM ('REQUEST', 'LINK');

CREATE TABLE export_item (
    id                    BIGINT                                    NOT NULL,
    created_date          TIMESTAMP WITHOUT TIME ZONE DEFAULT now() NOT NULL,
    last_modified_date    TIMESTAMP WITHOUT TIME ZONE DEFAULT now() NOT NULL,
    version               BIGINT                      DEFAULT 0     NOT NULL,

    export_declaration_id BIGINT                                    NOT NULL,
    reason                EXPORT_ITEM_REASON                        NOT NULL,
    source_table_name     CHARACTER VARYING(255)                    NOT NULL,
    source_primary_key    JSONB                                     NOT NULL,
    override_values       JSONB                                     NOT NULL
);

ALTER TABLE export_item
    ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
        SEQUENCE NAME export_item_id_seq
        START WITH 1
        INCREMENT BY 1
        NO MINVALUE
        NO MAXVALUE
        CACHE 1
        );

ALTER TABLE export_item
    ADD CONSTRAINT export_item_pkey PRIMARY KEY (id);


ALTER TABLE export_item
    ADD CONSTRAINT fk_export_declaration_id FOREIGN KEY (export_declaration_id) REFERENCES export_declaration(id) NOT VALID;

ALTER TABLE export_item
    VALIDATE CONSTRAINT fk_export_declaration_id;