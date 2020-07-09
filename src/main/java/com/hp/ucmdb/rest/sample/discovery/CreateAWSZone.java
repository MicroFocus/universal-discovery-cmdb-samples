package com.hp.ucmdb.rest.sample.discovery;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hp.ucmdb.rest.sample.utils.PayloadUtils;
import com.hp.ucmdb.rest.sample.utils.RestApiConnectionUtils;
import org.json.JSONObject;

import java.io.IOException;

public class CreateAWSZone {
    private String rootURL;

    public CreateAWSZone(String rootURL) {
        this.rootURL = rootURL;
    }
    public static void main(String[] args) {
        if(args.length < 3 ){
            System.out.println("Parameters: hostname username password");
            System.exit(0);
        }

        String hostname = args[0];
        String username = args[1];
        String password = args[2];
        String port = "8443";

        String rootURL = RestApiConnectionUtils.buildRootUrl(hostname, port,false);

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

        // start the task
        CreateAWSZone task = new CreateAWSZone(rootURL);
        task.execute(token);
    }

    private void execute(String token) {

        // check if new UI backend enabled
        String response = "";
        try {
            response = RestApiConnectionUtils.doGet(rootURL + "infrasetting?name=appilog.collectors.enableZoneBasedDiscovery", token);
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
        // create AWS credential
        String credentialId = "";
        try {
            credentialId = RestApiConnectionUtils.doPost(rootURL + "dataflowmanagement/credentials", token, content );
            credentialId = credentialId.substring(1,credentialId.length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // create AWS credential profile
        content = PayloadUtils.loadContent(this.getClass().getSimpleName(), count);
        count ++;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode o = objectMapper.readValue(content, JsonNode.class);
            ((ObjectNode)o.get("credentials").get(0).get("protocols")).putArray("awsprotocol").add(credentialId);
            RestApiConnectionUtils.doPost(rootURL + "discovery/credentialprofiles", token, o.toString() );
        } catch (Exception e) {
            e.printStackTrace();
        }


        // create AWS discovery profile
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
