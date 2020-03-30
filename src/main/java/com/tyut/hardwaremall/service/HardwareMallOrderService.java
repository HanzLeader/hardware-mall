package com.tyut.hardwaremall.service;

import com.tyut.hardwaremall.controller.vo.HardwareMallOrderDetailVO;
import com.tyut.hardwaremall.controller.vo.HardwareMallOrderItemVO;
import com.tyut.hardwaremall.controller.vo.HardwareMallShoppingCartItemVO;
import com.tyut.hardwaremall.controller.vo.HardwareMallUserVO;
import com.tyut.hardwaremall.entity.HardwareMallOrder;
import com.tyut.hardwaremall.util.PageQueryUtil;
import com.tyut.hardwaremall.util.PageResult;

import java.util.List;

public interface HardwareMallOrderService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getHardwareMallOrdersPage(PageQueryUtil pageUtil);

    /**
     * 订单信息修改
     *
     * @param hardwareMallOrder
     * @return
     */
    String updateOrderInfo(HardwareMallOrder hardwareMallOrder);

    /**
     * 配货
     *
     * @param ids
     * @return
     */
    String checkDone(Long[] ids);

    /**
     * 出库
     *
     * @param ids
     * @return
     */
    String checkOut(Long[] ids);

    /**
     * 关闭订单
     *
     * @param ids
     * @return
     */
    String closeOrder(Long[] ids);

    /**
     * 保存订单
     *
     * @param user
     * @param myShoppingCartItems
     * @return
     */
    String saveOrder(HardwareMallUserVO user, List<HardwareMallShoppingCartItemVO> myShoppingCartItems);

    /**
     * 获取订单详情
     *
     * @param orderNo
     * @param userId
     * @return
     */
    HardwareMallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId);

    /**
     * 获取订单详情
     *
     * @param orderNo
     * @return
     */
    HardwareMallOrder getHardwareMallOrderByOrderNo(String orderNo);

    /**
     * 我的订单列表
     *
     * @param pageUtil
     * @return
     */
    PageResult getMyOrders(PageQueryUtil pageUtil);

    /**
     * 手动取消订单
     *
     * @param orderNo
     * @param userId
     * @return
     */
    String cancelOrder(String orderNo, Long userId);

    /**
     * 确认收货
     *
     * @param orderNo
     * @param userId
     * @return
     */
    String finishOrder(String orderNo, Long userId);

    String paySuccess(String orderNo, int payType);

    List<HardwareMallOrderItemVO> getOrderItems(Long id);
}
