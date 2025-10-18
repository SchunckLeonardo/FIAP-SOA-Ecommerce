CREATE TABLE tb_customer
(
    id               UUID NOT NULL,
    name             VARCHAR(255),
    email            VARCHAR(255),
    password_encoded VARCHAR(255),
    dh_created       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_tb_customer PRIMARY KEY (id)
);