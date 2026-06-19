USE studiomuda;

SET @sql = IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pedido' AND COLUMN_NAME = 'status') = 0,
    'ALTER TABLE pedido ADD COLUMN status VARCHAR(40) NOT NULL DEFAULT ''PENDENTE'' AFTER valor_desconto',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pedido' AND COLUMN_NAME = 'cancelamento_solicitante_id') = 0,
    'ALTER TABLE pedido ADD COLUMN cancelamento_solicitante_id INT NULL AFTER data_pagamento',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pedido' AND COLUMN_NAME = 'cancelamento_solicitante_nome') = 0,
    'ALTER TABLE pedido ADD COLUMN cancelamento_solicitante_nome VARCHAR(120) NULL AFTER cancelamento_solicitante_id',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pedido' AND COLUMN_NAME = 'justificativa_cancelamento') = 0,
    'ALTER TABLE pedido ADD COLUMN justificativa_cancelamento VARCHAR(300) NULL AFTER cancelamento_solicitante_nome',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pedido' AND COLUMN_NAME = 'data_cancelamento') = 0,
    'ALTER TABLE pedido ADD COLUMN data_cancelamento TIMESTAMP NULL AFTER justificativa_cancelamento',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pedido' AND COLUMN_NAME = 'cancelamento_aprovador_id') = 0,
    'ALTER TABLE pedido ADD COLUMN cancelamento_aprovador_id INT NULL AFTER data_cancelamento',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pedido' AND COLUMN_NAME = 'cancelamento_aprovador_nome') = 0,
    'ALTER TABLE pedido ADD COLUMN cancelamento_aprovador_nome VARCHAR(120) NULL AFTER cancelamento_aprovador_id',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pedido' AND COLUMN_NAME = 'data_aprovacao_cancelamento') = 0,
    'ALTER TABLE pedido ADD COLUMN data_aprovacao_cancelamento TIMESTAMP NULL AFTER cancelamento_aprovador_nome',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS parametro_cancelamento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    limite_quantidade_sem_aprovacao INT NOT NULL DEFAULT 10
);

INSERT INTO parametro_cancelamento (limite_quantidade_sem_aprovacao)
SELECT 10
WHERE NOT EXISTS (SELECT 1 FROM parametro_cancelamento);

SET @sql = IF(
    (SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pedido' AND INDEX_NAME = 'idx_pedido_status') = 0,
    'CREATE INDEX idx_pedido_status ON pedido(status)',
    'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
