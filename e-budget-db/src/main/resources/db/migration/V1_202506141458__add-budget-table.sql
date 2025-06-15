CREATE TABLE budget (
    budget_id UUID NOT NULL PRIMARY KEY,
    budget_month INT NOT NULL,
    budget_year INT NOT NULL,
    category_id UUID NOT NULL,
    monthly_budget DECIMAL(10, 2) NOT NULL,
    monthly_budget_used DECIMAL(10, 2) NOT NULL,
    monthly_budget_used_percentage DECIMAL(10, 2) NOT NULL,
    monthly_budget_balance DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT now(),
    updated_at TIMESTAMPTZ DEFAULT now(),
    UNIQUE(category_id, budget_month, budget_year),
    FOREIGN KEY(category_id) REFERENCES category(category_id)
);