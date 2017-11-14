package com.fdz.library.selector;

/**
 * @author : fangdazhu
 * @version : 1.0.0
 * @since : 2017/11/13
 * 列表数据Item
 */

public interface SelectorItem {
    /**
     * 显示在列表上的标题
     * @return 返回列表Item标题
     */
    String getName();

    /**
     * 每个Item都应该有对应的唯一Id,方便确定选择状态的同时也可以根据ID做数据缓存
     * @return id
     */
    int getId();

    /**
     * 当前Item对应的数据信息
     * @return 返回Item对应的对象
     */
    Object asObject();
}
