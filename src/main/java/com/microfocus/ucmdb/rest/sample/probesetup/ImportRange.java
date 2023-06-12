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
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.microfocus.ucmdb.rest.sample.probesetup;

import com.microfocus.ucmdb.rest.sample.utils.PayloadUtils;
import com.microfocus.ucmdb.rest.sample.utils.RestApiConnectionUtils;

import java.io.Console;
import java.io.File;
import java.util.Scanner;

public class ImportRange {
    private String rootURL;

    public ImportRange(String rootURL) {
        this.rootURL = rootURL;
    }

    public static void main(String[] args) throws Exception {
        String hostname;
        String port;
        String username;
        String password;

        if (args.length < 4) {
            Scanner sc = new Scanner(System.in);
            System.out.print("Please enter hostname/IP of UCMDB Server: ");
            hostname = sc.hasNextLine() ? sc.nextLine() : "";
            System.out.print("Please enter port of UCMDB Server: ");
            port = sc.hasNext() ? sc.next() : "";
            System.out.print("Please enter username for UCMDB: ");
            username = sc.hasNext() ? sc.next() : "";
            Console console = System.console();
            password = new String(console.readPassword("Please enter password for UCMDB: "));
        } else {
            hostname = args[0];
            port = args[1];
            username = args[2];
            password = args[3];
        }

        String rootURL = RestApiConnectionUtils.buildRootUrl(hostname, port, false);

        // authenticate
        String token = RestApiConnectionUtils.loginServer(rootURL, username, password);


        // start the task
        ImportRange task = new ImportRange(rootURL);
        task.execute(token);
    }

    private void execute(String token) throws Exception {
        //Import range from CSV with allowOverlap true
        File file= PayloadUtils.loadFile("Export_Data_1686548768110.CSV");
        RestApiConnectionUtils.uploadFile(rootURL + "dataflowmanagement/ranges/import?allowOverlap=true&importType=CSV", token, "Import range from CSV with allowOverlap true",file);
        //Import range from CSV with allowOverlap false
        RestApiConnectionUtils.uploadFile(rootURL + "dataflowmanagement/ranges/import?allowOverlap=false&importType=CSV", token, "Import range from CSV with allowOverlap false",file);
    }
}
