package com.scm.repository;

import com.scm.entity.DictData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 数据字典数据Repository
 */
@Repository
public interface DictDataRepository extends JpaRepository<DictData, Long> {
    List<DictData> findByDictTypeId(Long dictTypeId);
    List<DictData> findByStatus(Integer status);
}
