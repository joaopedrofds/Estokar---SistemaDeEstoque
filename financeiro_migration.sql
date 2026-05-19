-- Migração incremental: módulo Relatório Financeiro por Período
-- Execute apenas se o banco já existia antes desta funcionalidade:
-- mysql -u root studiomuda < financeiro_migration.sql

USE studiomuda;

CREATE TABLE IF NOT EXISTS categoria_financeira (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(120) NOT NULL UNIQUE,
    tipo VARCHAR(10) NOT NULL,
    origem_sistema VARCHAR(40),
    descricao VARCHAR(255),
    ativo BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS template_relatorio (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(120) NOT NULL UNIQUE,
    descricao VARCHAR(255),
    periodo_padrao VARCHAR(10) NOT NULL DEFAULT 'MES',
    agrupamento VARCHAR(10) NOT NULL DEFAULT 'MES',
    ativo BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS template_categoria (
    template_id INT NOT NULL,
    categoria_id INT NOT NULL,
    PRIMARY KEY (template_id, categoria_id),
    FOREIGN KEY (template_id) REFERENCES template_relatorio(id),
    FOREIGN KEY (categoria_id) REFERENCES categoria_financeira(id)
);

CREATE TABLE IF NOT EXISTS template_indicador (
    template_id INT NOT NULL,
    indicador VARCHAR(40) NOT NULL,
    PRIMARY KEY (template_id, indicador),
    FOREIGN KEY (template_id) REFERENCES template_relatorio(id)
);

CREATE TABLE IF NOT EXISTS relatorio_gerado (
    id INT AUTO_INCREMENT PRIMARY KEY,
    template_id INT NOT NULL,
    template_nome VARCHAR(120) NOT NULL,
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    data_inicio_anterior DATE NOT NULL,
    data_fim_anterior DATE NOT NULL,
    gerado_por_usuario_id INT NULL,
    gerado_por_username VARCHAR(60) NOT NULL,
    data_geracao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    receita_operacional DECIMAL(14,2) NOT NULL DEFAULT 0.00,
    custo_operacional DECIMAL(14,2) NOT NULL DEFAULT 0.00,
    resultado_operacional DECIMAL(14,2) NOT NULL DEFAULT 0.00,
    total_ajustes_receita DECIMAL(14,2) NOT NULL DEFAULT 0.00,
    total_ajustes_despesa DECIMAL(14,2) NOT NULL DEFAULT 0.00,
    resultado_consolidado DECIMAL(14,2) NOT NULL DEFAULT 0.00,
    quantidade_pedidos INT NOT NULL DEFAULT 0,
    FOREIGN KEY (template_id) REFERENCES template_relatorio(id),
    FOREIGN KEY (gerado_por_usuario_id) REFERENCES usuario_acesso(id)
);

CREATE TABLE IF NOT EXISTS relatorio_categoria_linha (
    id INT AUTO_INCREMENT PRIMARY KEY,
    relatorio_id INT NOT NULL,
    categoria_id INT NOT NULL,
    categoria_nome VARCHAR(120) NOT NULL,
    tipo_categoria VARCHAR(10) NOT NULL,
    valor_periodo DECIMAL(14,2) NOT NULL DEFAULT 0.00,
    valor_periodo_anterior DECIMAL(14,2) NOT NULL DEFAULT 0.00,
    variacao_percentual DECIMAL(8,2) NULL,
    origem_rastreio VARCHAR(255),
    ajuste_manual BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (relatorio_id) REFERENCES relatorio_gerado(id),
    FOREIGN KEY (categoria_id) REFERENCES categoria_financeira(id)
);

CREATE TABLE IF NOT EXISTS relatorio_indicador_linha (
    id INT AUTO_INCREMENT PRIMARY KEY,
    relatorio_id INT NOT NULL,
    indicador VARCHAR(40) NOT NULL,
    valor DECIMAL(14,4) NOT NULL DEFAULT 0.0000,
    valor_anterior DECIMAL(14,4) NULL,
    variacao_percentual DECIMAL(8,2) NULL,
    formula_descricao VARCHAR(255),
    FOREIGN KEY (relatorio_id) REFERENCES relatorio_gerado(id)
);

CREATE TABLE IF NOT EXISTS lancamento_ajuste (
    id INT AUTO_INCREMENT PRIMARY KEY,
    categoria_id INT NOT NULL,
    data_lancamento DATE NOT NULL,
    valor DECIMAL(14,2) NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    usuario_id INT NULL,
    username VARCHAR(60) NOT NULL,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (categoria_id) REFERENCES categoria_financeira(id),
    FOREIGN KEY (usuario_id) REFERENCES usuario_acesso(id)
);

INSERT IGNORE INTO categoria_financeira (nome, tipo, origem_sistema, descricao, ativo) VALUES
('Venda de Produto', 'RECEITA', 'PEDIDO_PAGO', 'Receita consolidada de pedidos pagos no período.', TRUE),
('Devolução', 'DESPESA', 'MOVIMENTACAO_ENTRADA_DEVOLUCAO', 'Estornos e devoluções registradas via movimentação de entrada.', TRUE),
('Custo de Reposição', 'DESPESA', 'MOVIMENTACAO_SAIDA', 'Custo estimado de saídas de estoque no período.', TRUE),
('Ajuste Manual', 'RECEITA', 'LANCAMENTO_AJUSTE', 'Lançamentos manuais de correção vinculados ao plano de contas.', TRUE);

INSERT IGNORE INTO template_relatorio (nome, descricao, periodo_padrao, agrupamento, ativo) VALUES
('Demonstrativo Mensal', 'Consolidação padrão mensal com margem e ticket médio.', 'MES', 'MES', TRUE),
('Fechamento Semanal', 'Visão semanal para acompanhamento operacional.', 'SEMANA', 'SEMANA', TRUE);

INSERT IGNORE INTO template_categoria (template_id, categoria_id)
SELECT t.id, c.id FROM template_relatorio t CROSS JOIN categoria_financeira c WHERE t.nome = 'Demonstrativo Mensal';

INSERT IGNORE INTO template_categoria (template_id, categoria_id)
SELECT t.id, c.id FROM template_relatorio t
JOIN categoria_financeira c ON c.nome IN ('Venda de Produto', 'Custo de Reposição', 'Ajuste Manual')
WHERE t.nome = 'Fechamento Semanal';

INSERT IGNORE INTO template_indicador (template_id, indicador)
SELECT id, 'MARGEM_BRUTA' FROM template_relatorio WHERE nome = 'Demonstrativo Mensal'
UNION ALL SELECT id, 'TICKET_MEDIO' FROM template_relatorio WHERE nome = 'Demonstrativo Mensal'
UNION ALL SELECT id, 'RESULTADO_LIQUIDO' FROM template_relatorio WHERE nome = 'Demonstrativo Mensal'
UNION ALL SELECT id, 'MARGEM_BRUTA' FROM template_relatorio WHERE nome = 'Fechamento Semanal'
UNION ALL SELECT id, 'TICKET_MEDIO' FROM template_relatorio WHERE nome = 'Fechamento Semanal';

INSERT IGNORE INTO permissao_perfil (perfil_id, recurso, operacao, permitido) VALUES
(1, 'FINANCEIRO', 'LEITURA', TRUE),
(1, 'FINANCEIRO', 'ESCRITA', TRUE),
(1, 'FINANCEIRO', 'APROVACAO', TRUE),
(2, 'FINANCEIRO', 'LEITURA', TRUE),
(2, 'FINANCEIRO', 'ESCRITA', TRUE);
