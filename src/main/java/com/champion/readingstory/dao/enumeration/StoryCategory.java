package com.champion.readingstory.dao.enumeration;

/**
 * @author: JiangPing Li
 * @date: 2018-09-03 16:04
 */
public enum StoryCategory {
    STORY("讲故事"),

    POETRY("读诗歌");

    public String getCategoryName() {
        return categoryName;
    }

    private String categoryName;

    StoryCategory(String categoryName) {
        this.categoryName = categoryName;
    }
}
