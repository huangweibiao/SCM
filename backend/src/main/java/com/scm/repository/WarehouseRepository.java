package com.scm.repository;

import com.scm.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * 仓库Repository
 */
@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    Optional<Warehouse> findByWarehouseCode(String warehouseCode);
    boolean existsByWarehouseCode(String warehouseCode);
    List<Warehouse> findByStatus(Integer status);
}
