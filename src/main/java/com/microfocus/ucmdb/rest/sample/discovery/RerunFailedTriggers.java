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

import java.io.Console;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Scanner;

public class RerunFailedTriggers {
    private String rootURL;

    public RerunFailedTriggers(String rootURL) {
        this.rootURL = rootURL;
    }

    public static void main(String[] args) throws Exception {
        String hostname;
        String port;
        String username;
        char[] password;
        String zonename;

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
            System.out.print("Please enter zonename: ");
            zonename = sc.hasNext() ? sc.next() : "";
        } else {
            hostname = args[0];
            port = args[1];
            username = args[2];
            password = args[3].toCharArray();
            zonename = args[4];
        }

        String rootURL = RestApiConnectionUtils.buildRootUrl(hostname, port,false);

        // authenticate
        String token = RestApiConnectionUtils.loginServer(rootURL, username, password);

        // start the task
        RerunFailedTriggers task = new RerunFailedTriggers(rootURL);
        task.execute(token, zonename);
    }

    private void execute(String token, String zonename) throws Exception {

        // check if new UI backend enabled
        RestApiConnectionUtils.ensureZoneBasedDiscoveryIsEnabled(rootURL, token);

        String response = null;
        int fileCount = 1;
        // run the zone
        RestApiConnectionUtils.doPatch(rootURL + "discovery/managementzones/" + zonename + "?operation=activate",
                token, null, "ACTIVATE THE ZONE.");

        // check zone status until finish
        boolean finished = false;
        while(!finished){
            Thread.sleep(5000);
            int total = 1;
            int count = 0;
            int start = 0;
            int bulkSize = 500;
            finished = true;
            for(; total > count + start;){
                response = RestApiConnectionUtils.doGet(rootURL + "discovery/triggers" + "?start=" + start
                                + "&count=" + bulkSize + "&sortField=ciLabel&orderByAsc=true&" + "filter=mzoneIds" + URLEncoder.encode("=[" + zonename + "]", "UTF-8"),
                        token, "CHECK IF ZONE DISCOVERY FINISHED.");
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode triggers = objectMapper.readTree(response);
                total = triggers.get("total").asInt();
                count = triggers.get("count").asInt();
                start = triggers.get("start").asInt();
                if (0 == triggers.get("items").size()) {
                    finished = false;
                } else {
                    for(JsonNode item : triggers.get("items")){
                        if(!("SUCCESS".equals(item.get("status").asText()) || "ERROR".equals(item.get("status").asText()) || "WARNING".equals(item.get("status").asText()))){
                            finished = false;
                        }
                    }
                }
            }
        }

        // check zone triggers and return failed
        int total = 1;
        int count = 0;
        int start = 0;
        int bulkSize = 500;
        try {
            for(; total > count + start;){
                response = RestApiConnectionUtils.doGet(rootURL + "discovery/triggers" + "?start=" + start
                        + "&count=" + bulkSize + "&sortField=ciLabel&orderByAsc=true&" + "filter=mzoneIds" + URLEncoder.encode("=[" + zonename + "]", "UTF-8"),
                        token, "GET TRIGGERS OF ZONE.");
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode triggers = objectMapper.readTree(response);
                total = triggers.get("total").asInt();
                count = triggers.get("count").asInt();
                start = triggers.get("start").asInt();
                for(JsonNode item : triggers.get("items")){
                    if("ERROR".equals(item.get("status").asText())){
                        String content = PayloadUtils.loadContent(this.getClass().getSimpleName(), fileCount);
                        JsonNode o = objectMapper.readValue(content, JsonNode.class);
                        ((ObjectNode)o.get("triggerItems").get(0)).put("jobId", item.get("jobId"));
                        ((ObjectNode)o.get("triggerItems").get(0)).put("mzoneId", item.get("mzoneId"));
                        ((ObjectNode)o.get("triggerItems").get(0)).put("triggerCiId", item.get("triggerCiId"));

                        RestApiConnectionUtils.doPatch(rootURL + "discovery/triggers", token, o.toString(), "RERUN FAILED TRIGGERS.");
                    }
                }
            }
            fileCount ++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
