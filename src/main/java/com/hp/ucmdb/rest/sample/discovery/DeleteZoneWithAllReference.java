package com.hp.ucmdb.rest.sample.discovery;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.ucmdb.rest.sample.utils.PayloadUtils;
import com.hp.ucmdb.rest.sample.utils.RestApiConnectionUtils;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DeleteZoneWithAllReference {
    private String rootURL;

    public DeleteZoneWithAllReference(String rootURL) {
        this.rootURL = rootURL;
    }

    public static void main(String[] args) {
        if(args.length < 4 ){
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
        if(token == null || token.length() == 0){
            System.out.println("Can not log in to the UCMDB server. Check your serverIp, userName or password!");
            System.exit(0);;
        }
        System.out.println(token);

        // start the task
        DeleteZoneWithAllReference task = new DeleteZoneWithAllReference("https://" + hostname + ":" + port + "/rest-api/");
        task.execute(token, zonename);
    }

    private void execute(String token, String zonename) {
        // check if new UI backend enabled
        String response = "";
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


        int count = 1;
        // get zone info
        Set<String> iprangeProfiles = new HashSet<String>();
        Set<String> discoveryProfiles = new HashSet<String>();
        Set<String> scheduleProfiles = new HashSet<String>();
        Set<String> credentialProfiles = new HashSet<String>();
        try {
            response = RestApiConnectionUtils.doGet(rootURL + "discovery/managementzones/" + zonename, token );
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


        } catch (Exception e) {
            e.printStackTrace();
        }

        // delete the zone
        try {
            RestApiConnectionUtils.doPatch(rootURL + "discovery/managementzones/" + zonename + "?operation=deactivate", token, null );
            response = RestApiConnectionUtils.doDelete(rootURL + "discovery/managementzones/" + zonename, token, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // delete underline iprange profile
        try {
            for(String ipProfile : iprangeProfiles){
                response = RestApiConnectionUtils.doGet(rootURL + "discovery/iprangeprofiles/" + URLEncoder.encode(ipProfile,"UTF-8").replace("+", "%20"), token );
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode o = objectMapper.readValue(response, JsonNode.class);
                if(o.get("oob").asText().equals("false")){
                    // delete the profile
                    RestApiConnectionUtils.doDelete(rootURL + "discovery/iprangeprofiles/" + URLEncoder.encode(ipProfile,"UTF-8").replace("+", "%20"), token, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            for(String discoveryProfile : discoveryProfiles){
                response = RestApiConnectionUtils.doGet(rootURL + "discovery/discoveryprofiles/" + URLEncoder.encode(discoveryProfile,"UTF-8").replace("+", "%20"), token );
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode o = objectMapper.readValue(response, JsonNode.class);
                if(o.get("oob").asText().equals("false")){
                    // delete the profile
                    RestApiConnectionUtils.doDelete(rootURL + "discovery/discoveryprofiles/" + URLEncoder.encode(discoveryProfile,"UTF-8").replace("+", "%20"), token, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            for(String scheduleProfile : scheduleProfiles){
                response = RestApiConnectionUtils.doGet(rootURL + "discovery/scheduleprofiles/" + URLEncoder.encode(scheduleProfile, "UTF-8").replace("+", "%20"), token );
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode o = objectMapper.readValue(response, JsonNode.class);
                if(o.get("oob").asText().equals("false")){
                    // delete the profile
                    RestApiConnectionUtils.doDelete(rootURL + "discovery/scheduleprofiles/" + URLEncoder.encode(scheduleProfile, "UTF-8").replace("+", "%20"), token, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            for(String credentialProfile : credentialProfiles){
                response = RestApiConnectionUtils.doGet(rootURL + "discovery/credentialprofiles/" + URLEncoder.encode(credentialProfile, "UTF-8").replace("+", "%20"), token );
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode o = objectMapper.readValue(response, JsonNode.class);
                if(o.get("oob").asText().equals("false")){
                    // delete the profile
                    RestApiConnectionUtils.doDelete(rootURL + "discovery/credentialprofiles/" + URLEncoder.encode(credentialProfile, "UTF-8").replace("+", "%20"), token, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
