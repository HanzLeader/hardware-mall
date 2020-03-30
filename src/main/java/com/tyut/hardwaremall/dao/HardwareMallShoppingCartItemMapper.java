package com.tyut.hardwaremall.dao;

import com.tyut.hardwaremall.entity.HardwareMallShoppingCartItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HardwareMallShoppingCartItemMapper {
    int deleteByPrimaryKey(Long cartItemId);

    int insert(HardwareMallShoppingCartItem record);

    int insertSelective(HardwareMallShoppingCartItem record);

    HardwareMallShoppingCartItem selectByPrimaryKey(Long cartItemId);

    HardwareMallShoppingCartItem selectByUserIdAndGoodsId(@Param("newBeeMallUserId") Long newBeeMallUserId, @Param("goodsId") Long goodsId);

    List<HardwareMallShoppingCartItem> selectByUserId(@Param("newBeeMallUserId") Long newBeeMallUserId, @Param("number") int number);

    int selectCountByUserId(Long newBeeMallUserId);

    int updateByPrimaryKeySelective(HardwareMallShoppingCartItem record);

    int updateByPrimaryKey(HardwareMallShoppingCartItem record);

    int deleteBatch(List<Long> ids);
}