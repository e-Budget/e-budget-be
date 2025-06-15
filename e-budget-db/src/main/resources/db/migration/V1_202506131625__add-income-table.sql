CREATE TABLE income (
    income_id UUID NOT NULL PRIMARY KEY,
    income_description VARCHAR(100) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    account_id UUID NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now(),
    FOREIGN KEY(account_id) REFERENCES account(account_id)
);