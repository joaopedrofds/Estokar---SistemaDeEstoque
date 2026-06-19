CREATE TABLE IF NOT EXISTS parametro_ajuste_estoque (
    id INT AUTO_INCREMENT PRIMARY KEY,
    limite_quantidade_sem_aprovacao INT NOT NULL DEFAULT 5,
    percentual_risco_alto INT NOT NULL DEFAULT 30
);

INSERT INTO parametro_ajuste_estoque (limite_quantidade_sem_aprovacao, percentual_risco_alto)
SELECT 5, 30
WHERE NOT EXISTS (
    SELECT 1 FROM parametro_ajuste_estoque
);

CREATE TABLE IF NOT EXISTS solicitacao_ajuste_estoque (
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

CREATE TABLE IF NOT EXISTS historico_ajuste_estoque (
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

INSERT INTO permissao_perfil (perfil_id, recurso, operacao, permitido)
SELECT perfil_id, 'AJUSTE_ESTOQUE', operacao, permitido
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
      AND pp.recurso = 'AJUSTE_ESTOQUE'
      AND pp.operacao = permissoes.operacao
);
