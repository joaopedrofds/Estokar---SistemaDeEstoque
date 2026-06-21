USE studiomuda;

SET SQL_SAFE_UPDATES = 0;
DELETE FROM item_pedido;
DELETE FROM pedido;
DELETE FROM movimentacao_estoque;
DELETE FROM historico_ajuste_estoque;
DELETE FROM solicitacao_ajuste_estoque;
DELETE FROM item_ordem_compra;
DELETE FROM ordem_compra;
DELETE FROM parametro_estoque;
DELETE FROM fornecedor;
DELETE FROM agendamento_remessa;
DELETE FROM calendario_excecao;
DELETE FROM distribuidora;
DELETE FROM doca;
DELETE FROM historico_estoque;
DELETE FROM historico_cliente;
DELETE FROM historico_funcionario;
DELETE FROM precificacao_componente;
DELETE FROM precificacao_simulacao;
DELETE FROM precificacao_politica;
DELETE FROM precificacao_parametro;
DELETE FROM historico_preco;
DELETE FROM log_acesso;
DELETE FROM usuario_perfil;
DELETE FROM permissao_perfil;
DELETE FROM usuario_acesso;
DELETE FROM perfil_acesso;
DELETE FROM produto;
DELETE FROM cliente;
DELETE FROM funcionario;
DELETE FROM cupom;

ALTER TABLE funcionario AUTO_INCREMENT = 1;
ALTER TABLE cliente AUTO_INCREMENT = 1;
ALTER TABLE produto AUTO_INCREMENT = 1;
ALTER TABLE precificacao_parametro AUTO_INCREMENT = 1;
ALTER TABLE precificacao_politica AUTO_INCREMENT = 1;
ALTER TABLE precificacao_simulacao AUTO_INCREMENT = 1;
ALTER TABLE precificacao_componente AUTO_INCREMENT = 1;
ALTER TABLE historico_preco AUTO_INCREMENT = 1;
ALTER TABLE fornecedor AUTO_INCREMENT = 1;
ALTER TABLE parametro_estoque AUTO_INCREMENT = 1;
ALTER TABLE ordem_compra AUTO_INCREMENT = 1;
ALTER TABLE item_ordem_compra AUTO_INCREMENT = 1;
ALTER TABLE doca AUTO_INCREMENT = 1;
ALTER TABLE distribuidora AUTO_INCREMENT = 1;
ALTER TABLE calendario_excecao AUTO_INCREMENT = 1;
ALTER TABLE agendamento_remessa AUTO_INCREMENT = 1;
ALTER TABLE solicitacao_ajuste_estoque AUTO_INCREMENT = 1;
ALTER TABLE historico_ajuste_estoque AUTO_INCREMENT = 1;
ALTER TABLE cupom AUTO_INCREMENT = 1;
ALTER TABLE pedido AUTO_INCREMENT = 1;
ALTER TABLE item_pedido AUTO_INCREMENT = 1;
ALTER TABLE movimentacao_estoque AUTO_INCREMENT = 1;
ALTER TABLE perfil_acesso AUTO_INCREMENT = 1;
ALTER TABLE usuario_acesso AUTO_INCREMENT = 1;
ALTER TABLE permissao_perfil AUTO_INCREMENT = 1;
ALTER TABLE log_acesso AUTO_INCREMENT = 1;

INSERT INTO perfil_acesso (nome, descricao, ativo) VALUES
('ADMINISTRADOR', 'Perfil responsável por governança completa de acessos e cadastros.', TRUE),
('GERENTE_OPERACIONAL', 'Perfil com operação supervisionada e permissões de aprovação.', TRUE),
('OPERADOR_VENDEDOR', 'Perfil de execução diária com permissões restritas.', TRUE);

INSERT INTO usuario_acesso (username, nome, senha, ativo) VALUES
('admin', 'Administrador do Sistema', '{bcrypt}$2y$10$g0LlknzvHwnbcxNZUqwqNe5Cf7akFs6HZLqHA8jcKf1qqZznkUQGW', TRUE),
('gerente', 'Gerente Operacional', '{bcrypt}$2y$10$hJCAgOl0r.UG62AIDEQ8N.c5mMAr8sOU9fL6Ozm.5auqHYlvXCaD.', TRUE),
('operador', 'Operador de Vendas', '{bcrypt}$2y$10$zboD1cTxc2.94QKCP0v9V.We12Guw.2rug5mDF0.SmKT.NmVtyMyi', TRUE);

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
    SELECT 'SUPRIMENTO' UNION ALL
    SELECT 'REMESSA' UNION ALL
    SELECT 'CLIENTE' UNION ALL
    SELECT 'FUNCIONARIO' UNION ALL
    SELECT 'DASHBOARD' UNION ALL
    SELECT 'KPI' UNION ALL
    SELECT 'DEVOLUCAO' UNION ALL
    SELECT 'FINANCEIRO' UNION ALL
    SELECT 'FRETE' UNION ALL
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
(2, 'AJUSTE_ESTOQUE', 'LEITURA', TRUE),
(2, 'AJUSTE_ESTOQUE', 'ESCRITA', TRUE),
(2, 'AJUSTE_ESTOQUE', 'APROVACAO', TRUE),
(2, 'DEVOLUCAO', 'LEITURA', TRUE),
(2, 'DEVOLUCAO', 'ESCRITA', TRUE),
(2, 'DEVOLUCAO', 'APROVACAO', TRUE),
(2, 'FINANCEIRO', 'LEITURA', TRUE),
(2, 'FINANCEIRO', 'ESCRITA', TRUE),
(2, 'FRETE', 'LEITURA', TRUE),
(2, 'FRETE', 'ESCRITA', TRUE),
(2, 'FRETE', 'APROVACAO', TRUE),
(2, 'API', 'LEITURA', TRUE),
(2, 'API', 'ESCRITA', TRUE),
(2, 'HOME', 'LEITURA', TRUE);

INSERT INTO permissao_perfil (perfil_id, recurso, operacao, permitido) VALUES
(3, 'PRODUTO', 'LEITURA', TRUE),
(3, 'CUPOM', 'LEITURA', TRUE),
(3, 'PEDIDO', 'LEITURA', TRUE),
(3, 'PEDIDO', 'ESCRITA', TRUE),
(3, 'ESTOQUE', 'LEITURA', TRUE),
(3, 'AJUSTE_ESTOQUE', 'LEITURA', TRUE),
(3, 'AJUSTE_ESTOQUE', 'ESCRITA', TRUE),
(3, 'DEVOLUCAO', 'LEITURA', TRUE),
(3, 'DEVOLUCAO', 'ESCRITA', TRUE),
(3, 'FRETE', 'LEITURA', TRUE),
(3, 'FRETE', 'ESCRITA', TRUE),
(3, 'CLIENTE', 'LEITURA', TRUE),
(3, 'CLIENTE', 'ESCRITA', TRUE),
(3, 'DASHBOARD', 'LEITURA', TRUE),
(3, 'API', 'LEITURA', TRUE),
(3, 'HOME', 'LEITURA', TRUE);

