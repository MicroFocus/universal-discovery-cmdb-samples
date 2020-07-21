/**
 * Copyright 2020 EntIT Software LLC, a Micro Focus company, L.P.
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

public class GetComlogOnTrigger {
    private String rootURL;

    public GetComlogOnTrigger(String rootURL) {
        this.rootURL = rootURL;
    }

    public static void main(String[] args) throws Exception {
        String hostname;
        String port;
        String username;
        String password;
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
            password = new String(console.readPassword("Please enter password for UCMDB: "));
            System.out.print("Please enter zonename: ");
            zonename = sc.hasNext() ? sc.next() : "";
        } else {
            hostname = args[0];
            port = args[1];
            username = args[2];
            password = args[3];
            zonename = args[4];
        }

        String rootURL = RestApiConnectionUtils.buildRootUrl(hostname, port,false);

        // authenticate
        String token = RestApiConnectionUtils.loginServer(rootURL, username, password);

        // start the task
        GetComlogOnTrigger task = new GetComlogOnTrigger(rootURL);
        task.execute(token, zonename);
    }

    private void execute(String token, String zonename) throws Exception {
        String response = "";
        int fileCount = 1;
        // check if new UI backend enabled
        RestApiConnectionUtils.ensureZoneBasedDiscoveryIsEnabled(rootURL, token);

        // run the zone
        RestApiConnectionUtils.doPatch(rootURL + "discovery/managementzones/" + zonename + "?operation=activate", token, null, "ACTIVATE ZONE.");

        // Get the first trigger and return it with comlog
        int total = 1;
        int count = 0;
        int start = 0;
        int bulkSize = 500;
        String jobId = "";
        String triggerId = "";
        String probeName = "";
        response = RestApiConnectionUtils.doGet(rootURL + "discovery/triggers" + "?start=" + start
                + "&count=" + bulkSize + "&sortField=ciLabel&orderByAsc=true&" + "filter=mzoneIds" + URLEncoder.encode("=[" + zonename + "]", "UTF-8"),
                token, "GET TRIGGERS OF ZONE.");

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode triggers = objectMapper.readTree(response);
        total = triggers.get("total").asInt();
        count = triggers.get("count").asInt();
        start = triggers.get("start").asInt();
        JsonNode item = triggers.get("items").get(0);
        jobId = item.get("jobId").asText();
        String zoneId = item.get("mzoneId").asText();
        triggerId = item.get("triggerCiId").asText();
        probeName = item.get("probeName").asText();

        // rerun with comlog
        String content = PayloadUtils.loadContent(this.getClass().getSimpleName(), fileCount);
        fileCount++;
        JsonNode o = objectMapper.readValue(content, JsonNode.class);
        ((ObjectNode) o.get("triggerItems").get(0)).put("jobId", jobId);
        ((ObjectNode) o.get("triggerItems").get(0)).put("mzoneId",  zoneId);
        ((ObjectNode) o.get("triggerItems").get(0)).put("triggerCiId", triggerId);

        RestApiConnectionUtils.doPatch(rootURL + "discovery/triggers", token, o.toString(), "RERUN A TRIGGER.");

        // wait for finish and get log
        boolean finished = false;
        while(!finished){
            Thread.sleep(5000);
            response = RestApiConnectionUtils.doGet(rootURL + "discovery/triggers/" + triggerId
                    + "?mzoneId=" + zonename + "&jobId=" + URLEncoder.encode(jobId, "UTF-8").replace("+", "%20"),
                    token, "CHECK TRIGGER RUNNING STATUS.");
            objectMapper = new ObjectMapper();
            JsonNode trigger = objectMapper.readTree(response);
            String status = trigger.get("status").asText();
            if("SUCCESS".equals(status) || "ERROR".equals(status)|| "WARNING".equals(status)){
                finished = true;
            }
        }

        // get log
        response = RestApiConnectionUtils.doGet(rootURL + "discovery/triggers/" + triggerId + "/communicationlog?"
                + "probeName=" + probeName + "&zoneId=" + zonename + "&jobId=" + URLEncoder.encode(jobId, "UTF-8").replace("+", "%20"),
                token, "GET COMMUNICATION LOG OF TRIGGER.");

    }
}
