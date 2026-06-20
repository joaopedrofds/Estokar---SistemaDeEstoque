package com.studiomuda.estoque.jpa.repository;
import com.studiomuda.estoque.jpa.entity.TransportadoraJpaEntity; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface TransportadoraJpaRepository extends JpaRepository<TransportadoraJpaEntity,Integer>{List<TransportadoraJpaEntity> findAllByOrderByNomeAsc(); List<TransportadoraJpaEntity> findByAtivoTrueOrderByNomeAsc();}