INSERT INTO produto (nome, descricao, tipo, quantidade, valor) VALUES
('Adubo Orgânico Premium', 'Adubo para plantas 5kg super concentrado', 'Materiais', 120, 28.90),
('Semente de Grama Esmeralda', 'Semente premium para grama esmeralda', 'Materiais', 300, 18.50),
('Terra Vegetal', 'Terra vegetal enriquecida 20kg', 'Materiais', 80, 35.00),
('Substrato para Vasos', 'Substrato especial para vasos e jardineiras', 'Materiais', 60, 22.90),
('Pedra Brita Decorativa', 'Pedra brita colorida para decoração', 'Materiais', 45, 42.00),
('Piso Drenante Jardim', 'Piso modular drenante para áreas externas', 'Materiais', 150, 65.00),
('Manta Geotêxtil', 'Manta para controle de ervas daninhas', 'Materiais', 25, 89.90),
('Casca de Pinus', 'Casca decorativa para cobertura do solo', 'Materiais', 40, 32.50),

('Tesoura de Poda Profissional', 'Tesoura de poda bypass 8 polegadas', 'Ferramentas', 35, 125.00),
('Kit Jardinagem Completo', 'Kit com pás, ancinhos, regadores e luvas', 'Ferramentas', 50, 95.00),
('Pulverizador Manual', 'Pulverizador manual 2 litros', 'Ferramentas', 28, 45.90),
('Enxada de Jardinagem', 'Enxada pequena para canteiros', 'Ferramentas', 42, 38.00),
('Régua de Plantio', 'Régua graduada para medição de áreas', 'Ferramentas', 60, 28.90),
('Borrifador Spray', 'Borrifador spray para plantas', 'Ferramentas', 80, 15.90),
('Ancinho de Folhas', 'Ancinho específico para coleta de folhas', 'Ferramentas', 30, 52.00),

('Cortador de Grama Elétrico', 'Cortador elétrico 1500W com recolhedor', 'Equipamentos', 18, 450.00),
('Aspirador de Folhas', 'Aspirador/soprador de folhas 2000W', 'Equipamentos', 12, 320.00),
('Motosserra de Poda', 'Motosserra elétrica para poda de galhos', 'Equipamentos', 8, 280.00),
('Roçadeira Elétrica', 'Roçadeira elétrica 1200W profissional', 'Equipamentos', 15, 390.00),
('Mangueira de Jardim', 'Mangueira flexível 30 metros', 'Equipamentos', 25, 85.00),
('Bancada Jardinagem Móvel', 'Bancada com rodas para trabalho', 'Equipamentos', 10, 380.00),

('Iluminação LED Solar', 'Lâmpada LED solar para jardim', 'Acessórios', 75, 68.00),
('Vaso Cerâmica Grande', 'Vaso decorativo de cerâmica 40cm', 'Acessórios', 30, 95.00),
('Timer para Irrigação', 'Timer automático para sistema de irrigação', 'Acessórios', 20, 150.00),
('Suporte para Plantas', 'Suporte metálico ajustável para plantas', 'Acessórios', 55, 42.90),
('Fertilizante Líquido', 'Fertilizante concentrado NPK 500ml', 'Acessórios', 90, 24.90),

('Herbicida Seletivo', 'Herbicida para controle de ervas', 'Materiais', 5, 85.00),
('Bomba de Irrigação', 'Bomba submersa para irrigação', 'Equipamentos', 3, 680.00),
('Sensor de Umidade', 'Sensor digital de umidade do solo', 'Acessórios', 2, 220.00),

('Adubo Especial Orquídeas', 'Adubo específico para orquídeas', 'Materiais', 0, 35.90),
('Cortador de Cerca Viva', 'Cortador elétrico para cerca viva', 'Equipamentos', 0, 590.00);

UPDATE produto SET custo = 15.80 WHERE id = 1;
UPDATE produto SET custo = 72.00 WHERE id = 9;
UPDATE produto SET custo = 390.00 WHERE id = 27;
UPDATE produto SET custo = 132.00 WHERE id = 28;

INSERT INTO precificacao_parametro
(margem_minima_global, desconto_maximo_global, margem_padrao_lucro, imposto_padrao_percentual, despesa_operacional_padrao_percentual, atualizado_em) VALUES
(30.00, 18.00, 45.00, 8.50, 12.00, NOW());

INSERT INTO precificacao_politica
(produto_id, margem_lucro_desejada, aliquota_impostos, percentual_despesas_operacionais, desconto_maximo_permitido, ativa, observacao, criado_em, atualizado_em) VALUES
(1, 42.00, 8.50, 11.00, 12.00, TRUE, 'Produto de alto giro: margem competitiva e desconto moderado.', NOW(), NOW()),
(9, 48.00, 9.25, 13.50, 10.00, TRUE, 'Ferramenta profissional: margem maior e desconto controlado.', NOW(), NOW()),
(27, 36.00, 12.00, 15.00, 6.00, TRUE, 'Equipamento de maior custo: proteger margem e limitar desconto.', NOW(), NOW()),
(25, 40.00, 7.50, 10.00, 14.00, TRUE, 'Acessório de recorrência alta com margem comercial equilibrada.', NOW(), NOW());

INSERT INTO precificacao_simulacao
(id, produto_id, produto_nome, preco_atual, custo_compra, valor_impostos, valor_despesas_operacionais, custo_total, preco_sugerido, preco_minimo_permitido, margem_lucro_desejada, margem_minima_global, margem_real, desconto_maximo_solicitado, desconto_maximo_efetivo, status, justificativa, usuario_responsavel, aplicado, data_simulacao, data_aplicacao) VALUES
(1, 1, 'Adubo Orgânico Premium', 28.90, 15.80, 1.34, 1.74, 18.88, 32.55, 26.97, 42.00, 30.00, 42.00, 12.00, 12.00, 'APROVADO', 'Preço aprovado: custo total, impostos e despesas foram cobertos pela margem desejada.', 'sistema.demo', FALSE, NOW(), NULL),
(2, 9, 'Tesoura de Poda Profissional', 125.00, 72.00, 6.66, 9.72, 88.38, 169.96, 126.26, 48.00, 30.00, 48.00, 10.00, 10.00, 'APROVADO', 'Preço aprovado para produto profissional com margem acima do mínimo.', 'sistema.demo', FALSE, NOW(), NULL),
(3, 27, 'Bomba de Irrigação', 680.00, 390.00, 46.80, 58.50, 495.30, 773.91, 707.57, 36.00, 30.00, 36.00, 6.00, 6.00, 'APROVADO', 'Preço aprovado com margem protegida para equipamento de alto custo.', 'sistema.demo', FALSE, NOW(), NULL);

