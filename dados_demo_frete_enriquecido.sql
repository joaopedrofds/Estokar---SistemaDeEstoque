-- =====================================================================
-- DADOS DE DEMONSTRAÇÃO ENRIQUECIDOS: Funcionalidade 3 — Cotação e
-- Expedição de Fretes.
--
-- Este script COMPLEMENTA o dados_teste_frete.sql já existente (não o
-- substitui). Ele já garante: 3 transportadoras, 2 faixas de contingência,
-- 1 log de cache recente e o "Cliente Inativo PF" bloqueado para RN3.
--
-- O que falta para a funcionalidade parecer "viva" na demonstração:
--   - Histórico de cotações variado (várias origens: API, CACHE, CONTINGENCIA)
--   - Cotações de mais de um vendedor, para RN4 (rate limit) fazer sentido
--   - Ordens de despacho já geradas, para a tela /frete/despachos não ficar vazia
--   - Um cenário pronto para gerar uma NOVA ordem ao vivo na demonstração
--
-- Pré-requisitos: rode ANTES, nesta ordem:
--   1) dados_teste.sql (clientes, pedidos, usuários)
--   2) dados_teste_cobranca.sql (opcional, mas recomendado para coerência)
--   3) dados_teste_frete.sql (schema básico de frete já existente)
--   4) ESTE SCRIPT
-- =====================================================================

USE studiomuda;

-- ---------------------------------------------------------------------
-- 1) HISTÓRICO DE COTAÇÕES — múltiplos vendedores, múltiplas origens
-- ---------------------------------------------------------------------
-- Usa subqueries por critério de negócio (não IDs fixos) para ser seguro
-- de rodar independente da ordem real de inserção no seu banco.

-- Cotação via API, vendedor "operador", CEP fora da faixa de contingência
INSERT INTO log_cotacao (usuario_id, cliente_id, pedido_id, cep_destino, peso, comprimento, largura, altura, hash_parametros, valor_cotado, origem_resultado, transportadora_id, data_cotacao)
SELECT u.id, c.id, NULL, '01310100', 3.500, 30.00, 20.00, 15.00,
       SHA2('01310100|3.5|30|20|15', 256), 32.50, 'API', t.id, NOW() - INTERVAL 3 DAY
FROM usuario_acesso u, cliente c, transportadora t
WHERE u.username = 'operador'
  AND c.nome = 'Maria Silva Santos'
  AND t.nome = 'Rota Certa Express'
LIMIT 1;

-- Cotação via CONTINGÊNCIA (simula falha de API), vendedor "gerente"
INSERT INTO log_cotacao (usuario_id, cliente_id, pedido_id, cep_destino, peso, comprimento, largura, altura, hash_parametros, valor_cotado, origem_resultado, transportadora_id, data_cotacao)
SELECT u.id, c.id, NULL, '61500000', 5.000, 25.00, 25.00, 20.00,
       SHA2('61500000|5|25|25|20', 256), 39.90, 'CONTINGENCIA', t.id, NOW() - INTERVAL 2 DAY
FROM usuario_acesso u, cliente c, transportadora t
WHERE u.username = 'gerente'
  AND c.nome = 'Carlos Roberto Lima'
  AND t.nome = 'Nordeste Cargo'
LIMIT 1;

-- Cotação via API, vendedor "admin", outro CEP/peso
INSERT INTO log_cotacao (usuario_id, cliente_id, pedido_id, cep_destino, peso, comprimento, largura, altura, hash_parametros, valor_cotado, origem_resultado, transportadora_id, data_cotacao)
SELECT u.id, c.id, NULL, '04038001', 1.200, 15.00, 10.00, 8.00,
       SHA2('04038001|1.2|15|10|8', 256), 18.70, 'API', t.id, NOW() - INTERVAL 1 DAY
FROM usuario_acesso u, cliente c, transportadora t
WHERE u.username = 'admin'
  AND c.nome = 'Beatriz Torres Oliveira'
  AND t.nome = 'Estokar Log'
LIMIT 1;

-- Mais 3 cotações do "operador" na última hora, para deixar a contagem de
-- RN4 (rate limit, limite de 50/hora) com um número não-zero e plausível
-- de mostrar ao examinador (ex.: "47 de 50 usadas" é mais didático que 0).
INSERT INTO log_cotacao (usuario_id, cliente_id, pedido_id, cep_destino, peso, comprimento, largura, altura, hash_parametros, valor_cotado, origem_resultado, transportadora_id, data_cotacao)
SELECT u.id, c.id, NULL, '12900000', 2.000, 18.00, 12.00, 10.00,
       SHA2(CONCAT('12900000|2|18|12|10|seq', n.seq), 256), 22.00 + n.seq, 'API', t.id, NOW() - INTERVAL (n.seq * 5) MINUTE
