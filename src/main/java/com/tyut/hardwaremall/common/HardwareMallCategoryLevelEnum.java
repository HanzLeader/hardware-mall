package com.tyut.hardwaremall.common;

/**
 * @apiNote 分类级别
 */
public enum HardwareMallCategoryLevelEnum {

    DEFAULT(0, "ERROR"),
    LEVEL_ONE(1, "一级分类"),
    LEVEL_TWO(2, "二级分类"),
    LEVEL_THREE(3, "三级分类");

    private int level;

    private String name;

    HardwareMallCategoryLevelEnum(int level, String name) {
        this.level = level;
        this.name = name;
    }

    public static HardwareMallCategoryLevelEnum getHardwareMallOrderStatusEnumByLevel(int level) {
        for (HardwareMallCategoryLevelEnum hardwareMallCategoryLevelEnum : HardwareMallCategoryLevelEnum.values()) {
            if (hardwareMallCategoryLevelEnum.getLevel() == level) {
                return hardwareMallCategoryLevelEnum;
            }
        }
        return DEFAULT;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
