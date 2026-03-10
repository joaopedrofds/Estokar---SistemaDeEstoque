-- Script de inserção ABRANGENTE de dados para teste COMPLETO do DASHBOARD - Studio Muda Paisagismo
-- Este arquivo contém dados variados e realistas para testar todas as funcionalidades do dashboard
USE studiomuda;

-- Limpar dados existentes (opcional - remova se quiser preservar dados atuais)
DELETE FROM item_pedido;
DELETE FROM pedido;
DELETE FROM movimentacao_estoque;
DELETE FROM historico_estoque;
DELETE FROM historico_cliente;
DELETE FROM historico_funcionario;
DELETE FROM produto;
DELETE FROM cliente;
DELETE FROM funcionario;
DELETE FROM cupom;

-- Resetar auto_increment das tabelas para garantir IDs previsíveis
ALTER TABLE funcionario AUTO_INCREMENT = 1;
ALTER TABLE cliente AUTO_INCREMENT = 1;
ALTER TABLE produto AUTO_INCREMENT = 1;
ALTER TABLE cupom AUTO_INCREMENT = 1;
ALTER TABLE pedido AUTO_INCREMENT = 1;
ALTER TABLE item_pedido AUTO_INCREMENT = 1;
ALTER TABLE movimentacao_estoque AUTO_INCREMENT = 1;

-- Inserir produtos VARIADOS para teste completo do dashboard
INSERT INTO produto (nome, descricao, tipo, quantidade, valor) VALUES 
-- Materiais (categoria alta demanda)
('Adubo Orgânico Premium', 'Adubo para plantas 5kg super concentrado', 'Materiais', 120, 28.90),
('Semente de Grama Esmeralda', 'Semente premium para grama esmeralda', 'Materiais', 300, 18.50),
('Terra Vegetal', 'Terra vegetal enriquecida 20kg', 'Materiais', 80, 35.00),
('Substrato para Vasos', 'Substrato especial para vasos e jardineiras', 'Materiais', 60, 22.90),
('Pedra Brita Decorativa', 'Pedra brita colorida para decoração', 'Materiais', 45, 42.00),
('Piso Drenante Jardim', 'Piso modular drenante para áreas externas', 'Materiais', 150, 65.00),
('Manta Geotêxtil', 'Manta para controle de ervas daninhas', 'Materiais', 25, 89.90),
('Casca de Pinus', 'Casca decorativa para cobertura do solo', 'Materiais', 40, 32.50),

-- Ferramentas (categoria média demanda)
('Tesoura de Poda Profissional', 'Tesoura de poda bypass 8 polegadas', 'Ferramentas', 35, 125.00),
('Kit Jardinagem Completo', 'Kit com pás, ancinhos, regadores e luvas', 'Ferramentas', 50, 95.00),
('Pulverizador Manual', 'Pulverizador manual 2 litros', 'Ferramentas', 28, 45.90),
('Enxada de Jardinagem', 'Enxada pequena para canteiros', 'Ferramentas', 42, 38.00),
('Régua de Plantio', 'Régua graduada para medição de áreas', 'Ferramentas', 60, 28.90),
('Borrifador Spray', 'Borrifador spray para plantas', 'Ferramentas', 80, 15.90),
('Ancinho de Folhas', 'Ancinho específico para coleta de folhas', 'Ferramentas', 30, 52.00),

-- Equipamentos (categoria alta margem)
('Cortador de Grama Elétrico', 'Cortador elétrico 1500W com recolhedor', 'Equipamentos', 18, 450.00),
('Aspirador de Folhas', 'Aspirador/soprador de folhas 2000W', 'Equipamentos', 12, 320.00),
('Motosserra de Poda', 'Motosserra elétrica para poda de galhos', 'Equipamentos', 8, 280.00),
('Roçadeira Elétrica', 'Roçadeira elétrica 1200W profissional', 'Equipamentos', 15, 390.00),
('Mangueira de Jardim', 'Mangueira flexível 30 metros', 'Equipamentos', 25, 85.00),
('Bancada Jardinagem Móvel', 'Bancada com rodas para trabalho', 'Equipamentos', 10, 380.00),

-- Acessórios (categoria diversos)
('Iluminação LED Solar', 'Lâmpada LED solar para jardim', 'Acessórios', 75, 68.00),
('Vaso Cerâmica Grande', 'Vaso decorativo de cerâmica 40cm', 'Acessórios', 30, 95.00),
('Timer para Irrigação', 'Timer automático para sistema de irrigação', 'Acessórios', 20, 150.00),
('Suporte para Plantas', 'Suporte metálico ajustável para plantas', 'Acessórios', 55, 42.90),
('Fertilizante Líquido', 'Fertilizante concentrado NPK 500ml', 'Acessórios', 90, 24.90),

-- Produtos com estoque baixo (para teste de alertas)
('Herbicida Seletivo', 'Herbicida para controle de ervas', 'Materiais', 5, 85.00),
('Bomba de Irrigação', 'Bomba submersa para irrigação', 'Equipamentos', 3, 680.00),
('Sensor de Umidade', 'Sensor digital de umidade do solo', 'Acessórios', 2, 220.00),

-- Produtos sem estoque (para teste de ruptura)
('Adubo Especial Orquídeas', 'Adubo específico para orquídeas', 'Materiais', 0, 35.90),
('Cortador de Cerca Viva', 'Cortador elétrico para cerca viva', 'Equipamentos', 0, 590.00);

