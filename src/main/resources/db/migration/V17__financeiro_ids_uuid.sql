-- E-17: converte todo o cluster Financeiro de PKs/FKs INT AUTO_INCREMENT para VARCHAR(36),
-- alinhando os agregados ao padrão PetCollar (identidade por UUID String gerado no domínio).
--
-- Colunas convertidas para VARCHAR(36):
--   PKs:  categoria_financeira.id, template_relatorio.id, relatorio_gerado.id
--   Refs: lancamento_ajuste.categoria_id; template_categoria.template_id;
--         template_categoria.categoria_id; template_indicador.template_id;
--         relatorio_gerado.template_id; relatorio_categoria_linha.relatorio_id;
--         relatorio_categoria_linha.categoria_id; relatorio_indicador_linha.relatorio_id
--
-- NAO alteradas (permanecem INT):
--   relatorio_categoria_linha.id e relatorio_indicador_linha.id (continuam INT AUTO_INCREMENT,
--   pois nao sao referenciadas por nenhuma FK e nao fazem parte da identidade de dominio);
--   relatorio_gerado.gerado_por_usuario_id e lancamento_ajuste.usuario_id (FK para
--   usuario_acesso.id, que permanece INT) e suas FKs *_ibfk_2 ficam intactas.
--
-- Coreografia: como o MySQL nao permite MODIFY de colunas envolvidas em FKs,
-- as 8 FKs de entrada do cluster sao DROPADAS, as colunas sao convertidas, e as
-- mesmas 8 FKs sao RECRIADAS com o nome original. Todas as FKs sao ON DELETE NO ACTION
-- (default), conforme referential_constraints; replicamos o comportamento padrao.
-- As PKs compostas de template_categoria(template_id,categoria_id) e
-- template_indicador(template_id,indicador) NAO precisam ser dropadas: o MODIFY das
-- colunas e aceito sem mexer na PRIMARY KEY.

-- =========================================================================
-- 1) DROP das 8 FKs de entrada do cluster Financeiro
-- =========================================================================
ALTER TABLE lancamento_ajuste          DROP FOREIGN KEY lancamento_ajuste_ibfk_1;
ALTER TABLE template_categoria         DROP FOREIGN KEY template_categoria_ibfk_1;
ALTER TABLE template_categoria         DROP FOREIGN KEY template_categoria_ibfk_2;
ALTER TABLE template_indicador         DROP FOREIGN KEY template_indicador_ibfk_1;
ALTER TABLE relatorio_gerado           DROP FOREIGN KEY relatorio_gerado_ibfk_1;
ALTER TABLE relatorio_categoria_linha  DROP FOREIGN KEY relatorio_categoria_linha_ibfk_1;
ALTER TABLE relatorio_categoria_linha  DROP FOREIGN KEY relatorio_categoria_linha_ibfk_2;
ALTER TABLE relatorio_indicador_linha  DROP FOREIGN KEY relatorio_indicador_linha_ibfk_1;

-- =========================================================================
-- 2) MODIFY das colunas para VARCHAR(36) (PKs primeiro, depois colunas de referencia)
--    Remover AUTO_INCREMENT das PKs ao converter para VARCHAR.
-- =========================================================================
ALTER TABLE categoria_financeira       MODIFY COLUMN id VARCHAR(36) NOT NULL;
ALTER TABLE template_relatorio         MODIFY COLUMN id VARCHAR(36) NOT NULL;
ALTER TABLE relatorio_gerado           MODIFY COLUMN id VARCHAR(36) NOT NULL;

ALTER TABLE lancamento_ajuste          MODIFY COLUMN categoria_id VARCHAR(36) NOT NULL;
ALTER TABLE template_categoria         MODIFY COLUMN template_id  VARCHAR(36) NOT NULL;
ALTER TABLE template_categoria         MODIFY COLUMN categoria_id VARCHAR(36) NOT NULL;
ALTER TABLE template_indicador         MODIFY COLUMN template_id  VARCHAR(36) NOT NULL;
ALTER TABLE relatorio_gerado           MODIFY COLUMN template_id  VARCHAR(36) NOT NULL;
ALTER TABLE relatorio_categoria_linha  MODIFY COLUMN relatorio_id VARCHAR(36) NOT NULL;
ALTER TABLE relatorio_categoria_linha  MODIFY COLUMN categoria_id VARCHAR(36) NOT NULL;
ALTER TABLE relatorio_indicador_linha  MODIFY COLUMN relatorio_id VARCHAR(36) NOT NULL;

-- =========================================================================
-- 3) RE-ADD das 8 FKs com o nome original (ON DELETE NO ACTION = default)
-- =========================================================================
ALTER TABLE lancamento_ajuste
    ADD CONSTRAINT lancamento_ajuste_ibfk_1
    FOREIGN KEY (categoria_id) REFERENCES categoria_financeira(id);

ALTER TABLE template_categoria
    ADD CONSTRAINT template_categoria_ibfk_1
    FOREIGN KEY (template_id) REFERENCES template_relatorio(id);

ALTER TABLE template_categoria
    ADD CONSTRAINT template_categoria_ibfk_2
    FOREIGN KEY (categoria_id) REFERENCES categoria_financeira(id);

ALTER TABLE template_indicador
    ADD CONSTRAINT template_indicador_ibfk_1
    FOREIGN KEY (template_id) REFERENCES template_relatorio(id);

ALTER TABLE relatorio_gerado
    ADD CONSTRAINT relatorio_gerado_ibfk_1
    FOREIGN KEY (template_id) REFERENCES template_relatorio(id);

ALTER TABLE relatorio_categoria_linha
    ADD CONSTRAINT relatorio_categoria_linha_ibfk_1
    FOREIGN KEY (relatorio_id) REFERENCES relatorio_gerado(id);

ALTER TABLE relatorio_categoria_linha
    ADD CONSTRAINT relatorio_categoria_linha_ibfk_2
    FOREIGN KEY (categoria_id) REFERENCES categoria_financeira(id);

ALTER TABLE relatorio_indicador_linha
    ADD CONSTRAINT relatorio_indicador_linha_ibfk_1
    FOREIGN KEY (relatorio_id) REFERENCES relatorio_gerado(id);
