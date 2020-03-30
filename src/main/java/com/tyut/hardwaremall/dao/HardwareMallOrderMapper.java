package com.tyut.hardwaremall.dao;

import com.tyut.hardwaremall.entity.HardwareMallOrder;
import com.tyut.hardwaremall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HardwareMallOrderMapper {
    int deleteByPrimaryKey(Long orderId);

    int insert(HardwareMallOrder record);

    int insertSelective(HardwareMallOrder record);

    HardwareMallOrder selectByPrimaryKey(Long orderId);

    HardwareMallOrder selectByOrderNo(String orderNo);

    int updateByPrimaryKeySelective(HardwareMallOrder record);

    int updateByPrimaryKey(HardwareMallOrder record);

    List<HardwareMallOrder> findNewBeeMallOrderList(PageQueryUtil pageUtil);

    int getTotalNewBeeMallOrders(PageQueryUtil pageUtil);

    List<HardwareMallOrder> selectByPrimaryKeys(@Param("orderIds") List<Long> orderIds);

    int checkOut(@Param("orderIds") List<Long> orderIds);

    int closeOrder(@Param("orderIds") List<Long> orderIds, @Param("orderStatus") int orderStatus);

    int checkDone(@Param("orderIds") List<Long> asList);
}