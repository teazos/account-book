CREATE TABLE IF NOT EXISTS account_user (
    id BIGINT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(64) NOT NULL,
    password_salt VARCHAR(32) NOT NULL,
    nickname VARCHAR(100),
    token VARCHAR(64),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    deleted BOOLEAN DEFAULT FALSE
);
CREATE UNIQUE INDEX IF NOT EXISTS idx_user_email ON account_user(email);

CREATE TABLE IF NOT EXISTS account_book (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    currency VARCHAR(20) DEFAULT 'CNY',
    cover VARCHAR(255),
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    deleted BOOLEAN DEFAULT FALSE
);
ALTER TABLE account_book ADD COLUMN IF NOT EXISTS user_id BIGINT;
ALTER TABLE account_book ADD COLUMN IF NOT EXISTS cover VARCHAR(255);
CREATE INDEX IF NOT EXISTS idx_book_user ON account_book(user_id, deleted);
ALTER TABLE account_book ADD COLUMN IF NOT EXISTS user_id BIGINT;

CREATE TABLE IF NOT EXISTS account_category (
    id BIGINT PRIMARY KEY,
    book_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    icon VARCHAR(100),
    color VARCHAR(50),
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    deleted BOOLEAN DEFAULT FALSE
);
CREATE INDEX IF NOT EXISTS idx_category_book ON account_category(book_id, type, deleted);

CREATE TABLE IF NOT EXISTS account_transaction (
    id BIGINT PRIMARY KEY,
    book_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    transaction_date DATE NOT NULL,
    note VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    deleted BOOLEAN DEFAULT FALSE
);
CREATE INDEX IF NOT EXISTS idx_transaction_book_date ON account_transaction(book_id, transaction_date, deleted);
CREATE INDEX IF NOT EXISTS idx_transaction_category ON account_transaction(category_id, deleted);
