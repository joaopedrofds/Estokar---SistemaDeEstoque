-- E-13: converte snapshot_indicador.id (e a FK alerta_indicador.snapshot_id que o
-- referencia) de INT para VARCHAR(36), alinhando o agregado SnapshotIndicador ao
-- padrão PetCollar (identidade por SnapshotIndicadorId String/UUID).
--
-- A FK alerta_indicador_ibfk_2 é removida, ambas as colunas convertidas e a FK
-- recriada (ON DELETE CASCADE, como no schema original). As linhas existentes
-- mantêm os ids antigos convertidos para texto ("1".."N"); as referências
-- snapshot_id permanecem consistentes.
ALTER TABLE alerta_indicador DROP FOREIGN KEY alerta_indicador_ibfk_2;

ALTER TABLE alerta_indicador MODIFY COLUMN snapshot_id VARCHAR(36) NOT NULL;
ALTER TABLE snapshot_indicador MODIFY COLUMN id VARCHAR(36) NOT NULL;

ALTER TABLE alerta_indicador
    ADD CONSTRAINT alerta_indicador_ibfk_2 FOREIGN KEY (snapshot_id)
    REFERENCES snapshot_indicador (id) ON DELETE CASCADE;
