# SkinPlugin

### 参考
[插件化_换肤方案](https://forevercoder.com/2021/01/20/plugin_change_skin/)

[解决mFactorySet在Android Q中被非SDK接口限制的问题](https://blog.csdn.net/qq_25412055/article/details/100033637)

### 效果图

![](https://github.com/wuchao226/SkinPlugin/blob/master/images/preview.gif)

### 思路
**优点：**
- 不闪烁
- 无需启动
- 架构独立
- 无继承

插件化 --- 现在

换肤：资源文件、图片、字体、文字颜色、等等

res

---


View view = tryCreateView(parent, name, context, attrs);

mFactory2  处理换肤


换肤的思路
1. 知道xml的View 怎么解析的？？ ---》 
2. 如何拦截系统的创建流程？ setFactory2 可以拦截  --- aop的思路去实现
3. 拦截后怎么做？？ 重写系统的创建过程的代码（复制）
4. 收集View以及属性
每个Activity的 View 及属性都需要收集

5. 创建皮肤包 -- apk  
6. 如何使用？？只用插件的res（插件的 java、res）
    1. 系统的资源是如何加载的？ Resources、Assetmanager
    2. 通过Hook技术，创建一个Assetmanager 专门加载皮肤包的资源
    3. 通过 反射 addAssetPath 方法放入皮肤包的路径 从而得到 加载皮肤包资源的 Assetmanager 
    4. 首先通过 app的资源id --》 找到 app的资源name --》 皮肤包的资源id

观察者模式、aop、Hook技术



Hook技术 --- 反射、动态代理的使用
通过反射、动态代理等技术 改变代码的原有流程





```
public void setFactory2(Factory2 factory) {
    // Factory2 只能创建一次
    if (mFactorySet) {
        throw new IllegalStateException("A factory has already been set on this LayoutInflater");
    }
    if (factory == null) {
        throw new NullPointerException("Given factory can not be null");
    }
    mFactorySet = true;
}
```
```
dispatchActivityCreated(savedInstanceState)@Activity.java
--> onActivityCreated


Resources  AssertManager --> context 


performLaunchActivity@ActivityThread.java
--> ContextImpl appContext = createBaseContextForActivity(r);
--> ContextImpl.createActivityContext
--> context.setResources
--> createResources
--> ResourcesImpl resourcesImpl = findOrCreateResourcesImplForKeyLocked(key);
--> impl = createResourcesImpl(key);
--> final AssetManager assets = createAssetManager(key);
--> builder.addApkAssets(loadApkAssets(key.mResDir, false /*sharedLib*/,
                        false /*overlay*/));
```


AssertManager 加载资源 --》 资源路径 --》 默认传入的资源路径 key.mResDir（app下面的res）
                                       改成皮肤包的资源路径 ---Resources  AssertManager  皮肤包的
Hook的思路：不能改变原有的资源加载，单独创建一个AssertManager--> 专门加载皮肤包的资源



```
皮肤包的 
0x7f070092  t_window_bg                         res/drawable-hdpi-v4/t_window_bg.jpg                            
app的 
0x7f070095  t_window_bg rgb8(0xffffffff)                        

skinResources.Drawable(0x7f070095);
```

首先通过 app的资源id --》 找到 app的资源name --》 皮肤包的资源id

```
// app的resId
String resName=mAppResources.getResourceEntryName(resId); // 通过app的resId 找到 resName
String resType=mAppResources.getResourceTypeName(resId);// 通过app的resId 找到 类型，layout、drawable
// 获取对应皮肤包的资源Id
int skinId=mSkinResources.getIdentifier(resName,resType,mSkinPkgName);
```

参考：https://github.com/ximsfei/Android-skin-support#toc14

```
静态换肤  ---》 flag  
if(){
    tv.setColor(红色);
}else{
    tv.setColor(黑色);
}
```
