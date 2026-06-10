# 新蜂商城 (NewBee2)

基于 Android 原生开发的新蜂商城电商 App，采用 Java 语言，通过 OkHttp 与后端 REST API 交互，实现完整的电商购物流程。

## 技术栈

| 类别 | 技术 |
|------|------|
| 开发语言 | Java 11 |
| 最低 SDK | Android 7.0 (API 24) |
| 目标 SDK | Android 15 (API 35) |
| 网络请求 | OkHttp 4.9.3 |
| JSON 解析 | Gson 2.10.1 |
| 图片加载 | Glide 4.12.0 |
| 列表控件 | RecyclerView + CardView |
| 架构模式 | MVC + Fragment |

## 功能模块

### 首页
- Banner 轮播展示
- 搜索商品（支持模糊搜索）
- 三大商品板块：**新品上线**、**热门商品**、**最新推荐**
- 左上角 ☰ 菜单跳转分类页
- 右上角 👤 个人中心入口

### 分类
- 左侧一级分类导航
- 右侧二级分类网格展示
- 点击进入商品详情

### 购物车
- 商品列表展示（图片、名称、单价、数量）
- 数量增减、删除商品
- 底部导航栏购物车红点角标（显示商品数量）
- 选中商品下单

### 订单
- **全部** / **待支付** / **待确认** / **待发货** / **待收货** / **已完成** 六个 Tab
- 待支付：**立即支付**按钮（微信/支付宝选择）
- 待收货：**确认收货**按钮
- 订单状态实时更新

### 支付
- 微信支付（绿色按钮）
- 支付宝支付（蓝色按钮）
- 支付成功 → 订单进入待发货
- 支付失败 → 订单保留待支付状态

### 我的
- 用户信息展示（昵称、登录名、签名）
- 订单入口（带红点角标：待支付、待发货、待收货数量）
- 地址管理
- 关于我们
- 退出登录

### 其他
- 登录 / 注册
- 收货地址管理（增删改查）
- 商品详情 → 加入购物车 / 直接购买
- 关于我们页面

## 项目结构

```
NewBee2/
├── app/
│   └── src/main/
│       ├── java/com/example/newbee2/
│       │   ├── MainActivity.java          # 主页面（底部导航）
│       │   ├── LoginActivity.java         # 登录注册
│       │   ├── SearchActivity.java        # 搜索页面
│       │   ├── DetailActivity.java        # 商品详情
│       │   ├── CreateOrderActivity.java   # 创建订单
│       │   ├── OrderListActivity.java     # 订单列表
│       │   ├── AddressListActivity.java   # 地址列表
│       │   ├── AddressEditActivity.java   # 地址编辑
│       │   ├── AboutActivity.java         # 关于我们
│       │   ├── adapter/                   # 适配器
│       │   │   ├── GoodsAdapter.java
│       │   │   ├── CartAdapter.java
│       │   │   ├── OrderAdapter.java
│       │   │   ├── OrderGoodsAdapter.java
│       │   │   ├── BannerAdapter.java
│       │   │   ├── CategoryLeftAdapter.java
│       │   │   ├── CategoryGridAdapter.java
│       │   │   ├── SearchGoodsAdapter.java
│       │   │   └── AddressAdapter.java
│       │   ├── fragment/                  # Fragment
│       │   │   ├── HomeFragment.java
│       │   │   ├── CategoryFragment.java
│       │   │   ├── CartFragment.java
│       │   │   └── MyFragment.java
│       │   ├── model/                     # 数据模型
│       │   │   ├── Goods.java
│       │   │   ├── Category.java
│       │   │   ├── CartItem.java
│       │   │   ├── Order.java
│       │   │   ├── OrderItem.java
│       │   │   ├── Address.java
│       │   │   ├── User.java
│       │   │   └── Result.java
│       │   ├── utils/                     # 工具类
│       │   │   ├── HttpUtil.java          # OkHttp 封装
│       │   │   ├── ImageUtil.java         # Glide 封装
│       │   │   └── MD5Util.java           # MD5 加密
│       │   └── widget/
│       │       └── MyGridView.java
│       └── res/
│           ├── layout/                    # 布局文件
│           ├── drawable/                  # 图形资源
│           ├── mipmap/                    # 图标
│           ├── values/                    # 颜色、字符串、主题
│           └── xml/                       # 配置文件
├── build.gradle.kts
├── settings.gradle.kts
└── gradle/
```

## API 接口

后端基于 Spring Boot，主要接口：

| 接口 | 方法 | 说明 |
|------|------|------|
| `/user/login` | POST | 用户登录 |
| `/user/register` | POST | 用户注册 |
| `/user/info` | GET | 获取用户信息 |
| `/index-infos` | GET | 首页数据（轮播图、新品、热销、推荐） |
| `/goods/detail/{id}` | GET | 商品详情 |
| `/goods/search` | GET | 搜索商品 |
| `/categories` | GET | 商品分类 |
| `/shop-cart` | GET | 获取购物车 |
| `/shop-cart` | POST | 添加购物车 |
| `/shop-cart/{id}` | PUT | 修改购物车数量 |
| `/shop-cart/{id}` | DELETE | 删除购物车商品 |
| `/saveOrder` | POST | 创建订单 |
| `/order` | GET | 订单列表（支持 status 筛选） |
| `/paySuccess` | GET | 支付（orderNo + payType） |
| `/order/{orderNo}/finish` | PUT | 确认收货 |
| `/address` | GET/POST/PUT/DELETE | 地址管理 |

## 运行环境

- Android Studio Hedgehog 或更高版本
- JDK 11+
- Gradle 8.x
- 后端服务运行在 `http://172.30.130.131:28019/mallapi/`

## 构建与运行

```bash
# 克隆项目
git clone git@github.com:zyfgg-666/androidshixunnewbee.git

# 用 Android Studio 打开项目目录，同步 Gradle，运行即可
```

## 实训任务

本项目为 2026 年 Android 开发实训任务成果。