-- Inserir funcionários DIVERSOS para teste completo (diferentes cargos e desempenhos)
INSERT INTO funcionario (nome, cpf, cargo, data_nasc, telefone, cep, rua, numero, bairro, cidade, estado, ativo) VALUES 
-- Diretores (alta performance)
('Ana Paula Diretor', '12345678901', 'Diretor', '1980-02-15', '(11) 99888-7766', '01234-567', 'Rua dos Jardins', '123', 'Centro', 'São Paulo', 'SP', true),
('Carlos Eduardo Diretor', '23456789012', 'Diretor', '1978-11-08', '(11) 99777-8855', '02345-678', 'Av. Verde', '456', 'Vila Verde', 'São Paulo', 'SP', true),

-- Gerentes (média-alta performance)
('Fernanda Silva Gerente', '34567890123', 'Gerente', '1985-06-10', '(11) 99666-5544', '03456-789', 'Rua das Flores', '789', 'Vila Nova', 'São Paulo', 'SP', true),
('Roberto Santos Gerente', '45678901234', 'Gerente', '1982-09-22', '(11) 99555-4433', '04567-890', 'Av. Brasil', '1010', 'Consolação', 'São Paulo', 'SP', true),

-- Vendedores (performance variada)
('Juliana Costa Vendas', '56789012345', 'Vendedor', '1990-03-30', '(11) 99444-3322', '05678-901', 'Rua das Palmeiras', '234', 'Pinheiros', 'São Paulo', 'SP', true),
('Marcos Oliveira Vendas', '67890123456', 'Vendedor', '1988-12-14', '(11) 99333-2211', '06789-012', 'Av. Paulista', '567', 'Bela Vista', 'São Paulo', 'SP', true),
('Patrícia Almeida Vendas', '78901234567', 'Vendedor', '1992-07-25', '(11) 99222-1100', '07890-123', 'Rua Augusta', '890', 'Cerqueira César', 'São Paulo', 'SP', true),

-- Estoquistas (baixa performance de vendas, focados em operação)
('João Pedro Estoque', '89012345678', 'Estoquista', '1995-01-18', '(11) 99111-0099', '08901-234', 'Rua do Comércio', '321', 'República', 'São Paulo', 'SP', true),
('Maria José Estoque', '90123456789', 'Estoquista', '1993-04-05', '(11) 99000-9988', '09012-345', 'Av. São João', '654', 'Centro', 'São Paulo', 'SP', true),

-- Auxiliares (baixa performance)
('Lucas Silva Auxiliar', '01234567890', 'Auxiliar', '1997-08-12', '(11) 98899-8877', '10123-456', 'Rua da Liberdade', '987', 'Liberdade', 'São Paulo', 'SP', true),
('Camila Santos Auxiliar', '11234567801', 'Auxiliar', '1996-10-30', '(11) 98788-7766', '11234-567', 'Rua Voluntários', '159', 'Santana', 'São Paulo', 'SP', true),

-- Funcionário inativo (para teste)
('Pedro Inativo', '22334455667', 'Auxiliar', '1994-05-15', '(11) 98677-6655', '12345-678', 'Rua Teste', '753', 'Vila Test', 'São Paulo', 'SP', false);

-- Inserir clientes VARIADOS (diferentes cidades, tipos e perfis) para teste completo
INSERT INTO cliente (nome, cpf_cnpj, telefone, email, tipo, ativo, cep, rua, numero, bairro, cidade, estado, dataNascimento) VALUES 
-- Clientes Pessoa Física - São Paulo Capital
('Maria Silva Santos', '11122233344', '(11) 98765-4321', 'maria@email.com', 'PF', true, '01310-100', 'Av. Paulista', '1000', 'Jardim Paulista', 'São Paulo', 'SP', '1985-05-21'),
('Carlos Roberto Lima', '22233344455', '(11) 97654-3210', 'carlos@email.com', 'PF', true, '04038-001', 'Rua Augusta', '2500', 'Moema', 'São Paulo', 'SP', '1978-11-03'),
('Beatriz Torres Oliveira', '33344455566', '(11) 96543-2109', 'beatriz@email.com', 'PF', true, '05422-001', 'Rua Teodoro Sampaio', '300', 'Itaim', 'São Paulo', 'SP', '1990-02-14'),
('João Pedro Costa', '44455566677', '(11) 95432-1098', 'joao@email.com', 'PF', true, '01402-001', 'Rua da Consolação', '1500', 'Consolação', 'São Paulo', 'SP', '1983-08-30'),
('Ana Claudia Rocha', '55566677788', '(11) 94321-0987', 'ana@email.com', 'PF', true, '01310-200', 'Rua Haddock Lobo', '800', 'Cerqueira César', 'São Paulo', 'SP', '1992-12-08'),

-- Clientes Pessoa Física - Interior SP
('Ricardo Fernandes', '66677788899', '(11) 93210-9876', 'ricardo@email.com', 'PF', true, '13100-000', 'Rua Central', '456', 'Centro', 'Campinas', 'SP', '1987-04-15'),
('Fernanda Almeida', '77788899900', '(11) 92109-8765', 'fernanda@email.com', 'PF', true, '12900-000', 'Av. Brasil', '789', 'Vila Nova', 'Bragança Paulista', 'SP', '1989-09-22'),
('Paulo Santos Junior', '88899900011', '(11) 91098-7654', 'paulo@email.com', 'PF', true, '18100-000', 'Rua das Flores', '123', 'Jardim América', 'Sorocaba', 'SP', '1975-06-12'),

