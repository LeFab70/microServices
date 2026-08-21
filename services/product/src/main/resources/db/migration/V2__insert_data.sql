INSERT INTO category (id, name, description) VALUES
                                                 (1, 'Electronics', 'Electronic devices and accessories'),
                                                 (51, 'Books', 'Books and educational resources'),
                                                 (101, 'Computers', 'Desktop and laptop computers'),
                                                 (151, 'Smartphones', 'Mobile phones and accessories'),
                                                 (201, 'Gaming', 'Gaming consoles and gaming accessories'),
                                                 (251, 'Home Appliances', 'Appliances for home and kitchen'),
                                                 (301, 'Furniture', 'Home and office furniture'),
                                                 (351, 'Sports', 'Sports equipment and accessories'),
                                                 (401, 'Fashion', 'Clothing and fashion accessories'),
                                                 (451, 'Beauty', 'Beauty and personal care products'),
                                                 (501, 'Toys', 'Toys and games for all ages'),
                                                 (551, 'Automotive', 'Car parts and accessories'),
                                                 (601, 'Health', 'Health and wellness products'),
                                                 (651, 'Music', 'Musical instruments and accessories'),
                                                 (701, 'Movies', 'Movies and entertainment products'),
                                                 (751, 'Garden', 'Garden tools and outdoor equipment'),
                                                 (801, 'Pet Supplies', 'Products for pets and animals'),
                                                 (851, 'Office', 'Office supplies and equipment'),
                                                 (901, 'Jewelry', 'Jewelry and luxury accessories'),
                                                 (951, 'Food', 'Food and beverage products');

INSERT INTO product (
    name,
    description,
    price,
    available_quantity,
    category_id
)
SELECT
    'Product ' || gs,
    'Description for product ' || gs,
    ROUND((10 + random() * 990)::numeric, 2),
    FLOOR(10 + random() * 90),
    (
        ARRAY[
            1,51,101,151,201,
            251,301,351,401,451,
            501,551,601,651,701,
            751,801,851,901,951
            ]
        )[1 + FLOOR(random() * 20)::int]
FROM generate_series(1, 100) gs;