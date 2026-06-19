-- Recriação completa do banco de dados
DROP DATABASE IF EXISTS studiomuda;
CREATE DATABASE studiomuda;
USE studiomuda;

-- Tabela de produtos
CREATE TABLE produto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,                        -- Nome do produto
    descricao TEXT,                                    -- Descrição opcional
    tipo VARCHAR(50),                                  -- Tipo/categoria
    quantidade INT DEFAULT 0,                          -- Quantidade em estoque
    valor DECIMAL(10,2) NOT NULL DEFAULT 0.00          -- Valor unitário
);

-- Tabelas de suprimentos e reposicao inteligente
CREATE TABLE fornecedor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    lead_time_dias INT NOT NULL DEFAULT 1,
    ativo BOOLEAN DEFAULT TRUE
);

CREATE TABLE parametro_estoque (
    id INT AUTO_INCREMENT PRIMARY KEY,
    produto_id INT NOT NULL UNIQUE,
    fornecedor_id INT NOT NULL,
    margem_seguranca INT NOT NULL DEFAULT 0,
    FOREIGN KEY (produto_id) REFERENCES produto(id),
    FOREIGN KEY (fornecedor_id) REFERENCES fornecedor(id)
);

CREATE TABLE ordem_compra (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fornecedor_id INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'RASCUNHO',
    valor_total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    data_criacao DATE NOT NULL,
    data_aprovacao DATE NULL,
    FOREIGN KEY (fornecedor_id) REFERENCES fornecedor(id)
);

CREATE TABLE item_ordem_compra (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ordem_compra_id INT NOT NULL,
    produto_id INT NOT NULL,
    quantidade INT NOT NULL,
    valor_unitario DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    FOREIGN KEY (ordem_compra_id) REFERENCES ordem_compra(id),
    FOREIGN KEY (produto_id) REFERENCES produto(id)
);

-- Tabelas de roteirizacao de remessas
CREATE TABLE doca (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    capacidade_paletes_diaria INT NOT NULL,
    ativa BOOLEAN DEFAULT TRUE
);

CREATE TABLE distribuidora (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    nivel_prioridade VARCHAR(10) NOT NULL DEFAULT 'MEDIA',
    ativa BOOLEAN DEFAULT TRUE
);

CREATE TABLE calendario_excecao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    data DATE NOT NULL,
    motivo VARCHAR(180) NOT NULL,
    ativa BOOLEAN DEFAULT TRUE
);

CREATE TABLE agendamento_remessa (
    id INT AUTO_INCREMENT PRIMARY KEY,
    doca_id INT NOT NULL,
    distribuidora_id INT NOT NULL,
    data DATE NOT NULL,
    horario VARCHAR(5) NOT NULL,
    volume_paletes INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMADO',
    FOREIGN KEY (doca_id) REFERENCES doca(id),
    FOREIGN KEY (distribuidora_id) REFERENCES distribuidora(id)
);

CREATE INDEX idx_agendamento_remessa_doca_data ON agendamento_remessa(doca_id, data, horario, status);
CREATE INDEX idx_calendario_excecao_data ON calendario_excecao(data, ativa);

-- Tabela de funcionários
CREATE TABLE funcionario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,                        -- Nome do funcionário
    cpf VARCHAR(11) NOT NULL UNIQUE,                   -- CPF (único)
    cargo VARCHAR(50),                                 -- Cargo no sistema
    data_nasc DATE,                                    -- Data de nascimento
    telefone VARCHAR(20),                              -- Telefone de contato
    ativo BOOLEAN DEFAULT TRUE,                        -- Ativo/inativo
    cep VARCHAR(10),
    rua VARCHAR(100),
    numero VARCHAR(10),
    bairro VARCHAR(50),
    cidade VARCHAR(50),
    estado VARCHAR(2)
);

