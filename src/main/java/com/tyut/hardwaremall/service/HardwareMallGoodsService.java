package com.tyut.hardwaremall.service;

import com.tyut.hardwaremall.entity.HardwareMallGoods;
import com.tyut.hardwaremall.util.PageQueryUtil;
import com.tyut.hardwaremall.util.PageResult;

import java.util.List;

public interface HardwareMallGoodsService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getNewBeeMallGoodsPage(PageQueryUtil pageUtil);

    /**
     * 添加商品
     *
     * @param goods
     * @return
     */
    String saveNewBeeMallGoods(HardwareMallGoods goods);

    /**
     * 批量新增商品数据
     *
     * @param hardwareMallGoodsList
     * @return
     */
    void batchSaveHardwareMallGoods(List<HardwareMallGoods> hardwareMallGoodsList);

    /**
     * 修改商品信息
     *
     * @param goods
     * @return
     */
    String updateHardwareMallGoods(HardwareMallGoods goods);

    /**
     * 获取商品详情
     *
     * @param id
     * @return
     */
    HardwareMallGoods getHardwareMallGoodsById(Long id);

    /**
     * 批量修改销售状态(上架下架)
     *
     * @param ids
     * @return
     */
    Boolean batchUpdateSellStatus(Long[] ids, int sellStatus);

    /**
     * 商品搜索
     *
     * @param pageUtil
     * @return
     */
    PageResult searchHardwareMallGoods(PageQueryUtil pageUtil);
}
