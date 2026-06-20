package com.studiomuda.estoque.jpa.repository;
import com.studiomuda.estoque.jpa.entity.LogCotacaoJpaEntity; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import java.time.LocalDateTime; import java.util.List;
public interface LogCotacaoJpaRepository extends JpaRepository<LogCotacaoJpaEntity,Integer>{
 @Query("select l from LogCotacaoJpaEntity l where l.hashParametros=:hash and l.dataCotacao>=:desde order by l.dataCotacao desc") List<LogCotacaoJpaEntity> buscarCacheRecente(@Param("hash") String hash,@Param("desde") LocalDateTime desde);
 @Query("select count(l) from LogCotacaoJpaEntity l where l.usuarioId=:usuarioId and l.dataCotacao>=:desde and l.origemResultado='API'") long contarCotacoesExternas(@Param("usuarioId") Integer usuarioId,@Param("desde") LocalDateTime desde);
 List<LogCotacaoJpaEntity> findAllByOrderByDataCotacaoDesc();
}
