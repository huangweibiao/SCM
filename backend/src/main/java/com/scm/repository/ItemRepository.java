package com.scm.repository;

import com.scm.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * 物料Repository
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByItemCode(String itemCode);
    boolean existsByItemCode(String itemCode);
    List<Item> findByCategoryId(Long categoryId);
    List<Item> findByStatus(Integer status);

    @Query("SELECT i FROM Item i WHERE i.status = 1")
    List<Item> findAllActive();
}
