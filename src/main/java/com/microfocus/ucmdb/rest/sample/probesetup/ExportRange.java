/**
 * © Copyright 2011 - 2020 Micro Focus or one of its affiliates
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
package com.microfocus.ucmdb.rest.sample.probesetup;

import com.microfocus.ucmdb.rest.sample.utils.PayloadUtils;
import com.microfocus.ucmdb.rest.sample.utils.RestApiConnectionUtils;

import java.io.Console;
import java.util.Scanner;

public class ExportRange {
    private String rootURL;

    public ExportRange(String rootURL) {
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

        String rootURL = RestApiConnectionUtils.buildRootUrl(hostname, port,false);

        // authenticate
        String token = RestApiConnectionUtils.loginServer(rootURL, username, password);


        // start the task
        ExportRange task = new ExportRange(rootURL);
        task.execute(token);
    }

    private void execute(String token) throws Exception {
        int count = 1;
        String content = PayloadUtils.loadContent(this.getClass().getSimpleName(), count);
        RestApiConnectionUtils.downloadFile(rootURL + "dataflowmanagement/ranges/export?exportType=CSV&exportScope=ALL", token, content, "Export range with ALL scope","CSV");
        count ++;
        content = PayloadUtils.loadContent(this.getClass().getSimpleName(), count);
        RestApiConnectionUtils.downloadFile(rootURL + "dataflowmanagement/ranges/export?exportType=CSV&exportScope=SELECTED", token, content, "Export range with SELECTED scope","CSV");
    }
}