-- Tabela de clientes
CREATE TABLE cliente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,                         -- Nome do cliente
    cpf_cnpj VARCHAR(20) NOT NULL UNIQUE,               -- CPF ou CNPJ (único)
    telefone VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    tipo VARCHAR(2) NOT NULL,                           -- PF ou PJ
    ativo BOOLEAN DEFAULT TRUE,                         -- Exclusão lógica
    cep VARCHAR(10),
    rua VARCHAR(100),
    numero VARCHAR(10),
    bairro VARCHAR(50),
    cidade VARCHAR(50),
    estado VARCHAR(2),
    dataNascimento DATE
);

-- Tabela de cupons
CREATE TABLE cupom (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,                -- Código único
    descricao TEXT,
    valor DECIMAL(10,2) NOT NULL,                      -- Valor do desconto
    data_inicio DATE,
    validade DATE,
    condicoes_uso TEXT
);

-- Tabela de pedidos
CREATE TABLE pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    data_requisicao DATE,                              -- Data do pedido
    data_entrega DATE,                                 -- Data de entrega
    itens TEXT,                                        -- Itens (legado)
    cliente_id INT,                                    -- Cliente associado
    funcionario_id INT,                                -- Funcionário associado à venda
    cupom_id INT,                                      -- Cupom de desconto aplicado
    valor_desconto DECIMAL(10,2) DEFAULT 0.00,         -- Valor do desconto aplicado
    status VARCHAR(40) NOT NULL DEFAULT 'PENDENTE',    -- PENDENTE, CONCLUIDO, CANCELADO, CANCELAMENTO_PENDENTE_APROVACAO
    status_pagamento VARCHAR(20) NOT NULL DEFAULT 'PENDENTE', -- PENDENTE ou PAGO
    data_pagamento DATE,                               -- Data de quitação
    cancelamento_solicitante_id INT NULL,
    cancelamento_solicitante_nome VARCHAR(120) NULL,
    justificativa_cancelamento VARCHAR(300) NULL,
    data_cancelamento TIMESTAMP NULL,
    cancelamento_aprovador_id INT NULL,
    cancelamento_aprovador_nome VARCHAR(120) NULL,
    data_aprovacao_cancelamento TIMESTAMP NULL,
    FOREIGN KEY (cupom_id) REFERENCES cupom(id),
    FOREIGN KEY (funcionario_id) REFERENCES funcionario(id)
);

-- Criar índices para melhorar a performance das consultas em pedidos
CREATE INDEX idx_pedido_funcionario ON pedido(funcionario_id);
CREATE INDEX idx_pedido_cupom ON pedido(cupom_id);
CREATE INDEX idx_pedido_cliente_pagamento ON pedido(cliente_id, status_pagamento, data_requisicao);
CREATE INDEX idx_pedido_status ON pedido(status);

CREATE TABLE parametro_cancelamento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    limite_quantidade_sem_aprovacao INT NOT NULL DEFAULT 10
);

INSERT INTO parametro_cancelamento (limite_quantidade_sem_aprovacao) VALUES (10);

CREATE TABLE parametro_inventario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tolerancia_quantidade INT NOT NULL DEFAULT 3
);

INSERT INTO parametro_inventario (tolerancia_quantidade) VALUES (3);

CREATE TABLE parametro_ajuste_estoque (
    id INT AUTO_INCREMENT PRIMARY KEY,
    limite_quantidade_sem_aprovacao INT NOT NULL DEFAULT 5,
    percentual_risco_alto INT NOT NULL DEFAULT 30
);

INSERT INTO parametro_ajuste_estoque (limite_quantidade_sem_aprovacao, percentual_risco_alto) VALUES (5, 30);

CREATE TABLE solicitacao_ajuste_estoque (
    id INT AUTO_INCREMENT PRIMARY KEY,
    produto_id INT NOT NULL,
    tipo VARCHAR(40) NOT NULL,
    quantidade INT NOT NULL,
    justificativa VARCHAR(300) NOT NULL,
    status VARCHAR(40) NOT NULL,
    risco VARCHAR(20) NOT NULL,
    saldo_antes INT NOT NULL,
    saldo_depois INT NOT NULL,
    exige_aprovacao BOOLEAN NOT NULL DEFAULT FALSE,
    solicitante_id INT NOT NULL,
    solicitante_nome VARCHAR(120) NOT NULL,
    aprovador_id INT NULL,
    aprovador_nome VARCHAR(120) NULL,
    motivo_decisao VARCHAR(300) NULL,
    data_solicitacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_decisao TIMESTAMP NULL,
    FOREIGN KEY (produto_id) REFERENCES produto(id)
);