INSERT INTO precificacao_componente
(simulacao_id, nome, tipo, percentual, valor, base_calculo, ordem) VALUES
(1, 'Custo de compra do produto', 'CUSTO_COMPRA', 0.00, 15.80, 15.80, 1),
(1, 'Impostos sobre compra/venda', 'IMPOSTO', 8.50, 1.34, 15.80, 2),
(1, 'Rateio de despesas operacionais', 'DESPESA_OPERACIONAL', 11.00, 1.74, 15.80, 3),
(2, 'Custo de compra do produto', 'CUSTO_COMPRA', 0.00, 72.00, 72.00, 1),
(2, 'Impostos sobre compra/venda', 'IMPOSTO', 9.25, 6.66, 72.00, 2),
(2, 'Rateio de despesas operacionais', 'DESPESA_OPERACIONAL', 13.50, 9.72, 72.00, 3),
(3, 'Custo de compra do produto', 'CUSTO_COMPRA', 0.00, 390.00, 390.00, 1),
(3, 'Impostos sobre compra/venda', 'IMPOSTO', 12.00, 46.80, 390.00, 2),
(3, 'Rateio de despesas operacionais', 'DESPESA_OPERACIONAL', 15.00, 58.50, 390.00, 3);

INSERT INTO historico_preco
(produto_id, preco_anterior, preco_novo, percentual_variacao, usuario_responsavel, data_alteracao) VALUES
(1, 27.50, 28.90, 5.09, 'sistema.demo', NOW()),
(9, 119.90, 125.00, 4.25, 'sistema.demo', NOW());

INSERT INTO fornecedor (nome, lead_time_dias, ativo) VALUES
('Viveiro Central Studio Muda', 4, true),
('Distribuidora Jardim Pro', 7, true),
('Equipamentos Verde Sul', 10, true);

INSERT INTO parametro_estoque (produto_id, fornecedor_id, margem_seguranca) VALUES
(27, 2, 8),
(28, 3, 3),
(29, 2, 5),
(30, 1, 6),
(31, 3, 2);

INSERT INTO produto (id, nome, descricao, tipo, quantidade, valor) VALUES
(32, 'Muda de Palmeira Imperador', 'Muda ornamental de alto giro para paisagismo', 'Materiais', 4, 42.00),
(33, 'Substrato Profissional 25kg', 'Substrato premium para replantio e mudas', 'Materiais', 2, 31.50),
(34, 'Tubo de Irrigacao PVC 20mm', 'Tubo flexivel para sistemas de irrigacao', 'Acessorios', 6, 18.90);

INSERT INTO fornecedor (id, nome, lead_time_dias, ativo) VALUES
(4, 'Fornecedor Demo Suprimentos', 5, true);

INSERT INTO parametro_estoque (produto_id, fornecedor_id, margem_seguranca) VALUES
(32, 4, 7),
(33, 4, 5),
(34, 4, 4);

INSERT INTO ordem_compra (id, codigo_ordem, fornecedor_id, status, valor_total, data_criacao, data_aprovacao) VALUES
(1, 'SUP-DEMO-0001', 4, 'RASCUNHO', 210.00, NOW(), NULL);

INSERT INTO item_ordem_compra (id, ordem_compra_id, produto_id, quantidade, valor_unitario) VALUES
(1, 1, 32, 5, 42.00);

INSERT INTO doca (nome, capacidade_paletes_diaria, ativa) VALUES
('Doca Norte', 18, true),
('Doca Sul', 12, true),
('Doca Express', 8, true);

INSERT INTO distribuidora (nome, nivel_prioridade, ativa) VALUES
('Verde Express', 'ALTA', true),
('Jardim Log', 'MEDIA', true),
('Rota Paisagismo', 'BAIXA', true);

INSERT INTO calendario_excecao (data, motivo, ativa) VALUES
('2026-06-12', 'Manutencao preventiva nas docas', true);

INSERT INTO agendamento_remessa (doca_id, distribuidora_id, data, horario, volume_paletes, status) VALUES
(1, 1, '2026-06-10', '08:00', 6, 'CONFIRMADO'),
(1, 2, '2026-06-10', '10:00', 8, 'CONFIRMADO'),
(2, 3, '2026-06-10', '13:00', 4, 'CONFIRMADO');

INSERT INTO funcionario (nome, cpf, cargo, data_nasc, telefone, cep, rua, numero, bairro, cidade, estado, ativo) VALUES
('Ana Paula Diretor', '12345678901', 'Diretor', '1980-02-15', '(11) 99888-7766', '01234-567', 'Rua dos Jardins', '123', 'Centro', 'São Paulo', 'SP', true),
('Carlos Eduardo Diretor', '23456789012', 'Diretor', '1978-11-08', '(11) 99777-8855', '02345-678', 'Av. Verde', '456', 'Vila Verde', 'São Paulo', 'SP', true),

('Fernanda Silva Gerente', '34567890123', 'Gerente', '1985-06-10', '(11) 99666-5544', '03456-789', 'Rua das Flores', '789', 'Vila Nova', 'São Paulo', 'SP', true),
('Roberto Santos Gerente', '45678901234', 'Gerente', '1982-09-22', '(11) 99555-4433', '04567-890', 'Av. Brasil', '1010', 'Consolação', 'São Paulo', 'SP', true),

('Juliana Costa Vendas', '56789012345', 'Vendedor', '1990-03-30', '(11) 99444-3322', '05678-901', 'Rua das Palmeiras', '234', 'Pinheiros', 'São Paulo', 'SP', true),
('Marcos Oliveira Vendas', '67890123456', 'Vendedor', '1988-12-14', '(11) 99333-2211', '06789-012', 'Av. Paulista', '567', 'Bela Vista', 'São Paulo', 'SP', true),
('Patrícia Almeida Vendas', '78901234567', 'Vendedor', '1992-07-25', '(11) 99222-1100', '07890-123', 'Rua Augusta', '890', 'Cerqueira César', 'São Paulo', 'SP', true),

('João Pedro Estoque', '89012345678', 'Estoquista', '1995-01-18', '(11) 99111-0099', '08901-234', 'Rua do Comércio', '321', 'República', 'São Paulo', 'SP', true),
('Maria José Estoque', '90123456789', 'Estoquista', '1993-04-05', '(11) 99000-9988', '09012-345', 'Av. São João', '654', 'Centro', 'São Paulo', 'SP', true),

