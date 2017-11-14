package com.fdz.library.selector.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fdz.library.R;
import com.fdz.library.selector.SelectorDataProvider;
import com.fdz.library.selector.listener.SelectorSubmitListener;
import com.fdz.library.selector.SelectorItem;
import com.fdz.library.selector.adapter.SelectorAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : fangdazhu
 * @version : 1.0.0
 * @since : 2017/11/13
 * 仿京东地址选择器
 */

public class JDSelector {
    private int tabIndex;
    private View indicator;
    private View contextView;
    private ViewPager viewPager;
    private Context context;
    private boolean isLoadSuccess;
    private SelectorSubmitListener onSelectedListener;

    /**
     * 每一层选中的数据
     */
    private List<SelectorItem> selectedList = new ArrayList<>();
    /**
     * 标签指示器
     */
    private List<TextView> tabViewList = new ArrayList<>();
    /**
     * 页面列表数据
     */
    private List<List<SelectorItem>> viewPagerDataList = new ArrayList<>();
    /**
     * viewPager 页面
     */
    private List<ListView> viewPagerViewList = new ArrayList<>();

    /**
     * 数据提供者
     */
    private SelectorDataProvider selectorDataProvider;
    /**
     * 最大层级
     */
    private int maxDeep;

    /**
     * 最后选中的Item
     */
    private SelectorItem lastSelectedItem;


    public JDSelector(Context context, int maxDeep) {
        this(context, maxDeep, null, null);
    }

    public JDSelector(Context context, int maxDeep, List<SelectorItem> selectedList, List<List<SelectorItem>> dataList) {
        this.context = context;
        this.maxDeep = maxDeep;
        initData(selectedList);
        initViews(dataList);
        updateTabsStatus();
        updateIndicator(0);
    }

    private void initData(List<SelectorItem> selectedList) {
        int selectedSize = selectedList == null ? 0 : selectedList.size();
        for (int i = 0; i < maxDeep; i++) {
            viewPagerDataList.add(new ArrayList<>());
            if (selectedSize > 0 && selectedSize > i) {
                this.selectedList.add(selectedList.get(i));
            } else {
                this.selectedList.add(null);
            }
        }
    }

    private void initViews(List<List<SelectorItem>> dataList) {
        contextView = LayoutInflater.from(context).inflate(R.layout.jd_ui_selector_view, null);
        LinearLayout llTabLayout = contextView.findViewById(R.id.layout_tab);
        contextView.findViewById(R.id.ll_selector_status).setOnTouchListener((v, e) -> true);
        int dataSize = dataList == null ? 0 : dataList.size();
        for (int i = 0; i < maxDeep; i++) {
            final int finalI = i;
            // 初始化页面列表
            ListView listView = (ListView) LayoutInflater.from(context).inflate(R.layout.jd_ui_selector_page, null);
            listView.setAdapter(new SelectorAdapter(viewPagerDataList.get(i)));
            listView.setOnItemClickListener(onItemClickListener);
            viewPagerViewList.add(listView);
            // 初始化导航内容
            TextView textView = (TextView) LayoutInflater.from(context).inflate(R.layout.jd_ui_selector_tab_view, llTabLayout, false);
            llTabLayout.addView(textView);
            textView.setOnClickListener(v -> {
                if (isLoadSuccess) {
                    viewPager.setCurrentItem(finalI);
                }
            });
            tabViewList.add(textView);
            // 初始化数据还原选中状态,并把选中Item置顶
            final List<SelectorItem> datList = dataSize > 0 && dataSize > i ? dataList.get(i) : null;
            if (datList != null && !datList.isEmpty()) {
                SelectorItem select = this.selectedList.get(i);
                if (select != null && datList.indexOf(select) != -1) {
                    listView.setSelection(datList.indexOf(select));
                }
                updateListViewData(datList, i, select);
            }
        }
        contextView.findViewById(R.id.bt_try_again).setOnClickListener(v -> getAndSetDeepData(lastSelectedItem, tabIndex));
        indicator = contextView.findViewById(R.id.indicator);
        viewPager = contextView.findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(viewPagerViewList.size());
        viewPager.addOnPageChangeListener(onPageChangeListener);
        viewPager.setAdapter(selectorAdapter);
    }

