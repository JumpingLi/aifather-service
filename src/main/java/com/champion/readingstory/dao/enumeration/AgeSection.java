package com.champion.readingstory.dao.enumeration;

/**
 * @author: JiangPing Li
 * @date: 2018-09-03 15:55
 */
public enum AgeSection {
    ZERO_TO_TWO("0-3岁"),

    ABOVE_THREE("3岁以上"),

    ABOVE_SIX("6岁以上");

    AgeSection(String ageName) {
        this.ageName = ageName;
    }

    public String getAgeName() {
        return ageName;
    }

    private String ageName;
}
