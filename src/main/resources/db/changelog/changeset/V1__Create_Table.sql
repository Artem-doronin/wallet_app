CREATE TABLE wallet
(
    id      UUID PRIMARY KEY NOT NULL,
    balance NUMERIC(19, 4)   NOT NULL,
    version BIGINT           NOT NULL DEFAULT 0
);

TRUNCATE TABLE wallet;

INSERT INTO wallet (id, balance, version)
VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 10000.0000, 0),
       ('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 5000.5000, 0),
       ('c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a13', 750.2500, 0),
       ('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380a14', 0.0000, 0),
       ('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 150000.9900, 0);



