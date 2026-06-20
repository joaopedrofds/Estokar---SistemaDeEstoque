USE studiomuda;
-- =====================================================================
-- DADOS DE TESTE: Funcionalidade 1 — Gestão de Cobranças, Acordos e
-- Bloqueio Automático de Inadimplentes.
--
-- Causa raiz do "financeiro zerado" nesta funcionalidade: as tabelas
-- politica_credito, fatura, acordo_pagamento e historico_cobranca nunca
-- foram populadas por nenhum script do projeto (dados_teste.sql não as
-- contempla). As telas e regras já estão implementadas corretamente —
-- faltava só o dado. Este script usa subqueries por nome de cliente
-- (mais seguro que IDs fixos) reaproveitando os clientes já existentes
-- em dados_teste.sql.
--
-- Cada bloco abaixo foi desenhado para provar uma regra de negócio
-- específica (RN1 a RN4) de forma visual e fácil de demonstrar.
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1) POLÍTICA DE CRÉDITO (RN3 — apenas uma política ativa por vez)
-- ---------------------------------------------------------------------
-- Insere uma política antiga (já encerrada) e a política vigente atual.
-- Isso já demonstra o histórico de versionamento que a RN3 garante.
INSERT INTO politica_credito (nome, limite_dias_atraso, limite_credito, data_inicio, data_fim, ativa) VALUES
('Política Inicial 2024', 60, 5000.00, '2024-01-01', '2025-12-31', false),
('Política Vigente 2026', 30, 8000.00, '2026-01-01', NULL, true);

-- Gancho de demonstração da RN3: ao cadastrar uma NOVA política ativa pela
-- tela /cobrancas/politicas, o sistema deve encerrar automaticamente a
-- "Política Vigente 2026" acima (setar ativa=false e data_fim). Não rode
-- esse encerramento manualmente — deixe para o examinador ver acontecer
-- ao vivo ao cadastrar uma 3ª política pela UI.

-- ---------------------------------------------------------------------
-- 2) FATURAS — cenário de cliente normal (sem atraso, em dia)
-- ---------------------------------------------------------------------
INSERT INTO fatura (cliente_id, pedido_id, numero, data_emissao, data_vencimento, data_pagamento, valor, status)
SELECT id, NULL, 'FAT-NORM-001', '2026-05-20', '2026-06-19', '2026-06-15', 350.00, 'PAGA'
FROM cliente WHERE nome = 'Maria Silva Santos';

-- ---------------------------------------------------------------------
-- 3) FATURAS — cenário de cliente em atraso SEM acordo (deve bloquear)
-- ---------------------------------------------------------------------
-- "Cliente Inativo PF" já existe nos seus dados de fidelidade — reaproveite
-- aqui para reforçar a narrativa: o mesmo cliente "problemático" aparece
-- em risco tanto no engajamento quanto no financeiro.
INSERT INTO fatura (cliente_id, pedido_id, numero, data_emissao, data_vencimento, data_pagamento, valor, status)
SELECT id, NULL, 'FAT-ATRASO-001', '2026-03-10', '2026-04-09', NULL, 890.00, 'VENCIDA'
FROM cliente WHERE nome = 'Cliente Inativo PF';

-- Gancho de demonstração da RN1: este cliente está com fatura vencida há
-- mais de 30 dias (limite da política vigente) e SEM acordo ativo. Ao
-- chamar a avaliação de crédito desse cliente (ex.: nova venda no PDV), o
-- sistema deve bloquear automaticamente e inativar o cliente.

-- ---------------------------------------------------------------------
-- 4) ACORDO DE PAGAMENTO ATIVO E EM DIA (RN1 — protege o cliente do bloqueio)
-- ---------------------------------------------------------------------
INSERT INTO acordo_pagamento (cliente_id, data_acordo, data_inicio, data_fim, valor_total, status)
SELECT id, '2026-05-01', '2026-05-01', '2026-08-01', 1200.00, 'ATIVO'
FROM cliente WHERE nome = 'João Pedro Costa';

-- Parcelas do acordo acima — todas dentro do prazo (nenhuma vencida ainda)
INSERT INTO fatura (cliente_id, acordo_pagamento_id, pedido_id, numero, data_emissao, data_vencimento, data_pagamento, valor, status)
SELECT c.id, a.id, NULL, 'FAT-ACORDO-001-P1', '2026-05-01', '2026-06-01', '2026-05-28', 400.00, 'PAGA'
FROM cliente c JOIN acordo_pagamento a ON a.cliente_id = c.id
WHERE c.nome = 'João Pedro Costa' AND a.valor_total = 1200.00;

INSERT INTO fatura (cliente_id, acordo_pagamento_id, pedido_id, numero, data_emissao, data_vencimento, data_pagamento, valor, status)
SELECT c.id, a.id, NULL, 'FAT-ACORDO-001-P2', '2026-05-01', '2026-07-01', NULL, 400.00, 'PENDENTE'
FROM cliente c JOIN acordo_pagamento a ON a.cliente_id = c.id
WHERE c.nome = 'João Pedro Costa' AND a.valor_total = 1200.00;

