package com.hp.ucmdb.rest.sample.discovery;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hp.ucmdb.rest.sample.utils.PayloadUtils;
import com.hp.ucmdb.rest.sample.utils.RestApiConnectionUtils;

import java.io.IOException;

public class CreateInventoryZoneForWindowsProbe {
    private String rootURL;

    public CreateInventoryZoneForWindowsProbe(String rootURL) {
        this.rootURL = rootURL;
    }
    public static void main(String[] args) {
        if(args.length < 5 ){
            System.out.println("Parameters: hostname username password probelogin probepassword");
            System.exit(0);
        }

        String hostname = args[0];
        String username = args[1];
        String password = args[2];
        String probeLogin = args[3];
        String probePassword = args[4];
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
        CreateInventoryZoneForWindowsProbe task = new CreateInventoryZoneForWindowsProbe("https://" + hostname + ":" + port + "/rest-api/");
        task.execute(token);
    }

    private void execute(String token) {
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
        String content = PayloadUtils.loadContent(this.getClass().getSimpleName(), count);
        count ++;
        // create NTCMD credential
        String credentialId = "";
        try {
            credentialId = RestApiConnectionUtils.doPost(rootURL + "dataflowmanagement/credentials", token, content );
            credentialId = credentialId.substring(1,credentialId.length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // create Inventory credential profile
        content = PayloadUtils.loadContent(this.getClass().getSimpleName(), count);
        count ++;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode o = objectMapper.readValue(content, JsonNode.class);
            ((ObjectNode)o.get("credentials").get(0).get("protocols")).putArray("ntadminprotocol").add(credentialId);
            RestApiConnectionUtils.doPost(rootURL + "discovery/credentialprofiles", token, o.toString() );
        } catch (Exception e) {
            e.printStackTrace();
        }


        // create Inventory discovery profile
        content = PayloadUtils.loadContent(this.getClass().getSimpleName(), count);
        count ++;
        try {
            RestApiConnectionUtils.doPost(rootURL + "discovery/discoveryprofiles", token, content);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // create the zone
        content = PayloadUtils.loadContent(this.getClass().getSimpleName(), count);
        count ++;
        try {
            RestApiConnectionUtils.doPost(rootURL + "discovery/managementzones", token, content);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
