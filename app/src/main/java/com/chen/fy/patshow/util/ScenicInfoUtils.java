package com.chen.fy.patshow.util;

/**
 * 景区景物描述类
 */
public class ScenicInfoUtils {

    public static String getSceneryInfo(String name) {
        switch (name) {
            case "象鼻山":
                return getElephantHillInfo();
            case "普贤塔":
                return getTownInfo();
            default:
                return getDefaultInfo();

        }
    }

    /// 象鼻山
    private static String getElephantHillInfo() {
        return "象鼻山\n原名漓山，位于广西区桂林市内桃花江与漓江汇流处。\n" +
                "山因酷似一只站在江边伸鼻豪饮漓江甘泉的巨象而得名，被人们称为桂林山水的象征。\n" +
                "象鼻山以神奇著称。其神奇，首先是形神毕似，其次是在鼻腿之间造就一轮临水明月，构成“象山水月”奇景。\n";
    }

    /// 普贤塔
    private static String getTownInfo() {
        return "普贤塔\n在桂林市市区象鼻山顶，是一座喇嘛或实心砖塔，建于明代初期。塔身第二层正北面嵌有青石" +
                "线刻“南无普贤菩萨”。远看此塔，像插在象背一支剑柄或置于象背一只宝瓶,故亦有剑柄塔、宝瓶塔之称。";
    }

    /// 识别失败
    private static String getDefaultInfo() {
        return "未能成功识别";
    }
}