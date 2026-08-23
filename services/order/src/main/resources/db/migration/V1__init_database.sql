CREATE SEQUENCE IF NOT EXISTS order_seq
    START WITH 1
    INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS order_line_seq
    START WITH 1
    INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS orders (
    id BIGINT PRIMARY KEY DEFAULT nextval('order_seq'),
    customer_id VARCHAR(50) NOT NULL,
    reference VARCHAR(100) NOT NULL UNIQUE,
    order_status VARCHAR(20) NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    create_at TIMESTAMP NOT NULL,
    update_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS order_line (
    id BIGINT PRIMARY KEY DEFAULT nextval('order_line_seq'),
    product_id BIGINT NOT NULL,
    quantity DOUBLE PRECISION NOT NULL,
    unit_price NUMERIC(12,2) NOT NULL,
    order_id BIGINT NOT NULL,

    CONSTRAINT fk_order_line_order
        FOREIGN KEY (order_id)
            REFERENCES orders(id)
);
