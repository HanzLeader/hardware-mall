package com.tyut.hardwaremall.service;

import com.tyut.hardwaremall.controller.vo.HardwareMallIndexCategoryVO;
import com.tyut.hardwaremall.controller.vo.SearchPageCategoryVO;
import com.tyut.hardwaremall.entity.GoodsCategory;
import com.tyut.hardwaremall.util.PageQueryUtil;
import com.tyut.hardwaremall.util.PageResult;

import java.util.List;

public interface HardwareMallCategoryService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getCategorisPage(PageQueryUtil pageUtil);

    String saveCategory(GoodsCategory goodsCategory);

    String updateGoodsCategory(GoodsCategory goodsCategory);

    GoodsCategory getGoodsCategoryById(Long id);

    Boolean deleteBatch(Integer[] ids);

    /**
     * 返回分类数据(首页调用)
     *
     * @return
     */
    List<HardwareMallIndexCategoryVO> getCategoriesForIndex();

    /**
     * 返回分类数据(搜索页调用)
     *
     * @param categoryId
     * @return
     */
    SearchPageCategoryVO getCategoriesForSearch(Long categoryId);

    /**
     * 根据parentId和level获取分类列表
     *
     * @param parentIds
     * @param categoryLevel
     * @return
     */
    List<GoodsCategory> selectByLevelAndParentIdsAndNumber(List<Long> parentIds, int categoryLevel);
}
