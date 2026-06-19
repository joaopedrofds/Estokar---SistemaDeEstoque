-- Módulo de KPIs Operacionais (Gestão de Indicadores Operacionais com Metas e Alertas Persistidos)
USE studiomuda;

CREATE TABLE IF NOT EXISTS indicador_operacional (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(40) NOT NULL UNIQUE,
    nome VARCHAR(120) NOT NULL,
    descricao VARCHAR(255),
    tipo_calculo VARCHAR(30) NOT NULL,   -- TICKET_MEDIO, ESTOQUE_CRITICO, TAXA_CANCELAMENTO, SEM_ESTOQUE
    periodo_padrao VARCHAR(10) NOT NULL DEFAULT 'MES',
    ativo BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS meta_indicador (
    id INT AUTO_INCREMENT PRIMARY KEY,
    indicador_id INT NOT NULL,
    valor_alvo DECIMAL(14,4) NOT NULL,
    limite_critico DECIMAL(14,4) NOT NULL,
    operador VARCHAR(20) NOT NULL DEFAULT 'MAIOR_IGUAL', -- MAIOR_IGUAL ou MENOR_IGUAL
    vigencia_inicio DATE NOT NULL,
    vigencia_fim DATE NULL,
    ativo BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (indicador_id) REFERENCES indicador_operacional(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS snapshot_indicador (
    id INT AUTO_INCREMENT PRIMARY KEY,
    indicador_id INT NOT NULL,
    valor_calculado DECIMAL(14,4) NOT NULL,
    periodo_inicio DATE NOT NULL,
    periodo_fim DATE NOT NULL,
    executado_por_id INT NULL,
    executado_por VARCHAR(60) NOT NULL,
    data_execucao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    detalhe_rastreio VARCHAR(255),
    FOREIGN KEY (indicador_id) REFERENCES indicador_operacional(id) ON DELETE CASCADE,
    FOREIGN KEY (executado_por_id) REFERENCES usuario_acesso(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS alerta_indicador (
    id INT AUTO_INCREMENT PRIMARY KEY,
    indicador_id INT NOT NULL,
    snapshot_id INT NOT NULL,
    tipo_violacao VARCHAR(20) NOT NULL,   -- ABAIXO_META, ACIMA_CRITICO
    valor_esperado DECIMAL(14,4) NOT NULL,
    valor_encontrado DECIMAL(14,4) NOT NULL,
    mensagem TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ATIVO', -- ATIVO, RESOLVIDO
    resolvido_por VARCHAR(60) NULL,
    observacao TEXT NULL,
    data_alerta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_resolucao TIMESTAMP NULL,
    FOREIGN KEY (indicador_id) REFERENCES indicador_operacional(id) ON DELETE CASCADE,
    FOREIGN KEY (snapshot_id) REFERENCES snapshot_indicador(id) ON DELETE CASCADE
);

-- Índices idempotentes (seguro reexecutar o script)
SET @sql = IF(
    (SELECT COUNT(*) FROM information_schema.statistics
     WHERE table_schema = DATABASE()
       AND table_name = 'snapshot_indicador'
       AND index_name = 'idx_snapshot_indicador_periodo') = 0,
    'CREATE INDEX idx_snapshot_indicador_periodo ON snapshot_indicador(indicador_id, periodo_inicio, periodo_fim)',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF(
    (SELECT COUNT(*) FROM information_schema.statistics
     WHERE table_schema = DATABASE()
       AND table_name = 'alerta_indicador'
       AND index_name = 'idx_alerta_indicador_status') = 0,
    'CREATE INDEX idx_alerta_indicador_status ON alerta_indicador(status, data_alerta)',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Inserção de dados iniciais para os indicadores operacionais
INSERT IGNORE INTO indicador_operacional (codigo, nome, descricao, tipo_calculo, periodo_padrao, ativo) VALUES
('TICKET_MEDIO', 'Ticket Médio de Vendas', 'Média de faturamento por pedido finalizado.', 'TICKET_MEDIO', 'MES', TRUE),
('PRODUTOS_CRITICOS', 'Produtos em Estoque Crítico', 'Quantidade de produtos com estoque igual ou abaixo do ponto de pedido.', 'ESTOQUE_CRITICO', 'MES', TRUE),
('TAXA_CANCELAMENTO', 'Taxa de Cancelamento', 'Percentual de pedidos cancelados em relação ao total de pedidos.', 'TAXA_CANCELAMENTO', 'MES', TRUE),
('PRODUTOS_SEM_ESTOQUE', 'Produtos Sem Estoque', 'Quantidade de produtos ativos com saldo zerado no estoque.', 'SEM_ESTOQUE', 'MES', TRUE);

-- Inserção de metas padrão para cada indicador
INSERT IGNORE INTO meta_indicador (indicador_id, valor_alvo, limite_critico, operador, vigencia_inicio, vigencia_fim, ativo)
VALUES 
((SELECT id FROM indicador_operacional WHERE codigo = 'TICKET_MEDIO'), 150.0000, 100.0000, 'MAIOR_IGUAL', '2026-01-01', NULL, TRUE),
((SELECT id FROM indicador_operacional WHERE codigo = 'PRODUTOS_CRITICOS'), 0.0000, 3.0000, 'MENOR_IGUAL', '2026-01-01', NULL, TRUE),
((SELECT id FROM indicador_operacional WHERE codigo = 'TAXA_CANCELAMENTO'), 5.0000, 10.0000, 'MENOR_IGUAL', '2026-01-01', NULL, TRUE),
((SELECT id FROM indicador_operacional WHERE codigo = 'PRODUTOS_SEM_ESTOQUE'), 0.0000, 2.0000, 'MENOR_IGUAL', '2026-01-01', NULL, TRUE);