FROM usuario_acesso u, cliente c, transportadora t,
     (SELECT 1 AS seq UNION SELECT 2 UNION SELECT 3) n
WHERE u.username = 'operador'
  AND c.nome = 'Fernanda Almeida'
  AND t.nome = 'Rota Certa Express';

-- ---------------------------------------------------------------------
-- 2) ORDENS DE DESPACHO JÁ GERADAS — para a tela /frete/despachos mostrar
--    rastreio real, com status variados.
-- ---------------------------------------------------------------------
-- Cada ordem PRECISA referenciar um log_cotacao cujo pedido_id seja NULL
-- ou bata com o pedido da ordem (regra real do DespachoService). Por isso,
-- cada bloco abaixo cria o log_cotacao JÁ vinculado ao pedido_id certo,
-- e na sequência cria a ordem_despacho usando esse mesmo log.

-- Cenário A: pedido pago e entregue, despacho já ENTREGUE (fluxo completo)
INSERT INTO log_cotacao (usuario_id, cliente_id, pedido_id, cep_destino, peso, comprimento, largura, altura, hash_parametros, valor_cotado, origem_resultado, transportadora_id, data_cotacao)
SELECT u.id, p.cliente_id, p.id, '01310100', 4.000, 28.00, 18.00, 12.00,
       SHA2(CONCAT('despacho-a-', p.id), 256), 28.50, 'API', t.id, p.data_requisicao
FROM pedido p
JOIN usuario_acesso u ON u.username = 'admin'
JOIN transportadora t ON t.nome = 'Rota Certa Express'
WHERE p.status_pagamento = 'PAGO' AND p.data_entrega IS NOT NULL
ORDER BY p.id ASC
LIMIT 1;

INSERT INTO ordem_despacho (pedido_id, transportadora_id, log_cotacao_id, valor_frete, status, codigo_rastreio, data_despacho, data_entrega_prevista, data_entrega_realizada)
SELECT lc.pedido_id, lc.transportadora_id, lc.id, lc.valor_cotado, 'ENTREGUE',
       CONCAT('EST-', lc.pedido_id, '-DEMO-A'), lc.data_cotacao, lc.data_cotacao + INTERVAL 4 DAY, lc.data_cotacao + INTERVAL 3 DAY
FROM log_cotacao lc
WHERE lc.hash_parametros = SHA2(CONCAT('despacho-a-', lc.pedido_id), 256)
  AND NOT EXISTS (SELECT 1 FROM ordem_despacho od WHERE od.pedido_id = lc.pedido_id)
LIMIT 1;

-- Cenário B: segundo pedido pago, despacho EM_TRANSITO (ainda a caminho)
INSERT INTO log_cotacao (usuario_id, cliente_id, pedido_id, cep_destino, peso, comprimento, largura, altura, hash_parametros, valor_cotado, origem_resultado, transportadora_id, data_cotacao)
SELECT u.id, p.cliente_id, p.id, '61500000', 6.500, 35.00, 22.00, 18.00,
       SHA2(CONCAT('despacho-b-', p.id), 256), 45.00, 'API', t.id, p.data_requisicao
FROM pedido p
JOIN usuario_acesso u ON u.username = 'gerente'
JOIN transportadora t ON t.nome = 'Nordeste Cargo'
WHERE p.status_pagamento = 'PAGO' AND p.data_entrega IS NOT NULL
ORDER BY p.id ASC
LIMIT 1 OFFSET 1;

INSERT INTO ordem_despacho (pedido_id, transportadora_id, log_cotacao_id, valor_frete, status, codigo_rastreio, data_despacho, data_entrega_prevista, data_entrega_realizada)
SELECT lc.pedido_id, lc.transportadora_id, lc.id, lc.valor_cotado, 'EM_TRANSITO',
       CONCAT('EST-', lc.pedido_id, '-DEMO-B'), NOW() - INTERVAL 1 DAY, NOW() + INTERVAL 5 DAY, NULL
FROM log_cotacao lc
WHERE lc.hash_parametros = SHA2(CONCAT('despacho-b-', lc.pedido_id), 256)
  AND NOT EXISTS (SELECT 1 FROM ordem_despacho od WHERE od.pedido_id = lc.pedido_id)
