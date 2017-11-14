package com.fdz.library.selector;

/**
 * @author : fangdazhu
 * @version : 1.0.0
 * @since : 2017/11/13
 * 数据提供者，为列表提供数据
 */

public interface SelectorDataProvider {

    /**
     *
     * @param currentDeep 当前所处的层级
     * @param selectedItem 当前层级选中的Item
     * @param receiver 数据接受者，通过currentDeep、selectedItem处理逻辑后返回下一层级的数据
     */
    void provide(int currentDeep, SelectorItem selectedItem, SelectorDataReceiver receiver);
}
