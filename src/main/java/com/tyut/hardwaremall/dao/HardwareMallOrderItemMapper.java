package com.tyut.hardwaremall.dao;

import com.tyut.hardwaremall.entity.HardwareMallOrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HardwareMallOrderItemMapper {
    int deleteByPrimaryKey(Long orderItemId);

    int insert(HardwareMallOrderItem record);

    int insertSelective(HardwareMallOrderItem record);

    HardwareMallOrderItem selectByPrimaryKey(Long orderItemId);

    /**
     * 根据订单id获取订单项列表
     *
     * @param orderId
     * @return
     */
    List<HardwareMallOrderItem> selectByOrderId(Long orderId);

    /**
     * 根据订单ids获取订单项列表
     *
     * @param orderIds
     * @return
     */
    List<HardwareMallOrderItem> selectByOrderIds(@Param("orderIds") List<Long> orderIds);

    /**
     * 批量insert订单项数据
     *
     * @param orderItems
     * @return
     */
    int insertBatch(@Param("orderItems") List<HardwareMallOrderItem> orderItems);

    int updateByPrimaryKeySelective(HardwareMallOrderItem record);

    int updateByPrimaryKey(HardwareMallOrderItem record);
}