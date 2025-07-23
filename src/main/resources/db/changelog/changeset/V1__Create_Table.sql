CREATE TABLE wallet (
                        id UUID PRIMARY KEY NOT NULL,
                        balance NUMERIC(19,4) NOT NULL,
                        version BIGINT NOT NULL DEFAULT 0
);