CREATE TABLE historico_ajuste_estoque (
    id INT AUTO_INCREMENT PRIMARY KEY,
    solicitacao_id INT NOT NULL,
    status_anterior VARCHAR(40) NULL,
    status_novo VARCHAR(40) NOT NULL,
    descricao VARCHAR(350) NOT NULL,
    usuario_id INT NOT NULL,
    usuario_nome VARCHAR(120) NOT NULL,
    data_evento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (solicitacao_id) REFERENCES solicitacao_ajuste_estoque(id)
);

CREATE INDEX idx_solicitacao_ajuste_status_data ON solicitacao_ajuste_estoque(status, data_solicitacao);
CREATE INDEX idx_historico_ajuste_solicitacao_data ON historico_ajuste_estoque(solicitacao_id, data_evento);

-- Tabela de movimentações de estoque
CREATE TABLE movimentacao_estoque (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_produto INT,                                    -- Produto afetado
    tipo VARCHAR(20),                                  -- Entrada/Saída
    quantidade INT,                                    -- Quantidade movida
    motivo TEXT,                                       -- Motivo
    data DATE                                          -- Data da movimentação
);

-- Tabela de histórico de alterações no estoque
CREATE TABLE historico_estoque (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_produto INT,
    quantidade_antiga INT,
    quantidade_nova INT,
    motivo TEXT,
    data_alteracao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sessao_inventario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    setor VARCHAR(80) NOT NULL,
    data_abertura DATE NOT NULL,
    gerente_id INT NOT NULL,
    gerente_nome VARCHAR(120) NOT NULL,
    status VARCHAR(40) NOT NULL DEFAULT 'EM_ANDAMENTO',
    bloqueia_saidas BOOLEAN NOT NULL DEFAULT FALSE,
    tolerancia_quantidade INT NOT NULL DEFAULT 3,
    aprovador_id INT NULL,
    aprovador_nome VARCHAR(120) NULL,
    data_aprovacao TIMESTAMP NULL,
    data_fechamento TIMESTAMP NULL,
    observacao VARCHAR(300) NULL
);

CREATE TABLE inventario_escopo_produto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sessao_id INT NOT NULL,
    produto_id INT NOT NULL,
    quantidade_sistema_abertura INT NOT NULL DEFAULT 0,
    UNIQUE KEY uq_inventario_escopo_produto (sessao_id, produto_id),
    FOREIGN KEY (sessao_id) REFERENCES sessao_inventario(id),
    FOREIGN KEY (produto_id) REFERENCES produto(id)
);

CREATE TABLE contagem_item (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sessao_id INT NOT NULL,
    produto_id INT NOT NULL,
    quantidade_fisica INT NOT NULL,
    auxiliar_id INT NOT NULL,
    auxiliar_nome VARCHAR(120) NOT NULL,
    data_contagem TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sessao_id) REFERENCES sessao_inventario(id),
    FOREIGN KEY (produto_id) REFERENCES produto(id)
);

CREATE INDEX idx_sessao_inventario_setor_status ON sessao_inventario(setor, status);
CREATE INDEX idx_contagem_item_sessao_produto_data ON contagem_item(sessao_id, produto_id, data_contagem);

-- Tabela intermediária de itens dos pedidos
CREATE TABLE item_pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT,
    id_produto INT,
    quantidade INT,
    FOREIGN KEY (id_pedido) REFERENCES pedido(id),
    FOREIGN KEY (id_produto) REFERENCES produto(id)
);

