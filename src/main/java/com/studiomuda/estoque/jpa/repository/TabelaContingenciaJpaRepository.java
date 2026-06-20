package com.studiomuda.estoque.jpa.repository;
import com.studiomuda.estoque.jpa.entity.TabelaContingenciaJpaEntity; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import java.math.BigDecimal; import java.util.List;
public interface TabelaContingenciaJpaRepository extends JpaRepository<TabelaContingenciaJpaEntity,Integer>{
 List<TabelaContingenciaJpaEntity> findAllByOrderByCepInicioAscPesoMinimoAsc();
 @Query("select t from TabelaContingenciaJpaEntity t where t.ativo=true and :cep between t.cepInicio and t.cepFim and :peso between t.pesoMinimo and t.pesoMaximo order by t.valorFrete asc") List<TabelaContingenciaJpaEntity> buscarAplicaveis(@Param("cep") String cep,@Param("peso") BigDecimal peso);
}
