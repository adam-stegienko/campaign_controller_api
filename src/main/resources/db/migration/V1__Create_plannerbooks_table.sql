CREATE TABLE plannerbooks (
    uuid UUID PRIMARY KEY,
    campaign VARCHAR(255),
    action INT CHECK (action IN (0, 1)),
    executionDate TIMESTAMP
);