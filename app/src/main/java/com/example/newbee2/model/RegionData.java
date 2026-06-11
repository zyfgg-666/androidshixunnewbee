package com.example.newbee2.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 中国省市区三级联动数据
 */
public class RegionData {

    public static class City {
        public String name;
        public List<String> districts;

        public City(String name, String... districts) {
            this.name = name;
            this.districts = Arrays.asList(districts);
        }
    }

    public static class Province {
        public String name;
        public List<City> cities;

        public Province(String name, City... cities) {
            this.name = name;
            this.cities = Arrays.asList(cities);
        }
    }

    private static final List<Province> PROVINCES = new ArrayList<>();

    static {
        PROVINCES.add(new Province("北京市",
                new City("北京市", "东城区", "西城区", "朝阳区", "丰台区", "石景山区", "海淀区", "顺义区", "通州区", "大兴区", "房山区", "门头沟区", "昌平区", "平谷区", "密云区", "怀柔区", "延庆区")
        ));
        PROVINCES.add(new Province("天津市",
                new City("天津市", "和平区", "河东区", "河西区", "南开区", "河北区", "红桥区", "东丽区", "西青区", "津南区", "北辰区", "武清区", "宝坻区", "滨海新区", "宁河区", "静海区", "蓟州区")
        ));
        PROVINCES.add(new Province("河北省",
                new City("石家庄市", "长安区", "桥西区", "新华区", "井陉矿区", "裕华区", "藁城区", "鹿泉区", "栾城区", "正定县", "行唐县"),
                new City("唐山市", "路南区", "路北区", "古冶区", "开平区", "丰南区", "丰润区", "曹妃甸区", "滦南县", "乐亭县", "玉田县"),
                new City("保定市", "竞秀区", "莲池区", "满城区", "清苑区", "徐水区", "定州市", "涿州市", "安国市"),
                new City("廊坊市", "安次区", "广阳区", "固安县", "永清县", "香河县", "大城县", "三河市", "霸州市"),
                new City("邯郸市", "邯山区", "丛台区", "复兴区", "峰峰矿区", "肥乡区", "永年区", "武安市")
        ));
        PROVINCES.add(new Province("山西省",
                new City("太原市", "小店区", "迎泽区", "杏花岭区", "尖草坪区", "万柏林区", "晋源区", "古交市"),
                new City("大同市", "平城区", "云冈区", "新荣区", "云州区", "阳高县", "天镇县", "浑源县")
        ));
        PROVINCES.add(new Province("辽宁省",
                new City("沈阳市", "和平区", "沈河区", "大东区", "皇姑区", "铁西区", "苏家屯区", "浑南区", "沈北新区", "于洪区", "辽中区", "新民市"),
                new City("大连市", "中山区", "西岗区", "沙河口区", "甘井子区", "旅顺口区", "金州区", "普兰店区", "瓦房店市", "庄河市")
        ));
        PROVINCES.add(new Province("吉林省",
                new City("长春市", "南关区", "宽城区", "朝阳区", "二道区", "绿园区", "双阳区", "九台区", "榆树市", "德惠市"),
                new City("吉林市", "昌邑区", "龙潭区", "船营区", "丰满区", "永吉县", "蛟河市", "桦甸市")
        ));
        PROVINCES.add(new Province("黑龙江省",
                new City("哈尔滨市", "道里区", "南岗区", "道外区", "平房区", "松北区", "香坊区", "呼兰区", "阿城区", "双城区", "五常市"),
                new City("大庆市", "萨尔图区", "龙凤区", "让胡路区", "红岗区", "大同区")
        ));
        PROVINCES.add(new Province("上海市",
                new City("上海市", "黄浦区", "徐汇区", "长宁区", "静安区", "普陀区", "虹口区", "杨浦区", "闵行区", "宝山区", "嘉定区", "浦东新区", "金山区", "松江区", "青浦区", "奉贤区", "崇明区")
        ));
        PROVINCES.add(new Province("江苏省",
                new City("南京市", "玄武区", "秦淮区", "建邺区", "鼓楼区", "浦口区", "栖霞区", "雨花台区", "江宁区", "六合区", "溧水区", "高淳区"),
                new City("苏州市", "虎丘区", "吴中区", "相城区", "姑苏区", "吴江区", "常熟市", "张家港市", "昆山市", "太仓市"),
                new City("无锡市", "锡山区", "惠山区", "滨湖区", "梁溪区", "新吴区", "江阴市", "宜兴市"),
                new City("常州市", "天宁区", "钟楼区", "新北区", "武进区", "金坛区", "溧阳市"),
                new City("南通市", "崇川区", "通州区", "海门区", "如东县", "启东市", "如皋市", "海安市")
        ));
        PROVINCES.add(new Province("浙江省",
                new City("杭州市", "上城区", "拱墅区", "西湖区", "滨江区", "萧山区", "余杭区", "富阳区", "临安区", "桐庐县", "建德市"),
                new City("宁波市", "海曙区", "江北区", "北仑区", "镇海区", "鄞州区", "奉化区", "象山县", "宁海县", "余姚市", "慈溪市"),
                new City("温州市", "鹿城区", "龙湾区", "瓯海区", "洞头区", "永嘉县", "平阳县", "苍南县", "瑞安市", "乐清市"),
                new City("嘉兴市", "南湖区", "秀洲区", "嘉善县", "海盐县", "海宁市", "平湖市", "桐乡市")
        ));
        PROVINCES.add(new Province("安徽省",
                new City("合肥市", "瑶海区", "庐阳区", "蜀山区", "包河区", "长丰县", "肥东县", "肥西县", "庐江县", "巢湖市"),
                new City("芜湖市", "镜湖区", "弋江区", "鸠江区", "三山区", "繁昌县", "南陵县", "无为市")
        ));
        PROVINCES.add(new Province("福建省",
                new City("福州市", "鼓楼区", "台江区", "仓山区", "马尾区", "晋安区", "长乐区", "闽侯县", "连江县", "福清市"),
                new City("厦门市", "思明区", "湖里区", "集美区", "海沧区", "同安区", "翔安区"),
                new City("泉州市", "鲤城区", "丰泽区", "洛江区", "泉港区", "惠安县", "晋江市", "石狮市", "南安市")
        ));
        PROVINCES.add(new Province("江西省",
                new City("南昌市", "东湖区", "西湖区", "青云谱区", "青山湖区", "新建区", "红谷滩区", "南昌县", "进贤县"),
                new City("九江市", "濂溪区", "浔阳区", "柴桑区", "武宁县", "修水县", "瑞昌市")
        ));
        PROVINCES.add(new Province("山东省",
                new City("济南市", "历下区", "市中区", "槐荫区", "天桥区", "历城区", "长清区", "章丘区", "济阳区", "莱芜区"),
                new City("青岛市", "市南区", "市北区", "黄岛区", "崂山区", "李沧区", "城阳区", "即墨区", "胶州市", "平度市", "莱西市"),
                new City("烟台市", "芝罘区", "福山区", "牟平区", "莱山区", "蓬莱区", "龙口市", "莱阳市", "莱州市", "招远市")
        ));
        PROVINCES.add(new Province("河南省",
                new City("郑州市", "中原区", "二七区", "管城回族区", "金水区", "上街区", "惠济区", "中牟县", "巩义市", "新郑市", "登封市"),
                new City("洛阳市", "老城区", "西工区", "瀍河区", "涧西区", "洛龙区", "偃师区", "孟津区", "新安县", "栾川县")
        ));
        PROVINCES.add(new Province("湖北省",
                new City("武汉市", "江岸区", "江汉区", "硚口区", "汉阳区", "武昌区", "青山区", "洪山区", "东西湖区", "蔡甸区", "江夏区", "黄陂区", "新洲区"),
                new City("宜昌市", "西陵区", "伍家岗区", "点军区", "猇亭区", "夷陵区", "宜都市", "当阳市", "枝江市"),
                new City("襄阳市", "襄城区", "樊城区", "襄州区", "南漳县", "谷城县", "保康县", "枣阳市", "宜城市")
        ));
        PROVINCES.add(new Province("湖南省",
                new City("长沙市", "芙蓉区", "天心区", "岳麓区", "开福区", "雨花区", "望城区", "长沙县", "浏阳市", "宁乡市"),
                new City("株洲市", "荷塘区", "芦淞区", "石峰区", "天元区", "渌口区", "攸县", "醴陵市")
        ));
        PROVINCES.add(new Province("广东省",
                new City("广州市", "荔湾区", "越秀区", "海珠区", "天河区", "白云区", "黄埔区", "番禺区", "花都区", "南沙区", "从化区", "增城区"),
                new City("深圳市", "罗湖区", "福田区", "南山区", "宝安区", "龙岗区", "盐田区", "龙华区", "坪山区", "光明区"),
                new City("东莞市", "东城街道", "南城街道", "万江街道", "莞城街道", "石碣镇", "石龙镇", "茶山镇", "石排镇", "企石镇", "横沥镇"),
                new City("佛山市", "禅城区", "南海区", "顺德区", "三水区", "高明区"),
                new City("珠海市", "香洲区", "斗门区", "金湾区"),
                new City("惠州市", "惠城区", "惠阳区", "博罗县", "惠东县", "龙门县")
        ));
        PROVINCES.add(new Province("广西壮族自治区",
                new City("南宁市", "兴宁区", "青秀区", "江南区", "西乡塘区", "良庆区", "邕宁区", "武鸣区", "横州市"),
                new City("桂林市", "秀峰区", "叠彩区", "象山区", "七星区", "雁山区", "临桂区", "阳朔县", "灵川县")
        ));
        PROVINCES.add(new Province("海南省",
                new City("海口市", "秀英区", "龙华区", "琼山区", "美兰区"),
                new City("三亚市", "海棠区", "吉阳区", "天涯区", "崖州区")
        ));
        PROVINCES.add(new Province("重庆市",
                new City("重庆市", "万州区", "涪陵区", "渝中区", "大渡口区", "江北区", "沙坪坝区", "九龙坡区", "南岸区", "北碚区", "綦江区", "大足区", "渝北区", "巴南区", "黔江区", "长寿区", "江津区", "合川区", "永川区")
        ));
        PROVINCES.add(new Province("四川省",
                new City("成都市", "锦江区", "青羊区", "金牛区", "武侯区", "成华区", "龙泉驿区", "青白江区", "新都区", "温江区", "双流区", "郫都区", "新津区", "都江堰市", "彭州市", "邛崃市"),
                new City("绵阳市", "涪城区", "游仙区", "安州区", "三台县", "盐亭县", "江油市")
        ));
        PROVINCES.add(new Province("贵州省",
                new City("贵阳市", "南明区", "云岩区", "花溪区", "乌当区", "白云区", "观山湖区", "开阳县", "息烽县", "修文县", "清镇市"),
                new City("遵义市", "红花岗区", "汇川区", "播州区", "桐梓县", "绥阳县", "正安县", "仁怀市")
        ));
        PROVINCES.add(new Province("云南省",
                new City("昆明市", "五华区", "盘龙区", "官渡区", "西山区", "东川区", "呈贡区", "晋宁区", "富民县", "宜良县", "安宁市"),
                new City("大理市", "大理镇", "下关镇", "凤仪镇")
        ));
        PROVINCES.add(new Province("陕西省",
                new City("西安市", "新城区", "碑林区", "莲湖区", "灞桥区", "未央区", "雁塔区", "阎良区", "临潼区", "长安区", "高陵区", "鄠邑区", "蓝田县"),
                new City("咸阳市", "秦都区", "杨陵区", "渭城区", "三原县", "泾阳县", "乾县", "兴平市")
        ));
        PROVINCES.add(new Province("甘肃省",
                new City("兰州市", "城关区", "七里河区", "西固区", "安宁区", "红古区", "永登县", "皋兰县", "榆中县"),
                new City("天水市", "秦州区", "麦积区", "清水县", "秦安县", "甘谷县", "武山县")
        ));
        PROVINCES.add(new Province("内蒙古自治区",
                new City("呼和浩特市", "新城区", "回民区", "玉泉区", "赛罕区", "土默特左旗", "托克托县"),
                new City("包头市", "东河区", "昆都仑区", "青山区", "石拐区", "白云鄂博矿区", "九原区")
        ));
        PROVINCES.add(new Province("西藏自治区",
                new City("拉萨市", "城关区", "堆龙德庆区", "达孜区", "林周县", "当雄县", "尼木县")
        ));
        PROVINCES.add(new Province("宁夏回族自治区",
                new City("银川市", "兴庆区", "西夏区", "金凤区", "灵武市", "永宁县", "贺兰县")
        ));
        PROVINCES.add(new Province("新疆维吾尔自治区",
                new City("乌鲁木齐市", "天山区", "沙依巴克区", "新市区", "水磨沟区", "头屯河区", "达坂城区", "米东区", "乌鲁木齐县")
        ));
    }

    public static List<Province> getProvinces() {
        return PROVINCES;
    }

    public static List<City> getCities(int provinceIndex) {
        if (provinceIndex >= 0 && provinceIndex < PROVINCES.size()) {
            return PROVINCES.get(provinceIndex).cities;
        }
        return new ArrayList<>();
    }

    public static List<String> getDistricts(int provinceIndex, int cityIndex) {
        List<City> cities = getCities(provinceIndex);
        if (cityIndex >= 0 && cityIndex < cities.size()) {
            return cities.get(cityIndex).districts;
        }
        return new ArrayList<>();
    }
}
