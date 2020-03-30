package com.tyut.hardwaremall.service.impl;

import com.tyut.hardwaremall.common.ServiceResultEnum;
import com.tyut.hardwaremall.controller.vo.HardwareMallSearchGoodsVO;
import com.tyut.hardwaremall.dao.HardwareMallGoodsMapper;
import com.tyut.hardwaremall.entity.HardwareMallGoods;
import com.tyut.hardwaremall.service.HardwareMallGoodsService;
import com.tyut.hardwaremall.util.BeanUtil;
import com.tyut.hardwaremall.util.PageQueryUtil;
import com.tyut.hardwaremall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class HardwareMallGoodsServiceImpl implements HardwareMallGoodsService {

    @Autowired
    private HardwareMallGoodsMapper goodsMapper;

    @Override
    public PageResult getNewBeeMallGoodsPage(PageQueryUtil pageUtil) {
        List<HardwareMallGoods> goodsList = goodsMapper.findHardwareMallGoodsList(pageUtil);
        int total = goodsMapper.getTotalHardwareMallGoods(pageUtil);
        PageResult pageResult = new PageResult(goodsList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveNewBeeMallGoods(HardwareMallGoods goods) {
        if (goodsMapper.insertSelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public void batchSaveHardwareMallGoods(List<HardwareMallGoods> hardwareMallGoodsList) {
        if (!CollectionUtils.isEmpty(hardwareMallGoodsList)) {
            goodsMapper.batchInsert(hardwareMallGoodsList);
        }
    }

    @Override
    public String updateHardwareMallGoods(HardwareMallGoods goods) {
        HardwareMallGoods temp = goodsMapper.selectByPrimaryKey(goods.getGoodsId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        goods.setUpdateTime(new Date());
        if (goodsMapper.updateByPrimaryKeySelective(goods) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public HardwareMallGoods getHardwareMallGoodsById(Long id) {
        return goodsMapper.selectByPrimaryKey(id);
    }
    
    @Override
    public Boolean batchUpdateSellStatus(Long[] ids, int sellStatus) {
        return goodsMapper.batchUpdateSellStatus(ids, sellStatus) > 0;
    }

    @Override
    public PageResult searchHardwareMallGoods(PageQueryUtil pageUtil) {
        List<HardwareMallGoods> goodsList = goodsMapper.findHardwareMallGoodsListBySearch(pageUtil);
        int total = goodsMapper.getTotalHardwareMallGoodsBySearch(pageUtil);
        List<HardwareMallSearchGoodsVO> hardwareMallSearchGoodsVOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(goodsList)) {
            hardwareMallSearchGoodsVOS = BeanUtil.copyList(goodsList, HardwareMallSearchGoodsVO.class);
            for (HardwareMallSearchGoodsVO hardwareMallSearchGoodsVO : hardwareMallSearchGoodsVOS) {
                String goodsName = hardwareMallSearchGoodsVO.getGoodsName();
                String goodsIntro = hardwareMallSearchGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 28) {
                    goodsName = goodsName.substring(0, 28) + "...";
                    hardwareMallSearchGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 30) {
                    goodsIntro = goodsIntro.substring(0, 30) + "...";
                    hardwareMallSearchGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        PageResult pageResult = new PageResult(hardwareMallSearchGoodsVOS, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
}