('Lucas Silva Auxiliar', '01234567890', 'Auxiliar', '1997-08-12', '(11) 98899-8877', '10123-456', 'Rua da Liberdade', '987', 'Liberdade', 'São Paulo', 'SP', true),
('Camila Santos Auxiliar', '11234567801', 'Auxiliar', '1996-10-30', '(11) 98788-7766', '11234-567', 'Rua Voluntários', '159', 'Santana', 'São Paulo', 'SP', true),

('Pedro Inativo', '22334455667', 'Auxiliar', '1994-05-15', '(11) 98677-6655', '12345-678', 'Rua Teste', '753', 'Vila Test', 'São Paulo', 'SP', false);

INSERT INTO cliente (nome, cpf_cnpj, telefone, email, tipo, ativo, cep, rua, numero, bairro, cidade, estado, dataNascimento) VALUES
('Maria Silva Santos', '11122233344', '(11) 98765-4321', 'maria@email.com', 'PF', true, '01310-100', 'Av. Paulista', '1000', 'Jardim Paulista', 'São Paulo', 'SP', '1985-05-21'),
('Carlos Roberto Lima', '22233344455', '(11) 97654-3210', 'carlos@email.com', 'PF', true, '04038-001', 'Rua Augusta', '2500', 'Moema', 'São Paulo', 'SP', '1978-11-03'),
('Beatriz Torres Oliveira', '33344455566', '(11) 96543-2109', 'beatriz@email.com', 'PF', true, '05422-001', 'Rua Teodoro Sampaio', '300', 'Itaim', 'São Paulo', 'SP', '1990-02-14'),
('João Pedro Costa', '44455566677', '(11) 95432-1098', 'joao@email.com', 'PF', true, '01402-001', 'Rua da Consolação', '1500', 'Consolação', 'São Paulo', 'SP', '1983-08-30'),
('Ana Claudia Rocha', '55566677788', '(11) 94321-0987', 'ana@email.com', 'PF', true, '01310-200', 'Rua Haddock Lobo', '800', 'Cerqueira César', 'São Paulo', 'SP', '1992-12-08'),

('Ricardo Fernandes', '66677788899', '(11) 93210-9876', 'ricardo@email.com', 'PF', true, '13100-000', 'Rua Central', '456', 'Centro', 'Campinas', 'SP', '1987-04-15'),
('Fernanda Almeida', '77788899900', '(11) 92109-8765', 'fernanda@email.com', 'PF', true, '12900-000', 'Av. Brasil', '789', 'Vila Nova', 'Bragança Paulista', 'SP', '1989-09-22'),
('Paulo Santos Junior', '88899900011', '(11) 91098-7654', 'paulo@email.com', 'PF', true, '18100-000', 'Rua das Flores', '123', 'Jardim América', 'Sorocaba', 'SP', '1975-06-12'),

('Luciana Martins', '99900011122', '(21) 98765-4321', 'luciana@email.com', 'PF', true, '22071-900', 'Av. Atlântica', '2000', 'Copacabana', 'Rio de Janeiro', 'RJ', '1984-01-25'),
('Roberto Silva Neto', '00011122233', '(31) 97654-3210', 'roberto@email.com', 'PF', true, '30112-000', 'Rua da Bahia', '1500', 'Centro', 'Belo Horizonte', 'MG', '1981-07-18'),

('Paisagismo Premium LTDA', '12345678000190', '(11) 3322-1100', 'contato@paisagismopremium.com', 'PJ', true, '04543-001', 'Av. Faria Lima', '3000', 'Itaim Bibi', 'São Paulo', 'SP', NULL),
('Jardins & Cia Empreendimentos', '23456789000101', '(11) 3311-2200', 'vendas@jardinsecia.com', 'PJ', true, '01310-915', 'Av. Paulista', '1500', 'Bela Vista', 'São Paulo', 'SP', NULL),
('Verde Vida Paisagismo S/A', '34567890000112', '(11) 3300-3300', 'comercial@verdevida.com', 'PJ', true, '04702-000', 'Av. Santo Amaro', '5000', 'Brooklin', 'São Paulo', 'SP', NULL),

('Construções Floridas LTDA', '45678901000123', '(11) 3299-4400', 'obras@construfloridas.com', 'PJ', true, '03031-000', 'Rua do Gasômetro', '800', 'Brás', 'São Paulo', 'SP', NULL),
('Residencial Garden', '56789012000134', '(11) 3288-5500', 'contato@residencialgarden.com', 'PJ', true, '02033-000', 'Av. Cruzeiro do Sul', '1200', 'Santana', 'São Paulo', 'SP', NULL),

('Fazenda Santa Clara', '67890123000145', '(19) 3277-6600', 'administracao@fazendaclara.com', 'PJ', true, '13100-001', 'Rodovia SP-001', 'KM 15', 'Rural', 'Campinas', 'SP', NULL),
('Hotel Jardim Tropical', '78901234000156', '(21) 3266-7700', 'compras@hoteljardim.com', 'PJ', true, '22070-001', 'Av. Nossa Senhora', '2500', 'Ipanema', 'Rio de Janeiro', 'RJ', NULL),

('Empresa Inativa LTDA', '89012345000167', '(11) 3255-8800', 'inativa@empresa.com', 'PJ', false, '01000-000', 'Rua Teste', '999', 'Centro', 'São Paulo', 'SP', NULL),
('Cliente Inativo PF', '90123456789', '(11) 99999-9999', 'inativo@email.com', 'PF', false, '00000-000', 'Rua Inativa', '000', 'Teste', 'São Paulo', 'SP', '1990-01-01');

INSERT INTO cupom (codigo, descricao, valor, data_inicio, validade, condicoes_uso) VALUES
('BEMVINDO10', 'Cupom de boas-vindas R$ 10,00', 10.00, '2024-01-01', '2025-12-31', 'Válido para primeira compra'),
('PROMOCAO20', 'Promoção especial R$ 20,00', 20.00, '2024-05-01', '2025-06-30', 'Compras acima de R$ 100,00'),
('ANIVERSARIO50', 'Super desconto aniversário R$ 50,00', 50.00, '2024-05-01', '2025-05-31', 'Compras acima de R$ 300,00'),
('INDICACAO15', 'Indique um amigo R$ 15,00', 15.00, '2024-04-01', '2025-12-31', 'Válido uma vez por cliente'),
('FIDELIDADE25', 'Cliente fiel R$ 25,00', 25.00, '2024-06-01', '2025-12-31', 'Clientes com mais de 3 compras'),
('NATAL30', 'Promoção de Natal R$ 30,00', 30.00, '2024-12-01', '2025-01-15', 'Válido até Janeiro'),
('BLACKFRIDAY', 'Black Friday R$ 40,00', 40.00, '2024-11-20', '2025-11-30', 'Promoção especial'),
('JARDIM5', 'Desconto jardim R$ 5,00', 5.00, '2024-03-01', '2025-12-31', 'Produtos categoria Materiais'),

