CREATE TABLE IF NOT EXISTS alerta_reposicao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    produto_id INT NOT NULL,
    produto_nome VARCHAR(120) NOT NULL,
    fornecedor_nome VARCHAR(120) NOT NULL,
    estoque_atual INT NOT NULL,
    ponto_pedido INT NOT NULL,
    quantidade_sugerida INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO',
    observacao VARCHAR(300),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    resolvido_em TIMESTAMP NULL,
    CONSTRAINT fk_alerta_reposicao_produto
        FOREIGN KEY (produto_id) REFERENCES produto(id)
);

CREATE INDEX idx_alerta_reposicao_status ON alerta_reposicao(status, criado_em);
CREATE INDEX idx_alerta_reposicao_produto_status ON alerta_reposicao(produto_id, status);
