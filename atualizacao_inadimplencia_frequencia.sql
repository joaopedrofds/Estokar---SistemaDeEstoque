-- Atualização: Bloqueio por inadimplência + análise de frequência
-- Execute este script em bancos já existentes.

USE studiomuda;

ALTER TABLE pedido
    ADD COLUMN IF NOT EXISTS status_pagamento VARCHAR(20) NOT NULL DEFAULT 'PENDENTE' AFTER valor_desconto,
    ADD COLUMN IF NOT EXISTS data_pagamento DATE NULL AFTER status_pagamento;

CREATE INDEX idx_pedido_cliente_pagamento ON pedido(cliente_id, status_pagamento, data_requisicao);

CREATE TABLE IF NOT EXISTS alerta_financeiro (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT NOT NULL,
    pedido_id INT NULL,
    dias_atraso INT NOT NULL,
    mensagem TEXT NOT NULL,
    resolvido BOOLEAN DEFAULT FALSE,
    data_alerta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    FOREIGN KEY (pedido_id) REFERENCES pedido(id)
);

CREATE INDEX idx_alerta_financeiro_resolvido ON alerta_financeiro(resolvido, data_alerta);

CREATE TABLE IF NOT EXISTS politica_credito (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    dias_limite_atraso INT NOT NULL,
    ativa BOOLEAN NOT NULL DEFAULT TRUE,
    data_inicio DATE NOT NULL,
    data_fim DATE NULL,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_politica_credito_ativa ON politica_credito(ativa, data_fim);

INSERT INTO politica_credito (nome, dias_limite_atraso, ativa, data_inicio)
SELECT 'Politica padrao', 45, TRUE, CURDATE()
WHERE NOT EXISTS (SELECT 1 FROM politica_credito);

CREATE TABLE IF NOT EXISTS fatura (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT NOT NULL,
    pedido_id INT NULL,
    data_vencimento DATE NOT NULL,
    data_pagamento DATE NULL,
    valor DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) NOT NULL DEFAULT 'ABERTA',
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    FOREIGN KEY (pedido_id) REFERENCES pedido(id)
);

CREATE INDEX idx_fatura_cliente_status_vencimento ON fatura(cliente_id, status, data_vencimento);

CREATE TABLE IF NOT EXISTS acordo_pagamento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'EM_ACORDO',
    data_inicio DATE NOT NULL,
    data_fim DATE NULL,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);

CREATE INDEX idx_acordo_cliente_status ON acordo_pagamento(cliente_id, status, data_inicio, data_fim);

CREATE TABLE IF NOT EXISTS parcela_acordo (
    id INT AUTO_INCREMENT PRIMARY KEY,
    acordo_id INT NOT NULL,
    numero INT NOT NULL,
    data_vencimento DATE NOT NULL,
    data_pagamento DATE NULL,
    valor DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ABERTA',
    FOREIGN KEY (acordo_id) REFERENCES acordo_pagamento(id)
);

CREATE INDEX idx_parcela_acordo_vencimento ON parcela_acordo(acordo_id, status, data_vencimento);

CREATE TABLE IF NOT EXISTS historico_cobranca (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT NOT NULL,
    fatura_id INT NULL,
    acordo_id INT NULL,
    registro_original_id INT NULL,
    tipo VARCHAR(40) NOT NULL,
    descricao TEXT NOT NULL,
    usuario VARCHAR(120) NOT NULL,
    criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    FOREIGN KEY (fatura_id) REFERENCES fatura(id),
    FOREIGN KEY (acordo_id) REFERENCES acordo_pagamento(id),
    FOREIGN KEY (registro_original_id) REFERENCES historico_cobranca(id)
);

CREATE INDEX idx_historico_cobranca_cliente ON historico_cobranca(cliente_id, criado_em);

-- Reclassificação inicial para não bloquear retroativamente pedidos já entregues.
UPDATE pedido
SET status_pagamento = 'PAGO',
    data_pagamento = COALESCE(data_pagamento, data_entrega, data_requisicao)
WHERE data_entrega IS NOT NULL;
