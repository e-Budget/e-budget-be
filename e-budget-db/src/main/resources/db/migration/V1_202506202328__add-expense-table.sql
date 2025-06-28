CREATE TABLE expense (
    expense_id UUID NOT NULL PRIMARY KEY,
    expense_description VARCHAR(100) NULL,
    expense_month INT NOT NULL,
    expense_year INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    category_id UUID NULL,
    account_id UUID NOT NULL,
    date DATE NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now(),
    FOREIGN KEY(category_id) REFERENCES category(category_id),
    FOREIGN KEY(account_id) REFERENCES account(account_id)
);