-- Clientes Pessoa Física - Outros Estados
('Luciana Martins', '99900011122', '(21) 98765-4321', 'luciana@email.com', 'PF', true, '22071-900', 'Av. Atlântica', '2000', 'Copacabana', 'Rio de Janeiro', 'RJ', '1984-01-25'),
('Roberto Silva Neto', '00011122233', '(31) 97654-3210', 'roberto@email.com', 'PF', true, '30112-000', 'Rua da Bahia', '1500', 'Centro', 'Belo Horizonte', 'MG', '1981-07-18'),

-- Clientes Pessoa Jurídica - Grandes
('Paisagismo Premium LTDA', '12345678000190', '(11) 3322-1100', 'contato@paisagismopremium.com', 'PJ', true, '04543-001', 'Av. Faria Lima', '3000', 'Itaim Bibi', 'São Paulo', 'SP', NULL),
('Jardins & Cia Empreendimentos', '23456789000101', '(11) 3311-2200', 'vendas@jardinsecia.com', 'PJ', true, '01310-915', 'Av. Paulista', '1500', 'Bela Vista', 'São Paulo', 'SP', NULL),
('Verde Vida Paisagismo S/A', '34567890000112', '(11) 3300-3300', 'comercial@verdevida.com', 'PJ', true, '04702-000', 'Av. Santo Amaro', '5000', 'Brooklin', 'São Paulo', 'SP', NULL),

-- Clientes Pessoa Jurídica - Médias
('Construções Floridas LTDA', '45678901000123', '(11) 3299-4400', 'obras@construfloridas.com', 'PJ', true, '03031-000', 'Rua do Gasômetro', '800', 'Brás', 'São Paulo', 'SP', NULL),
('Residencial Garden', '56789012000134', '(11) 3288-5500', 'contato@residencialgarden.com', 'PJ', true, '02033-000', 'Av. Cruzeiro do Sul', '1200', 'Santana', 'São Paulo', 'SP', NULL),

-- Clientes Pessoa Jurídica - Interior/Outros Estados
('Fazenda Santa Clara', '67890123000145', '(19) 3277-6600', 'administracao@fazendaclara.com', 'PJ', true, '13100-001', 'Rodovia SP-001', 'KM 15', 'Rural', 'Campinas', 'SP', NULL),
('Hotel Jardim Tropical', '78901234000156', '(21) 3266-7700', 'compras@hoteljardim.com', 'PJ', true, '22070-001', 'Av. Nossa Senhora', '2500', 'Ipanema', 'Rio de Janeiro', 'RJ', NULL),

-- Clientes Inativos (para teste)
('Empresa Inativa LTDA', '89012345000167', '(11) 3255-8800', 'inativa@empresa.com', 'PJ', false, '01000-000', 'Rua Teste', '999', 'Centro', 'São Paulo', 'SP', NULL),
('Cliente Inativo PF', '90123456789', '(11) 99999-9999', 'inativo@email.com', 'PF', false, '00000-000', 'Rua Inativa', '000', 'Teste', 'São Paulo', 'SP', '1990-01-01');

-- Inserir cupons VARIADOS (ativos, vencidos, usados, não usados) para teste completo
INSERT INTO cupom (codigo, descricao, valor, data_inicio, validade, condicoes_uso) VALUES 
-- Cupons Ativos
('BEMVINDO10', 'Cupom de boas-vindas R$ 10,00', 10.00, '2024-01-01', '2025-12-31', 'Válido para primeira compra'),
('PROMOCAO20', 'Promoção especial R$ 20,00', 20.00, '2024-05-01', '2025-06-30', 'Compras acima de R$ 100,00'),
('ANIVERSARIO50', 'Super desconto aniversário R$ 50,00', 50.00, '2024-05-01', '2025-05-31', 'Compras acima de R$ 300,00'),
('INDICACAO15', 'Indique um amigo R$ 15,00', 15.00, '2024-04-01', '2025-12-31', 'Válido uma vez por cliente'),
('FIDELIDADE25', 'Cliente fiel R$ 25,00', 25.00, '2024-06-01', '2025-12-31', 'Clientes com mais de 3 compras'),
('NATAL30', 'Promoção de Natal R$ 30,00', 30.00, '2024-12-01', '2025-01-15', 'Válido até Janeiro'),
('BLACKFRIDAY', 'Black Friday R$ 40,00', 40.00, '2024-11-20', '2025-11-30', 'Promoção especial'),
('JARDIM5', 'Desconto jardim R$ 5,00', 5.00, '2024-03-01', '2025-12-31', 'Produtos categoria Materiais'),

-- Cupons Vencidos (para teste)
('VERAO2024', 'Promoção de verão R$ 35,00', 35.00, '2024-01-01', '2024-03-31', 'Cupom vencido'),
('INVERNO2024', 'Promoção de inverno R$ 45,00', 45.00, '2024-06-01', '2024-08-31', 'Cupom vencido'),
('EXPIREDTEST', 'Teste cupom expirado R$ 100,00', 100.00, '2023-01-01', '2023-12-31', 'Cupom teste vencido'),

-- Cupons com valores altos (para teste de grandes descontos)
('VIP100', 'Cupom VIP R$ 100,00', 100.00, '2024-01-01', '2025-12-31', 'Apenas clientes VIP'),
('MEGA200', 'Mega desconto R$ 200,00', 200.00, '2024-01-01', '2025-12-31', 'Compras acima de R$ 1000,00');

