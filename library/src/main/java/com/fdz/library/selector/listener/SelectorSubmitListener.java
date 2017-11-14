package com.fdz.library.selector.listener;

import com.fdz.library.selector.SelectorItem;

import java.util.List;

/**
 * @author : fangdazhu
 * @version : 1.0.0
 * @since : 2017/11/13
 */

public interface SelectorSubmitListener {
    /**
     * 数据选中回调接口
     *
     * @param selectedList 返回数据，按照层级递增
     */
    void onSelected(List<SelectorItem> selectedList);
}