-- Tabela de alertas financeiros por inadimplência
CREATE TABLE alerta_financeiro (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT NOT NULL,
    pedido_id INT,
    dias_atraso INT NOT NULL,
    mensagem TEXT NOT NULL,
    resolvido BOOLEAN DEFAULT FALSE,
    data_alerta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    FOREIGN KEY (pedido_id) REFERENCES pedido(id)
);

CREATE INDEX idx_alerta_financeiro_resolvido ON alerta_financeiro(resolvido, data_alerta);

-- Tabela de devoluções
CREATE TABLE devolucao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id INT NOT NULL,
    cliente_id INT NOT NULL,
    motivo TEXT,
    tipo_restituicao VARCHAR(50),
    status VARCHAR(20) DEFAULT 'PENDENTE',
    observacao_gestor TEXT,
    data_solicitacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_resolucao TIMESTAMP NULL,
    FOREIGN KEY (pedido_id) REFERENCES pedido(id),
    FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);

CREATE INDEX idx_devolucao_pedido ON devolucao(pedido_id);
CREATE INDEX idx_devolucao_cliente ON devolucao(cliente_id);
CREATE INDEX idx_devolucao_status ON devolucao(status);

-- Tabela de itens de devolução
CREATE TABLE devolucao_item (
    id INT AUTO_INCREMENT PRIMARY KEY,
    devolucao_id INT NOT NULL,
    produto_id INT NOT NULL,
    quantidade INT NOT NULL,
    FOREIGN KEY (devolucao_id) REFERENCES devolucao(id) ON DELETE CASCADE,
    FOREIGN KEY (produto_id) REFERENCES produto(id)
);

-- Tabelas de controle de acesso por perfil
CREATE TABLE perfil_acesso (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(80) NOT NULL UNIQUE,
    descricao VARCHAR(255),
    ativo BOOLEAN DEFAULT TRUE
);

CREATE TABLE usuario_acesso (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(60) NOT NULL UNIQUE,
    nome VARCHAR(120) NOT NULL,
    senha VARCHAR(255) NOT NULL,
    ativo BOOLEAN DEFAULT TRUE
);

CREATE TABLE usuario_perfil (
    usuario_id INT NOT NULL,
    perfil_id INT NOT NULL,
    PRIMARY KEY (usuario_id, perfil_id),
    FOREIGN KEY (usuario_id) REFERENCES usuario_acesso(id),
    FOREIGN KEY (perfil_id) REFERENCES perfil_acesso(id)
);

CREATE TABLE permissao_perfil (
    id INT AUTO_INCREMENT PRIMARY KEY,
    perfil_id INT NOT NULL,
    recurso VARCHAR(50) NOT NULL,
    operacao VARCHAR(20) NOT NULL,
    permitido BOOLEAN DEFAULT TRUE,
    UNIQUE KEY uq_permissao_perfil (perfil_id, recurso, operacao),
    FOREIGN KEY (perfil_id) REFERENCES perfil_acesso(id)
);

CREATE TABLE log_acesso (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NULL,
    username VARCHAR(60),
    recurso VARCHAR(50) NOT NULL,
    operacao VARCHAR(20) NOT NULL,
    resultado VARCHAR(20) NOT NULL,
    detalhe VARCHAR(255),
    data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuario_acesso(id)
);

CREATE INDEX idx_permissao_perfil_lookup ON permissao_perfil(perfil_id, recurso, operacao, permitido);
CREATE INDEX idx_log_acesso_data ON log_acesso(data_hora, resultado);

-- Módulo: Relatório Financeiro por Período
CREATE TABLE categoria_financeira (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(120) NOT NULL UNIQUE,
    tipo VARCHAR(10) NOT NULL,
    origem_sistema VARCHAR(40),
    descricao VARCHAR(255),
    ativo BOOLEAN DEFAULT TRUE
);

CREATE TABLE template_relatorio (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(120) NOT NULL UNIQUE,
    descricao VARCHAR(255),
    periodo_padrao VARCHAR(10) NOT NULL DEFAULT 'MES',
    agrupamento VARCHAR(10) NOT NULL DEFAULT 'MES',
    ativo BOOLEAN DEFAULT TRUE
);

