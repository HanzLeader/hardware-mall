package com.tyut.hardwaremall.service.impl;

import com.tyut.hardwaremall.common.*;
import com.tyut.hardwaremall.controller.vo.*;
import com.tyut.hardwaremall.dao.HardwareMallGoodsMapper;
import com.tyut.hardwaremall.dao.HardwareMallOrderItemMapper;
import com.tyut.hardwaremall.dao.HardwareMallOrderMapper;
import com.tyut.hardwaremall.dao.HardwareMallShoppingCartItemMapper;
import com.tyut.hardwaremall.entity.HardwareMallGoods;
import com.tyut.hardwaremall.entity.HardwareMallOrder;
import com.tyut.hardwaremall.entity.HardwareMallOrderItem;
import com.tyut.hardwaremall.entity.StockNumDTO;
import com.tyut.hardwaremall.service.HardwareMallOrderService;
import com.tyut.hardwaremall.util.BeanUtil;
import com.tyut.hardwaremall.util.NumberUtil;
import com.tyut.hardwaremall.util.PageQueryUtil;
import com.tyut.hardwaremall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class HardwareMallOrderServiceImpl implements HardwareMallOrderService {

    @Autowired
    private HardwareMallOrderMapper hardwareMallOrderMapper;
    @Autowired
    private HardwareMallOrderItemMapper hardwareMallOrderItemMapper;
    @Autowired
    private HardwareMallShoppingCartItemMapper hardwareMallShoppingCartItemMapper;
    @Autowired
    private HardwareMallGoodsMapper hardwareMallGoodsMapper;

    @Override
    public PageResult getHardwareMallOrdersPage(PageQueryUtil pageUtil) {
        List<HardwareMallOrder> hardwareMallOrders = hardwareMallOrderMapper.findNewBeeMallOrderList(pageUtil);
        int total = hardwareMallOrderMapper.getTotalNewBeeMallOrders(pageUtil);
        PageResult pageResult = new PageResult(hardwareMallOrders, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    @Transactional
    public String updateOrderInfo(HardwareMallOrder hardwareMallOrder) {
        HardwareMallOrder temp = hardwareMallOrderMapper.selectByPrimaryKey(hardwareMallOrder.getOrderId());
        //不为空且orderStatus>=0且状态为出库之前可以修改部分信息
        if (temp != null && temp.getOrderStatus() >= 0 && temp.getOrderStatus() < 3) {
            temp.setTotalPrice(hardwareMallOrder.getTotalPrice());
            temp.setUserAddress(hardwareMallOrder.getUserAddress());
            temp.setUpdateTime(new Date());
            if (hardwareMallOrderMapper.updateByPrimaryKeySelective(temp) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            }
            return ServiceResultEnum.DB_ERROR.getResult();
        }
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkDone(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<HardwareMallOrder> orders = hardwareMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (HardwareMallOrder hardwareMallOrder : orders) {
                if (hardwareMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += hardwareMallOrder.getOrderNo() + " ";
                    continue;
                }
                if (hardwareMallOrder.getOrderStatus() != 1) {
                    errorOrderNos += hardwareMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行配货完成操作 修改订单状态和更新时间
                if (hardwareMallOrderMapper.checkDone(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功的订单，无法执行配货完成操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String checkOut(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<HardwareMallOrder> orders = hardwareMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (HardwareMallOrder hardwareMallOrder : orders) {
                if (hardwareMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += hardwareMallOrder.getOrderNo() + " ";
                    continue;
                }
                if (hardwareMallOrder.getOrderStatus() != 1 && hardwareMallOrder.getOrderStatus() != 2) {
                    errorOrderNos += hardwareMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行出库操作 修改订单状态和更新时间
                if (hardwareMallOrderMapper.checkOut(Arrays.asList(ids)) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行出库操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单的状态不是支付成功或配货完成无法执行出库操作";
                } else {
                    return "你选择了太多状态不是支付成功或配货完成的订单，无法执行出库操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String closeOrder(Long[] ids) {
        //查询所有的订单 判断状态 修改状态和更新时间
        List<HardwareMallOrder> orders = hardwareMallOrderMapper.selectByPrimaryKeys(Arrays.asList(ids));
        String errorOrderNos = "";
        if (!CollectionUtils.isEmpty(orders)) {
            for (HardwareMallOrder hardwareMallOrder : orders) {
                // isDeleted=1 一定为已关闭订单
                if (hardwareMallOrder.getIsDeleted() == 1) {
                    errorOrderNos += hardwareMallOrder.getOrderNo() + " ";
                    continue;
                }
                //已关闭或者已完成无法关闭订单
                if (hardwareMallOrder.getOrderStatus() == 4 || hardwareMallOrder.getOrderStatus() < 0) {
                    errorOrderNos += hardwareMallOrder.getOrderNo() + " ";
                }
            }
            if (StringUtils.isEmpty(errorOrderNos)) {
                //订单状态正常 可以执行关闭操作 修改订单状态和更新时间
                if (hardwareMallOrderMapper.closeOrder(Arrays.asList(ids), HardwareMallOrderStatusEnum.ORDER_CLOSED_BY_JUDGE.getOrderStatus()) > 0) {
                    return ServiceResultEnum.SUCCESS.getResult();
                } else {
                    return ServiceResultEnum.DB_ERROR.getResult();
                }
            } else {
                //订单此时不可执行关闭操作
                if (errorOrderNos.length() > 0 && errorOrderNos.length() < 100) {
                    return errorOrderNos + "订单不能执行关闭操作";
                } else {
                    return "你选择的订单不能执行关闭操作";
                }
            }
        }
        //未查询到数据 返回错误提示
        return ServiceResultEnum.DATA_NOT_EXIST.getResult();
    }

    @Override
    @Transactional
    public String saveOrder(HardwareMallUserVO user, List<HardwareMallShoppingCartItemVO> myShoppingCartItems) {
        List<Long> itemIdList = myShoppingCartItems.stream().map(HardwareMallShoppingCartItemVO::getCartItemId).collect(Collectors.toList());
        List<Long> goodsIds = myShoppingCartItems.stream().map(HardwareMallShoppingCartItemVO::getGoodsId).collect(Collectors.toList());
        List<HardwareMallGoods> hardwareMallGoods = hardwareMallGoodsMapper.selectByPrimaryKeys(goodsIds);
        //检查是否包含已下架商品
        List<HardwareMallGoods> goodsListNotSelling = hardwareMallGoods.stream()
                .filter(newBeeMallGoodsTemp -> newBeeMallGoodsTemp.getGoodsSellStatus() != Constants.SELL_STATUS_UP)
                .collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(goodsListNotSelling)) {
            //goodsListNotSelling 对象非空则表示有下架商品
            HardwareMallException.fail(goodsListNotSelling.get(0).getGoodsName() + "已下架，无法生成订单");
        }
        Map<Long, HardwareMallGoods> newBeeMallGoodsMap = hardwareMallGoods.stream().collect(Collectors.toMap(HardwareMallGoods::getGoodsId, Function.identity(), (entity1, entity2) -> entity1));
        //判断商品库存
        for (HardwareMallShoppingCartItemVO shoppingCartItemVO : myShoppingCartItems) {
            //查出的商品中不存在购物车中的这条关联商品数据，直接返回错误提醒
            if (!newBeeMallGoodsMap.containsKey(shoppingCartItemVO.getGoodsId())) {
                HardwareMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
            }
            //存在数量大于库存的情况，直接返回错误提醒
            if (shoppingCartItemVO.getGoodsCount() > newBeeMallGoodsMap.get(shoppingCartItemVO.getGoodsId()).getStockNum()) {
                HardwareMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
            }
        }
        //删除购物项
        if (!CollectionUtils.isEmpty(itemIdList) && !CollectionUtils.isEmpty(goodsIds) && !CollectionUtils.isEmpty(hardwareMallGoods)) {
            if (hardwareMallShoppingCartItemMapper.deleteBatch(itemIdList) > 0) {
                List<StockNumDTO> stockNumDTOS = BeanUtil.copyList(myShoppingCartItems, StockNumDTO.class);
                int updateStockNumResult = hardwareMallGoodsMapper.updateStockNum(stockNumDTOS);
                if (updateStockNumResult < 1) {
                    HardwareMallException.fail(ServiceResultEnum.SHOPPING_ITEM_COUNT_ERROR.getResult());
                }
                //生成订单号
                String orderNo = NumberUtil.genOrderNo();
                int priceTotal = 0;
                //保存订单
                HardwareMallOrder hardwareMallOrder = new HardwareMallOrder();
                hardwareMallOrder.setOrderNo(orderNo);
                hardwareMallOrder.setUserId(user.getUserId());
                hardwareMallOrder.setUserAddress(user.getAddress());
                //总价
                for (HardwareMallShoppingCartItemVO hardwareMallShoppingCartItemVO : myShoppingCartItems) {
                    priceTotal += hardwareMallShoppingCartItemVO.getGoodsCount() * hardwareMallShoppingCartItemVO.getSellingPrice();
                }
                if (priceTotal < 1) {
                    HardwareMallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                hardwareMallOrder.setTotalPrice(priceTotal);
                //todo 订单body字段，用来作为生成支付单描述信息，暂时未接入第三方支付接口，故该字段暂时设为空字符串
                String extraInfo = "";
                hardwareMallOrder.setExtraInfo(extraInfo);
                //生成订单项并保存订单项纪录
                if (hardwareMallOrderMapper.insertSelective(hardwareMallOrder) > 0) {
                    //生成所有的订单项快照，并保存至数据库
                    List<HardwareMallOrderItem> hardwareMallOrderItems = new ArrayList<>();
                    for (HardwareMallShoppingCartItemVO hardwareMallShoppingCartItemVO : myShoppingCartItems) {
                        HardwareMallOrderItem hardwareMallOrderItem = new HardwareMallOrderItem();
                        //使用BeanUtil工具类将newBeeMallShoppingCartItemVO中的属性复制到newBeeMallOrderItem对象中
                        BeanUtil.copyProperties(hardwareMallShoppingCartItemVO, hardwareMallOrderItem);
                        //NewBeeMallOrderMapper文件insert()方法中使用了useGeneratedKeys因此orderId可以获取到
                        hardwareMallOrderItem.setOrderId(hardwareMallOrder.getOrderId());
                        hardwareMallOrderItems.add(hardwareMallOrderItem);
                    }
                    //保存至数据库
                    if (hardwareMallOrderItemMapper.insertBatch(hardwareMallOrderItems) > 0) {
                        //所有操作成功后，将订单号返回，以供Controller方法跳转到订单详情
                        return orderNo;
                    }
                    HardwareMallException.fail(ServiceResultEnum.ORDER_PRICE_ERROR.getResult());
                }
                HardwareMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
            }
            HardwareMallException.fail(ServiceResultEnum.DB_ERROR.getResult());
        }
        HardwareMallException.fail(ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult());
        return ServiceResultEnum.SHOPPING_ITEM_ERROR.getResult();
    }

    @Override
    public HardwareMallOrderDetailVO getOrderDetailByOrderNo(String orderNo, Long userId) {
        HardwareMallOrder hardwareMallOrder = hardwareMallOrderMapper.selectByOrderNo(orderNo);
        if (hardwareMallOrder != null) {
            //todo 验证是否是当前userId下的订单，否则报错
            List<HardwareMallOrderItem> orderItems = hardwareMallOrderItemMapper.selectByOrderId(hardwareMallOrder.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<HardwareMallOrderItemVO> hardwareMallOrderItemVOS = BeanUtil.copyList(orderItems, HardwareMallOrderItemVO.class);
                HardwareMallOrderDetailVO hardwareMallOrderDetailVO = new HardwareMallOrderDetailVO();
                BeanUtil.copyProperties(hardwareMallOrder, hardwareMallOrderDetailVO);
                hardwareMallOrderDetailVO.setOrderStatusString(HardwareMallOrderStatusEnum.getHardwareMallOrderStatusEnumByStatus(hardwareMallOrderDetailVO.getOrderStatus()).getName());
                hardwareMallOrderDetailVO.setPayTypeString(PayTypeEnum.getPayTypeEnumByType(hardwareMallOrderDetailVO.getPayType()).getName());
                hardwareMallOrderDetailVO.setHardwareMallOrderItemVOS(hardwareMallOrderItemVOS);
                return hardwareMallOrderDetailVO;
            }
        }
        return null;
    }

    @Override
    public HardwareMallOrder getHardwareMallOrderByOrderNo(String orderNo) {
        return hardwareMallOrderMapper.selectByOrderNo(orderNo);
    }

    @Override
    public PageResult getMyOrders(PageQueryUtil pageUtil) {
        int total = hardwareMallOrderMapper.getTotalNewBeeMallOrders(pageUtil);
        List<HardwareMallOrder> hardwareMallOrders = hardwareMallOrderMapper.findNewBeeMallOrderList(pageUtil);
        List<HardwareMallOrderListVO> orderListVOS = new ArrayList<>();
        if (total > 0) {
            //数据转换 将实体类转成vo
            orderListVOS = BeanUtil.copyList(hardwareMallOrders, HardwareMallOrderListVO.class);
            //设置订单状态中文显示值
            for (HardwareMallOrderListVO hardwareMallOrderListVO : orderListVOS) {
                hardwareMallOrderListVO.setOrderStatusString(HardwareMallOrderStatusEnum.getHardwareMallOrderStatusEnumByStatus(hardwareMallOrderListVO.getOrderStatus()).getName());
            }
            List<Long> orderIds = hardwareMallOrders.stream().map(HardwareMallOrder::getOrderId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(orderIds)) {
                List<HardwareMallOrderItem> orderItems = hardwareMallOrderItemMapper.selectByOrderIds(orderIds);
                Map<Long, List<HardwareMallOrderItem>> itemByOrderIdMap = orderItems.stream().collect(groupingBy(HardwareMallOrderItem::getOrderId));
                for (HardwareMallOrderListVO hardwareMallOrderListVO : orderListVOS) {
                    //封装每个订单列表对象的订单项数据
                    if (itemByOrderIdMap.containsKey(hardwareMallOrderListVO.getOrderId())) {
                        List<HardwareMallOrderItem> orderItemListTemp = itemByOrderIdMap.get(hardwareMallOrderListVO.getOrderId());
                        //将NewBeeMallOrderItem对象列表转换成NewBeeMallOrderItemVO对象列表
                        List<HardwareMallOrderItemVO> hardwareMallOrderItemVOS = BeanUtil.copyList(orderItemListTemp, HardwareMallOrderItemVO.class);
                        hardwareMallOrderListVO.setHardwareMallOrderItemVOS(hardwareMallOrderItemVOS);
                    }
                }
            }
        }
        PageResult pageResult = new PageResult(orderListVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String cancelOrder(String orderNo, Long userId) {
        HardwareMallOrder hardwareMallOrder = hardwareMallOrderMapper.selectByOrderNo(orderNo);
        if (hardwareMallOrder != null) {
            //todo 验证是否是当前userId下的订单，否则报错
            //todo 订单状态判断
            if (hardwareMallOrderMapper.closeOrder(Collections.singletonList(hardwareMallOrder.getOrderId()), HardwareMallOrderStatusEnum.ORDER_CLOSED_BY_MALLUSER.getOrderStatus()) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String finishOrder(String orderNo, Long userId) {
        HardwareMallOrder hardwareMallOrder = hardwareMallOrderMapper.selectByOrderNo(orderNo);
        if (hardwareMallOrder != null) {
            //todo 验证是否是当前userId下的订单，否则报错
            //todo 订单状态判断
            hardwareMallOrder.setOrderStatus((byte) HardwareMallOrderStatusEnum.ORDER_SUCCESS.getOrderStatus());
            hardwareMallOrder.setUpdateTime(new Date());
            if (hardwareMallOrderMapper.updateByPrimaryKeySelective(hardwareMallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public String paySuccess(String orderNo, int payType) {
        HardwareMallOrder hardwareMallOrder = hardwareMallOrderMapper.selectByOrderNo(orderNo);
        if (hardwareMallOrder != null) {
            //todo 订单状态判断 非待支付状态下不进行修改操作
            hardwareMallOrder.setOrderStatus((byte) HardwareMallOrderStatusEnum.OREDER_PAID.getOrderStatus());
            hardwareMallOrder.setPayType((byte) payType);
            hardwareMallOrder.setPayStatus((byte) PayStatusEnum.PAY_SUCCESS.getPayStatus());
            hardwareMallOrder.setPayTime(new Date());
            hardwareMallOrder.setUpdateTime(new Date());
            if (hardwareMallOrderMapper.updateByPrimaryKeySelective(hardwareMallOrder) > 0) {
                return ServiceResultEnum.SUCCESS.getResult();
            } else {
                return ServiceResultEnum.DB_ERROR.getResult();
            }
        }
        return ServiceResultEnum.ORDER_NOT_EXIST_ERROR.getResult();
    }

    @Override
    public List<HardwareMallOrderItemVO> getOrderItems(Long id) {
        HardwareMallOrder hardwareMallOrder = hardwareMallOrderMapper.selectByPrimaryKey(id);
        if (hardwareMallOrder != null) {
            List<HardwareMallOrderItem> orderItems = hardwareMallOrderItemMapper.selectByOrderId(hardwareMallOrder.getOrderId());
            //获取订单项数据
            if (!CollectionUtils.isEmpty(orderItems)) {
                List<HardwareMallOrderItemVO> hardwareMallOrderItemVOS = BeanUtil.copyList(orderItems, HardwareMallOrderItemVO.class);
                return hardwareMallOrderItemVOS;
            }
        }
        return null;
    }
}