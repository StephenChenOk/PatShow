package com.chen.fy.patshow.util;

import com.chen.fy.patshow.R;

import java.util.ArrayList;
import java.util.List;

public class EditorUtil {

    private static List<Integer> colorDrawables;
    private static List<Integer> colorIDs;

    private static List<Integer> mappingDrawables;
    private static List<Integer> textLogoDrawables;

    /// 颜色填充圆
    public static List<Integer> getColorDrawables(){
        if(colorDrawables == null) {
            colorDrawables = new ArrayList<>();
            colorDrawables.add(R.drawable.color1);
            colorDrawables.add(R.drawable.color12);
            colorDrawables.add(R.drawable.color11);
            colorDrawables.add(R.drawable.color13);
            colorDrawables.add(R.drawable.color14);
            colorDrawables.add(R.drawable.color8);
            colorDrawables.add(R.drawable.color9);
            colorDrawables.add(R.drawable.color16);
            colorDrawables.add(R.drawable.color2);
            colorDrawables.add(R.drawable.color3);
            colorDrawables.add(R.drawable.color4);
            colorDrawables.add(R.drawable.color5);
            colorDrawables.add(R.drawable.color6);
            colorDrawables.add(R.drawable.color7);
        }
        return colorDrawables;
    }

    /// 颜色ID
    public static int getColorID(int position){
        if(colorIDs == null) {
            colorIDs = new ArrayList<>();
            colorIDs.add(R.color.color1);
            colorIDs.add(R.color.color12);
            colorIDs.add(R.color.color11);
            colorIDs.add(R.color.color13);
            colorIDs.add(R.color.color14);
            colorIDs.add(R.color.color8);
            colorIDs.add(R.color.color9);
            colorIDs.add(R.color.color16);
            colorIDs.add(R.color.color2);
            colorIDs.add(R.color.color3);
            colorIDs.add(R.color.color4);
            colorIDs.add(R.color.color5);
            colorIDs.add(R.color.color6);
            colorIDs.add(R.color.color7);
        }
        return colorIDs.get(position);
    }

    /// 贴图
    public static List<Integer> getMappingDrawables(){
        if(mappingDrawables == null) {
            mappingDrawables = new ArrayList<>();
            mappingDrawables.add(R.drawable.mapping_7);
            mappingDrawables.add(R.drawable.mapping_8);
            mappingDrawables.add(R.drawable.mapping_1);
            mappingDrawables.add(R.drawable.mapping_2);
            mappingDrawables.add(R.drawable.mapping_3);
            mappingDrawables.add(R.drawable.mapping_4);
            mappingDrawables.add(R.drawable.mapping_5);
            mappingDrawables.add(R.drawable.mapping_6);
            mappingDrawables.add(R.drawable.star);

            mappingDrawables.add(R.drawable.snow);
        }
        return mappingDrawables;
    }

    /// 文字Logo
    public static List<Integer> getTextLogoDrawables(){
        if(textLogoDrawables == null){
            textLogoDrawables = new ArrayList<>();
            textLogoDrawables.add(R.drawable.xbs_logo_black);
            textLogoDrawables.add(R.drawable.xbs_logo_white);
            textLogoDrawables.add(R.drawable.pxt_logo_black);
            textLogoDrawables.add(R.drawable.pxt_logo_white);
            textLogoDrawables.add(R.drawable.wenyi_1);
            textLogoDrawables.add(R.drawable.wenyi_2);
        }
        return textLogoDrawables;
    }
}
