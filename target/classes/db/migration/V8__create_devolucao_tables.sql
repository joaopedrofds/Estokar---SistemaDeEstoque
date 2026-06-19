-- Tabelas de devoluções (RMA)
USE studiomuda;

CREATE TABLE IF NOT EXISTS devolucao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id INT NOT NULL,
    cliente_id INT NOT NULL,
    motivo VARCHAR(255),
    tipo_restituicao VARCHAR(50),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    observacao_gestor TEXT,
    data_solicitacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_resolucao TIMESTAMP NULL,
    FOREIGN KEY (pedido_id) REFERENCES pedido(id),
    FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);

CREATE TABLE IF NOT EXISTS item_devolucao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    devolucao_id INT NOT NULL,
    produto_id INT NOT NULL,
    quantidade INT NOT NULL,
    valor_unitario DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    condicao VARCHAR(20) NOT NULL DEFAULT 'BOM',
    FOREIGN KEY (devolucao_id) REFERENCES devolucao(id),
    FOREIGN KEY (produto_id) REFERENCES produto(id)
);

CREATE INDEX idx_devolucao_pedido ON devolucao(pedido_id);
CREATE INDEX idx_devolucao_cliente ON devolucao(cliente_id);
CREATE INDEX idx_devolucao_status ON devolucao(status);
CREATE INDEX idx_item_devolucao_devolucao ON item_devolucao(devolucao_id);