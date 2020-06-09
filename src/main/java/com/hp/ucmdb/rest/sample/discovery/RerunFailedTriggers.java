package com.hp.ucmdb.rest.sample.discovery;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hp.ucmdb.rest.sample.utils.PayloadUtils;
import com.hp.ucmdb.rest.sample.utils.RestApiConnectionUtils;

import java.io.IOException;
import java.net.URLEncoder;

public class RerunFailedTriggers {
    private String rootURL;

    public RerunFailedTriggers(String rootURL) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (token == null || token.length() == 0) {
            System.out.println("Can not log in to the UCMDB server. Check your serverIp, userName or password!");
            System.exit(0);
            ;
        }
        System.out.println(token);

        // start the task
        RerunFailedTriggers task = new RerunFailedTriggers("https://" + hostname + ":" + port + "/rest-api/");
        task.execute(token, zonename);
    }

    private void execute(String token, String zonename) {
        String response = "";
        int fileCount = 1;
        // check if new UI backend enabled
        try {
            response = RestApiConnectionUtils.doGet(rootURL + "infrasetting?name=appilog.collectors.enableZoneBasedDiscovery", token );
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode node = objectMapper.readTree(response);
            if(!"true".equals(node.get("value").asText())){
                System.out.println("New Discovery backend is not enabled.");
                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }


        // run the zone
        try {
            RestApiConnectionUtils.doPatch(rootURL + "discovery/managementzones/" + zonename + "?operation=activate", token, null );
        } catch (IOException e) {
            e.printStackTrace();
        }


        // check zone triggers and return failed
        int total = 1;
        int count = 0;
        int start = 0;
        int bulkSize = 500;
        try {
            for(; total > count + start;){
                response = RestApiConnectionUtils.doGet( rootURL + "discovery/triggers?start=" + start + "&count=" + bulkSize
                        + "&sortField=ciLabel&orderByAsc=true&filter=mzoneIds"+ URLEncoder.encode("=[" + zonename + "]","UTF-8"), token );
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
                        ((ObjectNode)o.get("triggerItems").get(0)).put("jobId", item.get("triggerCiType"));

                        RestApiConnectionUtils.doPatch(rootURL + "discovery/triggers", token, o.toString() );
                    }
                }
            }
            fileCount ++;
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}
