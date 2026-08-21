CREATE SEQUENCE IF NOT EXISTS category_seq
    START WITH 1
    INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS product_seq
    START WITH 1
    INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS category (
                                        id BIGINT PRIMARY KEY DEFAULT nextval('category_seq'),
                                        name VARCHAR(100) NOT NULL UNIQUE,
                                        description VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS product (
                                       id BIGINT PRIMARY KEY DEFAULT nextval('product_seq'),
                                       name VARCHAR(255) NOT NULL UNIQUE,
                                       description VARCHAR(500),
                                       price NUMERIC(12,2) NOT NULL,
                                       available_quantity DOUBLE PRECISION NOT NULL,
                                       category_id BIGINT NOT NULL,

                                       CONSTRAINT fk_product_category
                                           FOREIGN KEY (category_id)
                                               REFERENCES category(id)
);
