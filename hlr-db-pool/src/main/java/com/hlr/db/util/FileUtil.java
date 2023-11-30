package com.hlr.db.util;

import com.hlr.db.DBConnectionPools;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * FileUtil
 * Description:
 * date: 2023/11/30 11:57
 *
 * @author hlr
 */
public class FileUtil {

    public static String readFileToString(String path) {
        StringBuilder stringBuilder = new StringBuilder();
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(path);
            int c;
            while ((c = fileReader.read()) != -1) {
                stringBuilder.append((char) c);
            }
        } catch (Exception e) {

        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return stringBuilder.toString();
    }

    public static boolean isFile(String path) {
        File file = new File(path);
        return file.exists() && file.isFile();
    }

    public static void main(String[] args) {
        DBConnectionPools instance = DBConnectionPools.getInstance();
        System.out.println(instance);
    }

}
