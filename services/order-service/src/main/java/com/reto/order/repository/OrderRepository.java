package com.reto.order.repository;

import com.reto.order.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    // Listar todos los pedidos de un usuario
    List<OrderEntity> findByUsuarioId(Long usuarioId);


    // Contar pedidos de un usuario
    Long countByUsuarioId(Long usuarioId);
}
