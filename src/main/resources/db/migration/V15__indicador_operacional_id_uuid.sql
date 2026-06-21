-- E-13: converte a PK de indicador_operacional de INT AUTO_INCREMENT para
-- VARCHAR(36) (UUID), alinhando o agregado raiz ao padrão PetCollar (identidade
-- por IndicadorOperacionalId String/UUID). Complementa V13 (meta_indicador.id) e
-- V14 (snapshot_indicador.id + alerta_indicador.snapshot_id), que já foram VARCHAR.
--
-- As três FKs de entrada para indicador_operacional.id precisam ser removidas
-- antes do MODIFY (MySQL não permite alterar o tipo de uma coluna referenciada por
-- FK) e recriadas depois, com o mesmo nome e ON DELETE CASCADE do schema original:
--   meta_indicador.indicador_id     -> meta_indicador_ibfk_1
--   snapshot_indicador.indicador_id -> snapshot_indicador_ibfk_1
--   alerta_indicador.indicador_id   -> alerta_indicador_ibfk_1
--
-- As linhas existentes mantêm os ids antigos convertidos para texto ("1".."N");
-- as colunas indicador_id que apontam para elas permanecem consistentes. Novos
-- indicadores recebem UUID gerado no domínio.
ALTER TABLE meta_indicador DROP FOREIGN KEY meta_indicador_ibfk_1;
ALTER TABLE snapshot_indicador DROP FOREIGN KEY snapshot_indicador_ibfk_1;
ALTER TABLE alerta_indicador DROP FOREIGN KEY alerta_indicador_ibfk_1;

ALTER TABLE indicador_operacional MODIFY COLUMN id VARCHAR(36) NOT NULL;
ALTER TABLE meta_indicador MODIFY COLUMN indicador_id VARCHAR(36) NOT NULL;
ALTER TABLE snapshot_indicador MODIFY COLUMN indicador_id VARCHAR(36) NOT NULL;
ALTER TABLE alerta_indicador MODIFY COLUMN indicador_id VARCHAR(36) NOT NULL;

ALTER TABLE meta_indicador ADD CONSTRAINT meta_indicador_ibfk_1 FOREIGN KEY (indicador_id) REFERENCES indicador_operacional (id) ON DELETE CASCADE;
ALTER TABLE snapshot_indicador ADD CONSTRAINT snapshot_indicador_ibfk_1 FOREIGN KEY (indicador_id) REFERENCES indicador_operacional (id) ON DELETE CASCADE;
ALTER TABLE alerta_indicador ADD CONSTRAINT alerta_indicador_ibfk_1 FOREIGN KEY (indicador_id) REFERENCES indicador_operacional (id) ON DELETE CASCADE;
