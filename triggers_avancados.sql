-- =====================================================
-- SISTEMA DE TRIGGERS AVANÇADO - STUDIO MUDA
-- Automatiza filtros, contadores e validações
-- =====================================================

-- Primeiro, vamos criar uma tabela para contadores automáticos
CREATE TABLE IF NOT EXISTS contadores_sistema (
    id INT PRIMARY KEY AUTO_INCREMENT,
    tipo_contador VARCHAR(50) NOT NULL UNIQUE,
    valor_atual INT DEFAULT 0,
    ultima_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Inserir contadores iniciais
INSERT IGNORE INTO contadores_sistema (tipo_contador, valor_atual) VALUES
('total_produtos', 0),
('total_clientes', 0),
('total_pedidos', 0),
('total_movimentacoes', 0),
('total_cupons', 0),
('total_funcionarios', 0),
('produtos_estoque_baixo', 0),
('produtos_sem_estoque', 0),
('pedidos_pendentes', 0),
('clientes_ativos', 0);

-- Tabela para logs de auditoria avançados
CREATE TABLE IF NOT EXISTS auditoria_sistema (
    id INT PRIMARY KEY AUTO_INCREMENT,
    tabela_afetada VARCHAR(50) NOT NULL,
    operacao ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    registro_id INT,
    dados_anteriores JSON,
    dados_novos JSON,
    usuario VARCHAR(100),
    data_operacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_origem VARCHAR(45),
    observacoes TEXT
);

-- =====================================================
-- TRIGGERS PARA PRODUTOS
-- =====================================================

DELIMITER $$

-- Trigger: Atualizar contadores após inserir produto
CREATE TRIGGER tr_produto_after_insert
AFTER INSERT ON produtos
FOR EACH ROW
BEGIN
    -- Atualizar contador total de produtos
    UPDATE contadores_sistema 
    SET valor_atual = valor_atual + 1 
    WHERE tipo_contador = 'total_produtos';
    
    -- Verificar estoque baixo/zerado
    IF NEW.quantidade <= 5 THEN
        UPDATE contadores_sistema 
        SET valor_atual = valor_atual + 1 
        WHERE tipo_contador = 'produtos_estoque_baixo';
    END IF;
    
    IF NEW.quantidade = 0 THEN
        UPDATE contadores_sistema 
        SET valor_atual = valor_atual + 1 
        WHERE tipo_contador = 'produtos_sem_estoque';
    END IF;
    
    -- Log de auditoria
    INSERT INTO auditoria_sistema (tabela_afetada, operacao, registro_id, dados_novos, observacoes)
    VALUES ('produtos', 'INSERT', NEW.id, JSON_OBJECT(
        'nome', NEW.nome,
        'tipo', NEW.tipo,
        'preco', NEW.preco,
        'quantidade', NEW.quantidade
    ), 'Produto criado automaticamente');
END$$

-- Trigger: Atualizar contadores após atualizar produto
CREATE TRIGGER tr_produto_after_update
AFTER UPDATE ON produtos
FOR EACH ROW
BEGIN
    DECLARE estoque_baixo_antes INT DEFAULT 0;
    DECLARE estoque_baixo_depois INT DEFAULT 0;
    DECLARE sem_estoque_antes INT DEFAULT 0;
    DECLARE sem_estoque_depois INT DEFAULT 0;
    
    -- Verificar mudanças no estoque
    IF OLD.quantidade <= 5 THEN SET estoque_baixo_antes = 1; END IF;
    IF NEW.quantidade <= 5 THEN SET estoque_baixo_depois = 1; END IF;
    IF OLD.quantidade = 0 THEN SET sem_estoque_antes = 1; END IF;
    IF NEW.quantidade = 0 THEN SET sem_estoque_depois = 1; END IF;
    
    -- Atualizar contador de estoque baixo
    UPDATE contadores_sistema 
    SET valor_atual = valor_atual + (estoque_baixo_depois - estoque_baixo_antes)
    WHERE tipo_contador = 'produtos_estoque_baixo';
    
    -- Atualizar contador sem estoque
    UPDATE contadores_sistema 
    SET valor_atual = valor_atual + (sem_estoque_depois - sem_estoque_antes)
    WHERE tipo_contador = 'produtos_sem_estoque';
    
    -- Log de auditoria
    INSERT INTO auditoria_sistema (tabela_afetada, operacao, registro_id, dados_anteriores, dados_novos, observacoes)
    VALUES ('produtos', 'UPDATE', NEW.id, 
        JSON_OBJECT('nome', OLD.nome, 'tipo', OLD.tipo, 'preco', OLD.preco, 'quantidade', OLD.quantidade),
        JSON_OBJECT('nome', NEW.nome, 'tipo', NEW.tipo, 'preco', NEW.preco, 'quantidade', NEW.quantidade),
        CONCAT('Produto atualizado - Estoque: ', OLD.quantidade, ' → ', NEW.quantidade)
    );
END$$

-- Trigger: Atualizar contadores após deletar produto
CREATE TRIGGER tr_produto_after_delete
AFTER DELETE ON produtos
FOR EACH ROW
BEGIN
    -- Decrementar contador total
    UPDATE contadores_sistema 
    SET valor_atual = valor_atual - 1 
    WHERE tipo_contador = 'total_produtos';
    
    -- Ajustar contadores de estoque
    IF OLD.quantidade <= 5 THEN
        UPDATE contadores_sistema 
        SET valor_atual = valor_atual - 1 
        WHERE tipo_contador = 'produtos_estoque_baixo';
    END IF;
    
    IF OLD.quantidade = 0 THEN
        UPDATE contadores_sistema 
        SET valor_atual = valor_atual - 1 
        WHERE tipo_contador = 'produtos_sem_estoque';
    END IF;
    
    -- Log de auditoria
    INSERT INTO auditoria_sistema (tabela_afetada, operacao, registro_id, dados_anteriores, observacoes)
    VALUES ('produtos', 'DELETE', OLD.id, JSON_OBJECT(
        'nome', OLD.nome,
        'tipo', OLD.tipo,
        'preco', OLD.preco,
        'quantidade', OLD.quantidade
    ), 'Produto removido do sistema');
END$$

-- =====================================================
-- TRIGGERS PARA CLIENTES
-- =====================================================

-- Trigger: Após inserir cliente
CREATE TRIGGER tr_cliente_after_insert
AFTER INSERT ON clientes
FOR EACH ROW
BEGIN
    -- Atualizar contador total
    UPDATE contadores_sistema 
    SET valor_atual = valor_atual + 1 
    WHERE tipo_contador = 'total_clientes';
    
    -- Atualizar contador de ativos (se aplicável)
    IF NEW.ativo = 1 THEN
        UPDATE contadores_sistema 
        SET valor_atual = valor_atual + 1 
        WHERE tipo_contador = 'clientes_ativos';
    END IF;
    
    -- Log de auditoria
    INSERT INTO auditoria_sistema (tabela_afetada, operacao, registro_id, dados_novos, observacoes)
    VALUES ('clientes', 'INSERT', NEW.id, JSON_OBJECT(
        'nome', NEW.nome,
        'tipo', NEW.tipo,
        'cpf_cnpj', NEW.cpf_cnpj,
        'ativo', NEW.ativo
    ), 'Cliente cadastrado no sistema');
END$$

-- Trigger: Após atualizar cliente
CREATE TRIGGER tr_cliente_after_update
AFTER UPDATE ON clientes
FOR EACH ROW
BEGIN
    DECLARE ativo_antes INT DEFAULT 0;
    DECLARE ativo_depois INT DEFAULT 0;
    
    IF OLD.ativo = 1 THEN SET ativo_antes = 1; END IF;
    IF NEW.ativo = 1 THEN SET ativo_depois = 1; END IF;
    
    -- Atualizar contador de clientes ativos
    UPDATE contadores_sistema 
    SET valor_atual = valor_atual + (ativo_depois - ativo_antes)
    WHERE tipo_contador = 'clientes_ativos';
    
    -- Log de auditoria
    INSERT INTO auditoria_sistema (tabela_afetada, operacao, registro_id, dados_anteriores, dados_novos, observacoes)
    VALUES ('clientes', 'UPDATE', NEW.id,
        JSON_OBJECT('nome', OLD.nome, 'tipo', OLD.tipo, 'ativo', OLD.ativo),
        JSON_OBJECT('nome', NEW.nome, 'tipo', NEW.tipo, 'ativo', NEW.ativo),
        'Dados do cliente atualizados'
    );
END$$

-- Trigger: Após deletar cliente
CREATE TRIGGER tr_cliente_after_delete
AFTER DELETE ON clientes
FOR EACH ROW
BEGIN
    -- Decrementar contador total
    UPDATE contadores_sistema 
    SET valor_atual = valor_atual - 1 
    WHERE tipo_contador = 'total_clientes';
    
    -- Ajustar contador de ativos
    IF OLD.ativo = 1 THEN
        UPDATE contadores_sistema 
        SET valor_atual = valor_atual - 1 
        WHERE tipo_contador = 'clientes_ativos';
    END IF;
    
    -- Log de auditoria
    INSERT INTO auditoria_sistema (tabela_afetada, operacao, registro_id, dados_anteriores, observacoes)
    VALUES ('clientes', 'DELETE', OLD.id, JSON_OBJECT(
        'nome', OLD.nome,
        'tipo', OLD.tipo,
        'cpf_cnpj', OLD.cpf_cnpj
    ), 'Cliente removido do sistema');
END$$

-- =====================================================
-- TRIGGERS PARA PEDIDOS
-- =====================================================

-- Trigger: Após inserir pedido
CREATE TRIGGER tr_pedido_after_insert
AFTER INSERT ON pedidos
FOR EACH ROW
BEGIN
    -- Atualizar contador total
    UPDATE contadores_sistema 
    SET valor_atual = valor_atual + 1 
    WHERE tipo_contador = 'total_pedidos';
    
    -- Atualizar contador de pendentes
    IF NEW.status = 'PENDENTE' THEN
        UPDATE contadores_sistema 
        SET valor_atual = valor_atual + 1 
        WHERE tipo_contador = 'pedidos_pendentes';
    END IF;
    
    -- Log de auditoria
    INSERT INTO auditoria_sistema (tabela_afetada, operacao, registro_id, dados_novos, observacoes)
    VALUES ('pedidos', 'INSERT', NEW.id, JSON_OBJECT(
        'id_cliente', NEW.id_cliente,
        'status', NEW.status,
        'valor_total', NEW.valor_total,
        'data_pedido', NEW.data_pedido
    ), 'Novo pedido criado');
END$$

-- Trigger: Após atualizar pedido
CREATE TRIGGER tr_pedido_after_update
AFTER UPDATE ON pedidos
FOR EACH ROW
BEGIN
    DECLARE pendente_antes INT DEFAULT 0;
    DECLARE pendente_depois INT DEFAULT 0;
    
    IF OLD.status = 'PENDENTE' THEN SET pendente_antes = 1; END IF;
    IF NEW.status = 'PENDENTE' THEN SET pendente_depois = 1; END IF;
    
    -- Atualizar contador de pendentes
    UPDATE contadores_sistema 
    SET valor_atual = valor_atual + (pendente_depois - pendente_antes)
    WHERE tipo_contador = 'pedidos_pendentes';
    
    -- Log de auditoria
    INSERT INTO auditoria_sistema (tabela_afetada, operacao, registro_id, dados_anteriores, dados_novos, observacoes)
    VALUES ('pedidos', 'UPDATE', NEW.id,
        JSON_OBJECT('status', OLD.status, 'valor_total', OLD.valor_total),
        JSON_OBJECT('status', NEW.status, 'valor_total', NEW.valor_total),
        CONCAT('Status alterado: ', OLD.status, ' → ', NEW.status)
    );
END$$

-- Trigger: Após deletar pedido
CREATE TRIGGER tr_pedido_after_delete
AFTER DELETE ON pedidos
FOR EACH ROW
BEGIN
    -- Decrementar contador total
    UPDATE contadores_sistema 
    SET valor_atual = valor_atual - 1 
    WHERE tipo_contador = 'total_pedidos';
    
    -- Ajustar contador de pendentes
    IF OLD.status = 'PENDENTE' THEN
        UPDATE contadores_sistema 
        SET valor_atual = valor_atual - 1 
        WHERE tipo_contador = 'pedidos_pendentes';
    END IF;
    
    -- Log de auditoria
    INSERT INTO auditoria_sistema (tabela_afetada, operacao, registro_id, dados_anteriores, observacoes)
    VALUES ('pedidos', 'DELETE', OLD.id, JSON_OBJECT(
        'id_cliente', OLD.id_cliente,
        'status', OLD.status,
        'valor_total', OLD.valor_total
    ), 'Pedido removido do sistema');
END$$

-- =====================================================
-- TRIGGERS PARA MOVIMENTAÇÕES DE ESTOQUE
-- =====================================================

-- Trigger: Após inserir movimentação
CREATE TRIGGER tr_movimentacao_after_insert
AFTER INSERT ON movimentacoes_estoque
FOR EACH ROW
BEGIN
    -- Atualizar contador total
    UPDATE contadores_sistema 
    SET valor_atual = valor_atual + 1 
    WHERE tipo_contador = 'total_movimentacoes';
    
    -- Atualizar estoque do produto automaticamente
    IF NEW.tipo = 'entrada' THEN
        UPDATE produtos 
        SET quantidade = quantidade + NEW.quantidade 
        WHERE id = NEW.id_produto;
    ELSEIF NEW.tipo = 'saida' THEN
        UPDATE produtos 
        SET quantidade = quantidade - NEW.quantidade 
        WHERE id = NEW.id_produto;
    END IF;
    
    -- Log de auditoria
    INSERT INTO auditoria_sistema (tabela_afetada, operacao, registro_id, dados_novos, observacoes)
    VALUES ('movimentacoes_estoque', 'INSERT', NEW.id, JSON_OBJECT(
        'id_produto', NEW.id_produto,
        'tipo', NEW.tipo,
        'quantidade', NEW.quantidade,
        'observacoes', NEW.observacoes
    ), CONCAT('Movimentação de estoque: ', NEW.tipo, ' de ', NEW.quantidade, ' unidades'));
END$$

-- Trigger: Após deletar movimentação (estorno)
CREATE TRIGGER tr_movimentacao_after_delete
AFTER DELETE ON movimentacoes_estoque
FOR EACH ROW
BEGIN
    -- Decrementar contador
    UPDATE contadores_sistema 
    SET valor_atual = valor_atual - 1 
    WHERE tipo_contador = 'total_movimentacoes';
    
    -- Estornar movimento no estoque
    IF OLD.tipo = 'entrada' THEN
        UPDATE produtos 
        SET quantidade = quantidade - OLD.quantidade 
        WHERE id = OLD.id_produto;
    ELSEIF OLD.tipo = 'saida' THEN
        UPDATE produtos 
        SET quantidade = quantidade + OLD.quantidade 
        WHERE id = OLD.id_produto;
    END IF;
    
    -- Log de auditoria
    INSERT INTO auditoria_sistema (tabela_afetada, operacao, registro_id, dados_anteriores, observacoes)
    VALUES ('movimentacoes_estoque', 'DELETE', OLD.id, JSON_OBJECT(
        'id_produto', OLD.id_produto,
        'tipo', OLD.tipo,
        'quantidade', OLD.quantidade
    ), CONCAT('Estorno de movimentação: ', OLD.tipo, ' de ', OLD.quantidade, ' unidades'));
END$$

-- =====================================================
-- PROCEDURES PARA FILTROS AVANÇADOS
-- =====================================================

-- Procedure: Buscar produtos com filtros
CREATE PROCEDURE sp_buscar_produtos(
    IN p_nome VARCHAR(255),
    IN p_tipo VARCHAR(50),
    IN p_estoque_status VARCHAR(20)
)
BEGIN
    SELECT p.*, 
           CASE 
               WHEN p.quantidade = 0 THEN 'SEM_ESTOQUE'
               WHEN p.quantidade <= 5 THEN 'ESTOQUE_BAIXO'
               ELSE 'DISPONIVEL'
           END as status_estoque
    FROM produtos p
    WHERE (p_nome IS NULL OR p.nome LIKE CONCAT('%', p_nome, '%'))
      AND (p_tipo IS NULL OR p.tipo = p_tipo)
      AND (p_estoque_status IS NULL OR 
           (p_estoque_status = 'disponivel' AND p.quantidade > 5) OR
           (p_estoque_status = 'baixo' AND p.quantidade > 0 AND p.quantidade <= 5) OR
           (p_estoque_status = 'zerado' AND p.quantidade = 0))
    ORDER BY p.nome;
END$$

-- Procedure: Buscar clientes com filtros
CREATE PROCEDURE sp_buscar_clientes(
    IN p_nome VARCHAR(255),
    IN p_tipo VARCHAR(10),
    IN p_status VARCHAR(20)
)
BEGIN
    SELECT c.*,
           CASE WHEN c.ativo = 1 THEN 'ATIVO' ELSE 'INATIVO' END as status_texto
    FROM clientes c
    WHERE (p_nome IS NULL OR c.nome LIKE CONCAT('%', p_nome, '%'))
      AND (p_tipo IS NULL OR c.tipo = p_tipo)
      AND (p_status IS NULL OR 
           (p_status = 'ativo' AND c.ativo = 1) OR
           (p_status = 'inativo' AND c.ativo = 0))
    ORDER BY c.nome;
END$$

-- Procedure: Buscar movimentações com filtros
CREATE PROCEDURE sp_buscar_movimentacoes(
    IN p_produto_nome VARCHAR(255),
    IN p_tipo VARCHAR(20),
    IN p_data_inicio DATE,
    IN p_data_fim DATE
)
BEGIN
    SELECT m.*, p.nome as produto_nome
    FROM movimentacoes_estoque m
    JOIN produtos p ON m.id_produto = p.id
    WHERE (p_produto_nome IS NULL OR p.nome LIKE CONCAT('%', p_produto_nome, '%'))
      AND (p_tipo IS NULL OR m.tipo = p_tipo)
      AND (p_data_inicio IS NULL OR m.data >= p_data_inicio)
      AND (p_data_fim IS NULL OR m.data <= p_data_fim)
    ORDER BY m.data DESC, m.id DESC;
END$$

-- Procedure: Atualizar todos os contadores
CREATE PROCEDURE sp_recalcular_contadores()
BEGIN
    -- Produtos
    UPDATE contadores_sistema SET valor_atual = (SELECT COUNT(*) FROM produtos) WHERE tipo_contador = 'total_produtos';
    UPDATE contadores_sistema SET valor_atual = (SELECT COUNT(*) FROM produtos WHERE quantidade <= 5) WHERE tipo_contador = 'produtos_estoque_baixo';
    UPDATE contadores_sistema SET valor_atual = (SELECT COUNT(*) FROM produtos WHERE quantidade = 0) WHERE tipo_contador = 'produtos_sem_estoque';
    
    -- Clientes
    UPDATE contadores_sistema SET valor_atual = (SELECT COUNT(*) FROM clientes) WHERE tipo_contador = 'total_clientes';
    UPDATE contadores_sistema SET valor_atual = (SELECT COUNT(*) FROM clientes WHERE ativo = 1) WHERE tipo_contador = 'clientes_ativos';
    
    -- Pedidos
    UPDATE contadores_sistema SET valor_atual = (SELECT COUNT(*) FROM pedidos) WHERE tipo_contador = 'total_pedidos';
    UPDATE contadores_sistema SET valor_atual = (SELECT COUNT(*) FROM pedidos WHERE status = 'PENDENTE') WHERE tipo_contador = 'pedidos_pendentes';
    
    -- Movimentações
    UPDATE contadores_sistema SET valor_atual = (SELECT COUNT(*) FROM movimentacoes_estoque) WHERE tipo_contador = 'total_movimentacoes';
END$$

DELIMITER ;

-- =====================================================
-- ÍNDICES PARA OTIMIZAÇÃO DE FILTROS
-- =====================================================

-- Índices para produtos
CREATE INDEX idx_produtos_nome ON produtos(nome);
CREATE INDEX idx_produtos_tipo ON produtos(tipo);
CREATE INDEX idx_produtos_quantidade ON produtos(quantidade);
CREATE INDEX idx_produtos_nome_tipo ON produtos(nome, tipo);

-- Índices para clientes
CREATE INDEX idx_clientes_nome ON clientes(nome);
CREATE INDEX idx_clientes_tipo ON clientes(tipo);
CREATE INDEX idx_clientes_ativo ON clientes(ativo);
CREATE INDEX idx_clientes_cpf_cnpj ON clientes(cpf_cnpj);

-- Índices para pedidos
CREATE INDEX idx_pedidos_status ON pedidos(status);
CREATE INDEX idx_pedidos_data ON pedidos(data_pedido);
CREATE INDEX idx_pedidos_cliente ON pedidos(id_cliente);

-- Índices para movimentações
CREATE INDEX idx_movimentacoes_data ON movimentacoes_estoque(data);
CREATE INDEX idx_movimentacoes_tipo ON movimentacoes_estoque(tipo);
CREATE INDEX idx_movimentacoes_produto ON movimentacoes_estoque(id_produto);

-- Índices para auditoria
CREATE INDEX idx_auditoria_tabela ON auditoria_sistema(tabela_afetada);
CREATE INDEX idx_auditoria_data ON auditoria_sistema(data_operacao);
CREATE INDEX idx_auditoria_operacao ON auditoria_sistema(operacao);

-- =====================================================
-- EXECUTAR CÁLCULO INICIAL DOS CONTADORES
-- =====================================================
CALL sp_recalcular_contadores();

-- =====================================================
-- VIEWS PARA RELATÓRIOS E FILTROS
-- =====================================================

-- View: Produtos com status de estoque
CREATE OR REPLACE VIEW vw_produtos_completa AS
SELECT p.*,
       CASE 
           WHEN p.quantidade = 0 THEN 'SEM_ESTOQUE'
           WHEN p.quantidade <= 5 THEN 'ESTOQUE_BAIXO'
           ELSE 'DISPONIVEL'
       END as status_estoque,
       CASE 
           WHEN p.quantidade = 0 THEN 'danger'
           WHEN p.quantidade <= 5 THEN 'warning'
           ELSE 'success'
       END as css_class
FROM produtos p;

-- View: Clientes com informações extras
CREATE OR REPLACE VIEW vw_clientes_completa AS
SELECT c.*,
       CASE WHEN c.ativo = 1 THEN 'ATIVO' ELSE 'INATIVO' END as status_texto,
       CASE WHEN c.ativo = 1 THEN 'success' ELSE 'secondary' END as css_class,
       (SELECT COUNT(*) FROM pedidos p WHERE p.id_cliente = c.id) as total_pedidos
FROM clientes c;

-- View: Relatório de estoque crítico
CREATE OR REPLACE VIEW vw_estoque_critico AS
SELECT p.*, 
       'ATENÇÃO' as alerta,
       CASE 
           WHEN p.quantidade = 0 THEN 'PRODUTO SEM ESTOQUE'
           ELSE 'ESTOQUE BAIXO'
       END as tipo_alerta
FROM produtos p 
WHERE p.quantidade <= 5
ORDER BY p.quantidade ASC, p.nome;

-- Finalização
SELECT 'SISTEMA DE TRIGGERS E FILTROS INSTALADO COM SUCESSO!' as status;
