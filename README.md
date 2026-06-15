# 新蜂商城 (NewBee2)

基于 Android 原生开发的新蜂商城电商 App，采用 Java 语言，通过 HttpURLConnection 与后端 REST API 交互，实现完整的电商购物流程。

> 注：项目同时保留了 OkHttp 版本的完整实现（已注释在 HttpUtil.java 底部），方便对比学习。

## 技术栈

| 类别 | 技术 |
|------|------|
| 开发语言 | Java 11 |
| 最低 SDK | Android 7.0 (API 24) |
| 目标 SDK | Android 15 (API 35，compileSdk 35) |
| 网络请求 | HttpURLConnection（线程池异步） |
| JSON 解析 | Gson 2.10.1 |
| 图片加载 | Glide 4.12.0 |
| 列表控件 | RecyclerView + CardView + GridView |
| 页面导航 | ViewPager2 + BottomNavigationView + TabLayout |
| 角标提示 | Material BadgeDrawable |
| 架构模式 | MVC + Fragment |

## 快速体验

- 测试账号：`13900139000` / 密码：`12345678`
- 首页点击"测试登录"可一键填入

## 功能模块

### 🏠 首页
- ViewPager2 轮播图（3 秒自动轮播，离开页面自动暂停）
- 青绿色搜索栏（☰ 分类入口 + 搜索框 + 👤 个人中心）
- 10 个分类快捷入口（矢量图标 + 彩色主题），点击跳转分类页并自动选中
- 三大商品板块：**🆕 新品上线**、**🔥 热门商品**、**⭐ 最新推荐**
- ScrollView 嵌套 GridView（自定义 MyGridView 解决高度问题）

### 📂 分类
- 青绿色标题栏
- 左侧一级分类列表（点击高亮），右侧 2 列商品网格
- 首页分类点击联动（通过静态 pendingCategoryId 传递）
- 分类→关键词映射（后端 goodsCategoryId 不生效的变通方案）

### 🔍 搜索
- 关键词搜索 + 排序切换（默认/新品/价格）
- 上拉加载更多分页（RecyclerView OnScrollListener）
- 2 列瀑布流网格展示

### 🛒 购物车
- 商品列表（图+名+价+数量±+删除）
- 全选 / 取消全选（防递归联动）
- 数量增减实时同步服务器（PUT /shop-cart/update）
- 底部合计 + 结算按钮
- 底部导航栏红点角标（BadgeDrawable 数字显示）
- 详情页购物车图标红点角标

### 📦 商品详情
- 商品大图（Glide 加载）+ 名称/简介/价格/详情
- 底部操作栏：购物车图标(红点) + 加入购物车 + 立即购买
- 购物车图标点击 → 跳转购物车页面
- 立即购买：加购物车 → 创建订单 → 支付

### 💰 订单系统
- **全部 / 待支付 / 待确认 / 待发货 / 待收货 / 已完成** 六个 Tab
- 待支付：弹出微信(绿)/支付宝(蓝) 选择对话框 → 调用支付接口
- 待收货：确认收货按钮 → PUT /order/{no}/finish
- 订单内嵌商品列表（RecyclerView 嵌套）
- 支付成功 → 跳转待发货；支付失败 → 保留待支付可重试

### 👤 我的
- 青绿渐变用户信息卡片（头像+昵称+登录名+签名）
- 6 个订单入口图标（矢量图标 + 红色数量角标）
- 地址管理 / 关于我们 / 退出登录
- 未登录时角标隐藏、点击跳转登录

### 📍 地址管理
- 地址列表（增删改查 + 默认地址标记）
- 编辑/新增页面：收货人 + 手机 + 省市区三级联动选择器 + 详细地址
- 创建订单时可选择地址（selectMode）
- 省市区数据内置于 RegionData.java（31 个省/直辖市）

### 🎨 品牌设计
- 自定义 App 图标：青绿底 + 金色蜜蜂手拎购物袋（自适应 Adaptive Icon）
- 启动页：青绿渐变背景 + 蜜蜂手推购物车 + "新蜂商城"品牌名 + 2 秒跳转主页
- 底部导航全部使用自定义矢量图标（30+ Vector Drawable）

### ℹ️ 关于我们
- App 名称/版本/应用介绍/技术栈/联系方式/版权信息

## 项目结构

