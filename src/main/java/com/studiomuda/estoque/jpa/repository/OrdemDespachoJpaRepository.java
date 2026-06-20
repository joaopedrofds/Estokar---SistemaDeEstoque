package com.studiomuda.estoque.jpa.repository;
import com.studiomuda.estoque.jpa.entity.OrdemDespachoJpaEntity; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface OrdemDespachoJpaRepository extends JpaRepository<OrdemDespachoJpaEntity,Integer>{List<OrdemDespachoJpaEntity> findAllByOrderByIdDesc(); boolean existsByPedidoId(Integer pedidoId);}
