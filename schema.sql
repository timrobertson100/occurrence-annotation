DROP TABLE IF EXISTS comment;
DROP TABLE IF EXISTS rule;
DROP TABLE IF EXISTS project;

CREATE TABLE project (
    id SERIAL NOT NULL PRIMARY KEY,
    name TEXT NOT NULL,
    description TEXT NOT NULL,
    members TEXT[] NOT NULL DEFAULT '{}',
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by TEXT NOT NULL,
    modified TIMESTAMP WITHOUT TIME ZONE,
    modified_by TEXT,
    deleted TIMESTAMP WITHOUT TIME ZONE,
    deleted_by TEXT
);

CREATE TABLE rule (
    id SERIAL NOT NULL PRIMARY KEY,
    context_type TEXT NOT NULL,
    context_key TEXT NULL,
    geometry TEXT NULL,
    error_type TEXT,
    enrichment_type TEXT,
    project_id INT REFERENCES project ON DELETE CASCADE DEFERRABLE,
    supported_by TEXT[] NOT NULL DEFAULT '{}',
    contested_by TEXT[] NOT NULL DEFAULT '{}',
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by TEXT NOT NULL,
    deleted TIMESTAMP WITHOUT TIME ZONE,
    deleted_by TEXT
);

CREATE TABLE comment (
    id SERIAL NOT NULL PRIMARY KEY,
    rule_id INT REFERENCES rule ON DELETE CASCADE DEFERRABLE,
    comment TEXT,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_by TEXT NOT NULL,
    deleted TIMESTAMP WITHOUT TIME ZONE,
    deleted_by TEXT
);
