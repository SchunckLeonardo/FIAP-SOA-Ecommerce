CREATE TABLE tb_product
(
    id               UUID NOT NULL,
    name             VARCHAR(255),
    description      VARCHAR(255),
    category         VARCHAR(255),
    price            DOUBLE PRECISION,
    amount_available INTEGER,
    amount_sold      INTEGER,
    dh_updated       TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_tb_product PRIMARY KEY (id)
);