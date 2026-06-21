-- E-13: converte a PK de meta_indicador de INT AUTO_INCREMENT para VARCHAR(36),
-- alinhando o agregado ao padrão PetCollar (identidade por MetaIndicadorId String/UUID).
--
-- Seguro: nenhuma FK referencia meta_indicador.id (apenas meta_indicador.indicador_id
-- aponta para indicador_operacional, e permanece intacto). As linhas existentes mantêm
-- o id antigo convertido para texto ("1".."N"); novas metas recebem UUID gerado no domínio.
-- O MODIFY é idempotente: em uma base nova (já criada como VARCHAR por setup_database.sql)
-- reaplica o mesmo tipo sem efeito colateral.
ALTER TABLE meta_indicador MODIFY COLUMN id VARCHAR(36) NOT NULL;
