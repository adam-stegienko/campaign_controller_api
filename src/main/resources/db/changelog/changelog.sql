-- liquibase formatted sql

-- changeset liquibase:1
CREATE TABLE plannerbooks (
    id UUID PRIMARY KEY,
    campaign VARCHAR(255),
    action INT CHECK (action IN (0, 1)),
    execution_date TIMESTAMP
);
