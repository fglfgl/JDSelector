## 仿京东地址选择器JDSelector，处理了加载中、加载失败、填充历史选中数据、滑动切换等
### 啰嗦几句
前不久有个业务需求要用到地址选择器，发现京东的地址选择器比大家常用的滚轮选择器更加人性化。
1. 体验这么好的控件应该有人做了轮子吧？
2. 喵了一眼，发现Github上[JDSelector](https://github.com/dunwen/JDSelector)、[JDAddressSelector](https://github.com/chihane/JDAddressSelector) 并不是我想要的。没有处理加载中、加载失败、填充历史选中数据、滑动切换等。
3. 考虑到使用场景，上面提及的状态还是要考虑的（京东的小哥哥偷懒，没处理加载失败的状态）所以还是自己动手吧🙄
### 一、先看看效果
![](./image/demo.gif?imageMogr2/auto-orient/strip%7CimageView2/2/w/800)

### 二、项目结构
就这样，一个`Demo`，一个`Library`，`Library`是主要的代码，下面简单介绍下。

```
JDSelector
|--app :最终Demo
|--library :Android Library 选择器控件
```
### 三、Module `Library`

结构是这样的：
![library module](./image/module_library.png??imageMogr2/auto-orient/strip%7CimageView2/2/w/800)

- SelectorAdapter: 就是列表的Adapter(列表用了ListView)
- SelectorSubmitListener: 选到最后层级或者选到层级已经没数据的的回调
- JDSelector: 关键类，处理数据和UI，没有分Module和View了 写在一起就这样😂
- SelectorDataProvider: 数据提供者
- SelectorDataReceiver: 数据接收者
- SelectorItem: model

再简单贴一下代码，有需要直接看看源码，我没有做成依赖包，主要考虑到使用时的UI问题（好懒的样子🙄）直接拷代码到UI模块自己维护吧，不喜欢还可以改改改。

- `SelectorDataReceiver.java`

```
public interface SelectorDataReceiver {
    /**
     * 通过send方法发送数据给内容提供者
     * @param itemList 列表数据
     * @param isFail 当前数据数据是否获取正常，使用者可以通过这个状态做不同的控制，如数据提供失败，展示失败的界面
     */
    void send(List<SelectorItem> itemList, boolean isFail);
}
```

- `SelectorDataProvider.java`

```
public interface SelectorDataProvider {

    /**
     * @param currentDeep 当前所处的层级
     * @param selectedItem 当前层级选中的Item
     * @param receiver 数据接受者，通过currentDeep、selectedItem处理逻辑后返回下一层级的数据
     */
    void provide(int currentDeep, SelectorItem selectedItem, SelectorDataReceiver receiver);
}
```

- 说一下`JDSelector.java`的构造函数，其他逻辑看源码吧

```
public JDSelector(Context context, int maxDeep, List<SelectorItem> selectedList, List<List<SelectorItem>> dataList) {
// selectedList 初始状态选中的数据，index对应层级，默认为null
// dataList 初始状态下每个层级要展示的数据，默认为null
        ...
}
```

### 源码
[Github](https://github.com/fglfgl/JDSelector)


