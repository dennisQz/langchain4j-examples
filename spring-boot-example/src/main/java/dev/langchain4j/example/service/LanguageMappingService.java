package dev.langchain4j.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LanguageMappingService {

    private static final Logger log = LoggerFactory.getLogger(LanguageMappingService.class);

    private static final String DEFAULT_LANGUAGE = "英语";

    private static final Map<String, String> LANGUAGE_MAP = new HashMap<>();

    static {
        LANGUAGE_MAP.put("zh", "中文");
        LANGUAGE_MAP.put("zh-cn", "中文");
        LANGUAGE_MAP.put("zh-hans", "中文（简体）");
        LANGUAGE_MAP.put("zh-hant", "中文（繁体）");
        LANGUAGE_MAP.put("zh-tw", "中文（繁体）");
        LANGUAGE_MAP.put("zh-hk", "中文（繁体）");

        LANGUAGE_MAP.put("en", "英语");
        LANGUAGE_MAP.put("en-us", "英语");
        LANGUAGE_MAP.put("en-gb", "英语");
        LANGUAGE_MAP.put("en-au", "英语");
        LANGUAGE_MAP.put("en-ca", "英语");
        LANGUAGE_MAP.put("en-nz", "英语");
        LANGUAGE_MAP.put("en-za", "英语");
        LANGUAGE_MAP.put("en-ph", "英语");

        LANGUAGE_MAP.put("ja", "日语");
        LANGUAGE_MAP.put("ko", "韩语");
        LANGUAGE_MAP.put("fr", "法语");
        LANGUAGE_MAP.put("fr-ca", "法语");
        LANGUAGE_MAP.put("fr-ch", "法语");
        LANGUAGE_MAP.put("de", "德语");
        LANGUAGE_MAP.put("es", "西班牙语");
        LANGUAGE_MAP.put("es-mx", "西班牙语");
        LANGUAGE_MAP.put("es-es", "西班牙语");
        LANGUAGE_MAP.put("es-ar", "西班牙语");
        LANGUAGE_MAP.put("es-co", "西班牙语");
        LANGUAGE_MAP.put("es-cl", "西班牙语");
        LANGUAGE_MAP.put("es-pe", "西班牙语");

        LANGUAGE_MAP.put("pt", "葡萄牙语");
        LANGUAGE_MAP.put("pt-br", "葡萄牙语");
        LANGUAGE_MAP.put("pt-pt", "葡萄牙语");

        LANGUAGE_MAP.put("ru", "俄语");
        LANGUAGE_MAP.put("ar", "阿拉伯语");
        LANGUAGE_MAP.put("ar-sa", "阿拉伯语");

        LANGUAGE_MAP.put("it", "意大利语");
        LANGUAGE_MAP.put("nl", "荷兰语");
        LANGUAGE_MAP.put("nl-be", "荷兰语");

        LANGUAGE_MAP.put("pl", "波兰语");
        LANGUAGE_MAP.put("tr", "土耳其语");
        LANGUAGE_MAP.put("vi", "越南语");
        LANGUAGE_MAP.put("th", "泰语");
        LANGUAGE_MAP.put("id", "印度尼西亚语");
        LANGUAGE_MAP.put("ms", "马来语");
        LANGUAGE_MAP.put("hi", "印地语");
        LANGUAGE_MAP.put("bn", "孟加拉语");
        LANGUAGE_MAP.put("ta", "泰米尔语");
        LANGUAGE_MAP.put("te", "泰卢固语");
        LANGUAGE_MAP.put("mr", "马拉地语");
        LANGUAGE_MAP.put("gu", "古吉拉特语");
        LANGUAGE_MAP.put("kn", "卡纳达语");
        LANGUAGE_MAP.put("ml", "马拉雅拉姆语");
        LANGUAGE_MAP.put("uk", "乌克兰语");
        LANGUAGE_MAP.put("cs", "捷克语");
        LANGUAGE_MAP.put("el", "希腊语");
        LANGUAGE_MAP.put("he", "希伯来语");
        LANGUAGE_MAP.put("hu", "匈牙利语");
        LANGUAGE_MAP.put("sv", "瑞典语");
        LANGUAGE_MAP.put("da", "丹麦语");
        LANGUAGE_MAP.put("fi", "芬兰语");
        LANGUAGE_MAP.put("no", "挪威语");
        LANGUAGE_MAP.put("nb", "挪威语");
        LANGUAGE_MAP.put("bg", "保加利亚语");
        LANGUAGE_MAP.put("ro", "罗马尼亚语");
        LANGUAGE_MAP.put("sk", "斯洛伐克语");
        LANGUAGE_MAP.put("sl", "斯洛文尼亚语");
        LANGUAGE_MAP.put("hr", "克罗地亚语");
        LANGUAGE_MAP.put("sr", "塞尔维亚语");
        LANGUAGE_MAP.put("ca", "加泰罗尼亚语");
        LANGUAGE_MAP.put("tl", "菲律宾语");
        LANGUAGE_MAP.put("fil", "菲律宾语");
        LANGUAGE_MAP.put("sw", "斯瓦希里语");
        LANGUAGE_MAP.put("fa", "波斯语");
        LANGUAGE_MAP.put("ur", "乌尔都语");
        LANGUAGE_MAP.put("pa", "旁遮普语");
        LANGUAGE_MAP.put("bn-in", "孟加拉语");
        LANGUAGE_MAP.put("az", "阿塞拜疆语");
        LANGUAGE_MAP.put("be", "白俄罗斯语");
        LANGUAGE_MAP.put("bs", "波斯尼亚语");
        LANGUAGE_MAP.put("bs-cyrl", "波斯尼亚语");
        LANGUAGE_MAP.put("et", "爱沙尼亚语");
        LANGUAGE_MAP.put("ka", "格鲁吉亚语");
        LANGUAGE_MAP.put("ky", "吉尔吉斯语");
        LANGUAGE_MAP.put("lo", "老挝语");
        LANGUAGE_MAP.put("lv", "拉脱维亚语");
        LANGUAGE_MAP.put("lt", "立陶宛语");
        LANGUAGE_MAP.put("mk", "马其顿语");
        LANGUAGE_MAP.put("mn", "蒙古语");
        LANGUAGE_MAP.put("ne", "尼泊尔语");
        LANGUAGE_MAP.put("tg", "塔吉克语");
        LANGUAGE_MAP.put("uz", "乌兹别克语");
        LANGUAGE_MAP.put("cy", "威尔士语");
        LANGUAGE_MAP.put("zu", "祖鲁语");
        LANGUAGE_MAP.put("af", "南非荷兰语");
        LANGUAGE_MAP.put("sq", "阿尔巴尼亚语");
        LANGUAGE_MAP.put("am", "阿姆哈拉语");
        LANGUAGE_MAP.put("hy", "亚美尼亚语");
        LANGUAGE_MAP.put("eu", "巴斯克语");
        LANGUAGE_MAP.put("my", "缅甸语");
        LANGUAGE_MAP.put("fy", "弗里斯兰语");
        LANGUAGE_MAP.put("gl", "加利西亚语");
        LANGUAGE_MAP.put("gn", "瓜拉尼人");
        LANGUAGE_MAP.put("ha", "豪萨语");
        LANGUAGE_MAP.put("ig", "Igbo");
        LANGUAGE_MAP.put("ga", "爱尔兰语");
        LANGUAGE_MAP.put("is", "冰岛语");
        LANGUAGE_MAP.put("lb", "卢森堡语");
        LANGUAGE_MAP.put("mt", "马耳他语");
        LANGUAGE_MAP.put("or", "奥里亚语");
        LANGUAGE_MAP.put("gd", "苏格兰盖尔语");
        LANGUAGE_MAP.put("so", "索马里语");
    }

    public String getLanguageName(String languageCode) {
        if (languageCode == null || languageCode.trim().isEmpty()) {
            log.warn("Language code is null or empty, using default: {}", DEFAULT_LANGUAGE);
            return DEFAULT_LANGUAGE;
        }

        String normalizedCode = languageCode.toLowerCase().trim();
        String languageName = LANGUAGE_MAP.get(normalizedCode);

        if (languageName == null) {
            log.warn("Unsupported language code: {}, using default: {}", languageCode, DEFAULT_LANGUAGE);
            return DEFAULT_LANGUAGE;
        }

        return languageName;
    }
}
