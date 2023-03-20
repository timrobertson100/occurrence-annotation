DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS rule;
DROP TABLE IF EXISTS project;

CREATE TABLE project (
    id SERIAL NOT NULL PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    creator TEXT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE TABLE rule (
    id SERIAL NOT NULL PRIMARY KEY,
    context_type TEXT ,
    context_key TEXT ,
    geometry TEXT ,
    error_type TEXT,
    enrichment_type TEXT,
    project_id INT REFERENCES project ON DELETE CASCADE DEFERRABLE,
    creator TEXT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE TABLE comment (
    id SERIAL NOT NULL PRIMARY KEY,
    rule_id INT REFERENCES rule ON DELETE CASCADE DEFERRABLE,
    comment TEXT,
    creator TEXT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL
);