    public View getContextView() {
        return contextView;
    }

    /**
     * 设置数据来源 同时加载下一级别数据
     *
     * @param selectorDataProvider 数据提供者
     */
    public void setSelectorDataProvider(SelectorDataProvider selectorDataProvider) {
        this.selectorDataProvider = selectorDataProvider;
        int currentDeep = 0;
        for (int i = maxDeep; i > 0; i--) {
            currentDeep = i - 1;
            if (selectedList.get(currentDeep) != null) {
                break;
            }
        }
        getAndSetDeepData(selectedList.get(currentDeep), currentDeep);
    }

    /**
     * @param selectorItem 上一级选中选中Item
     * @param currentDeep  当前层级
     */
    private void getAndSetDeepData(final SelectorItem selectorItem, final int currentDeep) {
        if (selectorDataProvider != null) {
            lastSelectedItem = selectorItem;
            updateLoadingStatus(true, false);
            selectorDataProvider.provide(currentDeep, lastSelectedItem, (selectorItemList, isFail) -> {
                updateLoadingStatus(false, isFail);
                // 数据为空且不是失败状态表示到了最后一层
                if ((selectorItemList == null || selectorItemList.isEmpty()) && !isFail) {
                    callbackInternal();
                } else if (!isFail) {
                    int nextIndex = Math.min(maxDeep, currentDeep + (lastSelectedItem == null || lastSelectedItem.getId() == 0 ? 0 : 1));
                    updateListViewData(selectorItemList, nextIndex, null);
                    selectorAdapter.notifyDataSetChanged();
                    viewPager.setCurrentItem(nextIndex);
                    updateTabsStatus();
                }
            });
        }

    }

