package com.scm.repository;

import com.scm.entity.DictType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

/**
 * 数据字典类型Repository
 */
@Repository
public interface DictTypeRepository extends JpaRepository<DictType, Long> {
    Optional<DictType> findByDictCode(String dictCode);
    boolean existsByDictCode(String dictCode);
    List<DictType> findByStatus(Integer status);
}
