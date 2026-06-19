package com.studiomuda.estoque.repository;

import com.studiomuda.estoque.model.CreditoCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CreditoClienteRepository extends JpaRepository<CreditoCliente, Integer> {

    List<CreditoCliente> findByClienteId(int clienteId);
    List<CreditoCliente> findByDevolucaoId(int devolucaoId);
    List<CreditoCliente> findByStatus(String status);
}