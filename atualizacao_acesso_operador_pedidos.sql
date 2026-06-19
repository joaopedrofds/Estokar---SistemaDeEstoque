INSERT INTO permissao_perfil (perfil_id, recurso, operacao, permitido)
SELECT perfil_id, recurso, operacao, TRUE
FROM (
    SELECT 3 AS perfil_id, 'PEDIDO' AS recurso, 'LEITURA' AS operacao UNION ALL
    SELECT 3, 'PEDIDO', 'ESCRITA' UNION ALL
    SELECT 3, 'CLIENTE', 'LEITURA' UNION ALL
    SELECT 3, 'PRODUTO', 'LEITURA' UNION ALL
    SELECT 3, 'CUPOM', 'LEITURA' UNION ALL
    SELECT 3, 'FUNCIONARIO', 'LEITURA' UNION ALL
    SELECT 3, 'ESTOQUE', 'LEITURA' UNION ALL
    SELECT 3, 'ESTOQUE', 'ESCRITA'
) AS permissoes
WHERE NOT EXISTS (
    SELECT 1
    FROM permissao_perfil pp
    WHERE pp.perfil_id = permissoes.perfil_id
      AND pp.recurso = permissoes.recurso
      AND pp.operacao = permissoes.operacao
);
