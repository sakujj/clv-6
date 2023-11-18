CREATE TABLE
    Client
(
    client_id UUID               NOT NULL,
    username  VARCHAR(50)        NOT NULL,
    email     VARCHAR(100)       NOT NULL,
    password  CHAR(60)           NOT NULL,
    age       SMALLINT           NOT NULL,

    CONSTRAINT client_pk
        PRIMARY KEY (client_id),
    CONSTRAINT client_email_uq
        UNIQUE (email),
    CONSTRAINT client_age_feasible_chk
        CHECK (age > 13 AND age < 121)
);