-- INSERIR PEDIDOS ABRANGENTES com diferentes cenários (ENTREGUES NO PRAZO, ATRASADOS, PENDENTES)
-- Baseado no critério: NO PRAZO = até 7 dias, ATRASADO = mais de 7 dias

-- PEDIDOS ENTREGUES NO PRAZO (últimos 12 meses)
-- Janeiro 2024
INSERT INTO pedido (data_requisicao, data_entrega, cliente_id, funcionario_id, cupom_id, valor_desconto) VALUES 
('2024-01-05', '2024-01-10', 1, 1, 1, 10.00),  -- Ana Paula Diretor
('2024-01-15', '2024-01-20', 3, 5, 2, 20.00),  -- Juliana Costa Vendas
('2024-01-25', '2024-02-01', 11, 7, NULL, 0),  -- Patrícia Almeida Vendas

-- Fevereiro 2024
('2024-02-05', '2024-02-10', 4, 2, 3, 50.00),  -- Carlos Eduardo Diretor
('2024-02-14', '2024-02-20', 12, 6, NULL, 0),  -- Marcos Oliveira Vendas
('2024-02-28', '2024-03-06', 2, 4, 4, 15.00),  -- Roberto Santos Gerente

-- Março 2024
('2024-03-10', '2024-03-15', 13, 1, 5, 25.00), -- Ana Paula Diretor
('2024-03-20', '2024-03-25', 5, 7, NULL, 0),   -- Patrícia Almeida Vendas

-- Abril 2024
('2024-04-05', '2024-04-12', 14, 2, 6, 30.00), -- Carlos Eduardo Diretor
('2024-04-18', '2024-04-23', 6, 5, NULL, 0),   -- Juliana Costa Vendas
('2024-04-25', '2024-05-01', 15, 11, 7, 40.00), -- Camila Santos Auxiliar

-- Maio 2024 (mais vendas)
('2024-05-02', '2024-05-07', 7, 7, NULL, 0),   -- Patrícia Almeida Vendas
('2024-05-10', '2024-05-16', 16, 6, 8, 5.00),  -- Marcos Oliveira Vendas
('2024-05-15', '2024-05-22', 8, 1, NULL, 0),   -- Ana Paula Diretor
('2024-05-20', '2024-05-25', 17, 7, 1, 10.00), -- Patrícia Almeida Vendas

-- PEDIDOS ENTREGUES COM ATRASO (mais de 7 dias)
-- Janeiro 2024
('2024-01-08', '2024-01-20', 9, 8, NULL, 0),   -- João Pedro Estoque
('2024-01-18', '2024-02-02', 10, 9, NULL, 0), -- Maria José Estoque

-- Fevereiro 2024
('2024-02-08', '2024-02-25', 1, 10, 9, 35.00), -- Lucas Silva Auxiliar
('2024-02-22', '2024-03-15', 3, 8, NULL, 0),   -- João Pedro Estoque

-- Março 2024
('2024-03-05', '2024-03-20', 11, 9, NULL, 0), -- Maria José Estoque
('2024-03-25', '2024-04-10', 4, 10, 10, 45.00),-- Lucas Silva Auxiliar

-- Abril 2024
('2024-04-10', '2024-04-25', 12, 8, NULL, 0),  -- João Pedro Estoque
('2024-04-28', '2024-05-15', 13, 9, NULL, 0), -- Maria José Estoque

-- PEDIDOS PENDENTES (não entregues ainda) - 2025
('2025-01-10', NULL, 14, 1, 11, 100.00),  -- Ana Paula Diretor
('2025-01-12', NULL, 15, 2, NULL, 0),     -- Carlos Eduardo Diretor
('2025-01-14', NULL, 5, 3, 12, 200.00),  -- Fernanda Silva Gerente
('2025-01-15', NULL, 16, 4, NULL, 0),     -- Roberto Santos Gerente
('2025-01-16', NULL, 6, 5, 6, 30.00),    -- Juliana Costa Vendas
('2025-01-17', NULL, 17, 6, NULL, 0),    -- Marcos Oliveira Vendas
('2025-01-18', NULL, 7, 7, 13, 200.00),  -- Patrícia Almeida Vendas

-- PEDIDOS RECENTES DIVERSOS (últimos meses 2024)
-- Junho 2024
('2024-06-05', '2024-06-10', 8, 1, NULL, 0),   -- Ana Paula Diretor
('2024-06-15', '2024-06-25', 9, 2, NULL, 0),   -- Carlos Eduardo Diretor
('2024-06-25', '2024-07-02', 10, 3, 1, 10.00), -- Fernanda Silva Gerente

-- Julho 2024
('2024-07-08', '2024-07-15', 1, 4, 2, 20.00),  -- Roberto Santos Gerente
('2024-07-18', '2024-08-05', 2, 5, NULL, 0),   -- Juliana Costa Vendas
('2024-07-28', '2024-08-02', 3, 6, NULL, 0),   -- Marcos Oliveira Vendas

-- Agosto 2024
('2024-08-10', '2024-08-17', 11, 7, 3, 50.00), -- Patrícia Almeida Vendas
('2024-08-20', '2024-09-05', 12, 11, NULL, 0),  -- Camila Santos Auxiliar

-- Setembro 2024
('2024-09-12', '2024-09-18', 13, 1, NULL, 0),  -- Ana Paula Diretor
('2024-09-25', '2024-10-10', 14, 2, NULL, 0),  -- Carlos Eduardo Diretor

