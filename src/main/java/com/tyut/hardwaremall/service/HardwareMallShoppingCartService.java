package com.tyut.hardwaremall.service;

import com.tyut.hardwaremall.controller.vo.HardwareMallShoppingCartItemVO;
import com.tyut.hardwaremall.entity.HardwareMallShoppingCartItem;

import java.util.List;

public interface HardwareMallShoppingCartService {

    /**
     * 保存商品至购物车中
     *
     * @param hardwareMallShoppingCartItem
     * @return
     */
    String saveHardwareMallCartItem(HardwareMallShoppingCartItem hardwareMallShoppingCartItem);

    /**
     * 修改购物车中的属性
     *
     * @param hardwareMallShoppingCartItem
     * @return
     */
    String updateHardwareMallCartItem(HardwareMallShoppingCartItem hardwareMallShoppingCartItem);

    /**
     * 获取购物项详情
     *
     * @param hardwareMallShoppingCartItemId
     * @return
     */
    HardwareMallShoppingCartItem getHardwareMallCartItemById(Long hardwareMallShoppingCartItemId);

    /**
     * 删除购物车中的商品
     *
     * @param hardwareMallShoppingCartItemId
     * @return
     */
    Boolean deleteById(Long hardwareMallShoppingCartItemId);

    /**
     * 获取我的购物车中的列表数据
     *
     * @param hardwareMallUserId
     * @return
     */
    List<HardwareMallShoppingCartItemVO> getMyShoppingCartItems(Long hardwareMallUserId);
}
