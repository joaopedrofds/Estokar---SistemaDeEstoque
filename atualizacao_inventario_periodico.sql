CREATE TABLE IF NOT EXISTS parametro_inventario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tolerancia_quantidade INT NOT NULL DEFAULT 3
);

INSERT INTO parametro_inventario (id, tolerancia_quantidade)
SELECT 1, 3
WHERE NOT EXISTS (SELECT 1 FROM parametro_inventario WHERE id = 1);

CREATE TABLE IF NOT EXISTS sessao_inventario (
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

CREATE TABLE IF NOT EXISTS inventario_escopo_produto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sessao_id INT NOT NULL,
    produto_id INT NOT NULL,
    quantidade_sistema_abertura INT NOT NULL DEFAULT 0,
    UNIQUE KEY uq_inventario_escopo_produto (sessao_id, produto_id),
    FOREIGN KEY (sessao_id) REFERENCES sessao_inventario(id),
    FOREIGN KEY (produto_id) REFERENCES produto(id)
);

CREATE TABLE IF NOT EXISTS contagem_item (
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

INSERT INTO permissao_perfil (perfil_id, recurso, operacao, permitido)
SELECT perfil_id, 'INVENTARIO', operacao, permitido
FROM (
    SELECT 1 AS perfil_id, 'LEITURA' AS operacao, TRUE AS permitido UNION ALL
    SELECT 1, 'ESCRITA', TRUE UNION ALL
    SELECT 1, 'APROVACAO', TRUE UNION ALL
    SELECT 2, 'LEITURA', TRUE UNION ALL
    SELECT 2, 'ESCRITA', TRUE UNION ALL
    SELECT 2, 'APROVACAO', TRUE UNION ALL
    SELECT 3, 'LEITURA', TRUE UNION ALL
    SELECT 3, 'ESCRITA', TRUE
) AS permissoes
WHERE NOT EXISTS (
    SELECT 1
    FROM permissao_perfil pp
    WHERE pp.perfil_id = permissoes.perfil_id
      AND pp.recurso = 'INVENTARIO'
      AND pp.operacao = permissoes.operacao
);
