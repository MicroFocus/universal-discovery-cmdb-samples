package com.microfocus.ucmdb.rest.sample.discovery;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfocus.ucmdb.rest.sample.utils.RestApiConnectionUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

public class DeleteZoneWithAllReference {
    private String rootURL;

    public DeleteZoneWithAllReference(String rootURL) {
        this.rootURL = rootURL;
    }

    public static void main(String[] args) throws Exception {
        if(args.length < 5){
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
        DeleteZoneWithAllReference task = new DeleteZoneWithAllReference(rootURL);
        task.execute(token, zonename);
    }

    private void execute(String token, String zonename) throws Exception {
        // check if new UI backend enabled
        RestApiConnectionUtils.ensureZoneBasedDiscoveryIsEnabled(rootURL, token);

        String response = null;
        int count = 1;
        // get zone info
        Set<String> iprangeProfiles = new HashSet<String>();
        Set<String> discoveryProfiles = new HashSet<String>();
        Set<String> scheduleProfiles = new HashSet<String>();
        Set<String> credentialProfiles = new HashSet<String>();
        response = RestApiConnectionUtils.doGet(rootURL + "discovery/managementzones/" + zonename, token, "GET ZONE INFORMATION");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode zoneJson = objectMapper.readTree(response);
        if(zoneJson.get("ipRangeProfiles").isArray()){
            for(JsonNode ipProfile : zoneJson.get("ipRangeProfiles")){
                String id = ipProfile.get("ipRangeProfileId").toString();
                iprangeProfiles.add(id.substring(1, id.length() - 1 ));
            }
        }

        if(zoneJson.get("discoveryActivities").isArray()){
            for(JsonNode activity: zoneJson.get("discoveryActivities")){
                String id = activity.get("discoveryProfileId").toString();
                discoveryProfiles.add(id.substring(1, id.length() - 1));
                id = activity.get("scheduleProfileId").toString();
                scheduleProfiles.add(id.substring(1, id.length() - 1));
                id = activity.get("credentialProfileId").toString();
                credentialProfiles.add(id.substring(1, id.length() - 1));
            }
        }

        // delete the zone
        RestApiConnectionUtils.doPatch(rootURL + "discovery/managementzones/" + zonename + "?operation=deactivate",
                token, null, "DEACTIVATE ZONE.");

        RestApiConnectionUtils.doDelete(rootURL + "discovery/managementzones/" + zonename,
                token, null, "DELETE ZONE.");

        // delete underline iprange profile
        for(String ipProfile : iprangeProfiles){
            response = RestApiConnectionUtils.doGet(rootURL + "discovery/iprangeprofiles/"
                    + URLEncoder.encode(ipProfile,"UTF-8").replace("+", "%20"),
                    token, "GET IP RANGE GROUPS FROM ZONE.");
            objectMapper = new ObjectMapper();
            JsonNode o = objectMapper.readValue(response, JsonNode.class);
            if(o.get("oob").asText().equals("false")){
                // delete the profile
                RestApiConnectionUtils.doDelete(rootURL + "discovery/iprangeprofiles/"
                        + URLEncoder.encode(ipProfile,"UTF-8").replace("+", "%20"),
                        token, null, "DELETE IP RANGE GROUPS.");
            }
        }

        for(String discoveryProfile : discoveryProfiles){
            response = RestApiConnectionUtils.doGet(rootURL + "discovery/discoveryprofiles/"
                    + URLEncoder.encode(discoveryProfile,"UTF-8").replace("+", "%20"),
                    token, "GET JOB GROUPS FROM ZONE.");
            objectMapper = new ObjectMapper();
            JsonNode o = objectMapper.readValue(response, JsonNode.class);
            if(o.get("oob").asText().equals("false")){
                // delete the profile
                RestApiConnectionUtils.doDelete(rootURL + "discovery/discoveryprofiles/"
                        + URLEncoder.encode(discoveryProfile,"UTF-8").replace("+", "%20"),
                        token, null, "DELETE JOB GROUPS.");
            }
        }

        for(String credentialProfile : credentialProfiles){
            response = RestApiConnectionUtils.doGet(rootURL + "discovery/credentialprofiles/"
                            + URLEncoder.encode(credentialProfile, "UTF-8").replace("+", "%20"),
                    token, "GET CREDENTIAL GROUPS FROM ZONE.");
            objectMapper = new ObjectMapper();
            JsonNode o = objectMapper.readValue(response, JsonNode.class);
            if(o.get("oob").asText().equals("false")){
                // delete the profile
                RestApiConnectionUtils.doDelete(rootURL + "discovery/credentialprofiles/"
                                + URLEncoder.encode(credentialProfile, "UTF-8").replace("+", "%20"),
                        token, null, "DELETE CREDENTIAL GROUPS.");
            }
        }

        for(String scheduleProfile : scheduleProfiles){
            response = RestApiConnectionUtils.doGet(rootURL + "discovery/scheduleprofiles/"
                    + URLEncoder.encode(scheduleProfile, "UTF-8").replace("+", "%20"),
                    token, "GET SCHEDULES FROM ZONE.");
            objectMapper = new ObjectMapper();
            JsonNode o = objectMapper.readValue(response, JsonNode.class);
            if(o.get("oob").asText().equals("false")){
                // delete the profile
                RestApiConnectionUtils.doDelete(rootURL + "discovery/scheduleprofiles/"
                        + URLEncoder.encode(scheduleProfile, "UTF-8").replace("+", "%20"),
                        token, null, "DELETE SCHEDULES.");
            }
        }

    }
}
