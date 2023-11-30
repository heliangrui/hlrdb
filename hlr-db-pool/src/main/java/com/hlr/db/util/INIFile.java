package com.hlr.db.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * INIFile
 * Description:
 * date: 2023/11/30 14:57
 *
 * @author hlr
 */
public class INIFile {

    private final Map<String, Map<String, String>> data;

    public INIFile(String path) {
        data = new LinkedHashMap<>();
        init(path);
    }

    public String getParamData(String key, String key1) {
        Map<String, String> stringStringMap = data.get(key);
        return stringStringMap == null ? null : stringStringMap.get(key1);
    }

    public String getParamData(String key, String key1, String defaultString) {
        String paramData = getParamData(key, key1);
        return paramData == null ? defaultString : paramData;
    }
    
    public int getIntegerParamData(String key, String key1, int defaultInteger) {
        String paramData = getParamData(key, key1);
        int i = defaultInteger;
        try{
            i = Integer.parseInt(paramData);
        }catch (Exception e){
        }
        return i;
    }  
    
    public long getLongParamData(String key, String key1, long defaultLong) {
        String paramData = getParamData(key, key1);
        long i = defaultLong;
        try{
            i = Long.parseLong(paramData);
        }catch (Exception e){
        }
        return i;
    }    
    
    public short getShortParamData(String key, String key1, short defaultShort) {
        String paramData = getParamData(key, key1);
        short i = defaultShort;
        try{
            i = Short.parseShort(paramData);
        }catch (Exception e){
        }
        return i;
    }  
    
    public double getDoubleParamData(String key, String key1, double defaultDouble) {
        String paramData = getParamData(key, key1);
        double i = defaultDouble;
        try{
            i = Double.parseDouble(paramData);
        }catch (Exception e){
        }
        return i;
    }
    public BigDecimal getBigDecimalParamData(String key, String key1, BigDecimal defaultBigDecimal) {
        String paramData = getParamData(key, key1);
        BigDecimal i = defaultBigDecimal;
        try{
            i = new BigDecimal(paramData);
        }catch (Exception e){
        }
        return i;
    }
    

    public Set<String> getParamKey() {
        return data.keySet();
    }

    public Set<String> getParamDataKey(String key) {
        Map<String, String> stringStringMap = data.get(key);
        return stringStringMap == null ? null : stringStringMap.keySet();
    }


    // string 文件格式解析
    private void init(String path) {
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(path);
            bufferedReader = new BufferedReader(fileReader);
            String key = null;
            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine().trim();
                if (line.startsWith("[") && line.endsWith("]")) {
                    key = line.substring(1, line.length() - 1);
                } else if (line.contains("=")) {
                    int i = line.indexOf("=");
                    String mapKey = line.substring(0, i);
                    String mapValue = line.substring(i + 1);
                    Map<String, String> stringStringMap = data.computeIfAbsent(key, k -> new HashMap<>());
                    stringStringMap.put(mapKey, mapValue);
                }
            }


        } catch (Exception e) {
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                }
            }
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                }
            }

        }

    }


}
