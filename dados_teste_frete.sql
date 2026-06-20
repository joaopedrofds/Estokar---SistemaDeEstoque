USE studiomuda;
INSERT INTO transportadora (nome,ativo,prazo_medio_dias,observacoes) VALUES ('Rota Certa Express',TRUE,4,'Parceira principal'),('Nordeste Cargo',TRUE,6,'Cobertura regional'),('Estokar Log',TRUE,3,'Operação urbana');
INSERT INTO tabela_contingencia (transportadora_id,cep_inicio,cep_fim,peso_minimo,peso_maximo,valor_frete,ativo) SELECT id,'60000000','60999999',0.001,10.000,24.90,TRUE FROM transportadora WHERE nome='Estokar Log';
INSERT INTO tabela_contingencia (transportadora_id,cep_inicio,cep_fim,peso_minimo,peso_maximo,valor_frete,ativo) SELECT id,'61000000','63999999',0.001,30.000,39.90,TRUE FROM transportadora WHERE nome='Nordeste Cargo';
-- Cache recente para o cenário CEP 60000000, 2 kg, caixa 20x15x10. O SHA-256 corresponde exatamente a esses parâmetros normalizados.
INSERT INTO log_cotacao (usuario_id,cliente_id,pedido_id,cep_destino,peso,comprimento,largura,altura,hash_parametros,valor_cotado,origem_resultado,transportadora_id,data_cotacao)
SELECT u.id,c.id,NULL,'60000000',2.000,20.00,15.00,10.00,SHA2('60000000|2|20|15|10',256),19.90,'API',t.id,NOW()-INTERVAL 5 MINUTE FROM usuario_acesso u JOIN cliente c ON c.id=(SELECT MIN(id) FROM cliente) JOIN transportadora t ON t.nome='Rota Certa Express' ORDER BY u.id LIMIT 1;
-- Cliente bloqueado para demonstrar RN3 sem duplicar qualquer campo de status.
UPDATE cliente SET ativo=FALSE WHERE nome='Cliente Inativo PF';
SELECT 'transportadora' tabela,COUNT(*) total FROM transportadora UNION ALL SELECT 'tabela_contingencia',COUNT(*) FROM tabela_contingencia UNION ALL SELECT 'log_cotacao',COUNT(*) FROM log_cotacao;
