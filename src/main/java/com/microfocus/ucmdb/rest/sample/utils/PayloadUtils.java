package com.microfocus.ucmdb.rest.sample.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class PayloadUtils {
    public static String contentFolder = "/data/payload/";

    public static String loadContent(String contentName, int count) {
        StringBuilder rlt = new StringBuilder();
        File rootDirectory = new File("");
        String path = rootDirectory.getAbsolutePath() + contentFolder;
        File file = new File(path, contentName + "_" + count + ".json");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            while ((tempString = reader.readLine()) != null) {
                rlt.append(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return rlt.toString();
    }
}
