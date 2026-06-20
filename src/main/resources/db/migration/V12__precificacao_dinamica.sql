-- Migration opcional para adicionar a Funcionalidade 3 em um banco ja existente
USE studiomuda;

ALTER TABLE produto ADD COLUMN IF NOT EXISTS custo DECIMAL(10,2) NOT NULL DEFAULT 0.00;

CREATE TABLE IF NOT EXISTS precificacao_parametro (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    margem_minima_global DECIMAL(8,2) NOT NULL DEFAULT 30.00,
    desconto_maximo_global DECIMAL(8,2) NOT NULL DEFAULT 20.00,
    margem_padrao_lucro DECIMAL(8,2) NOT NULL DEFAULT 45.00,
    imposto_padrao_percentual DECIMAL(8,2) NOT NULL DEFAULT 8.50,
    despesa_operacional_padrao_percentual DECIMAL(8,2) NOT NULL DEFAULT 12.00,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS precificacao_politica (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    produto_id INT NOT NULL,
    margem_lucro_desejada DECIMAL(8,2) NOT NULL,
    aliquota_impostos DECIMAL(8,2) NOT NULL,
    percentual_despesas_operacionais DECIMAL(8,2) NOT NULL,
    desconto_maximo_permitido DECIMAL(8,2) NOT NULL,
    ativa BOOLEAN NOT NULL DEFAULT TRUE,
    observacao VARCHAR(300),
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (produto_id) REFERENCES produto(id)
);

CREATE TABLE IF NOT EXISTS precificacao_simulacao (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    produto_id INT NOT NULL,
    produto_nome VARCHAR(150) NOT NULL,
    preco_atual DECIMAL(12,2) NOT NULL,
    custo_compra DECIMAL(12,2) NOT NULL,
    valor_impostos DECIMAL(12,2) NOT NULL,
    valor_despesas_operacionais DECIMAL(12,2) NOT NULL,
    custo_total DECIMAL(12,2) NOT NULL,
    preco_sugerido DECIMAL(12,2) NOT NULL,
    preco_minimo_permitido DECIMAL(12,2) NOT NULL,
    margem_lucro_desejada DECIMAL(8,2) NOT NULL,
    margem_minima_global DECIMAL(8,2) NOT NULL,
    margem_real DECIMAL(8,2) NOT NULL,
    desconto_maximo_solicitado DECIMAL(8,2) NOT NULL,
    desconto_maximo_efetivo DECIMAL(8,2) NOT NULL,
    status VARCHAR(30) NOT NULL,
    justificativa TEXT,
    usuario_responsavel VARCHAR(80) NOT NULL,
    aplicado BOOLEAN NOT NULL DEFAULT FALSE,
    data_simulacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_aplicacao TIMESTAMP NULL,
    FOREIGN KEY (produto_id) REFERENCES produto(id)
);

CREATE TABLE IF NOT EXISTS precificacao_componente (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    simulacao_id BIGINT NOT NULL,
    nome VARCHAR(120) NOT NULL,
    tipo VARCHAR(40) NOT NULL,
    percentual DECIMAL(8,2) NOT NULL,
    valor DECIMAL(12,2) NOT NULL,
    base_calculo DECIMAL(12,2) NOT NULL,
    ordem INT NOT NULL,
    FOREIGN KEY (simulacao_id) REFERENCES precificacao_simulacao(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS historico_preco (
    id INT AUTO_INCREMENT PRIMARY KEY,
    produto_id INT NOT NULL,
    preco_anterior DECIMAL(10,2) NOT NULL,
    preco_novo DECIMAL(10,2) NOT NULL,
    percentual_variacao DECIMAL(8,2),
    usuario_responsavel VARCHAR(80),
    data_alteracao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (produto_id) REFERENCES produto(id)
);

CREATE INDEX idx_precificacao_politica_produto ON precificacao_politica(produto_id, ativa);
CREATE INDEX idx_precificacao_politica_atualizado ON precificacao_politica(atualizado_em);
CREATE INDEX idx_precificacao_simulacao_produto ON precificacao_simulacao(produto_id, data_simulacao);
CREATE INDEX idx_precificacao_simulacao_status ON precificacao_simulacao(status, data_simulacao);
CREATE INDEX idx_precificacao_componente_simulacao ON precificacao_componente(simulacao_id, ordem);
CREATE INDEX idx_historico_preco_produto_data ON historico_preco(produto_id, data_alteracao);