```
NewBee2/
├── app/src/main/
│   ├── java/com/example/newbee2/
│   │   ├── SplashActivity.java            # 启动页（2秒跳转）
│   │   ├── MainActivity.java              # 主框架（ViewPager2 + BottomNav + 购物车角标）
│   │   ├── LoginActivity.java             # 登录 / 注册
│   │   ├── SearchActivity.java            # 搜索（排序 + 分页）
│   │   ├── DetailActivity.java            # 商品详情（加购 + 立即购买 + 购物车角标）
│   │   ├── CreateOrderActivity.java       # 创建订单 + 支付
│   │   ├── OrderListActivity.java         # 订单列表（6 Tab）
│   │   ├── AddressListActivity.java       # 地址列表（普通/选择双模式）
│   │   ├── AddressEditActivity.java       # 地址编辑（含省市区选择器）
│   │   ├── AboutActivity.java             # 关于我们
│   │   ├── adapter/
│   │   │   ├── BannerAdapter.java         # 轮播图（ViewPager2）
│   │   │   ├── CategoryGridAdapter.java   # 分类网格（首页，矢量图标）
│   │   │   ├── CategoryLeftAdapter.java   # 分类左侧列表
│   │   │   ├── GoodsAdapter.java          # 商品网格（BaseAdapter）
│   │   │   ├── SearchGoodsAdapter.java    # 搜索结果（RecyclerView）
│   │   │   ├── CartAdapter.java           # 购物车（选择+数量+删除）
│   │   │   ├── OrderAdapter.java          # 订单列表（支付+收货）
│   │   │   ├── OrderGoodsAdapter.java     # 订单内商品（嵌套）
│   │   │   └── AddressAdapter.java        # 地址列表（普通/选择模式）
│   │   ├── fragment/
│   │   │   ├── HomeFragment.java          # 首页（轮播+分类+3板块）
│   │   │   ├── CategoryFragment.java      # 分类（左右联动+关键词映射）
│   │   │   ├── CartFragment.java          # 购物车（全选+结算）
│   │   │   └── MyFragment.java            # 我的（用户信息+订单红点）
│   │   ├── model/
│   │   │   ├── Result.java                # 统一接口响应包装
│   │   │   ├── User.java                  # 用户模型
│   │   │   ├── Goods.java                 # 商品模型
│   │   │   ├── Category.java              # 分类模型
│   │   │   ├── CartItem.java              # 购物车模型
│   │   │   ├── Order.java + OrderItem.java # 订单模型
│   │   │   ├── Address.java               # 地址模型
│   │   │   └── RegionData.java            # 省市区数据（31省）
│   │   ├── utils/
│   │   │   ├── HttpUtil.java              # 网络封装（HttpURLConnection）
│   │   │   ├── ImageUtil.java             # Glide 图片加载封装
│   │   │   └── MD5Util.java               # MD5 密码加密
│   │   └── widget/
│   │       └── MyGridView.java            # 自定义 GridView（ScrollView内展开）
│   └── res/
│       ├── layout/                        # 24 个 XML 布局文件
│       ├── drawable/                       # 35+ 个矢量图标 + 形状资源
│       ├── menu/bottom_nav_menu.xml        # 底部导航菜单
│       ├── mipmap/                         # App 启动图标
│       ├── values/                         # colors / strings / themes
│       └── xml/                            # shortcuts / backup
├── build.gradle.kts
├── settings.gradle.kts
├── gradle/
├── README.md
├── 项目答辩材料.txt                       # 答辩详细材料
├── 项目代码汇总（按界面整理）.txt          # 按界面整理的完整代码
├── 网络连接与接口通信实现详解.txt          # 网络层详解文档
└── 分类商品API连接实现说明.txt            # 分类接口适配说明
```

## API 接口

后端基础地址：`http://172.30.130.131:28019/mallapi/api/v1`

### 用户模块
| 接口 | 方法 | 说明 |
|------|------|------|
| `/user/login` | POST | 用户登录（MD5 密码） |
| `/user/register` | POST | 用户注册 |
| `/user/info` | GET | 获取用户信息 |

### 商品模块
| 接口 | 方法 | 说明 |
|------|------|------|
| `/index-infos` | GET | 首页聚合数据（轮播+新品+热销+推荐） |
| `/categories` | GET | 商品分类列表 |
| `/goods/detail/{id}` | GET | 商品详情 |
| `/search?keyword=&orderBy=&pageNumber=` | GET | 搜索商品 |

### 购物车模块
| 接口 | 方法 | 说明 |
|------|------|------|
| `/shop-cart` | GET | 获取购物车列表 |
| `/shop-cart` | POST | 添加商品 `{goodsId, goodsCount}` |
| `/shop-cart/update` | POST | 修改数量 `{cartItemId, goodsCount}` |
| `/shop-cart/{id}` | DELETE | 删除商品 |

### 订单模块
| 接口 | 方法 | 说明 |
|------|------|------|
| `/saveOrder` | POST | 创建订单 `{addressId, cartItemIds}` |
| `/order?pageNumber=1&status=` | GET | 订单列表（status 筛选） |
| `/paySuccess?orderNo=&payType=` | GET | 模拟支付（1=微信，2=支付宝） |
| `/order/{orderNo}/finish` | PUT | 确认收货 |

### 地址模块
| 接口 | 方法 | 说明 |
|------|------|------|
| `/address` | GET | 地址列表 |
| `/address` | POST | 新增地址 |
| `/address` | PUT | 修改地址 |
| `/address/{id}` | DELETE | 删除地址 |

## 运行环境

- Android Studio Hedgehog 或更高版本
- JDK 11+
- Gradle 8.x + Kotlin DSL
- 后端服务：`http://172.30.130.131:28019/mallapi/`
- Swagger 文档：`http://172.30.130.131:28019/mallapi/swagger-ui/index.html`

## 构建与运行

```bash
# 克隆项目
git clone git@github.com:zyfgg-666/androidshixunnewbee.git

# 用 Android Studio 打开项目目录，Gradle 同步后即可运行
```

## 项目文档

| 文档 | 说明 |
|------|------|
| `项目答辩材料.txt` | 详细答辩材料，含架构分析、代码解释、设计理由 |
| `项目代码汇总（按界面整理）.txt` | 按 18 个界面模块整理的完整 Java + XML 代码 |
| `网络连接与接口通信实现详解.txt` | HttpURLConnection 网络层完整实现原理 |
| `分类商品API连接实现说明.txt` | 分类页关键词映射方案的实现说明 |

## 实训任务

本项目为 2026 年 Android 开发实训任务成果。
