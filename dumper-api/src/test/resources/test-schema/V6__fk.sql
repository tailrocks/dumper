ALTER TABLE ONLY category
    ADD CONSTRAINT fk_category_parent_id FOREIGN KEY (parent_id) REFERENCES category(id);


ALTER TABLE ONLY post
    ADD CONSTRAINT fk_post_author_id FOREIGN KEY (author_id) REFERENCES account(id);

ALTER TABLE ONLY post
    ADD CONSTRAINT fk_post_parent_id FOREIGN KEY (parent_id) REFERENCES post(id);

ALTER TABLE ONLY post
    ADD CONSTRAINT fk_post_topic_id FOREIGN KEY (topic_id) REFERENCES topic(id);


ALTER TABLE ONLY topic
    ADD CONSTRAINT fk_topic_author_id FOREIGN KEY (author_id) REFERENCES account(id);

ALTER TABLE ONLY topic
    ADD CONSTRAINT fk_topic_category_id FOREIGN KEY (category_id) REFERENCES category(id);

ALTER TABLE ONLY topic
    ADD CONSTRAINT fk_topic_last_post_id FOREIGN KEY (last_post_id) REFERENCES post(id);