INSERT INTO fatura (cliente_id, acordo_pagamento_id, pedido_id, numero, data_emissao, data_vencimento, data_pagamento, valor, status)
SELECT c.id, a.id, NULL, 'FAT-ACORDO-001-P3', '2026-05-01', '2026-08-01', NULL, 400.00, 'PENDENTE'
FROM cliente c JOIN acordo_pagamento a ON a.cliente_id = c.id
WHERE c.nome = 'João Pedro Costa' AND a.valor_total = 1200.00;

-- Gancho de demonstração: 'João Pedro Costa' tem fatura vencida no passado
-- distante (poderia ter outra fatura vencida fora do acordo), mas o acordo
-- ATIVO com parcelas em dia protege ele do bloqueio. Avaliar a venda dele
-- deve retornar "liberada".

-- ---------------------------------------------------------------------
-- 5) ACORDO COM PARCELA EM ATRASO (RN2 — perde a proteção "EmAcordo")
-- ---------------------------------------------------------------------
INSERT INTO acordo_pagamento (cliente_id, data_acordo, data_inicio, data_fim, valor_total, status)
SELECT id, '2026-02-01', '2026-02-01', '2026-05-01', 900.00, 'ATIVO'
FROM cliente WHERE nome = 'Ricardo Fernandes';

-- Parcela 1: paga
INSERT INTO fatura (cliente_id, acordo_pagamento_id, pedido_id, numero, data_emissao, data_vencimento, data_pagamento, valor, status)
SELECT c.id, a.id, NULL, 'FAT-ACORDO-002-P1', '2026-02-01', '2026-03-01', '2026-02-28', 300.00, 'PAGA'
FROM cliente c JOIN acordo_pagamento a ON a.cliente_id = c.id
WHERE c.nome = 'Ricardo Fernandes' AND a.valor_total = 900.00;

-- Parcela 2: VENCIDA E NÃO PAGA — isso é o que quebra a proteção (RN2)
INSERT INTO fatura (cliente_id, acordo_pagamento_id, pedido_id, numero, data_emissao, data_vencimento, data_pagamento, valor, status)
SELECT c.id, a.id, NULL, 'FAT-ACORDO-002-P2', '2026-02-01', '2026-04-01', NULL, 300.00, 'VENCIDA'
FROM cliente c JOIN acordo_pagamento a ON a.cliente_id = c.id
WHERE c.nome = 'Ricardo Fernandes' AND a.valor_total = 900.00;

-- Parcela 3: ainda não venceu
INSERT INTO fatura (cliente_id, acordo_pagamento_id, pedido_id, numero, data_emissao, data_vencimento, data_pagamento, valor, status)
SELECT c.id, a.id, NULL, 'FAT-ACORDO-002-P3', '2026-02-01', '2026-05-01', NULL, 300.00, 'PENDENTE'
FROM cliente c JOIN acordo_pagamento a ON a.cliente_id = c.id
WHERE c.nome = 'Ricardo Fernandes' AND a.valor_total = 900.00;

-- Gancho de demonstração da RN2: ao avaliar a venda de 'Ricardo Fernandes',
-- o sistema deve detectar a parcela 2 vencida, marcar o acordo como
-- "QUEBRADO" automaticamente, e tratar o cliente como inadimplente comum
-- (sujeito a bloqueio se a fatura vencida ultrapassar o limite de dias).

-- ---------------------------------------------------------------------
-- 6) HISTÓRICO DE COBRANÇA (RN4 — imutável, com correção vinculada)
-- ---------------------------------------------------------------------
INSERT INTO historico_cobranca (cliente_id, fatura_id, data_contato, tipo_contato, responsavel, descricao)
SELECT c.id, f.id, '2026-04-15 10:30:00', 'LIGACAO', 'Admin', 'Cliente contatado, prometeu pagamento ate 20/04.'
FROM cliente c JOIN fatura f ON f.cliente_id = c.id
WHERE c.nome = 'Cliente Inativo PF' AND f.numero = 'FAT-ATRASO-001';

INSERT INTO historico_cobranca (cliente_id, fatura_id, data_contato, tipo_contato, responsavel, descricao)
SELECT c.id, f.id, '2026-04-25 14:00:00', 'EMAIL', 'Admin', 'Promessa nao cumprida. Segundo contato enviado por email.'
FROM cliente c JOIN fatura f ON f.cliente_id = c.id
WHERE c.nome = 'Cliente Inativo PF' AND f.numero = 'FAT-ATRASO-001';

-- Gancho de demonstração da RN4: tente, pela própria aplicação (não direto
-- no banco), editar ou excluir um desses registros de histórico. A entidade
-- HistoricoCobrancaJpaEntity possui @PreUpdate/@PreRemove que lança
-- IllegalStateException — mostre esse erro controlado ao examinador. Depois,
-- mostre o caminho correto: criar um novo registro de histórico do tipo
-- "CORRECAO" referenciando o registroOriginalId do registro acima pela
-- tela /cobrancas/historicos (campo "registroOriginalId").

-- ---------------------------------------------------------------------
-- Conferência rápida pós-seed
-- ---------------------------------------------------------------------
SELECT 'politica_credito' AS tabela, COUNT(*) AS total FROM politica_credito
UNION ALL
SELECT 'fatura', COUNT(*) FROM fatura
UNION ALL
SELECT 'acordo_pagamento', COUNT(*) FROM acordo_pagamento
UNION ALL
SELECT 'historico_cobranca', COUNT(*) FROM historico_cobranca;