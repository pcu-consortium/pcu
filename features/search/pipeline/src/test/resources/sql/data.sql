CREATE SEQUENCE seq_person_pk;
CREATE TABLE person
(
    id BIGINT DEFAULT nextval('seq_person_pk') PRIMARY KEY,
    -- company_id BIGINT NOT NULL,
    -- log_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    username VARCHAR(250),
    password VARCHAR(250) --,
    -- enabled BIGINT NOT NULL
);

-- person
INSERT INTO "person"("id","username", "password") VALUES (1, 'david', 'david');
INSERT INTO "person"("id","username", "password") VALUES (2, 'mark', 'mark');
INSERT INTO "person"("id","username", "password") VALUES (3, 'batch2', 'batch2');
