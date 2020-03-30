package com.tyut.hardwaremall.controller.mall;

import com.tyut.hardwaremall.common.Constants;
import com.tyut.hardwaremall.common.ServiceResultEnum;
import com.tyut.hardwaremall.controller.vo.HardwareMallShoppingCartItemVO;
import com.tyut.hardwaremall.controller.vo.HardwareMallUserVO;
import com.tyut.hardwaremall.entity.HardwareMallShoppingCartItem;
import com.tyut.hardwaremall.service.HardwareMallShoppingCartService;
import com.tyut.hardwaremall.util.Result;
import com.tyut.hardwaremall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ShoppingCartController {

    @Resource
    private HardwareMallShoppingCartService hardwareMallShoppingCartService;

    @GetMapping("/shop-cart")
    public String cartListPage(HttpServletRequest request,
                               HttpSession httpSession) {
        HardwareMallUserVO user = (HardwareMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        int itemsTotal = 0;
        int priceTotal = 0;
        List<HardwareMallShoppingCartItemVO> myShoppingCartItems = hardwareMallShoppingCartService.getMyShoppingCartItems(user.getUserId());
        if (!CollectionUtils.isEmpty(myShoppingCartItems)) {
            //购物项总数
            itemsTotal = myShoppingCartItems.stream().mapToInt(HardwareMallShoppingCartItemVO::getGoodsCount).sum();
            if (itemsTotal < 1) {
                return "error/error_5xx";
            }
            //总价
            for (HardwareMallShoppingCartItemVO hardwareMallShoppingCartItemVO : myShoppingCartItems) {
                priceTotal += hardwareMallShoppingCartItemVO.getGoodsCount() * hardwareMallShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                return "error/error_5xx";
            }
        }
        request.setAttribute("itemsTotal", itemsTotal);
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        return "mall/cart";
    }

    @PostMapping("/shop-cart")
    @ResponseBody
    public Result saveNewBeeMallShoppingCartItem(@RequestBody HardwareMallShoppingCartItem hardwareMallShoppingCartItem,
                                                 HttpSession httpSession) {
        HardwareMallUserVO user = (HardwareMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        hardwareMallShoppingCartItem.setUserId(user.getUserId());
        //todo 判断数量
        String saveResult = hardwareMallShoppingCartService.saveHardwareMallCartItem(hardwareMallShoppingCartItem);
        //添加成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(saveResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //添加失败
        return ResultGenerator.genFailResult(saveResult);
    }

    @PutMapping("/shop-cart")
    @ResponseBody
    public Result updateNewBeeMallShoppingCartItem(@RequestBody HardwareMallShoppingCartItem hardwareMallShoppingCartItem,
                                                   HttpSession httpSession) {
        HardwareMallUserVO user = (HardwareMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        hardwareMallShoppingCartItem.setUserId(user.getUserId());
        //todo 判断数量
        String updateResult = hardwareMallShoppingCartService.updateHardwareMallCartItem(hardwareMallShoppingCartItem);
        //修改成功
        if (ServiceResultEnum.SUCCESS.getResult().equals(updateResult)) {
            return ResultGenerator.genSuccessResult();
        }
        //修改失败
        return ResultGenerator.genFailResult(updateResult);
    }

    @DeleteMapping("/shop-cart/{newBeeMallShoppingCartItemId}")
    @ResponseBody
    public Result updateNewBeeMallShoppingCartItem(@PathVariable("newBeeMallShoppingCartItemId") Long newBeeMallShoppingCartItemId,
                                                   HttpSession httpSession) {
        HardwareMallUserVO user = (HardwareMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        Boolean deleteResult = hardwareMallShoppingCartService.deleteById(newBeeMallShoppingCartItemId);
        //删除成功
        if (deleteResult) {
            return ResultGenerator.genSuccessResult();
        }
        //删除失败
        return ResultGenerator.genFailResult(ServiceResultEnum.OPERATE_ERROR.getResult());
    }

    @GetMapping("/shop-cart/settle")
    public String settlePage(HttpServletRequest request,
                             HttpSession httpSession) {
        int priceTotal = 0;
        HardwareMallUserVO user = (HardwareMallUserVO) httpSession.getAttribute(Constants.MALL_USER_SESSION_KEY);
        List<HardwareMallShoppingCartItemVO> myShoppingCartItems = hardwareMallShoppingCartService.getMyShoppingCartItems(user.getUserId());
        if (CollectionUtils.isEmpty(myShoppingCartItems)) {
            //无数据则不跳转至结算页
            return "/shop-cart";
        } else {
            //总价
            for (HardwareMallShoppingCartItemVO hardwareMallShoppingCartItemVO : myShoppingCartItems) {
                priceTotal += hardwareMallShoppingCartItemVO.getGoodsCount() * hardwareMallShoppingCartItemVO.getSellingPrice();
            }
            if (priceTotal < 1) {
                return "error/error_5xx";
            }
        }
        request.setAttribute("priceTotal", priceTotal);
        request.setAttribute("myShoppingCartItems", myShoppingCartItems);
        return "mall/order-settle";
    }
}
