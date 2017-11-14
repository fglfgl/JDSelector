package com.fdz.jdselector;

import android.app.Activity;
import android.util.SparseArray;
import android.widget.Toast;

import com.fdz.library.selector.SelectorDataProvider;
import com.fdz.library.selector.SelectorDataReceiver;
import com.fdz.library.selector.SelectorItem;
import com.fdz.library.selector.ui.JDSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author : fangguiliang
 * @version : 1.0.0
 * @since : 2017/11/14
 */

public class SelectorViewModel implements SelectorDataProvider {
    /**
     * 缓存数据用,index为上一层级选中的Id
     */
    private List<SparseArray<List<SelectorItem>>> tempList = new ArrayList<>();
    private Activity activity;
    private SelectorDialog selectorDialog;

    public SelectorViewModel(Activity activity) {
        this.activity = activity;
    }

    public void showSelector(int deep) {
        tempList.clear();
        for (int i = 0; i < deep; i++) {
            tempList.add(new SparseArray<>());
        }
        selectorDialog = new SelectorDialog(activity);
        JDSelector jdSelector = new JDSelector(activity, deep, null, null);
        jdSelector.setSelectorDataProvider(this);
        jdSelector.setOnSelectedListener(list -> {
            StringBuilder builder = new StringBuilder();
            for (SelectorItem item : list) {
                builder.append(item.getName()).append(" ");
            }
            Toast.makeText(activity, builder.toString(), Toast.LENGTH_LONG).show();
            selectorDialog.dismiss();
        });
        selectorDialog.init(activity, jdSelector);
        selectorDialog.show();
    }

    @Override
    public void provide(int currentDeep, SelectorItem selectedItem, SelectorDataReceiver receiver) {
        // 第一个层级,数据不需要缓存
        if (selectedItem == null) {
            receiver.send(generateSelectorItemList(0), false);
        }
        // 取出缓存数据
        else if (tempList.get(currentDeep).get(selectedItem.getId()) != null) {
            receiver.send(tempList.get(currentDeep).get(selectedItem.getId()), false);
        } else {
            // 随机返回获取失败
            boolean isLoadFail = new Random().nextInt(2) % 2 == 0;
            List<SelectorItem> itemList = null;
            if (!isLoadFail) {
                itemList = generateSelectorItemList(currentDeep + 1);
                tempList.get(currentDeep).append(selectedItem.getId(), itemList);
            }
            receiver.send(itemList, isLoadFail);
        }
    }

    /**
     * 随机生成列表数据，格式："+第xx层第几个
     *
     * @param deep
     * @return
     */
    private List<SelectorItem> generateSelectorItemList(int deep) {
        int count = new Random().nextInt(20) + 1;
        List<SelectorItem> itemList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            final int tempI = i;
            final String title = "第" + deep + "层" + i;
            itemList.add(new SelectorItem() {
                @Override
                public String getName() {
                    return title;
                }

                @Override
                public int getId() {
                    return tempI;
                }

                @Override
                public Object asObject() {
                    return title;
                }
            });
        }
        return itemList;
    }
}
