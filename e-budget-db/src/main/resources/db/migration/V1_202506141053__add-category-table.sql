CREATE TABLE category (
    category_id UUID NOT NULL PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now()
);