('VERAO2024', 'Promoção de verão R$ 35,00', 35.00, '2024-01-01', '2024-03-31', 'Cupom vencido'),
('INVERNO2024', 'Promoção de inverno R$ 45,00', 45.00, '2024-06-01', '2024-08-31', 'Cupom vencido'),
('EXPIREDTEST', 'Teste cupom expirado R$ 100,00', 100.00, '2023-01-01', '2023-12-31', 'Cupom teste vencido'),

('VIP100', 'Cupom VIP R$ 100,00', 100.00, '2024-01-01', '2025-12-31', 'Apenas clientes VIP'),
('MEGA200', 'Mega desconto R$ 200,00', 200.00, '2024-01-01', '2025-12-31', 'Compras acima de R$ 1000,00');


INSERT INTO pedido (data_requisicao, data_entrega, cliente_id, funcionario_id, cupom_id, valor_desconto) VALUES
('2024-01-05', '2024-01-10', 1, 1, 1, 10.00),
('2024-01-15', '2024-01-20', 3, 5, 2, 20.00),
('2024-01-25', '2024-02-01', 11, 7, NULL, 0),

('2024-02-05', '2024-02-10', 4, 2, 3, 50.00),
('2024-02-14', '2024-02-20', 12, 6, NULL, 0),
('2024-02-28', '2024-03-06', 2, 4, 4, 15.00),

('2024-03-10', '2024-03-15', 13, 1, 5, 25.00),
('2024-03-20', '2024-03-25', 5, 7, NULL, 0),

('2024-04-05', '2024-04-12', 14, 2, 6, 30.00),
('2024-04-18', '2024-04-23', 6, 5, NULL, 0),
('2024-04-25', '2024-05-01', 15, 11, 7, 40.00),

('2024-05-02', '2024-05-07', 7, 7, NULL, 0),
('2024-05-10', '2024-05-16', 16, 6, 8, 5.00),
('2024-05-15', '2024-05-22', 8, 1, NULL, 0),
('2024-05-20', '2024-05-25', 17, 7, 1, 10.00),

('2024-01-08', '2024-01-20', 9, 8, NULL, 0),
('2024-01-18', '2024-02-02', 10, 9, NULL, 0),

('2024-02-08', '2024-02-25', 1, 10, 9, 35.00),
('2024-02-22', '2024-03-15', 3, 8, NULL, 0),

('2024-03-05', '2024-03-20', 11, 9, NULL, 0),
('2024-03-25', '2024-04-10', 4, 10, 10, 45.00),-- Lucas Silva Auxiliar

('2024-04-10', '2024-04-25', 12, 8, NULL, 0),
('2024-04-28', '2024-05-15', 13, 9, NULL, 0),

('2025-01-10', NULL, 14, 1, 11, 100.00),
('2025-01-12', NULL, 15, 2, NULL, 0),
('2025-01-14', NULL, 5, 3, 12, 200.00),
('2025-01-15', NULL, 16, 4, NULL, 0),
('2025-01-16', NULL, 6, 5, 6, 30.00),
('2025-01-17', NULL, 17, 6, NULL, 0),
('2025-01-18', NULL, 7, 7, 13, 200.00),

('2024-06-05', '2024-06-10', 8, 1, NULL, 0),
('2024-06-15', '2024-06-25', 9, 2, NULL, 0),
('2024-06-25', '2024-07-02', 10, 3, 1, 10.00),

('2024-07-08', '2024-07-15', 1, 4, 2, 20.00),
('2024-07-18', '2024-08-05', 2, 5, NULL, 0),
('2024-07-28', '2024-08-02', 3, 6, NULL, 0),

('2024-08-10', '2024-08-17', 11, 7, 3, 50.00),
('2024-08-20', '2024-09-05', 12, 11, NULL, 0),

('2024-09-12', '2024-09-18', 13, 1, NULL, 0),
('2024-09-25', '2024-10-10', 14, 2, NULL, 0),

('2024-10-05', '2024-10-12', 15, 3, 4, 15.00),
('2024-10-15', '2024-10-20', 16, 4, NULL, 0),
('2024-10-25', '2024-11-08', 17, 5, NULL, 0),

('2024-11-08', '2024-11-13', 4, 6, 7, 40.00),
('2024-11-20', '2024-11-25', 5, 7, NULL, 0),
('2024-11-28', '2024-12-10', 6, 11, NULL, 0),

('2024-12-05', '2024-12-12', 7, 1, 6, 30.00),
('2024-12-15', '2024-12-20', 8, 2, NULL, 0),
('2024-12-22', '2025-01-05', 9, 3, NULL, 0);

UPDATE pedido
SET status_pagamento = 'PAGO',
    data_pagamento = COALESCE(data_entrega, data_requisicao);

UPDATE pedido
SET status_pagamento = 'PENDENTE',
    data_pagamento = NULL
WHERE data_entrega IS NULL;

UPDATE pedido
SET status_pagamento = 'PENDENTE',
    data_pagamento = NULL
WHERE id IN (24, 25);

INSERT INTO item_pedido (id_pedido, id_produto, quantidade) VALUES
(1, 1, 3),
(1, 14, 2),

(2, 9, 1),
(2, 10, 2),
(2, 13, 1),

(3, 3, 10),
(3, 6, 5),
(3, 22, 3),

(4, 16, 1),
(4, 17, 1),
(4, 20, 2),

(5, 1, 15),
(5, 2, 8),
(5, 5, 3),

(6, 21, 2),
(6, 24, 4),
(6, 25, 6),

(7, 4, 5),
(7, 8, 2),
(7, 11, 3),

(8, 12, 2),
(8, 15, 1),
(8, 10, 1),

(9, 18, 1),
(9, 19, 1),
(9, 23, 2),

(10, 6, 8),
(10, 7, 1),

(11, 25, 10),
(11, 14, 5),

(12, 1, 8),
(12, 2, 12),
(12, 21, 6),

(13, 3, 20),
(13, 6, 15),
(13, 22, 8),

(14, 9, 2),
(14, 16, 1),
(14, 11, 4),

(15, 5, 6),
(15, 8, 4),
(15, 24, 8),

(16, 17, 2),
(16, 18, 1),
(16, 21, 1),

(17, 1, 6),
(17, 10, 3),

(18, 23, 5),
(18, 26, 2),

(19, 3, 25),
(19, 6, 20),

(20, 16, 2),
(20, 19, 1),

