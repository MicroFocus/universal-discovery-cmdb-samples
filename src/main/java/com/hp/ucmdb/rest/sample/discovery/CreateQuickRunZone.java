package com.hp.ucmdb.rest.sample.discovery;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.ucmdb.rest.sample.utils.PayloadUtils;
import com.hp.ucmdb.rest.sample.utils.RestApiConnectionUtils;

import java.io.IOException;

public class CreateQuickRunZone {
    private String rootURL;

    public CreateQuickRunZone(String rootURL) {
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
            System.exit(0);;
        }
        System.out.println(token);

        // start the task
        CreateQuickRunZone task = new CreateQuickRunZone(rootURL);
        task.execute(token);
    }

    public void execute(String token) {
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
        // create a quick zone
        String content = PayloadUtils.loadContent(this.getClass().getSimpleName(), count);
        count ++;

        try {
            RestApiConnectionUtils.doPost(rootURL + "discovery/zonewithreferencedresource", token,content);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // run the zone
        try {
            RestApiConnectionUtils.doPatch(rootURL + "discovery/managementzones/myzone?operation=activate", token, null );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
