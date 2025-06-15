CREATE TABLE transfer (
    transfer_id UUID NOT NULL PRIMARY KEY,
    transfer_description VARCHAR(100) NULL,
    amount DECIMAL(10, 2) NOT NULL,
    from_account UUID NOT NULL,
    to_account UUID NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now(),
    FOREIGN KEY(from_account) REFERENCES account(account_id),
    FOREIGN KEY(to_account) REFERENCES account(account_id)
);