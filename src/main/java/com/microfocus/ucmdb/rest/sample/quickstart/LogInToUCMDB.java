/**
 * Copyright 2023 Open Text
 * The only warranties for products and services of Open Text and its affiliates and licensors ("Open Text") are as may be set forth in the express warranty statements accompanying such products and services.
 * Nothing herein should be construed as constituting an additional warranty.
 * Open Text shall not be liable for technical or editorial errors or omissions contained herein.
 * The information contained herein is subject to change without notice.
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
package com.microfocus.ucmdb.rest.sample.quickstart;

import com.microfocus.ucmdb.rest.sample.utils.RestApiConnectionUtils;

import java.io.Console;
import java.io.IOException;
import java.util.Scanner;

/**
 * @author wusui
 * @version $Id: LoginUCMDB.java, 2020-07-07 3:58 PM wusui Exp $
 */
public class LogInToUCMDB {

    public static void main(String[] args) {
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





        String rootURL = RestApiConnectionUtils.buildRootUrl(hostname, port,  false);

        // authenticate
        String token = null;
        try {
            token = RestApiConnectionUtils.loginServer(rootURL, username, password);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(token == null || token.length() == 0){
            System.out.println("Can not log in to the UCMDB server. Check your serverIp, userName or password!");
            System.exit(0);
        }
        System.out.println("Login successful! You have got the token to access UCMDB: " + token);

    }

}
