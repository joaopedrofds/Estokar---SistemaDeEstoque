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
    FOREIGN KEY (cupom_id) REFERENCES cupom(id),
    FOREIGN KEY (funcionario_id) REFERENCES funcionario(id)
);

-- Criar índices para melhorar a performance das consultas em pedidos
CREATE INDEX idx_pedido_funcionario ON pedido(funcionario_id);
CREATE INDEX idx_pedido_cupom ON pedido(cupom_id);

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

-- Tabela intermediária de itens dos pedidos
CREATE TABLE item_pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT,
    id_produto INT,
    quantidade INT,
    FOREIGN KEY (id_pedido) REFERENCES pedido(id),
    FOREIGN KEY (id_produto) REFERENCES produto(id)
);

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