(21, 27, 1),
(21, 28, 3),

(22, 18, 2),
(22, 21, 3),

(23, 7, 3),
(23, 26, 4),

(24, 3, 30),
(24, 6, 25),
(24, 1, 20),

(25, 16, 3),
(25, 17, 2),

(26, 27, 2),
(26, 18, 3),
(26, 23, 8),

(27, 2, 15),
(27, 21, 10),

(28, 9, 4),
(28, 10, 6),

(29, 22, 12),
(29, 24, 15),

(30, 5, 8),
(30, 8, 6),

(31, 1, 5),
(31, 14, 8),

(32, 16, 1),
(32, 20, 3),

(33, 6, 12),
(33, 21, 8),

(34, 2, 10),
(34, 25, 12),

(35, 17, 1),
(35, 19, 1),

(36, 3, 15),
(36, 4, 8),

(37, 9, 3),
(37, 23, 4),

(38, 5, 10),
(38, 7, 2),

(39, 10, 4),
(39, 11, 6),

(40, 18, 1),
(40, 21, 2),

(41, 22, 6),
(41, 24, 10),

(42, 1, 12),
(42, 2, 8),

(43, 8, 5),
(43, 25, 15),

(44, 16, 2),
(44, 6, 18),

(45, 12, 4),
(45, 13, 3),

(46, 17, 1),
(46, 20, 4),

(47, 21, 12),
(47, 26, 3),

(48, 3, 8),
(48, 4, 6),

(49, 19, 2),
(49, 23, 6);

INSERT INTO movimentacao_estoque (id_produto, tipo, quantidade, motivo, data) VALUES
(1, 'entrada', 100, 'Reposição estoque - Fornecedor ABC', '2024-01-03'),
(2, 'entrada', 200, 'Reposição estoque - Fornecedor Sementes', '2024-01-03'),
(3, 'entrada', 50, 'Reposição estoque - Terra Fértil Ltda', '2024-01-05'),
(16, 'entrada', 10, 'Compra equipamentos - Fornecedor Tech', '2024-01-08'),
(17, 'entrada', 8, 'Compra equipamentos - Fornecedor Tech', '2024-01-08'),

(6, 'entrada', 80, 'Reposição piso - Construtora Pisos', '2024-02-01'),
(9, 'entrada', 20, 'Reposição ferramentas - Jardinex', '2024-02-03'),
(21, 'entrada', 50, 'Reposição iluminação - LED Solar Co', '2024-02-05'),
(22, 'entrada', 25, 'Reposição vasos - Cerâmica Bela', '2024-02-10'),

(4, 'entrada', 40, 'Reposição substrato - Substratos SP', '2024-03-01'),
(10, 'entrada', 30, 'Reposição kits - Jardinex', '2024-03-05'),
(23, 'entrada', 15, 'Compra timers - Irrigação Pro', '2024-03-08'),
(25, 'entrada', 60, 'Reposição fertilizante - Nutriplan', '2024-03-12'),

(18, 'entrada', 5, 'Compra motosserra - EquipGarden', '2024-04-02'),
(19, 'entrada', 8, 'Compra roçadeira - EquipGarden', '2024-04-02'),
(5, 'entrada', 30, 'Reposição brita - Pedreira Central', '2024-04-05'),
(26, 'entrada', 10, 'Compra herbicida - AgroQuímica', '2024-04-10'),

(1, 'entrada', 150, 'Reposição alta temporada - ABC', '2024-05-01'),
(2, 'entrada', 250, 'Reposição alta temporada - Sementes', '2024-05-01'),
(3, 'entrada', 80, 'Reposição alta temporada - Terra Fértil', '2024-05-03'),
(6, 'entrada', 100, 'Reposição alta temporada - Pisos', '2024-05-05'),
(21, 'entrada', 80, 'Reposição alta temporada - LED Solar', '2024-05-08'),

(7, 'entrada', 15, 'Reposição manta - Geotêxtil Pro', '2024-06-01'),
(8, 'entrada', 25, 'Reposição casca - Pinus Decoração', '2024-06-05'),
(11, 'entrada', 20, 'Reposição pulverizador - Jardinex', '2024-07-01'),
(12, 'entrada', 25, 'Reposição enxada - Ferramentas Sul', '2024-07-15'),
(20, 'entrada', 15, 'Reposição mangueira - Hidro Flex', '2024-08-01'),
(24, 'entrada', 40, 'Reposição suporte - Metal Garden', '2024-08-10'),
(27, 'entrada', 5, 'Compra bomba - Irrigação Pro', '2024-09-01'),
(28, 'entrada', 8, 'Compra sensor - Tech Garden', '2024-09-05'),
(13, 'entrada', 30, 'Reposição régua - Medidas Precisas', '2024-10-01'),
(14, 'entrada', 50, 'Reposição borrifador - Spray Tech', '2024-10-05'),
(15, 'entrada', 20, 'Reposição ancinho - Ferramentas Sul', '2024-11-01'),
(21, 'entrada', 40, 'Reposição fim ano - LED Solar', '2024-12-01');

INSERT INTO movimentacao_estoque (id_produto, tipo, quantidade, motivo, data) VALUES
(1, 'saida', 3, 'Venda - Pedido #1', '2024-01-05'),
(14, 'saida', 2, 'Venda - Pedido #1', '2024-01-05'),
(9, 'saida', 1, 'Venda - Pedido #2', '2024-01-15'),
(10, 'saida', 2, 'Venda - Pedido #2', '2024-01-15'),
(13, 'saida', 1, 'Venda - Pedido #2', '2024-01-15'),
(3, 'saida', 10, 'Venda - Pedido #3', '2024-01-25'),
(6, 'saida', 5, 'Venda - Pedido #3', '2024-01-25'),
(22, 'saida', 3, 'Venda - Pedido #3', '2024-01-25'),

(16, 'saida', 1, 'Venda - Pedido #4', '2024-02-05'),
(17, 'saida', 1, 'Venda - Pedido #4', '2024-02-05'),
(20, 'saida', 2, 'Venda - Pedido #4', '2024-02-05'),
(1, 'saida', 15, 'Venda - Pedido #5', '2024-02-14'),
(2, 'saida', 8, 'Venda - Pedido #5', '2024-02-14'),
(5, 'saida', 3, 'Venda - Pedido #5', '2024-02-14'),
(21, 'saida', 2, 'Venda - Pedido #6', '2024-02-28'),
(24, 'saida', 4, 'Venda - Pedido #6', '2024-02-28'),
(25, 'saida', 6, 'Venda - Pedido #6', '2024-02-28'),

