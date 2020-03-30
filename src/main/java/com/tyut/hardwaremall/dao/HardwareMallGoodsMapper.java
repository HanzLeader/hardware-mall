package com.tyut.hardwaremall.dao;

import com.tyut.hardwaremall.entity.HardwareMallGoods;
import com.tyut.hardwaremall.entity.StockNumDTO;
import com.tyut.hardwaremall.util.PageQueryUtil;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface HardwareMallGoodsMapper {
    int deleteByPrimaryKey(Long goodsId);

    int insert(HardwareMallGoods record);

    int insertSelective(HardwareMallGoods record);

    HardwareMallGoods selectByPrimaryKey(Long goodsId);

    int updateByPrimaryKeySelective(HardwareMallGoods record);

    int updateByPrimaryKeyWithBLOBs(HardwareMallGoods record);

    int updateByPrimaryKey(HardwareMallGoods record);

    List<HardwareMallGoods> findHardwareMallGoodsList(PageQueryUtil pageUtil);

    int getTotalHardwareMallGoods(PageQueryUtil pageUtil);

    List<HardwareMallGoods> selectByPrimaryKeys(List<Long> goodsIds);

    List<HardwareMallGoods> findHardwareMallGoodsListBySearch(PageQueryUtil pageUtil);

    int getTotalHardwareMallGoodsBySearch(PageQueryUtil pageUtil);

    int batchInsert(@Param("hardwareMallGoodsList") List<HardwareMallGoods> hardwareMallGoodsList);

    int updateStockNum(@Param("stockNumDTOS") List<StockNumDTO> stockNumDTOS);

    int batchUpdateSellStatus(@Param("orderIds") Long[] orderIds, @Param("sellStatus") int sellStatus);

}