-- Outubro 2024
('2024-10-05', '2024-10-12', 15, 3, 4, 15.00), -- Fernanda Silva Gerente
('2024-10-15', '2024-10-20', 16, 4, NULL, 0),  -- Roberto Santos Gerente
('2024-10-25', '2024-11-08', 17, 5, NULL, 0),  -- Juliana Costa Vendas

-- Novembro 2024
('2024-11-08', '2024-11-13', 4, 6, 7, 40.00),  -- Marcos Oliveira Vendas
('2024-11-20', '2024-11-25', 5, 7, NULL, 0),   -- Patrícia Almeida Vendas
('2024-11-28', '2024-12-10', 6, 11, NULL, 0),   -- Camila Santos Auxiliar

-- Dezembro 2024
('2024-12-05', '2024-12-12', 7, 1, 6, 30.00),  -- Ana Paula Diretor
('2024-12-15', '2024-12-20', 8, 2, NULL, 0),   -- Carlos Eduardo Diretor
('2024-12-22', '2025-01-05', 9, 3, NULL, 0);   -- Fernanda Silva Gerente

-- Inserir itens dos pedidos - Quantidades realistas e variadas
-- Pedidos 1-3 (Janeiro 2024 - NO PRAZO)
INSERT INTO item_pedido (id_pedido, id_produto, quantidade) VALUES 
-- Pedido 1: Pequeno (produtos básicos)
(1, 1, 3),   -- 3 Adubo Orgânico Premium
(1, 14, 2),  -- 2 Borrifador Spray

-- Pedido 2: Médio (ferramentas)
(2, 9, 1),   -- 1 Tesoura de Poda Profissional
(2, 10, 2),  -- 2 Kit Jardinagem Completo
(2, 13, 1),  -- 1 Régua de Plantio

-- Pedido 3: Grande empresa (materiais)
(3, 3, 10),  -- 10 Terra Vegetal
(3, 6, 5),   -- 5 Piso Drenante Jardim
(3, 22, 3),  -- 3 Vaso Cerâmica Grande

-- Pedidos 4-6 (Fevereiro 2024)
-- Pedido 4: Equipamentos profissionais
(4, 16, 1),  -- 1 Cortador de Grama Elétrico
(4, 17, 1),  -- 1 Aspirador de Folhas
(4, 20, 2),  -- 2 Mangueira de Jardim

-- Pedido 5: Cliente PJ - pedido grande
(5, 1, 15),  -- 15 Adubo Orgânico Premium
(5, 2, 8),   -- 8 Semente de Grama Esmeralda
(5, 5, 3),   -- 3 Pedra Brita Decorativa

-- Pedido 6: Produtos diversos
(6, 21, 2),  -- 2 Iluminação LED Solar
(6, 24, 4),  -- 4 Suporte para Plantas
(6, 25, 6),  -- 6 Fertilizante Líquido

-- Pedidos 7-8 (Março 2024)
-- Pedido 7: Materiais básicos
(7, 4, 5),   -- 5 Substrato para Vasos
(7, 8, 2),   -- 2 Casca de Pinus
(7, 11, 3),  -- 3 Pulverizador Manual

-- Pedido 8: Ferramentas variadas
(8, 12, 2),  -- 2 Enxada de Jardinagem
(8, 15, 1),  -- 1 Ancinho de Folhas
(8, 10, 1),  -- 1 Kit Jardinagem Completo

-- Pedidos 9-11 (Abril 2024)
-- Pedido 9: Alto valor - equipamentos
(9, 18, 1),  -- 1 Motosserra de Poda
(9, 19, 1),  -- 1 Roçadeira Elétrica
(9, 23, 2),  -- 2 Timer para Irrigação

-- Pedido 10: Médio valor
(10, 6, 8),  -- 8 Piso Drenante Jardim
(10, 7, 1),  -- 1 Manta Geotêxtil

-- Pedido 11: Pequeno valor
(11, 25, 10), -- 10 Fertilizante Líquido
(11, 14, 5),  -- 5 Borrifador Spray

-- Pedidos 12-15 (Maio 2024 - alta temporada)
-- Pedido 12: Cliente frequente
(12, 1, 8),   -- 8 Adubo Orgânico Premium
(12, 2, 12),  -- 12 Semente de Grama Esmeralda
(12, 21, 6),  -- 6 Iluminação LED Solar

-- Pedido 13: Projeto paisagismo
(13, 3, 20),  -- 20 Terra Vegetal
(13, 6, 15),  -- 15 Piso Drenante Jardim
(13, 22, 8),  -- 8 Vaso Cerâmica Grande

-- Pedido 14: Manutenção jardim
(14, 9, 2),   -- 2 Tesoura de Poda Profissional
(14, 16, 1),  -- 1 Cortador de Grama Elétrico
(14, 11, 4),  -- 4 Pulverizador Manual

-- Pedido 15: Materiais decorativos
(15, 5, 6),   -- 6 Pedra Brita Decorativa
(15, 8, 4),   -- 4 Casca de Pinus
(15, 24, 8),  -- 8 Suporte para Plantas

-- Pedidos 16-17 (ATRASADOS Janeiro 2024)
-- Pedido 16: Grande atraso
(16, 17, 2),  -- 2 Aspirador de Folhas
(16, 18, 1),  -- 1 Motosserra de Poda
(16, 21, 1),  -- 1 Bancada Jardinagem Móvel

-- Pedido 17: Atraso médio
(17, 1, 6),   -- 6 Adubo Orgânico Premium
(17, 10, 3),  -- 3 Kit Jardinagem Completo

