-- Tabela de histórico de uso de cupons
USE studiomuda;

CREATE TABLE IF NOT EXISTS cupom_uso (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cupom_id INT NOT NULL,
    pedido_id INT,
    cliente_id INT NOT NULL,
    valor_desconto DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    data_uso TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cupom_id) REFERENCES cupom(id),
    FOREIGN KEY (pedido_id) REFERENCES pedido(id),
    FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);

CREATE INDEX idx_cupom_uso_cupom ON cupom_uso(cupom_id);
CREATE INDEX idx_cupom_uso_pedido ON cupom_uso(pedido_id);
CREATE INDEX idx_cupom_uso_cliente ON cupom_uso(cliente_id);