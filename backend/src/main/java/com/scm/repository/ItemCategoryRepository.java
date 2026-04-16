package com.scm.repository;

import com.scm.entity.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

/**
 * 物料分类Repository
 */
@Repository
public interface ItemCategoryRepository extends JpaRepository<ItemCategory, Long> {
    Optional<ItemCategory> findByCategoryCode(String categoryCode);
    boolean existsByCategoryCode(String categoryCode);
    List<ItemCategory> findByParentId(Long parentId);
    List<ItemCategory> findByStatus(Integer status);
}
