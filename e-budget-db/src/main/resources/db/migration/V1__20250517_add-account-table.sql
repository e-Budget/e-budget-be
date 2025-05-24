CREATE TABLE account (
    account_id UUID NOT NULL PRIMARY KEY,
    account_logo VARCHAR(100) NULL,
    account_name VARCHAR(100) NOT NULL,
    account_type VARCHAR(50) NOT NULL,
    initial_balance DECIMAL(10, 2) NOT NULL,
    balance DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now()
);