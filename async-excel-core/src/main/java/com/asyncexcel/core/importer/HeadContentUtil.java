package com.asyncexcel.core.importer;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.alibaba.excel.util.MapUtils;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description TODO
 * @Author 姚仲杰#80998699
 * @Date 2022/7/11 15:55
 */
public class HeadContentUtil {
    
    public static final Map<Class<?>, Map<Integer,String>> CLASS_HEAD_CONTENT_CACHE = new ConcurrentHashMap<>();
    
    public static final List<String> ignoreField=new ArrayList<>(3);
    static {
        ignoreField.add("row");
        ignoreField.add("rowFailMessage");
        ignoreField.add("sheetIndex");
    }
    
    public static Map<Integer,String> declaredFieldHeadContentMap(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        return CLASS_HEAD_CONTENT_CACHE.computeIfAbsent(clazz, key -> {
            List<Field> tempFieldList = new ArrayList<>();
            Class<?> tempClass = clazz;
            while (tempClass != null) {
                Collections.addAll(tempFieldList, tempClass.getDeclaredFields());
                tempClass = tempClass.getSuperclass();
            }
            
            Map<Integer, String> headContentMap = MapUtils.newHashMapWithExpectedSize(
                tempFieldList.size());
            int position=0;
            for (Field field : tempFieldList) {
                if (ignoreField.contains(field.getName())){
                    continue;
                }
                ExcelContentProperty excelContentProperty = new ExcelContentProperty();
                excelContentProperty.setField(field);
                ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
                String[] value = excelProperty.value();
                headContentMap.put(position,value[0]);
                position++;
            }
            return headContentMap;
        });
    }
  
}
