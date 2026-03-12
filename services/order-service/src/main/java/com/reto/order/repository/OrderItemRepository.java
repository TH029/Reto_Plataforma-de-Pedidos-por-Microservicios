package com.reto.order.repository;

import com.reto.order.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
    
    // Listar items de una orden específica
    List<OrderItemEntity> findByOrdenId(Long ordenId);
}
