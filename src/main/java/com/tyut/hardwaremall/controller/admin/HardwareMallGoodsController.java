package com.tyut.hardwaremall.controller.admin;

import com.tyut.hardwaremall.common.Constants;
import com.tyut.hardwaremall.common.HardwareMallCategoryLevelEnum;
import com.tyut.hardwaremall.common.ServiceResultEnum;
import com.tyut.hardwaremall.entity.GoodsCategory;
import com.tyut.hardwaremall.entity.HardwareMallGoods;
import com.tyut.hardwaremall.service.HardwareMallCategoryService;
import com.tyut.hardwaremall.service.HardwareMallGoodsService;
import com.tyut.hardwaremall.util.PageQueryUtil;
import com.tyut.hardwaremall.util.Result;
import com.tyut.hardwaremall.util.ResultGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@Controller
@RequestMapping("/admin")
public class HardwareMallGoodsController {

    @Resource
    private HardwareMallGoodsService hardwareMallGoodsService;
    @Resource
    private HardwareMallCategoryService hardwareMallCategoryService;

    @GetMapping("/goods")
    public String goodsPage(HttpServletRequest request) {
        request.setAttribute("path", "newbee_mall_goods");
        return "admin/hardware_mall_goods";
    }

    @GetMapping("/goods/edit")
    public String edit(HttpServletRequest request) {
        request.setAttribute("path", "edit");
        //查询所有的一级分类
        List<GoodsCategory> firstLevelCategories = hardwareMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), HardwareMallCategoryLevelEnum.LEVEL_ONE.getLevel());
        if (!CollectionUtils.isEmpty(firstLevelCategories)) {
            //查询一级分类列表中第一个实体的所有二级分类
            List<GoodsCategory> secondLevelCategories = hardwareMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getCategoryId()), HardwareMallCategoryLevelEnum.LEVEL_TWO.getLevel());
            if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                //查询二级分类列表中第一个实体的所有三级分类
                List<GoodsCategory> thirdLevelCategories = hardwareMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), HardwareMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                request.setAttribute("firstLevelCategories", firstLevelCategories);
                request.setAttribute("secondLevelCategories", secondLevelCategories);
                request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                request.setAttribute("path", "goods-edit");
                return "admin/hardware_mall_goods_edit";
            }
        }
        return "error/error_5xx";
    }

    @GetMapping("/goods/edit/{goodsId}")
    public String edit(HttpServletRequest request, @PathVariable("goodsId") Long goodsId) {
        request.setAttribute("path", "edit");
        HardwareMallGoods hardwareMallGoods = hardwareMallGoodsService.getHardwareMallGoodsById(goodsId);
        if (hardwareMallGoods == null) {
            return "error/error_400";
        }
        if (hardwareMallGoods.getGoodsCategoryId() > 0) {
            if (hardwareMallGoods.getGoodsCategoryId() != null || hardwareMallGoods.getGoodsCategoryId() > 0) {
                //有分类字段则查询相关分类数据返回给前端以供分类的三级联动显示
                GoodsCategory currentGoodsCategory = hardwareMallCategoryService.getGoodsCategoryById(hardwareMallGoods.getGoodsCategoryId());
                //商品表中存储的分类id字段为三级分类的id，不为三级分类则是错误数据
                if (currentGoodsCategory != null && currentGoodsCategory.getCategoryLevel() == HardwareMallCategoryLevelEnum.LEVEL_THREE.getLevel()) {
                    //查询所有的一级分类
                    List<GoodsCategory> firstLevelCategories = hardwareMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), HardwareMallCategoryLevelEnum.LEVEL_ONE.getLevel());
                    //根据parentId查询当前parentId下所有的三级分类
                    List<GoodsCategory> thirdLevelCategories = hardwareMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(currentGoodsCategory.getParentId()), HardwareMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                    //查询当前三级分类的父级二级分类
                    GoodsCategory secondCategory = hardwareMallCategoryService.getGoodsCategoryById(currentGoodsCategory.getParentId());
                    if (secondCategory != null) {
                        //根据parentId查询当前parentId下所有的二级分类
                        List<GoodsCategory> secondLevelCategories = hardwareMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondCategory.getParentId()), HardwareMallCategoryLevelEnum.LEVEL_TWO.getLevel());
                        //查询当前二级分类的父级一级分类
                        GoodsCategory firestCategory = hardwareMallCategoryService.getGoodsCategoryById(secondCategory.getParentId());
                        if (firestCategory != null) {
                            //所有分类数据都得到之后放到request对象中供前端读取
                            request.setAttribute("firstLevelCategories", firstLevelCategories);
                            request.setAttribute("secondLevelCategories", secondLevelCategories);
                            request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                            request.setAttribute("firstLevelCategoryId", firestCategory.getCategoryId());
                            request.setAttribute("secondLevelCategoryId", secondCategory.getCategoryId());
                            request.setAttribute("thirdLevelCategoryId", currentGoodsCategory.getCategoryId());
                        }
                    }
                }
            }
        }
        if (hardwareMallGoods.getGoodsCategoryId() == 0) {
            //查询所有的一级分类
            List<GoodsCategory> firstLevelCategories = hardwareMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(0L), HardwareMallCategoryLevelEnum.LEVEL_ONE.getLevel());
            if (!CollectionUtils.isEmpty(firstLevelCategories)) {
                //查询一级分类列表中第一个实体的所有二级分类
                List<GoodsCategory> secondLevelCategories = hardwareMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(firstLevelCategories.get(0).getCategoryId()), HardwareMallCategoryLevelEnum.LEVEL_TWO.getLevel());
                if (!CollectionUtils.isEmpty(secondLevelCategories)) {
                    //查询二级分类列表中第一个实体的所有三级分类
                    List<GoodsCategory> thirdLevelCategories = hardwareMallCategoryService.selectByLevelAndParentIdsAndNumber(Collections.singletonList(secondLevelCategories.get(0).getCategoryId()), HardwareMallCategoryLevelEnum.LEVEL_THREE.getLevel());
                    request.setAttribute("firstLevelCategories", firstLevelCategories);
                    request.setAttribute("secondLevelCategories", secondLevelCategories);
                    request.setAttribute("thirdLevelCategories", thirdLevelCategories);
                }
            }
        }
        request.setAttribute("goods", hardwareMallGoods);
        request.setAttribute("path", "goods-edit");
        return "admin/hardware_mall_goods_edit";
    }

    /**
     * 列表
     */
    @RequestMapping(value = "/goods/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageQueryUtil pageUtil = new PageQueryUtil(params);
        return ResultGenerator.genSuccessResult(hardwareMallGoodsService.getNewBeeMallGoodsPage(pageUtil));
    }

    /**
     * 添加
     */
    @RequestMapping(value = "/goods/save", method = RequestMethod.POST)
    @ResponseBody
    public Result save(@RequestBody HardwareMallGoods hardwareMallGoods) {
        if (StringUtils.isEmpty(hardwareMallGoods.getGoodsName())
                || StringUtils.isEmpty(hardwareMallGoods.getGoodsIntro())
                || StringUtils.isEmpty(hardwareMallGoods.getTag())
                || Objects.isNull(hardwareMallGoods.getOriginalPrice())
                || Objects.isNull(hardwareMallGoods.getGoodsCategoryId())
                || Objects.isNull(hardwareMallGoods.getSellingPrice())
                || Objects.isNull(hardwareMallGoods.getStockNum())
                || Objects.isNull(hardwareMallGoods.getGoodsSellStatus())
                || StringUtils.isEmpty(hardwareMallGoods.getGoodsCoverImg())
                || StringUtils.isEmpty(hardwareMallGoods.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = hardwareMallGoodsService.saveNewBeeMallGoods(hardwareMallGoods);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }


    /**
     * 修改
     */
    @RequestMapping(value = "/goods/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestBody HardwareMallGoods HardwareMallGoods) {
        if (Objects.isNull(HardwareMallGoods.getGoodsId())
                || StringUtils.isEmpty(HardwareMallGoods.getGoodsName())
                || StringUtils.isEmpty(HardwareMallGoods.getGoodsIntro())
                || StringUtils.isEmpty(HardwareMallGoods.getTag())
                || Objects.isNull(HardwareMallGoods.getOriginalPrice())
                || Objects.isNull(HardwareMallGoods.getSellingPrice())
                || Objects.isNull(HardwareMallGoods.getGoodsCategoryId())
                || Objects.isNull(HardwareMallGoods.getStockNum())
                || Objects.isNull(HardwareMallGoods.getGoodsSellStatus())
                || StringUtils.isEmpty(HardwareMallGoods.getGoodsCoverImg())
                || StringUtils.isEmpty(HardwareMallGoods.getGoodsDetailContent())) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        String result = hardwareMallGoodsService.updateHardwareMallGoods(HardwareMallGoods);
        if (ServiceResultEnum.SUCCESS.getResult().equals(result)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult(result);
        }
    }

    /**
     * 详情
     */
    @GetMapping("/goods/info/{id}")
    @ResponseBody
    public Result info(@PathVariable("id") Long id) {
        HardwareMallGoods goods = hardwareMallGoodsService.getHardwareMallGoodsById(id);
        if (goods == null) {
            return ResultGenerator.genFailResult(ServiceResultEnum.DATA_NOT_EXIST.getResult());
        }
        return ResultGenerator.genSuccessResult(goods);
    }

    /**
     * 批量修改销售状态
     */
    @RequestMapping(value = "/goods/status/{sellStatus}", method = RequestMethod.PUT)
    @ResponseBody
    public Result delete(@RequestBody Long[] ids, @PathVariable("sellStatus") int sellStatus) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        if (sellStatus != Constants.SELL_STATUS_UP && sellStatus != Constants.SELL_STATUS_DOWN) {
            return ResultGenerator.genFailResult("状态异常！");
        }
        if (hardwareMallGoodsService.batchUpdateSellStatus(ids, sellStatus)) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("修改失败");
        }
    }

}