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

-- Reclassificação inicial para não bloquear retroativamente pedidos já entregues.
UPDATE pedido
SET status_pagamento = 'PAGO',
    data_pagamento = COALESCE(data_pagamento, data_entrega, data_requisicao)
WHERE data_entrega IS NOT NULL;