CREATE TABLE template_categoria (
    template_id INT NOT NULL,
    categoria_id INT NOT NULL,
    PRIMARY KEY (template_id, categoria_id),
    FOREIGN KEY (template_id) REFERENCES template_relatorio(id),
    FOREIGN KEY (categoria_id) REFERENCES categoria_financeira(id)
);

CREATE TABLE template_indicador (
    template_id INT NOT NULL,
    indicador VARCHAR(40) NOT NULL,
    PRIMARY KEY (template_id, indicador),
    FOREIGN KEY (template_id) REFERENCES template_relatorio(id)
);

CREATE TABLE relatorio_gerado (
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

CREATE TABLE relatorio_categoria_linha (
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

CREATE TABLE relatorio_indicador_linha (
    id INT AUTO_INCREMENT PRIMARY KEY,
    relatorio_id INT NOT NULL,
    indicador VARCHAR(40) NOT NULL,
    valor DECIMAL(14,4) NOT NULL DEFAULT 0.0000,
    valor_anterior DECIMAL(14,4) NULL,
    variacao_percentual DECIMAL(8,2) NULL,
    formula_descricao VARCHAR(255),
    FOREIGN KEY (relatorio_id) REFERENCES relatorio_gerado(id)
);

CREATE TABLE lancamento_ajuste (
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

CREATE INDEX idx_relatorio_gerado_periodo ON relatorio_gerado(data_inicio, data_fim, data_geracao);
CREATE INDEX idx_lancamento_ajuste_data ON lancamento_ajuste(data_lancamento, categoria_id);

-- Tabela de histórico de alterações no funcionário
CREATE TABLE historico_funcionario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_funcionario INT,
    campo VARCHAR(50),
    valor_antigo TEXT,
    valor_novo TEXT,
    data_alteracao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de histórico de alterações no cliente
CREATE TABLE historico_cliente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT,
    campo VARCHAR(50),
    valor_antigo TEXT,
    valor_novo TEXT,
    data_alteracao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Trigger de auditoria para funcionário
DELIMITER $$
CREATE TRIGGER trg_log_funcionario_geral
AFTER UPDATE ON funcionario
FOR EACH ROW
BEGIN
    IF OLD.telefone <> NEW.telefone THEN
        INSERT INTO historico_funcionario (id_funcionario, campo, valor_antigo, valor_novo)
        VALUES (OLD.id, 'telefone', OLD.telefone, NEW.telefone);
    END IF;

    IF OLD.cep <> NEW.cep THEN
        INSERT INTO historico_funcionario (id_funcionario, campo, valor_antigo, valor_novo)
        VALUES (OLD.id, 'cep', OLD.cep, NEW.cep);
    END IF;

    IF OLD.rua <> NEW.rua THEN
        INSERT INTO historico_funcionario (id_funcionario, campo, valor_antigo, valor_novo)
        VALUES (OLD.id, 'rua', OLD.rua, NEW.rua);
    END IF;

    IF OLD.numero <> NEW.numero THEN
        INSERT INTO historico_funcionario (id_funcionario, campo, valor_antigo, valor_novo)
        VALUES (OLD.id, 'numero', OLD.numero, NEW.numero);
    END IF;

    IF OLD.bairro <> NEW.bairro THEN
        INSERT INTO historico_funcionario (id_funcionario, campo, valor_antigo, valor_novo)
        VALUES (OLD.id, 'bairro', OLD.bairro, NEW.bairro);
    END IF;

    IF OLD.cidade <> NEW.cidade THEN
        INSERT INTO historico_funcionario (id_funcionario, campo, valor_antigo, valor_novo)
        VALUES (OLD.id, 'cidade', OLD.cidade, NEW.cidade);
    END IF;

    IF OLD.estado <> NEW.estado THEN
        INSERT INTO historico_funcionario (id_funcionario, campo, valor_antigo, valor_novo)
        VALUES (OLD.id, 'estado', OLD.estado, NEW.estado);
    END IF;

    IF OLD.cargo <> NEW.cargo THEN
        INSERT INTO historico_funcionario (id_funcionario, campo, valor_antigo, valor_novo)
        VALUES (OLD.id, 'cargo', OLD.cargo, NEW.cargo);
    END IF;
END$$
DELIMITER ;

-- Trigger de auditoria para cliente
DELIMITER $$
CREATE TRIGGER trg_log_cliente
AFTER UPDATE ON cliente
FOR EACH ROW
BEGIN
    IF OLD.telefone <> NEW.telefone THEN
        INSERT INTO historico_cliente (id_cliente, campo, valor_antigo, valor_novo)
        VALUES (OLD.id, 'telefone', OLD.telefone, NEW.telefone);
    END IF;

    IF OLD.cep <> NEW.cep THEN
        INSERT INTO historico_cliente (id_cliente, campo, valor_antigo, valor_novo)
        VALUES (OLD.id, 'cep', OLD.cep, NEW.cep);
    END IF;

    IF OLD.rua <> NEW.rua THEN
        INSERT INTO historico_cliente (id_cliente, campo, valor_antigo, valor_novo)
        VALUES (OLD.id, 'rua', OLD.rua, NEW.rua);
    END IF;

    IF OLD.numero <> NEW.numero THEN
        INSERT INTO historico_cliente (id_cliente, campo, valor_antigo, valor_novo)
        VALUES (OLD.id, 'numero', OLD.numero, NEW.numero);
    END IF;

    IF OLD.bairro <> NEW.bairro THEN
        INSERT INTO historico_cliente (id_cliente, campo, valor_antigo, valor_novo)
        VALUES (OLD.id, 'bairro', OLD.bairro, NEW.bairro);
    END IF;

    IF OLD.cidade <> NEW.cidade THEN
        INSERT INTO historico_cliente (id_cliente, campo, valor_antigo, valor_novo)
        VALUES (OLD.id, 'cidade', OLD.cidade, NEW.cidade);
    END IF;

    IF OLD.estado <> NEW.estado THEN
        INSERT INTO historico_cliente (id_cliente, campo, valor_antigo, valor_novo)
        VALUES (OLD.id, 'estado', OLD.estado, NEW.estado);
    END IF;

    IF OLD.email <> NEW.email THEN
        INSERT INTO historico_cliente (id_cliente, campo, valor_antigo, valor_novo)
        VALUES (OLD.id, 'email', OLD.email, NEW.email);
    END IF;
END$$
DELIMITER ;

-- Trigger para atualizar valor_desconto quando um cupom é alterado
DELIMITER $$
CREATE TRIGGER trg_atualiza_desconto_pedido
AFTER UPDATE ON cupom
FOR EACH ROW
BEGIN
    -- Atualiza o valor do desconto nos pedidos que usam este cupom se o valor do cupom foi alterado
    IF OLD.valor <> NEW.valor THEN
        UPDATE pedido 
        SET valor_desconto = NEW.valor
        WHERE cupom_id = NEW.id;
    END IF;
END$$
DELIMITER ;

-- Procedure para buscar cliente por CPF ou CNPJ
DELIMITER $$
CREATE PROCEDURE buscar_cliente_por_cpf_cnpj(IN p_cpf_cnpj VARCHAR(20))
BEGIN
    SELECT * FROM cliente WHERE cpf_cnpj = p_cpf_cnpj;
END$$
DELIMITER ;

-- Dados iniciais do RBAC (Controle de Acesso por Perfil)
INSERT INTO perfil_acesso (nome, descricao, ativo) VALUES
('ADMINISTRADOR', 'Perfil responsável por governança completa de acessos e cadastros.', TRUE),
('GERENTE_OPERACIONAL', 'Perfil com operação supervisionada e permissões de aprovação.', TRUE),
('OPERADOR_VENDEDOR', 'Perfil de execução diária com permissões restritas.', TRUE);

INSERT INTO usuario_acesso (username, nome, senha, ativo) VALUES
('admin', 'Administrador do Sistema', '{noop}Admin@123', TRUE),
('gerente', 'Gerente Operacional', '{noop}Gerente@123', TRUE),
('operador', 'Operador de Vendas', '{noop}Operador@123', TRUE);

INSERT INTO usuario_perfil (usuario_id, perfil_id) VALUES
(1, 1),
(2, 2),
(3, 3);

INSERT INTO permissao_perfil (perfil_id, recurso, operacao, permitido)
SELECT 1, recurso_base.recurso, operacao_base.operacao, TRUE
FROM (
    SELECT 'PRODUTO' AS recurso UNION ALL
    SELECT 'CUPOM' UNION ALL
    SELECT 'PEDIDO' UNION ALL
    SELECT 'ESTOQUE' UNION ALL
    SELECT 'AJUSTE_ESTOQUE' UNION ALL
    SELECT 'INVENTARIO' UNION ALL
    SELECT 'SUPRIMENTO' UNION ALL
    SELECT 'REMESSA' UNION ALL
    SELECT 'CLIENTE' UNION ALL
    SELECT 'FUNCIONARIO' UNION ALL
    SELECT 'DASHBOARD' UNION ALL
    SELECT 'KPI' UNION ALL
    SELECT 'FINANCEIRO' UNION ALL
    SELECT 'ACESSO' UNION ALL
    SELECT 'API' UNION ALL
    SELECT 'HOME'
) AS recurso_base
CROSS JOIN (
    SELECT 'LEITURA' AS operacao UNION ALL
    SELECT 'ESCRITA' UNION ALL
    SELECT 'APROVACAO'
) AS operacao_base;

INSERT INTO permissao_perfil (perfil_id, recurso, operacao, permitido) VALUES
(2, 'PRODUTO', 'LEITURA', TRUE),
(2, 'PRODUTO', 'ESCRITA', TRUE),
(2, 'CUPOM', 'LEITURA', TRUE),
(2, 'CUPOM', 'ESCRITA', TRUE),
(2, 'PEDIDO', 'LEITURA', TRUE),
(2, 'PEDIDO', 'ESCRITA', TRUE),
(2, 'PEDIDO', 'APROVACAO', TRUE),
(2, 'ESTOQUE', 'LEITURA', TRUE),
(2, 'ESTOQUE', 'ESCRITA', TRUE),
(2, 'AJUSTE_ESTOQUE', 'LEITURA', TRUE),
(2, 'AJUSTE_ESTOQUE', 'ESCRITA', TRUE),
(2, 'AJUSTE_ESTOQUE', 'APROVACAO', TRUE),
(2, 'INVENTARIO', 'LEITURA', TRUE),
(2, 'INVENTARIO', 'ESCRITA', TRUE),
(2, 'INVENTARIO', 'APROVACAO', TRUE),
(2, 'SUPRIMENTO', 'LEITURA', TRUE),
(2, 'SUPRIMENTO', 'ESCRITA', TRUE),
(2, 'SUPRIMENTO', 'APROVACAO', TRUE),
(2, 'REMESSA', 'LEITURA', TRUE),
(2, 'REMESSA', 'ESCRITA', TRUE),
(2, 'REMESSA', 'APROVACAO', TRUE),
(2, 'CLIENTE', 'LEITURA', TRUE),
(2, 'CLIENTE', 'ESCRITA', TRUE),
(2, 'FUNCIONARIO', 'LEITURA', TRUE),
(2, 'DASHBOARD', 'LEITURA', TRUE),
(2, 'KPI', 'LEITURA', TRUE),
(2, 'FINANCEIRO', 'LEITURA', TRUE),
(2, 'FINANCEIRO', 'ESCRITA', TRUE),
(2, 'API', 'LEITURA', TRUE),
(2, 'API', 'ESCRITA', TRUE),
(2, 'HOME', 'LEITURA', TRUE);

INSERT INTO permissao_perfil (perfil_id, recurso, operacao, permitido) VALUES
(3, 'PRODUTO', 'LEITURA', TRUE),
(3, 'CUPOM', 'LEITURA', TRUE),
(3, 'PEDIDO', 'LEITURA', TRUE),
(3, 'PEDIDO', 'ESCRITA', TRUE),
(3, 'ESTOQUE', 'LEITURA', TRUE),
(3, 'ESTOQUE', 'ESCRITA', TRUE),
(3, 'AJUSTE_ESTOQUE', 'LEITURA', TRUE),
(3, 'AJUSTE_ESTOQUE', 'ESCRITA', TRUE),
(3, 'INVENTARIO', 'LEITURA', TRUE),
(3, 'INVENTARIO', 'ESCRITA', TRUE),
(3, 'CLIENTE', 'LEITURA', TRUE),
(3, 'CLIENTE', 'ESCRITA', TRUE),
(3, 'FUNCIONARIO', 'LEITURA', TRUE),
(3, 'DASHBOARD', 'LEITURA', TRUE),
(3, 'API', 'LEITURA', TRUE),
(3, 'HOME', 'LEITURA', TRUE);

-- Dados iniciais do módulo financeiro
INSERT INTO categoria_financeira (nome, tipo, origem_sistema, descricao, ativo) VALUES
('Venda de Produto', 'RECEITA', 'PEDIDO_PAGO', 'Receita consolidada de pedidos pagos no período.', TRUE),
('Devolução', 'DESPESA', 'MOVIMENTACAO_ENTRADA_DEVOLUCAO', 'Estornos e devoluções registradas via movimentação de entrada.', TRUE),
('Custo de Reposição', 'DESPESA', 'MOVIMENTACAO_SAIDA', 'Custo estimado de saídas de estoque no período.', TRUE),
('Ajuste Manual', 'RECEITA', 'LANCAMENTO_AJUSTE', 'Lançamentos manuais de correção vinculados ao plano de contas.', TRUE);

INSERT INTO template_relatorio (nome, descricao, periodo_padrao, agrupamento, ativo) VALUES
('Demonstrativo Mensal', 'Consolidação padrão mensal com margem e ticket médio.', 'MES', 'MES', TRUE),
('Fechamento Semanal', 'Visão semanal para acompanhamento operacional.', 'SEMANA', 'SEMANA', TRUE);

INSERT INTO template_categoria (template_id, categoria_id)
SELECT t.id, c.id
FROM template_relatorio t
CROSS JOIN categoria_financeira c
WHERE t.nome = 'Demonstrativo Mensal';

INSERT INTO template_categoria (template_id, categoria_id)
SELECT t.id, c.id
FROM template_relatorio t
JOIN categoria_financeira c ON c.nome IN ('Venda de Produto', 'Custo de Reposição', 'Ajuste Manual')
WHERE t.nome = 'Fechamento Semanal';

INSERT INTO template_indicador (template_id, indicador)
SELECT id, 'MARGEM_BRUTA' FROM template_relatorio WHERE nome = 'Demonstrativo Mensal'
UNION ALL
SELECT id, 'TICKET_MEDIO' FROM template_relatorio WHERE nome = 'Demonstrativo Mensal'
UNION ALL
SELECT id, 'RESULTADO_LIQUIDO' FROM template_relatorio WHERE nome = 'Demonstrativo Mensal'
UNION ALL
SELECT id, 'MARGEM_BRUTA' FROM template_relatorio WHERE nome = 'Fechamento Semanal'
UNION ALL
SELECT id, 'TICKET_MEDIO' FROM template_relatorio WHERE nome = 'Fechamento Semanal';

-- Módulo de KPIs Operacionais (Gestão de Indicadores Operacionais com Metas e Alertas Persistidos)
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

CREATE INDEX idx_snapshot_indicador_periodo ON snapshot_indicador(indicador_id, periodo_inicio, periodo_fim);
CREATE INDEX idx_alerta_indicador_status ON alerta_indicador(status, data_alerta);

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
