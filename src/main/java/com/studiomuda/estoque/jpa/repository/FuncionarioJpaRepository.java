package com.studiomuda.estoque.jpa.repository;

import com.studiomuda.estoque.jpa.entity.FuncionarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FuncionarioJpaRepository extends JpaRepository<FuncionarioJpaEntity, Integer> {
    List<FuncionarioJpaEntity> findAllByOrderByIdAsc();
    Optional<FuncionarioJpaEntity> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
    boolean existsByCpfAndIdNot(String cpf, Integer id);

    @Query("select distinct case when f.ativo = true then 'ativo' else 'inativo' end from FuncionarioJpaEntity f")
    List<String> listarStatusDistintos();
}
