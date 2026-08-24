package org.lefab.order.order.repositories;

import org.lefab.order.order.entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
//    @Query("""
//       SELECT DISTINCT o
//       FROM OrderEntity o
//       LEFT JOIN FETCH o.orderLine
//       """)
//    List<OrderEntity> findAllWithLines();
}
