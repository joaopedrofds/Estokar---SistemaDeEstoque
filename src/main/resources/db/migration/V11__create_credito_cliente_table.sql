-- Tabela de crédito do cliente (gerado após devolução aprovada)
USE studiomuda;

CREATE TABLE IF NOT EXISTS credito_cliente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT NOT NULL,
    devolucao_id INT NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    saldo DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    validade DATE NOT NULL,
    data_geracao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    FOREIGN KEY (devolucao_id) REFERENCES devolucao(id)
);

CREATE INDEX idx_credito_cliente ON credito_cliente(cliente_id);
CREATE INDEX idx_credito_devolucao ON credito_cliente(devolucao_id);
CREATE INDEX idx_credito_status ON credito_cliente(status);