(4, 'saida', 5, 'Venda - Pedido #7', '2024-03-10'),
(8, 'saida', 2, 'Venda - Pedido #7', '2024-03-10'),
(11, 'saida', 3, 'Venda - Pedido #7', '2024-03-10'),
(12, 'saida', 2, 'Venda - Pedido #8', '2024-03-20'),
(15, 'saida', 1, 'Venda - Pedido #8', '2024-03-20'),
(10, 'saida', 1, 'Venda - Pedido #8', '2024-03-20'),

(18, 'saida', 1, 'Venda - Pedido #9', '2024-04-05'),
(19, 'saida', 1, 'Venda - Pedido #9', '2024-04-05'),
(23, 'saida', 2, 'Venda - Pedido #9', '2024-04-05'),
(6, 'saida', 8, 'Venda - Pedido #10', '2024-04-18'),
(7, 'saida', 1, 'Venda - Pedido #10', '2024-04-18'),
(25, 'saida', 10, 'Venda - Pedido #11', '2024-04-25'),
(14, 'saida', 5, 'Venda - Pedido #11', '2024-04-25'),

(1, 'saida', 8, 'Venda - Pedido #12', '2024-05-02'),
(2, 'saida', 12, 'Venda - Pedido #12', '2024-05-02'),
(21, 'saida', 6, 'Venda - Pedido #12', '2024-05-02'),
(3, 'saida', 20, 'Venda - Pedido #13', '2024-05-10'),
(6, 'saida', 15, 'Venda - Pedido #13', '2024-05-10'),
(22, 'saida', 8, 'Venda - Pedido #13', '2024-05-10'),
(9, 'saida', 2, 'Venda - Pedido #14', '2024-05-15'),
(16, 'saida', 1, 'Venda - Pedido #14', '2024-05-15'),
(11, 'saida', 4, 'Venda - Pedido #14', '2024-05-15'),
(5, 'saida', 6, 'Venda - Pedido #15', '2024-05-20'),
(8, 'saida', 4, 'Venda - Pedido #15', '2024-05-20'),
(24, 'saida', 8, 'Venda - Pedido #15', '2024-05-20'),

(17, 'saida', 2, 'Venda - Pedido #16', '2024-01-08'),
(18, 'saida', 1, 'Venda - Pedido #16', '2024-01-08'),
(21, 'saida', 1, 'Venda - Pedido #16', '2024-01-08'),
(1, 'saida', 6, 'Venda - Pedido #17', '2024-01-18'),
(10, 'saida', 3, 'Venda - Pedido #17', '2024-01-18'),

(26, 'saida', 1, 'Produto vencido - descarte', '2024-03-15'),
(28, 'saida', 1, 'Produto danificado - descarte', '2024-04-20'),
(3, 'entrada', 5, 'Devolução cliente - Pedido #18', '2024-05-10'),
(14, 'saida', 2, 'Amostra grátis - cliente premium', '2024-05-15'),
(25, 'saida', 3, 'Uso interno - teste qualidade', '2024-05-20'),

(1, 'entrada', 80, 'Reposição Janeiro 2025 - ABC', '2025-01-02'),
(2, 'entrada', 150, 'Reposição Janeiro 2025 - Sementes', '2025-01-02'),
(16, 'entrada', 5, 'Reposição equipamentos 2025', '2025-01-05'),
(27, 'entrada', 3, 'Compra bomba - pedidos pendentes', '2025-01-08');

UPDATE produto SET quantidade =
CASE
    WHEN id = 1 THEN 250 + 100 + 150 + 80 - 3 - 15 - 8 - 6
    WHEN id = 2 THEN 300 + 200 + 250 + 150 - 8 - 12
    WHEN id = 3 THEN 80 + 50 + 80 + 5 - 10 - 20
    WHEN id = 4 THEN 60 + 40 - 5
    WHEN id = 5 THEN 45 + 30 - 3 - 6
    WHEN id = 6 THEN 150 + 80 + 100 - 5 - 8 - 15
    WHEN id = 7 THEN 25 + 15 - 1
    WHEN id = 8 THEN 40 + 25 - 2 - 4
    WHEN id = 9 THEN 35 + 20 - 1 - 2
    WHEN id = 10 THEN 50 + 30 - 2 - 1 - 3
    WHEN id = 11 THEN 28 + 20 - 3 - 4
    WHEN id = 12 THEN 42 + 25 - 2
    WHEN id = 13 THEN 60 + 30 - 1
    WHEN id = 14 THEN 80 + 50 - 2 - 5 - 2
    WHEN id = 15 THEN 30 + 20 - 1
    WHEN id = 16 THEN 18 + 10 + 5 - 1 - 1
    WHEN id = 17 THEN 12 + 8 - 1 - 2
    WHEN id = 18 THEN 8 + 5 - 1 - 1
    WHEN id = 19 THEN 15 + 8 - 1
    WHEN id = 20 THEN 25 + 15 - 2
    WHEN id = 21 THEN 10 + 15 + 50 + 80 + 40 - 1 - 2 - 6
    WHEN id = 22 THEN 75 + 50 + 80 + 40 - 6 - 1 - 6
    WHEN id = 23 THEN 30 + 25 - 3 - 8
    WHEN id = 24 THEN 20 + 15 - 2
    WHEN id = 25 THEN 55 + 40 - 4 - 8
    WHEN id = 26 THEN 90 + 60 - 6 - 10 - 3
    WHEN id = 27 THEN 5 + 10 - 1
    WHEN id = 28 THEN 3 + 5 + 3 - 1
    WHEN id = 29 THEN 2 + 8 - 1
    WHEN id = 30 THEN 0
    ELSE quantidade
END;


SELECT '==================== RESUMO GERAL DOS DADOS ====================' AS 'STATUS';

SELECT 'Produtos inseridos:' AS 'Categoria', COUNT(*) AS 'Total' FROM produto
UNION ALL
SELECT 'Funcionários inseridos:', COUNT(*) FROM funcionario
UNION ALL
SELECT 'Clientes inseridos:', COUNT(*) FROM cliente
UNION ALL
SELECT 'Cupons inseridos:', COUNT(*) FROM cupom
UNION ALL
SELECT 'Pedidos inseridos:', COUNT(*) FROM pedido
UNION ALL
SELECT 'Itens de pedido:', COUNT(*) FROM item_pedido
UNION ALL
SELECT 'Movimentações estoque:', COUNT(*) FROM movimentacao_estoque;

SELECT '==================== PRODUTOS POR CATEGORIA ====================' AS 'STATUS';
SELECT tipo AS 'Categoria', COUNT(*) AS 'Qtd_Produtos', SUM(quantidade) AS 'Estoque_Total',
       ROUND(AVG(valor), 2) AS 'Valor_Médio'
FROM produto
GROUP BY tipo
ORDER BY COUNT(*) DESC;

