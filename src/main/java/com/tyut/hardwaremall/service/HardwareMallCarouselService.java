package com.tyut.hardwaremall.service;

import com.tyut.hardwaremall.controller.vo.HardwareMallIndexCarouselVO;
import com.tyut.hardwaremall.entity.Carousel;
import com.tyut.hardwaremall.util.PageQueryUtil;
import com.tyut.hardwaremall.util.PageResult;

import java.util.List;

public interface HardwareMallCarouselService {
    /**
     * 后台分页
     *
     * @param pageUtil
     * @return
     */
    PageResult getCarouselPage(PageQueryUtil pageUtil);

    String saveCarousel(Carousel carousel);

    String updateCarousel(Carousel carousel);

    Carousel getCarouselById(Integer id);

    Boolean deleteBatch(Integer[] ids);

    /**
     * 返回固定数量的轮播图对象(首页调用)
     *
     * @param number
     * @return
     */
    List<HardwareMallIndexCarouselVO> getCarouselsForIndex(int number);
}
