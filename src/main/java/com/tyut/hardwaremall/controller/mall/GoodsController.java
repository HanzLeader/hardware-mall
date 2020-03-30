package com.tyut.hardwaremall.controller.mall;

import com.tyut.hardwaremall.common.Constants;
import com.tyut.hardwaremall.common.HardwareMallException;
import com.tyut.hardwaremall.common.ServiceResultEnum;
import com.tyut.hardwaremall.controller.vo.HardwareMallGoodsDetailVO;
import com.tyut.hardwaremall.controller.vo.SearchPageCategoryVO;
import com.tyut.hardwaremall.entity.HardwareMallGoods;
import com.tyut.hardwaremall.service.HardwareMallCategoryService;
import com.tyut.hardwaremall.service.HardwareMallGoodsService;
import com.tyut.hardwaremall.util.BeanUtil;
import com.tyut.hardwaremall.util.PageQueryUtil;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class GoodsController {

    @Resource
    private HardwareMallGoodsService hardwareMallGoodsService;
    @Resource
    private HardwareMallCategoryService hardwareMallCategoryService;

    @GetMapping({"/search", "/search.html"})
    public String searchPage(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        if (StringUtils.isEmpty(params.get("page"))) {
            params.put("page", 1);
        }
        params.put("limit", Constants.GOODS_SEARCH_PAGE_LIMIT);
        //封装分类数据
        if (params.containsKey("goodsCategoryId") && !StringUtils.isEmpty(params.get("goodsCategoryId") + "")) {
            Long categoryId = Long.valueOf(params.get("goodsCategoryId") + "");
            SearchPageCategoryVO searchPageCategoryVO = hardwareMallCategoryService.getCategoriesForSearch(categoryId);
            if (searchPageCategoryVO != null) {
                request.setAttribute("goodsCategoryId", categoryId);
                request.setAttribute("searchPageCategoryVO", searchPageCategoryVO);
            }
        }
        //封装参数供前端回显
        if (params.containsKey("orderBy") && !StringUtils.isEmpty(params.get("orderBy") + "")) {
            request.setAttribute("orderBy", params.get("orderBy") + "");
        }
        String keyword = "";
        //对keyword做过滤 去掉空格
        if (params.containsKey("keyword") && !StringUtils.isEmpty((params.get("keyword") + "").trim())) {
            keyword = params.get("keyword") + "";
        }
        request.setAttribute("keyword", keyword);
        params.put("keyword", keyword);
        //封装商品数据
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        request.setAttribute("pageResult", hardwareMallGoodsService.searchHardwareMallGoods(pageUtil));
        return "mall/search";
    }

    @GetMapping("/goods/detail/{goodsId}")
    public String detailPage(@PathVariable("goodsId") Long goodsId, HttpServletRequest request) {
        if (goodsId < 1) {
            return "error/error_5xx";
        }
        HardwareMallGoods goods = hardwareMallGoodsService.getHardwareMallGoodsById(goodsId);
        if (goods == null) {
            HardwareMallException.fail(ServiceResultEnum.GOODS_NOT_EXIST.getResult());
        }
        if (Constants.SELL_STATUS_UP != goods.getGoodsSellStatus()){
            HardwareMallException.fail(ServiceResultEnum.GOODS_PUT_DOWN.getResult());
        }
        HardwareMallGoodsDetailVO goodsDetailVO = new HardwareMallGoodsDetailVO();
        BeanUtil.copyProperties(goods, goodsDetailVO);
        goodsDetailVO.setGoodsCarouselList(goods.getGoodsCarousel().split(","));
        request.setAttribute("goodsDetail", goodsDetailVO);
        return "mall/detail";
    }

}
