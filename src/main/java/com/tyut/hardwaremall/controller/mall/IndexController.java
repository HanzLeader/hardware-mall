package com.tyut.hardwaremall.controller.mall;

import com.tyut.hardwaremall.common.Constants;
import com.tyut.hardwaremall.common.IndexConfigTypeEnum;
import com.tyut.hardwaremall.controller.vo.HardwareMallIndexCarouselVO;
import com.tyut.hardwaremall.controller.vo.HardwareMallIndexCategoryVO;
import com.tyut.hardwaremall.controller.vo.HardwareMallIndexConfigGoodsVO;
import com.tyut.hardwaremall.service.HardwareMallCarouselService;
import com.tyut.hardwaremall.service.HardwareMallCategoryService;
import com.tyut.hardwaremall.service.HardwareMallIndexConfigService;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController {

    @Resource
    private HardwareMallCarouselService hardwareMallCarouselService;

    @Resource
    private HardwareMallIndexConfigService hardwareMallIndexConfigService;

    @Resource
    private HardwareMallCategoryService hardwareMallCategoryService;

    @GetMapping({"/index", "/", "/index.html"})
    public String indexPage(HttpServletRequest request) {
        List<HardwareMallIndexCategoryVO> categories = hardwareMallCategoryService.getCategoriesForIndex();
        if (CollectionUtils.isEmpty(categories)) {
            return "error/error_5xx";
        }
        List<HardwareMallIndexCarouselVO> carousels = hardwareMallCarouselService.getCarouselsForIndex(Constants.INDEX_CAROUSEL_NUMBER);
        List<HardwareMallIndexConfigGoodsVO> hotGoodses = hardwareMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_HOT.getType(), Constants.INDEX_GOODS_HOT_NUMBER);
        List<HardwareMallIndexConfigGoodsVO> newGoodses = hardwareMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_NEW.getType(), Constants.INDEX_GOODS_NEW_NUMBER);
        List<HardwareMallIndexConfigGoodsVO> recommendGoodses = hardwareMallIndexConfigService.getConfigGoodsesForIndex(IndexConfigTypeEnum.INDEX_GOODS_RECOMMOND.getType(), Constants.INDEX_GOODS_RECOMMOND_NUMBER);
        request.setAttribute("categories", categories);//分类数据
        request.setAttribute("carousels", carousels);//轮播图
        request.setAttribute("hotGoodses", hotGoodses);//热销商品
        request.setAttribute("newGoodses", newGoodses);//新品
        request.setAttribute("recommendGoodses", recommendGoodses);//推荐商品
        return "mall/index";
    }
}