-- Pedidos 18-21 (ATRASADOS Fevereiro-Março 2024)
-- Pedido 18: Atraso por produto especial
(18, 23, 5),  -- 5 Timer para Irrigação
(18, 26, 2),  -- 2 Herbicida Seletivo

-- Pedido 19: Grande pedido atrasado
(19, 3, 25),  -- 25 Terra Vegetal
(19, 6, 20),  -- 20 Piso Drenante Jardim

-- Pedido 20: Atraso operacional
(20, 16, 2),  -- 2 Cortador de Grama Elétrico
(20, 19, 1),  -- 1 Roçadeira Elétrica

-- Pedido 21: Atraso fornecedor
(21, 27, 1),  -- 1 Bomba de Irrigação
(21, 28, 3),  -- 3 Sensor de Umidade

-- Pedidos 22-24 (ATRASADOS Abril 2024)
-- Pedido 22: Equipamentos complexos
(22, 18, 2),  -- 2 Motosserra de Poda
(22, 21, 3),  -- 3 Bancada Jardinagem Móvel

-- Pedido 23: Materiais especiais
(23, 7, 3),   -- 3 Manta Geotêxtil
(23, 26, 4),  -- 4 Herbicida Seletivo

-- Pedidos 24-30 (PENDENTES 2025)
-- Pedido 24: Projeto grande pendente
(24, 3, 30),  -- 30 Terra Vegetal
(24, 6, 25),  -- 25 Piso Drenante Jardim
(24, 1, 20),  -- 20 Adubo Orgânico Premium

-- Pedido 25: Equipamentos pendente
(25, 16, 3),  -- 3 Cortador de Grama Elétrico
(25, 17, 2),  -- 2 Aspirador de Folhas

-- Pedido 26: Cliente VIP pendente
(26, 27, 2),  -- 2 Bomba de Irrigação
(26, 18, 3),  -- 3 Motosserra de Poda
(26, 23, 8),  -- 8 Timer para Irrigação

-- Pedidos 27-30: Diversos pendentes
(27, 2, 15),  -- 15 Semente de Grama Esmeralda
(27, 21, 10), -- 10 Iluminação LED Solar

(28, 9, 4),   -- 4 Tesoura de Poda Profissional
(28, 10, 6),  -- 6 Kit Jardinagem Completo

(29, 22, 12), -- 12 Vaso Cerâmica Grande
(29, 24, 15), -- 15 Suporte para Plantas

(30, 5, 8),   -- 8 Pedra Brita Decorativa
(30, 8, 6),   -- 6 Casca de Pinus

-- Pedidos 31-42 (Resto 2024 - Junho a Dezembro)
-- Junho
(31, 1, 5),   -- 5 Adubo Orgânico Premium
(31, 14, 8),  -- 8 Borrifador Spray

(32, 16, 1),  -- 1 Cortador de Grama Elétrico
(32, 20, 3),  -- 3 Mangueira de Jardim

(33, 6, 12),  -- 12 Piso Drenante Jardim
(33, 21, 8),  -- 8 Iluminação LED Solar

-- Julho
(34, 2, 10),  -- 10 Semente de Grama Esmeralda
(34, 25, 12), -- 12 Fertilizante Líquido

(35, 17, 1),  -- 1 Aspirador de Folhas
(35, 19, 1),  -- 1 Roçadeira Elétrica

(36, 3, 15),  -- 15 Terra Vegetal
(36, 4, 8),   -- 8 Substrato para Vasos

-- Agosto
(37, 9, 3),   -- 3 Tesoura de Poda Profissional
(37, 23, 4),  -- 4 Timer para Irrigação

(38, 5, 10),  -- 10 Pedra Brita Decorativa
(38, 7, 2),   -- 2 Manta Geotêxtil

-- Setembro
(39, 10, 4),  -- 4 Kit Jardinagem Completo
(39, 11, 6),  -- 6 Pulverizador Manual

(40, 18, 1),  -- 1 Motosserra de Poda
(40, 21, 2),  -- 2 Bancada Jardinagem Móvel

-- Outubro
(41, 22, 6),  -- 6 Vaso Cerâmica Grande
(41, 24, 10), -- 10 Suporte para Plantas

(42, 1, 12),  -- 12 Adubo Orgânico Premium
(42, 2, 8),   -- 8 Semente de Grama Esmeralda

(43, 8, 5),   -- 5 Casca de Pinus
(43, 25, 15), -- 15 Fertilizante Líquido

-- Novembro
(44, 16, 2),  -- 2 Cortador de Grama Elétrico
(44, 6, 18),  -- 18 Piso Drenante Jardim

(45, 12, 4),  -- 4 Enxada de Jardinagem
(45, 13, 3),  -- 3 Régua de Plantio

(46, 17, 1),  -- 1 Aspirador de Folhas
(46, 20, 4),  -- 4 Mangueira de Jardim

-- Dezembro
(47, 21, 12), -- 12 Iluminação LED Solar
(47, 26, 3),  -- 3 Herbicida Seletivo

(48, 3, 8),   -- 8 Terra Vegetal
(48, 4, 6),   -- 6 Substrato para Vasos

(49, 19, 2),  -- 2 Roçadeira Elétrica
(49, 23, 6);  -- 6 Timer para Irrigação

