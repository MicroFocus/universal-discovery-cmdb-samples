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
package com.microfocus.ucmdb.rest.sample.discovery;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microfocus.ucmdb.rest.sample.utils.PayloadUtils;
import com.microfocus.ucmdb.rest.sample.utils.RestApiConnectionUtils;

import java.io.CharArrayReader;
import java.io.Console;
import java.io.IOException;
import java.util.Scanner;

public class CreateInventoryZoneForWindowsProbe {
    private String rootURL;

    public CreateInventoryZoneForWindowsProbe(String rootURL) {
        this.rootURL = rootURL;
    }
    public static void main(String[] args) throws Exception {
        String hostname;
        String port;
        String username;
        char[] password;

        if (args.length < 5) {
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
        CreateInventoryZoneForWindowsProbe task = new CreateInventoryZoneForWindowsProbe(rootURL);
        task.execute(token);
    }

    private void execute(String token) throws Exception {
        // check if new UI backend enabled
        RestApiConnectionUtils.ensureZoneBasedDiscoveryIsEnabled(rootURL, token);

        int count = 1;
        // create IP range
        String content = PayloadUtils.loadContent(this.getClass().getSimpleName(), count);
        count ++;
        RestApiConnectionUtils.doPost(rootURL + "dataflowmanagement/probes/DataFlowProbe/ranges", token, content, "CREATE IP RANGE.");

        // create IP range group
        content = PayloadUtils.loadContent(this.getClass().getSimpleName(), count);
        count ++;
        RestApiConnectionUtils.doPatch(rootURL + "discovery/iprangeprofiles", token, content, "CREATE IP RANGE PROFILE.");

        // create NTCMD credential
        content = PayloadUtils.loadContent(this.getClass().getSimpleName(), count);
        count ++;
        String credentialId = RestApiConnectionUtils.doPost(rootURL + "dataflowmanagement/credentials", token, content, "CREATE NTCMD CREADENTIAL.");
        credentialId = credentialId.substring(1,credentialId.length() - 1);

        // create Inventory credential group
        content = PayloadUtils.loadContent(this.getClass().getSimpleName(), count);
        count ++;
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode o = objectMapper.readValue(content, JsonNode.class);
        ((ObjectNode)o.get("credentials").get(0).get("protocols")).putArray("ntadminprotocol").add(credentialId);
        RestApiConnectionUtils.doPost(rootURL + "discovery/credentialprofiles", token, o.toString(), "CREATE CREADENTIAL GROUP.");


        // create Inventory job group
        content = PayloadUtils.loadContent(this.getClass().getSimpleName(), count);
        count ++;
        RestApiConnectionUtils.doPost(rootURL + "discovery/discoveryprofiles", token, content, "CREATE JOB GROUP.");

        // create Inventory discovery schedule
        content = PayloadUtils.loadContent(this.getClass().getSimpleName(), count);
        count ++;
        RestApiConnectionUtils.doPost(rootURL + "discovery/scheduleprofiles", token, content, "CREATE SCHEDULE.");

        // create the zone
        content = PayloadUtils.loadContent(this.getClass().getSimpleName(), count);
        count ++;
        RestApiConnectionUtils.doPost(rootURL + "discovery/managementzones", token, content, "CREATE ZONE.");

        // run the zone
        RestApiConnectionUtils.doPatch(rootURL + "discovery/managementzones/ProbeInventoryZone?operation=activate", token, null, "RUN THE ZONE.");

    }
}
