package com.tyut.hardwaremall.service.impl;

import com.tyut.hardwaremall.common.ServiceResultEnum;
import com.tyut.hardwaremall.controller.vo.HardwareMallIndexConfigGoodsVO;
import com.tyut.hardwaremall.dao.IndexConfigMapper;
import com.tyut.hardwaremall.dao.HardwareMallGoodsMapper;
import com.tyut.hardwaremall.entity.IndexConfig;
import com.tyut.hardwaremall.entity.HardwareMallGoods;
import com.tyut.hardwaremall.service.HardwareMallIndexConfigService;
import com.tyut.hardwaremall.util.BeanUtil;
import com.tyut.hardwaremall.util.PageQueryUtil;
import com.tyut.hardwaremall.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HardwareMallIndexConfigServiceImpl implements HardwareMallIndexConfigService {

    @Autowired
    private IndexConfigMapper indexConfigMapper;

    @Autowired
    private HardwareMallGoodsMapper goodsMapper;

    @Override
    public PageResult getConfigsPage(PageQueryUtil pageUtil) {
        List<IndexConfig> indexConfigs = indexConfigMapper.findIndexConfigList(pageUtil);
        int total = indexConfigMapper.getTotalIndexConfigs(pageUtil);
        PageResult pageResult = new PageResult(indexConfigs, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public String saveIndexConfig(IndexConfig indexConfig) {
        //todo 判断是否存在该商品
        if (indexConfigMapper.insertSelective(indexConfig) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public String updateIndexConfig(IndexConfig indexConfig) {
        //todo 判断是否存在该商品
        IndexConfig temp = indexConfigMapper.selectByPrimaryKey(indexConfig.getConfigId());
        if (temp == null) {
            return ServiceResultEnum.DATA_NOT_EXIST.getResult();
        }
        if (indexConfigMapper.updateByPrimaryKeySelective(indexConfig) > 0) {
            return ServiceResultEnum.SUCCESS.getResult();
        }
        return ServiceResultEnum.DB_ERROR.getResult();
    }

    @Override
    public IndexConfig getIndexConfigById(Long id) {
        return null;
    }

    @Override
    public List<HardwareMallIndexConfigGoodsVO> getConfigGoodsesForIndex(int configType, int number) {
        List<HardwareMallIndexConfigGoodsVO> hardwareMallIndexConfigGoodsVOS = new ArrayList<>(number);
        List<IndexConfig> indexConfigs = indexConfigMapper.findIndexConfigsByTypeAndNum(configType, number);
        if (!CollectionUtils.isEmpty(indexConfigs)) {
            //取出所有的goodsId
            List<Long> goodsIds = indexConfigs.stream().map(IndexConfig::getGoodsId).collect(Collectors.toList());
            List<HardwareMallGoods> hardwareMallGoods = goodsMapper.selectByPrimaryKeys(goodsIds);
            hardwareMallIndexConfigGoodsVOS = BeanUtil.copyList(hardwareMallGoods, HardwareMallIndexConfigGoodsVO.class);
            for (HardwareMallIndexConfigGoodsVO hardwareMallIndexConfigGoodsVO : hardwareMallIndexConfigGoodsVOS) {
                String goodsName = hardwareMallIndexConfigGoodsVO.getGoodsName();
                String goodsIntro = hardwareMallIndexConfigGoodsVO.getGoodsIntro();
                // 字符串过长导致文字超出的问题
                if (goodsName.length() > 30) {
                    goodsName = goodsName.substring(0, 30) + "...";
                    hardwareMallIndexConfigGoodsVO.setGoodsName(goodsName);
                }
                if (goodsIntro.length() > 22) {
                    goodsIntro = goodsIntro.substring(0, 22) + "...";
                    hardwareMallIndexConfigGoodsVO.setGoodsIntro(goodsIntro);
                }
            }
        }
        return hardwareMallIndexConfigGoodsVOS;
    }

    @Override
    public Boolean deleteBatch(Long[] ids) {
        if (ids.length < 1) {
            return false;
        }
        //删除数据
        return indexConfigMapper.deleteBatch(ids) > 0;
    }
}
