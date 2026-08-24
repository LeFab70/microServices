ALTER TABLE payments ADD COLUMN customer_id VARCHAR(50) NOT NULL;
ALTER TABLE payments ADD COLUMN customer_first_name VARCHAR(100) NOT NULL;
ALTER TABLE payments ADD COLUMN customer_last_name VARCHAR(100) NOT NULL;

CREATE SEQUENCE IF NOT EXISTS payment_item_seq
    START WITH 1
    INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS payment_item (
    id BIGINT PRIMARY KEY DEFAULT nextval('payment_item_seq'),
    product_id BIGINT NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    quantity DOUBLE PRECISION NOT NULL,
    unit_price NUMERIC(12,2) NOT NULL,
    payment_id BIGINT NOT NULL,

    CONSTRAINT fk_payment_item_payment
        FOREIGN KEY (payment_id)
            REFERENCES payments(id)
);