-- INSERIR MOVIMENTAÇÕES DE ESTOQUE ABRANGENTES (entradas e saídas)
-- Movimentações de ENTRADA (reposição de estoque)
INSERT INTO movimentacao_estoque (id_produto, tipo, quantidade, motivo, data) VALUES 
-- Reposições Janeiro 2024
(1, 'entrada', 100, 'Reposição estoque - Fornecedor ABC', '2024-01-03'),
(2, 'entrada', 200, 'Reposição estoque - Fornecedor Sementes', '2024-01-03'),
(3, 'entrada', 50, 'Reposição estoque - Terra Fértil Ltda', '2024-01-05'),
(16, 'entrada', 10, 'Compra equipamentos - Fornecedor Tech', '2024-01-08'),
(17, 'entrada', 8, 'Compra equipamentos - Fornecedor Tech', '2024-01-08'),

-- Reposições Fevereiro 2024
(6, 'entrada', 80, 'Reposição piso - Construtora Pisos', '2024-02-01'),
(9, 'entrada', 20, 'Reposição ferramentas - Jardinex', '2024-02-03'),
(21, 'entrada', 50, 'Reposição iluminação - LED Solar Co', '2024-02-05'),
(22, 'entrada', 25, 'Reposição vasos - Cerâmica Bela', '2024-02-10'),

-- Reposições Março 2024
(4, 'entrada', 40, 'Reposição substrato - Substratos SP', '2024-03-01'),
(10, 'entrada', 30, 'Reposição kits - Jardinex', '2024-03-05'),
(23, 'entrada', 15, 'Compra timers - Irrigação Pro', '2024-03-08'),
(25, 'entrada', 60, 'Reposição fertilizante - Nutriplan', '2024-03-12'),

-- Reposições Abril 2024
(18, 'entrada', 5, 'Compra motosserra - EquipGarden', '2024-04-02'),
(19, 'entrada', 8, 'Compra roçadeira - EquipGarden', '2024-04-02'),
(5, 'entrada', 30, 'Reposição brita - Pedreira Central', '2024-04-05'),
(26, 'entrada', 10, 'Compra herbicida - AgroQuímica', '2024-04-10'),

-- Reposições Maio 2024 (alta temporada)
(1, 'entrada', 150, 'Reposição alta temporada - ABC', '2024-05-01'),
(2, 'entrada', 250, 'Reposição alta temporada - Sementes', '2024-05-01'),
(3, 'entrada', 80, 'Reposição alta temporada - Terra Fértil', '2024-05-03'),
(6, 'entrada', 100, 'Reposição alta temporada - Pisos', '2024-05-05'),
(21, 'entrada', 80, 'Reposição alta temporada - LED Solar', '2024-05-08'),

-- Reposições Junho-Dezembro 2024 (distribuídas)
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

-- Movimentações de SAÍDA (vendas dos pedidos) - Calculadas automaticamente baseadas nos itens
-- Janeiro 2024
INSERT INTO movimentacao_estoque (id_produto, tipo, quantidade, motivo, data) VALUES 
(1, 'saida', 3, 'Venda - Pedido #1', '2024-01-05'),
(14, 'saida', 2, 'Venda - Pedido #1', '2024-01-05'),
(9, 'saida', 1, 'Venda - Pedido #2', '2024-01-15'),
(10, 'saida', 2, 'Venda - Pedido #2', '2024-01-15'),
(13, 'saida', 1, 'Venda - Pedido #2', '2024-01-15'),
(3, 'saida', 10, 'Venda - Pedido #3', '2024-01-25'),
(6, 'saida', 5, 'Venda - Pedido #3', '2024-01-25'),
(22, 'saida', 3, 'Venda - Pedido #3', '2024-01-25'),

-- Fevereiro 2024
(16, 'saida', 1, 'Venda - Pedido #4', '2024-02-05'),
(17, 'saida', 1, 'Venda - Pedido #4', '2024-02-05'),
(20, 'saida', 2, 'Venda - Pedido #4', '2024-02-05'),
(1, 'saida', 15, 'Venda - Pedido #5', '2024-02-14'),
(2, 'saida', 8, 'Venda - Pedido #5', '2024-02-14'),
(5, 'saida', 3, 'Venda - Pedido #5', '2024-02-14'),
(21, 'saida', 2, 'Venda - Pedido #6', '2024-02-28'),
(24, 'saida', 4, 'Venda - Pedido #6', '2024-02-28'),
(25, 'saida', 6, 'Venda - Pedido #6', '2024-02-28'),

-- Março 2024
(4, 'saida', 5, 'Venda - Pedido #7', '2024-03-10'),
(8, 'saida', 2, 'Venda - Pedido #7', '2024-03-10'),
(11, 'saida', 3, 'Venda - Pedido #7', '2024-03-10'),
(12, 'saida', 2, 'Venda - Pedido #8', '2024-03-20'),
(15, 'saida', 1, 'Venda - Pedido #8', '2024-03-20'),
(10, 'saida', 1, 'Venda - Pedido #8', '2024-03-20'),

-- Abril 2024
(18, 'saida', 1, 'Venda - Pedido #9', '2024-04-05'),
(19, 'saida', 1, 'Venda - Pedido #9', '2024-04-05'),
(23, 'saida', 2, 'Venda - Pedido #9', '2024-04-05'),
(6, 'saida', 8, 'Venda - Pedido #10', '2024-04-18'),
(7, 'saida', 1, 'Venda - Pedido #10', '2024-04-18'),
(25, 'saida', 10, 'Venda - Pedido #11', '2024-04-25'),
(14, 'saida', 5, 'Venda - Pedido #11', '2024-04-25'),

-- Maio 2024 (continuando as saídas dos pedidos)
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