LIMIT 1;

-- Cenário C: terceiro pedido pago, despacho ainda PENDENTE (acabou de ser gerado)
INSERT INTO log_cotacao (usuario_id, cliente_id, pedido_id, cep_destino, peso, comprimento, largura, altura, hash_parametros, valor_cotado, origem_resultado, transportadora_id, data_cotacao)
SELECT u.id, p.cliente_id, p.id, '60000000', 1.800, 16.00, 11.00, 9.00,
       SHA2(CONCAT('despacho-c-', p.id), 256), 19.90, 'CACHE', t.id, p.data_requisicao
FROM pedido p
JOIN usuario_acesso u ON u.username = 'operador'
JOIN transportadora t ON t.nome = 'Estokar Log'
WHERE p.status_pagamento = 'PAGO' AND p.data_entrega IS NOT NULL
ORDER BY p.id ASC
LIMIT 1 OFFSET 2;

INSERT INTO ordem_despacho (pedido_id, transportadora_id, log_cotacao_id, valor_frete, status, codigo_rastreio, data_despacho, data_entrega_prevista, data_entrega_realizada)
SELECT lc.pedido_id, lc.transportadora_id, lc.id, lc.valor_cotado, 'PENDENTE',
       CONCAT('EST-', lc.pedido_id, '-DEMO-C'), NOW(), NOW() + INTERVAL 3 DAY, NULL
FROM log_cotacao lc
WHERE lc.hash_parametros = SHA2(CONCAT('despacho-c-', lc.pedido_id), 256)
  AND NOT EXISTS (SELECT 1 FROM ordem_despacho od WHERE od.pedido_id = lc.pedido_id)
LIMIT 1;

-- ---------------------------------------------------------------------
-- 3) CENÁRIO PRONTO PARA DEMONSTRAÇÃO AO VIVO (RN3 — bloqueio de despacho)
-- ---------------------------------------------------------------------
-- Gera uma cotação válida e já paga para o "Cliente Inativo PF" (que o
-- dados_teste_frete.sql já marca como ativo=FALSE). Isso permite ao
-- examinador tentar gerar uma Ordem de Despacho pela TELA para esse
-- cliente e ver o bloqueio da RN3 acontecer ao vivo, em vez de só ler
-- código. Não geramos a ordem aqui de propósito — o bloqueio só é visível
-- se a tentativa for feita pela própria aplicação.
INSERT INTO log_cotacao (usuario_id, cliente_id, pedido_id, cep_destino, peso, comprimento, largura, altura, hash_parametros, valor_cotado, origem_resultado, transportadora_id, data_cotacao)
SELECT u.id, c.id, p.id, '60000000', 2.500, 20.00, 15.00, 12.00,
       SHA2(CONCAT('bloqueio-demo-', p.id), 256), 24.90, 'API', t.id, NOW() - INTERVAL 10 MINUTE
FROM usuario_acesso u, cliente c, transportadora t,
     (SELECT pe.id, pe.cliente_id FROM pedido pe
      JOIN cliente cl ON cl.id = pe.cliente_id
      WHERE cl.nome = 'Cliente Inativo PF'
      LIMIT 1) p
WHERE u.username = 'operador'
  AND c.nome = 'Cliente Inativo PF'
  AND t.nome = 'Rota Certa Express'
  AND p.id IS NOT NULL;

-- ---------------------------------------------------------------------
-- Conferência rápida pós-seed
-- ---------------------------------------------------------------------
SELECT 'transportadora' AS tabela, COUNT(*) AS total FROM transportadora
UNION ALL
SELECT 'tabela_contingencia', COUNT(*) FROM tabela_contingencia
UNION ALL
SELECT 'log_cotacao', COUNT(*) FROM log_cotacao
UNION ALL
SELECT 'ordem_despacho', COUNT(*) FROM ordem_despacho;

-- Conferência por status de despacho (deve mostrar PENDENTE, EM_TRANSITO, ENTREGUE)
SELECT status, COUNT(*) AS total FROM ordem_despacho GROUP BY status;

-- Conferência de cotações por vendedor na última hora (base visual para RN4)
SELECT u.username, COUNT(*) AS cotacoes_ultima_hora
FROM log_cotacao lc
JOIN usuario_acesso u ON u.id = lc.usuario_id
WHERE lc.data_cotacao >= NOW() - INTERVAL 60 MINUTE
GROUP BY u.username;