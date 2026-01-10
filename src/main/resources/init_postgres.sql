-- Создание базы данных для правил рекомендаций
CREATE DATABASE bank_star_rules
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    CONNECTION LIMIT = -1;

-- Подключаемся к созданной базе данных
\c bank_star_rules;

-- Включаем расширение для JSONB
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Создание таблицы для динамических правил
CREATE TABLE IF NOT EXISTS dynamic_rules (
    id BIGSERIAL PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    product_id UUID NOT NULL UNIQUE,
    product_text TEXT NOT NULL,
    rule JSONB NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Создание индексов
CREATE INDEX idx_dynamic_rules_product_id ON dynamic_rules(product_id);
CREATE INDEX idx_dynamic_rules_created_at ON dynamic_rules(created_at);
CREATE INDEX idx_dynamic_rules_rule ON dynamic_rules USING gin(rule);

-- Добавление триггера для обновления updated_at
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_dynamic_rules_updated_at
    BEFORE UPDATE ON dynamic_rules
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Вставка тестовых правил
INSERT INTO dynamic_rules (product_name, product_id, product_text, rule) VALUES
('Премиум кредит',
 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8a',
 'Премиальный кредит с особыми условиями...',
 '[{"query": "USER_OF", "arguments": ["DEBIT"], "negate": false}, {"query": "TRANSACTION_SUM_COMPARE", "arguments": ["DEBIT", "DEPOSIT", ">", "50000"], "negate": false}]'::jsonb);