-- Vendas dos pedidos atrasados (Janeiro-Abril 2024)
(17, 'saida', 2, 'Venda - Pedido #16', '2024-01-08'),
(18, 'saida', 1, 'Venda - Pedido #16', '2024-01-08'),
(21, 'saida', 1, 'Venda - Pedido #16', '2024-01-08'),
(1, 'saida', 6, 'Venda - Pedido #17', '2024-01-18'),
(10, 'saida', 3, 'Venda - Pedido #17', '2024-01-18'),

-- Outras movimentações (perdas, ajustes, devoluções)
(26, 'saida', 1, 'Produto vencido - descarte', '2024-03-15'),
(28, 'saida', 1, 'Produto danificado - descarte', '2024-04-20'),
(3, 'entrada', 5, 'Devolução cliente - Pedido #18', '2024-05-10'),
(14, 'saida', 2, 'Amostra grátis - cliente premium', '2024-05-15'),
(25, 'saida', 3, 'Uso interno - teste qualidade', '2024-05-20'),

-- Movimentações recentes 2025
(1, 'entrada', 80, 'Reposição Janeiro 2025 - ABC', '2025-01-02'),
(2, 'entrada', 150, 'Reposição Janeiro 2025 - Sementes', '2025-01-02'),
(16, 'entrada', 5, 'Reposição equipamentos 2025', '2025-01-05'),
(27, 'entrada', 3, 'Compra bomba - pedidos pendentes', '2025-01-08');

-- ATUALIZAR ESTOQUES DOS PRODUTOS (calculando entradas - saídas)
-- Atualizando baseado nas movimentações acima
UPDATE produto SET quantidade = 
CASE 
    WHEN id = 1 THEN 250 + 100 + 150 + 80 - 3 - 15 - 8 - 6  -- Adubo: 568
    WHEN id = 2 THEN 300 + 200 + 250 + 150 - 8 - 12  -- Semente: 880
    WHEN id = 3 THEN 80 + 50 + 80 + 5 - 10 - 20  -- Terra: 185
    WHEN id = 4 THEN 60 + 40 - 5  -- Substrato: 95
    WHEN id = 5 THEN 45 + 30 - 3 - 6  -- Brita: 66
    WHEN id = 6 THEN 150 + 80 + 100 - 5 - 8 - 15  -- Piso: 302
    WHEN id = 7 THEN 25 + 15 - 1  -- Manta: 39
    WHEN id = 8 THEN 40 + 25 - 2 - 4  -- Casca: 59
    WHEN id = 9 THEN 35 + 20 - 1 - 2  -- Tesoura: 52
    WHEN id = 10 THEN 50 + 30 - 2 - 1 - 3  -- Kit: 74
    WHEN id = 11 THEN 28 + 20 - 3 - 4  -- Pulverizador: 41
    WHEN id = 12 THEN 42 + 25 - 2  -- Enxada: 65
    WHEN id = 13 THEN 60 + 30 - 1  -- Régua: 89
    WHEN id = 14 THEN 80 + 50 - 2 - 5 - 2  -- Borrifador: 121
    WHEN id = 15 THEN 30 + 20 - 1  -- Ancinho: 49
    WHEN id = 16 THEN 18 + 10 + 5 - 1 - 1  -- Cortador: 31
    WHEN id = 17 THEN 12 + 8 - 1 - 2  -- Aspirador: 17
    WHEN id = 18 THEN 8 + 5 - 1 - 1  -- Motosserra: 11
    WHEN id = 19 THEN 15 + 8 - 1  -- Roçadeira: 22
    WHEN id = 20 THEN 25 + 15 - 2  -- Mangueira: 38
    WHEN id = 21 THEN 10 + 15 + 50 + 80 + 40 - 1 - 2 - 6  -- Bancada: 186
    WHEN id = 22 THEN 75 + 50 + 80 + 40 - 6 - 1 - 6  -- LED: 232
    WHEN id = 23 THEN 30 + 25 - 3 - 8  -- Vaso: 44
    WHEN id = 24 THEN 20 + 15 - 2  -- Timer: 33
    WHEN id = 25 THEN 55 + 40 - 4 - 8  -- Suporte: 83
    WHEN id = 26 THEN 90 + 60 - 6 - 10 - 3  -- Fertilizante: 131
    WHEN id = 27 THEN 5 + 10 - 1  -- Herbicida: 14
    WHEN id = 28 THEN 3 + 5 + 3 - 1  -- Bomba: 10
    WHEN id = 29 THEN 2 + 8 - 1  -- Sensor: 9
    WHEN id = 30 THEN 0  -- Adubo Orquídeas: 0 (sem estoque)
    ELSE quantidade
END;

-- ============================================================================
-- CONSULTAS DE VERIFICAÇÃO E VALIDAÇÃO DOS DADOS INSERIDOS
-- ============================================================================

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

-- ============================================================================
-- FIM DOS DADOS DE TESTE - DASHBOARD STUDIO MUDA PAISAGISMO
-- Total de registros criados:
-- - 30 Produtos (variadas categorias, estoques diferentes)
-- - 12 Funcionários (diferentes cargos e performance)
-- - 19 Clientes (PF/PJ, várias cidades)
-- - 13 Cupons (ativos/vencidos, valores variados)
-- - 49 Pedidos (no prazo/atrasados/pendentes, 12 meses de histórico)
-- - 100+ Itens de pedido (quantidades realistas)
-- - 80+ Movimentações de estoque (entradas/saídas/ajustes)
-- ============================================================================
