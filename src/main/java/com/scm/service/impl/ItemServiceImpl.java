package com.scm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scm.common.exception.BusinessException;
import com.scm.common.util.BeanUtils;
import com.scm.common.util.StringUtils;
import com.scm.dto.ItemDTO;
import com.scm.entity.Item;
import com.scm.mapper.ItemMapper;
import com.scm.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 物料Service实现类
 *
 * @author SCM System
 * @since 2026-04-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements ItemService {

    @Override
    public PageResult<Item> pageQuery(String itemCode, String itemName, Long categoryId, Integer status, Integer pageNum, Integer pageSize) {
        Page<Item> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Item> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(itemCode), Item::getItemCode, itemCode)
                .like(StringUtils.isNotEmpty(itemName), Item::getItemName, itemName)
                .eq(categoryId != null, Item::getCategoryId, categoryId)
                .eq(status != null, Item::getStatus, status)
                .orderByDesc(Item::getCreateTime);
        Page<Item> result = this.page(page, wrapper);
        return PageResult.of(result.getTotal(), result.getRecords());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(ItemDTO itemDTO) {
        // 检查物料编码是否存在
        Long count = this.lambdaQuery()
                .eq(Item::getItemCode, itemDTO.getItemCode())
                .count();
        if (count > 0) {
            throw new BusinessException("物料编码已存在");
        }

        Item item = new Item();
        BeanUtils.copyProperties(itemDTO, item);
        item.setStatus(1); // 默认启用
        this.save(item);
        log.info("创建物料成功，物料id: {}, 物料编码: {}", item.getId(), item.getItemCode());
        return item.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(ItemDTO itemDTO) {
        Item existItem = this.getById(itemDTO.getId());
        if (existItem == null) {
            throw new BusinessException("物料不存在");
        }

        // 检查物料编码是否重复
        if (!existItem.getItemCode().equals(itemDTO.getItemCode())) {
            Long count = this.lambdaQuery()
                    .eq(Item::getItemCode, itemDTO.getItemCode())
                    .ne(Item::getId, itemDTO.getId())
                    .count();
            if (count > 0) {
                throw new BusinessException("物料编码已存在");
            }
        }

        Item item = new Item();
        BeanUtils.copyProperties(itemDTO, item);
        boolean result = this.updateById(item);
        log.info("更新物料成功，物料id: {}", item.getId());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(Long id) {
        Item item = this.getById(id);
        if (item == null) {
            throw new BusinessException("物料不存在");
        }

        // TODO: 校验物料是否被使用（采购订单明细、销售订单明细、库存等）

        boolean result = this.removeById(id);
        log.info("删除物料成功，物料id: {}", id);
        return result;
    }
}