    /**
     * 更新加载状态
     *
     * @param isLoading  加载中
     * @param isLoadFail 加载失败
     */
    private void updateLoadingStatus(boolean isLoading, boolean isLoadFail) {
        this.isLoadSuccess = !isLoading && !isLoadFail;
        View llStatus = contextView.findViewById(R.id.ll_selector_status);
        ProgressBar pbLoading = contextView.findViewById(R.id.pb_selector_loading);
        TextView tvLoadFail = contextView.findViewById(R.id.tv_load_fail);
        Button btTryAgain = contextView.findViewById(R.id.bt_try_again);
        if (isLoading) {
            llStatus.setVisibility(View.VISIBLE);
            pbLoading.setVisibility(View.VISIBLE);
            tvLoadFail.setVisibility(View.GONE);
            btTryAgain.setVisibility(View.GONE);
        } else {
            pbLoading.setVisibility(View.GONE);
            if (isLoadFail) {
                tvLoadFail.setVisibility(View.VISIBLE);
                btTryAgain.setVisibility(View.VISIBLE);
                llStatus.setVisibility(View.VISIBLE);
            } else {
                llStatus.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 回调选中结果
     */
    private void callbackInternal() {
        if (onSelectedListener == null) {
            return;
        }
        ArrayList<SelectorItem> resultList = new ArrayList<>();
        for (int i = 0; i < maxDeep; i++) {
            boolean isHasData = viewPagerDataList.get(i) != null && selectedList.get(i) != null;
            if (isHasData) {
                int id = selectedList.get(i).getId();
                List<SelectorItem> list = viewPagerDataList.get(i);
                for (SelectorItem item : list) {
                    if (id == item.getId()) {
                        resultList.add(item);
                        break;
                    }
                }
            }
        }
        onSelectedListener.onSelected(resultList);
    }

    /**
     * 更新指示器上的标题
     */
    private void updateTabsStatus() {
        int size = tabViewList.size();
        for (int i = 0; i < size; i++) {
            TextView tvTab = tabViewList.get(i);
            boolean isGone = viewPagerDataList.get(i) == null || viewPagerDataList.get(i).isEmpty();
            if (selectedList.get(i) != null && !isGone) {
                tvTab.setText(selectedList.get(i).getName());
            } else if (!isGone) {
                tvTab.setText("请选择");
            }
            tvTab.setVisibility(isGone ? View.GONE : View.VISIBLE);
            String text = tvTab.getText().toString();
            int color = ContextCompat.getColor(tvTab.getContext(), "请选择".equals(text) ? R.color.google_red : R.color.black);
            tvTab.setTextColor(color);
        }
    }

    /**
     * 更新列表数据
     *
     * @param dataList     数据源
     * @param position     viewPage位置
     * @param selectedItem 选中的数据
     */
    private void updateListViewData(List<SelectorItem> dataList, int position, SelectorItem selectedItem) {
        viewPagerDataList.get(position).clear();
        if (dataList != null && !dataList.isEmpty()) {
            viewPagerDataList.get(position).addAll(dataList);
        }
        selectedList.set(position, selectedItem);
        ListView tempListView = viewPagerViewList.get(position).findViewById(R.id.list_view);
        ((SelectorAdapter) tempListView.getAdapter()).setSelectedItem(selectedItem);
        ((SelectorAdapter) tempListView.getAdapter()).setDataList(dataList);
        ((SelectorAdapter) tempListView.getAdapter()).notifyDataSetChanged();
        updateLoadingStatus(false, false);
    }

    /**
     * 更新指示器位置
     * @param index 指示器位置
     */
    private void updateIndicator(final int index) {
        contextView.post(() -> {
            View textView = tabViewList.get(index);
            ObjectAnimator xAnimator = ObjectAnimator.ofFloat(indicator, "X", indicator.getX(), textView.getX());
            final ViewGroup.LayoutParams params = indicator.getLayoutParams();
            ValueAnimator widthAnimator = ValueAnimator.ofInt(params.width, textView.getMeasuredWidth());
            widthAnimator.addUpdateListener(animation -> {
                params.width = (int) animation.getAnimatedValue();
                indicator.setLayoutParams(params);
            });
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setInterpolator(new FastOutSlowInInterpolator());
            animatorSet.playTogether(xAnimator, widthAnimator);
            animatorSet.start();
        });
    }

    private PagerAdapter selectorAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            int count = 0;
            for (List<SelectorItem> list : viewPagerDataList) {
                if (list != null && !list.isEmpty()) {
                    count++;
                }
            }
            return count;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            if (viewPagerViewList.size() > position) {
                container.removeView(viewPagerViewList.get(position));
            }
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = viewPagerViewList.get(position);
            container.addView(view);
            return view;
        }
    };


    private AdapterView.OnItemClickListener onItemClickListener = (parent, view, position, id) -> {
        int nextIndex = Math.min(tabIndex + 1, maxDeep);
        SelectorItem selectAble = viewPagerDataList.get(tabIndex).get(position);
        // 和上次选中是否相同
        boolean isSameItem = selectedList.get(tabIndex) != null && selectedList.get(tabIndex).getId() == selectAble.getId();
        if (!isSameItem) {
            tabViewList.get(tabIndex).setText(selectAble.getName());
            selectedList.set(tabIndex, selectAble);
            updateIndicator(tabIndex);
        }
        // 重置层级数据
        for (int i = nextIndex; i < maxDeep; i++) {
            viewPagerDataList.get(i).clear();
            updateListViewData(null, i, null);
        }
        updateTabsStatus();
        selectorAdapter.notifyDataSetChanged();
        ListView tempListView = viewPagerViewList.get(tabIndex).findViewById(R.id.list_view);
        ((SelectorAdapter) tempListView.getAdapter()).setSelectedItem(selectAble);
        ((SelectorAdapter) tempListView.getAdapter()).notifyDataSetChanged();
        if (nextIndex == maxDeep) {
            callbackInternal();
        } else {
            getAndSetDeepData(selectAble, tabIndex);
        }
    };

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            tabIndex = position;
            updateIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    public void setOnSelectedListener(SelectorSubmitListener onSelectorSubmitListener) {
        this.onSelectedListener = onSelectorSubmitListener;
    }
}
