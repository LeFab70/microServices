CREATE SEQUENCE IF NOT EXISTS payment_seq
    START WITH 1
    INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS payments (
                                        id BIGINT PRIMARY KEY DEFAULT nextval('payment_seq'),

                                        order_id BIGINT NOT NULL,

                                        order_reference VARCHAR(255) NOT NULL,

                                        amount NUMERIC(12,2) NOT NULL,

                                        payment_status VARCHAR(50) NOT NULL,

                                        payment_method VARCHAR(50) NOT NULL,

                                        create_at TIMESTAMP NOT NULL,

                                        update_at TIMESTAMP NOT NULL
);