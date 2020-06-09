package com.hp.ucmdb.rest.sample.discovery;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hp.ucmdb.rest.sample.utils.PayloadUtils;
import com.hp.ucmdb.rest.sample.utils.RestApiConnectionUtils;

import java.io.IOException;
import java.net.URLEncoder;

public class GetComlogOnTrigger {
    private String rootURL;

    public GetComlogOnTrigger(String rootURL) {
        this.rootURL = rootURL;
    }

    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("Parameters: hostname username password zonename");
            System.exit(0);
        }

        String hostname = args[0];
        String username = args[1];
        String password = args[2];
        String zonename = args[3];
        String port = "8443";

        // authenticate
        String token = null;
        try {
            token = RestApiConnectionUtils.loginServer(hostname, username, password);
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        if (token == null || token.length() == 0) {
            System.out.println("Can not log in to the UCMDB server. Check your serverIp, userName or password!");
            System.exit(0);
            ;
        }
        System.out.println(token);

        // start the task
        GetComlogOnTrigger task = new GetComlogOnTrigger("https://" + hostname + ":" + port + "/rest-api/");
        task.execute(token, zonename);
    }

    private void execute(String token, String zonename) {
        String response = "";
        int fileCount = 1;
        // check if new UI backend enabled
        try {
            response = RestApiConnectionUtils.doGet(rootURL + "infrasetting?name=appilog.collectors.enableZoneBasedDiscovery", token);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(response);
            if (!"true".equals(node.get("value").asText())) {
                System.out.println("New Discovery backend is not enabled.");
                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }


        // run the zone
        try {
            RestApiConnectionUtils.doPatch(rootURL + "discovery/managementzones/" + zonename + "?operation=activate", token, null);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Get the first trigger and return it with comlog
        int total = 1;
        int count = 0;
        int start = 0;
        int bulkSize = 500;
        String jobId = "";
        String triggerId = "";
        String probeName = "";
        try {
            response = RestApiConnectionUtils.doGet(rootURL + "discovery/triggers?filter=mzoneIds" + URLEncoder.encode("=[" + zonename + "]", "UTF-8"), token);
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
            String content = PayloadUtils.loadContent(this.getClass().getSimpleName(), count);
            fileCount++;
            JsonNode o = objectMapper.readValue(content, JsonNode.class);
            ((ObjectNode) o.get("triggerItems").get(0)).put("jobId", jobId);
            ((ObjectNode) o.get("triggerItems").get(0)).put("mzoneId",  zoneId);
            ((ObjectNode) o.get("triggerItems").get(0)).put("triggerCiId", triggerId);

            RestApiConnectionUtils.doPatch(rootURL + "discovery/triggers", token, o.toString());


        } catch (Exception e) {
            e.printStackTrace();
        }

        // wait for finish and get log
        try {
            boolean finished = false;
            while(!finished){
                response = RestApiConnectionUtils.doGet(rootURL + "discovery/triggers?" + URLEncoder.encode("filter=mzoneIds=[" + zonename + "]&filter=triggerCiId=[" + triggerId + "]", "UTF-8"), token);
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode triggers = objectMapper.readTree(response);
                String status = triggers.get("items").get(0).get("status").asText();
                if("SUCCESS".equals(status) || "ERROR".equals(status)|| "WARNING".equals(status)){
                    finished = true;
                }
                Thread.sleep(5000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // get log
        try {
            response = RestApiConnectionUtils.doPatch(rootURL + "discovery/triggers/" + triggerId + "communicationlog?" + URLEncoder.encode("probeName=" + probeName + "&zoneId=" + zonename + "&jobId=" + jobId, "UTF-8"), token, null);
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
