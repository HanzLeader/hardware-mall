package com.tyut.hardwaremall.service.impl;

import com.tyut.hardwaremall.common.Constants;
import com.tyut.hardwaremall.common.ServiceResultEnum;
import com.tyut.hardwaremall.controller.vo.HardwareMallShoppingCartItemVO;
import com.tyut.hardwaremall.dao.HardwareMallGoodsMapper;
import com.tyut.hardwaremall.dao.HardwareMallShoppingCartItemMapper;
import com.tyut.hardwaremall.entity.HardwareMallGoods;
import com.tyut.hardwaremall.entity.HardwareMallShoppingCartItem;
import com.tyut.hardwaremall.service.HardwareMallShoppingCartService;
import com.tyut.hardwaremall.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class HardwareMallShoppingCartServiceImpl implements HardwareMallShoppingCartService {

    @Autowired
    private HardwareMallShoppingCartItemMapper hardwareMallShoppingCartItemMapper;

    @Autowired
    private HardwareMallGoodsMapper hardwareMallGoodsMapper;

    //todo 修改session中购物项数量

    @Override
    public String saveHardwareMallCartItem(HardwareMallShoppingCartItem hardwareMallShoppingCartItem) {
        HardwareMallShoppingCartItem temp = hardwareMallShoppingCartItemMapper.selectByUserIdAndGoodsId(hardwareMallShoppingCartItem.getUserId(), hardwareMallShoppingCartItem.getGoodsId());
        if (temp != null) {
            //已存在则修改该记录
            //todo count = tempCount + 1
            temp.setGoodsCount(hardwareMallShoppingCartItem.getGoodsCount());
            return updateHardwareMallCartItem(temp);
        }
        HardwareMallGoods hardwareMallGoods = hardwareMallGoodsMapper.selectByPrimaryKey(hardwareMallShoppingCartItem.getGoodsId());
        //商品为空
        if (hardwareMallGoods == null) {
            return ServiceResultEnum.GOODS_NOT_EXIST.getResult();
        }
        int totalItem = hardwareMallShoppingCartItemMapper.selectCountByUserId(hardwareMallShoppingCartItem.getUserId()) + 1;
        //超出单个商品的最大数量
        if (hardwareMallShoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //超出最大数量
        if (totalItem > Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_TOTAL_NUMBER_ERROR.getResult();
        }
        //保存记录
        if (hardwareMallShoppingCartItemMapper.insertSelective(hardwareMallShoppingCartItem) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateHardwareMallCartItem(HardwareMallShoppingCartItem hardwareMallShoppingCartItem) {
        HardwareMallShoppingCartItem hardwareMallShoppingCartItemUpdate = hardwareMallShoppingCartItemMapper.selectByPrimaryKey(hardwareMallShoppingCartItem.getCartItemId());
        if (hardwareMallShoppingCartItemUpdate == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        //超出单个商品的最大数量
        if (hardwareMallShoppingCartItem.getGoodsCount() > Constants.SHOPPING_CART_ITEM_LIMIT_NUMBER) {
            return ServiceResultEnum.SHOPPING_CART_ITEM_LIMIT_NUMBER_ERROR.getResult();
        }
        //todo 数量相同不会进行修改
        //todo userId不同不能修改
        hardwareMallShoppingCartItemUpdate.setGoodsCount(hardwareMallShoppingCartItem.getGoodsCount());
        hardwareMallShoppingCartItemUpdate.setUpdateTime(new Date());
        //修改记录
        if (hardwareMallShoppingCartItemMapper.updateByPrimaryKeySelective(hardwareMallShoppingCartItemUpdate) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public HardwareMallShoppingCartItem getHardwareMallCartItemById(Long newBeeMallShoppingCartItemId) {
        return hardwareMallShoppingCartItemMapper.selectByPrimaryKey(newBeeMallShoppingCartItemId);
    }

    @Override
    public Boolean deleteById(Long newBeeMallShoppingCartItemId) {
        //todo userId不同不能删除
        return hardwareMallShoppingCartItemMapper.deleteByPrimaryKey(newBeeMallShoppingCartItemId) > 0;
    }

    @Override
    public List<HardwareMallShoppingCartItemVO> getMyShoppingCartItems(Long newBeeMallUserId) {
        List<HardwareMallShoppingCartItemVO> hardwareMallShoppingCartItemVOS = new ArrayList<>();
        List<HardwareMallShoppingCartItem> hardwareMallShoppingCartItems = hardwareMallShoppingCartItemMapper.selectByUserId(newBeeMallUserId, Constants.SHOPPING_CART_ITEM_TOTAL_NUMBER);
        if (!CollectionUtils.isEmpty(hardwareMallShoppingCartItems)) {
            //查询商品信息并做数据转换
            List<Long> newBeeMallGoodsIds = hardwareMallShoppingCartItems.stream().map(HardwareMallShoppingCartItem::getGoodsId).collect(Collectors.toList());
            List<HardwareMallGoods> hardwareMallGoods = hardwareMallGoodsMapper.selectByPrimaryKeys(newBeeMallGoodsIds);
            Map<Long, HardwareMallGoods> newBeeMallGoodsMap = new HashMap<>();
            if (!CollectionUtils.isEmpty(hardwareMallGoods)) {
                newBeeMallGoodsMap = hardwareMallGoods.stream().collect(Collectors.toMap(HardwareMallGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
            }
            for (HardwareMallShoppingCartItem hardwareMallShoppingCartItem : hardwareMallShoppingCartItems) {
                HardwareMallShoppingCartItemVO hardwareMallShoppingCartItemVO = new HardwareMallShoppingCartItemVO();
                BeanUtil.copyProperties(hardwareMallShoppingCartItem, hardwareMallShoppingCartItemVO);
                if (newBeeMallGoodsMap.containsKey(hardwareMallShoppingCartItem.getGoodsId())) {
                    HardwareMallGoods hardwareMallGoodsTemp = newBeeMallGoodsMap.get(hardwareMallShoppingCartItem.getGoodsId());
                    hardwareMallShoppingCartItemVO.setGoodsCoverImg(hardwareMallGoodsTemp.getGoodsCoverImg());
                    String goodsName = hardwareMallGoodsTemp.getGoodsName();
                    // 字符串过长导致文字超出的问题
                    if (goodsName.length() > 28) {
                        goodsName = goodsName.substring(0, 28) + "...";
                    }
                    hardwareMallShoppingCartItemVO.setGoodsName(goodsName);
                    hardwareMallShoppingCartItemVO.setSellingPrice(hardwareMallGoodsTemp.getSellingPrice());
                    hardwareMallShoppingCartItemVOS.add(hardwareMallShoppingCartItemVO);
                }
            }
        }
        return hardwareMallShoppingCartItemVOS;
    }
}
