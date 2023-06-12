/**
 * Copyright 2023 Open Text
 * The only warranties for products and services of Open Text and its affiliates and licensors (“Open Text”) are as may be set forth in the express warranty statements accompanying such products and services.
 * Nothing herein should be construed as constituting an additional warranty.
 * Open Text shall not be liable for technical or editorial errors or omissions contained herein.
 * The information contained herein is subject to change without notice
 *
 * Except as specifically indicated otherwise, this document contains confidential information and a valid license is required for possession, use or copying.
 * If this work is provided to the U.S. Government, consistent with FAR 12.211 and 12.212, Commercial Computer Software, Computer Software Documentation, and Technical Data for Commercial Items are licensed to the U.S. Government under vendor's standard commercial license.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    public static File loadFile(String fileName) {
        File rootDirectory = new File("");
        String path = rootDirectory.getAbsolutePath() + contentFolder;
        return new File(path, fileName);
    }
}
