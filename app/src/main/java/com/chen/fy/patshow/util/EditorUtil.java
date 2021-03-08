package com.chen.fy.patshow.util;

import com.chen.fy.patshow.R;

import java.util.ArrayList;
import java.util.List;

public class EditorUtil {

    private static List<Integer> colorDrawables;
    private static List<Integer> colorIDs;

    private static List<Integer> mappingDrawables;

    public static List<Integer> getColorDrawables(){
        if(colorDrawables == null) {
            colorDrawables = new ArrayList<>();
            colorDrawables.add(R.drawable.color1);
            colorDrawables.add(R.drawable.color2);
            colorDrawables.add(R.drawable.color3);
            colorDrawables.add(R.drawable.color4);
            colorDrawables.add(R.drawable.color5);
            colorDrawables.add(R.drawable.color6);
            colorDrawables.add(R.drawable.color7);
            colorDrawables.add(R.drawable.color8);
            colorDrawables.add(R.drawable.color9);
        }
        return colorDrawables;
    }

    public static int getColorID(int position){
        if(colorIDs == null) {
            colorIDs = new ArrayList<>();
            colorIDs.add(R.color.color1);
            colorIDs.add(R.color.color2);
            colorIDs.add(R.color.color3);
            colorIDs.add(R.color.color4);
            colorIDs.add(R.color.color5);
            colorIDs.add(R.color.color6);
            colorIDs.add(R.color.color7);
            colorIDs.add(R.color.color8);
            colorIDs.add(R.color.color9);
        }
        return colorIDs.get(position);
    }

    public static List<Integer> getMappingDrawables(){
        if(mappingDrawables == null) {
            mappingDrawables = new ArrayList<>();
            mappingDrawables.add(R.drawable.star);
            mappingDrawables.add(R.drawable.snow);
        }
        return mappingDrawables;
    }

}
