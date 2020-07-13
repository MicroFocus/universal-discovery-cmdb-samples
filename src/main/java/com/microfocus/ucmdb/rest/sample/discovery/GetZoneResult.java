package com.microfocus.ucmdb.rest.sample.discovery;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfocus.ucmdb.rest.sample.utils.RestApiConnectionUtils;

import java.io.IOException;
import java.net.URLEncoder;

public class GetZoneResult {
    private String rootURL;

    public GetZoneResult(String rootURL) {
        this.rootURL = rootURL;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 5) {
            System.out.println("Parameters: hostname port username password zonename");
            System.exit(0);
        }

        String hostname = args[0];
        String port = args[1];
        String username = args[2];
        String password = args[3];
        String zonename = args[4];

        String rootURL = RestApiConnectionUtils.buildRootUrl(hostname, port,false);

        // authenticate
        String token = RestApiConnectionUtils.loginServer(rootURL, username, password);

        // start the task
        GetZoneResult task = new GetZoneResult(rootURL);
        task.execute(token, zonename);
    }

    private void execute(String token, String zonename) throws Exception {
        // check if new UI backend enabled
        RestApiConnectionUtils.ensureZoneBasedDiscoveryIsEnabled(rootURL, token);

        String response = null;
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

        // get zone result and print
        response = RestApiConnectionUtils.doGet(rootURL + "discovery/results/statistics?mzoneId=" + zonename ,
                token, "GET ZONE STATISTICS.");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree(response);
        for(JsonNode item : node){
            System.out.println(item);
        }

        String classType = node.get(0).get("ciType").asText();
        int count = node.get(0).get("count").asInt();

        response = RestApiConnectionUtils.doGet(rootURL + "discovery/results?zoneId=" + zonename + "&citype=" + classType,
                token, "GET ZONE RESULTS.");

    }
}
