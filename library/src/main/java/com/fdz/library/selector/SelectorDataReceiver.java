package com.fdz.library.selector;

import java.util.List;

/**
 * @author : fangdazhu
 * @version : 1.0.0
 * @since : 2017/11/13
 */

public interface SelectorDataReceiver {
    /**
     * 通过send方法发送数据给内容提供者
     * @param itemList 列表数据
     * @param isFail 当前数据数据是否获取正常，使用者可以通过这个状态做不同的控制，如数据提供失败，展示失败的界面
     */
    void send(List<SelectorItem> itemList, boolean isFail);
}