SELECT '==================== FUNCIONÁRIOS POR CARGO ====================' AS 'STATUS';
SELECT cargo AS 'Cargo', COUNT(*) AS 'Quantidade',
       CASE WHEN COUNT(*) > 0 THEN 'Adequado' ELSE 'Sem funcionários' END AS 'Status'
FROM funcionario
WHERE ativo = true
GROUP BY cargo
ORDER BY COUNT(*) DESC;

SELECT '==================== CLIENTES POR TIPO E REGIÃO ====================' AS 'STATUS';
SELECT tipo AS 'Tipo', cidade AS 'Cidade', COUNT(*) AS 'Quantidade'
FROM cliente
WHERE ativo = true
GROUP BY tipo, cidade
ORDER BY COUNT(*) DESC;

SELECT '==================== CUPONS POR STATUS ====================' AS 'STATUS';
SELECT
    CASE
        WHEN validade >= CURDATE() THEN 'Ativo'
        ELSE 'Vencido'
    END AS Status_Cupom,
    COUNT(*) AS Quantidade,
    ROUND(AVG(valor), 2) AS Valor_Medio
FROM cupom
GROUP BY Status_Cupom
ORDER BY Status_Cupom;

SELECT '==================== PEDIDOS POR STATUS DE ENTREGA ====================' AS 'STATUS';
SELECT
    CASE
        WHEN data_entrega IS NULL THEN 'Pendente'
        WHEN DATEDIFF(data_entrega, data_requisicao) <= 7 THEN 'No Prazo'
        ELSE 'Atrasado'
    END AS 'Status_Entrega',
    COUNT(*) AS 'Quantidade',
    ROUND(SUM(COALESCE(
        (SELECT SUM(ip.quantidade * p.valor)
         FROM item_pedido ip
         JOIN produto p ON ip.id_produto = p.id
         WHERE ip.id_pedido = pedido.id), 0
    ) - COALESCE(valor_desconto, 0)), 2) AS 'Valor_Total'
FROM pedido
GROUP BY
    CASE
        WHEN data_entrega IS NULL THEN 'Pendente'
        WHEN DATEDIFF(data_entrega, data_requisicao) <= 7 THEN 'No Prazo'
        ELSE 'Atrasado'
    END
ORDER BY 'Quantidade' DESC;

SELECT '==================== VENDAS POR MÊS (2024) ====================' AS 'STATUS';
SELECT
    YEAR(data_requisicao) AS 'Ano',
    MONTH(data_requisicao) AS 'Mês',
    COUNT(*) AS 'Qtd_Pedidos',
    ROUND(SUM(COALESCE(
        (SELECT SUM(ip.quantidade * p.valor)
         FROM item_pedido ip
         JOIN produto p ON ip.id_produto = p.id
         WHERE ip.id_pedido = pedido.id), 0
    ) - COALESCE(valor_desconto, 0)), 2) AS 'Faturamento'
FROM pedido
WHERE YEAR(data_requisicao) = 2024
GROUP BY YEAR(data_requisicao), MONTH(data_requisicao)
ORDER BY YEAR(data_requisicao), MONTH(data_requisicao);

SELECT '==================== TOP 10 PRODUTOS MAIS VENDIDOS ====================' AS 'STATUS';
SELECT
    p.nome AS 'Produto',
    p.tipo AS 'Categoria',
    SUM(ip.quantidade) AS 'Qtd_Vendida',
    ROUND(SUM(ip.quantidade * p.valor), 2) AS 'Receita_Total'
FROM item_pedido ip
JOIN produto p ON ip.id_produto = p.id
JOIN pedido ped ON ip.id_pedido = ped.id
GROUP BY p.id, p.nome, p.tipo
ORDER BY SUM(ip.quantidade) DESC
LIMIT 10;

SELECT '==================== FUNCIONÁRIOS POR DESEMPENHO ====================' AS 'STATUS';
SELECT
    f.nome AS 'Funcionário',
    f.cargo AS 'Cargo',
    COUNT(ped.id) AS 'Pedidos_Atendidos',
    ROUND(SUM(COALESCE(
        (SELECT SUM(ip.quantidade * p.valor)
         FROM item_pedido ip
         JOIN produto p ON ip.id_produto = p.id
         WHERE ip.id_pedido = ped.id), 0
    ) - COALESCE(ped.valor_desconto, 0)), 2) AS 'Vendas_Total'
FROM funcionario f
LEFT JOIN pedido ped ON f.id = ped.funcionario_id
WHERE f.ativo = true
GROUP BY f.id, f.nome, f.cargo
ORDER BY COUNT(ped.id) DESC, 'Vendas_Total' DESC;

SELECT '==================== ESTOQUE CRÍTICO (BAIXO) ====================' AS 'STATUS';
SELECT
    nome AS 'Produto',
    tipo AS 'Categoria',
    quantidade AS 'Estoque_Atual',
    CASE
        WHEN quantidade = 0 THEN 'SEM ESTOQUE'
        WHEN quantidade <= 5 THEN 'CRÍTICO'
        WHEN quantidade <= 15 THEN 'BAIXO'
        ELSE 'NORMAL'
    END AS 'Status_Estoque'
FROM produto
WHERE quantidade <= 15
ORDER BY quantidade ASC, valor DESC;

SELECT '==================== USO DE CUPONS ====================' AS 'STATUS';
SELECT
    c.codigo AS 'Código_Cupom',
    c.valor AS 'Valor_Desconto',
    COUNT(p.id) AS 'Vezes_Usado',
    ROUND(SUM(p.valor_desconto), 2) AS 'Desconto_Total',
    CASE
        WHEN c.validade >= CURDATE() THEN 'Ativo'
        ELSE 'Vencido'
    END AS 'Status'
FROM cupom c
LEFT JOIN pedido p ON c.id = p.cupom_id
GROUP BY c.id, c.codigo, c.valor, c.validade
ORDER BY COUNT(p.id) DESC;

SELECT '==================== MOVIMENTAÇÕES RECENTES (ÚLTIMOS 30 DIAS) ====================' AS 'STATUS';
SELECT
    p.nome AS 'Produto',
    me.tipo AS 'Tipo_Movimentação',
    me.quantidade AS 'Quantidade',
    me.motivo AS 'Motivo',
    me.data AS 'Data'
FROM movimentacao_estoque me
JOIN produto p ON me.id_produto = p.id
WHERE me.data >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
ORDER BY me.data DESC
LIMIT 20;

SELECT '==================== DADOS CRIADOS COM SUCESSO! ====================' AS 'STATUS';
SELECT 'Dashboard está pronto para teste com dados abrangentes e realistas!' AS 'RESULTADO';


SET SQL_SAFE_UPDATES = 1;


