**
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
package com.microfocus.ucmdb.rest.sample.probesetup;

import com.microfocus.ucmdb.rest.sample.utils.PayloadUtils;
import com.microfocus.ucmdb.rest.sample.utils.RestApiConnectionUtils;

import java.io.Console;
import java.util.Scanner;

public class NetworkScopeConfigurationForCredential {
    private String rootURL;

    public NetworkScopeConfigurationForCredential(String rootURL) {
        this.rootURL = rootURL;
    }

    public static void main(String[] args) throws Exception {
        String hostname;
        String port;
        String username;
        char[] password;

        if (args.length < 4) {
            Scanner sc = new Scanner(System.in);
            System.out.print("Please enter hostname/IP of UCMDB Server: ");
            hostname = sc.hasNextLine() ? sc.nextLine() : "";
            System.out.print("Please enter port of UCMDB Server: ");
            port = sc.hasNext() ? sc.next() : "";
            System.out.print("Please enter username for UCMDB: ");
            username = sc.hasNext() ? sc.next() : "";
            Console console = System.console();
            password = console.readPassword("Please enter password for UCMDB: ");
        } else {
            hostname = args[0];
            port = args[1];
            username = args[2];
            password = args[3].toCharArray();
        }

        String rootURL = RestApiConnectionUtils.buildRootUrl(hostname, port,false);

        // authenticate
        String token = RestApiConnectionUtils.loginServer(rootURL, username, password);

        // start the task
        NetworkScopeConfigurationForCredential task = new NetworkScopeConfigurationForCredential(rootURL);
        task.execute(token);

    }
    private void execute(String token) throws Exception {
        int count = 1;
        String content = PayloadUtils.loadContent(this.getClass().getSimpleName(), count);
        //Define the network scope (range level) for the credential
        RestApiConnectionUtils.doPost(rootURL + "dataflowmanagement/credentials", token, content, "Define the network scope (range level) for the credential ");
        count ++;
        //Disable the network scope (probe level) for the credential
        content = PayloadUtils.loadContent(this.getClass().getSimpleName(), count);
        RestApiConnectionUtils.doPost(rootURL + "dataflowmanagement/credentials", token, content, "Disable the network scope (probe level) for the credential");
        count ++;
        //Define the network scope (probe level) for the credential
        content = PayloadUtils.loadContent(this.getClass().getSimpleName(), count);
        RestApiConnectionUtils.doPost(rootURL + "dataflowmanagement/credentials", token, content, "Define the network scope (probe level) for the credential");
        count ++;
        //Define the network scope (both probe level and range level) for the credential
        content = PayloadUtils.loadContent(this.getClass().getSimpleName(), count);
        RestApiConnectionUtils.doPost(rootURL + "dataflowmanagement/credentials", token, content, "Define the network scope (both probe level and range level) for the credential");
    }
}
