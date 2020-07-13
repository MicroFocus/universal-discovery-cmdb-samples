package com.microfocus.ucmdb.rest.sample.discovery;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.microfocus.ucmdb.rest.sample.utils.PayloadUtils;
import com.microfocus.ucmdb.rest.sample.utils.RestApiConnectionUtils;

import java.io.IOException;

public class CreateInventoryZoneForWindowsProbe {
    private String rootURL;

    public CreateInventoryZoneForWindowsProbe(String rootURL) {
        this.rootURL = rootURL;
    }
    public static void main(String[] args) throws Exception {
        if(args.length < 4){
            System.out.println("Parameters: hostname port username password");
            System.exit(0);
        }

        String hostname = args[0];
        String port = args[1];
        String username = args[2];
        String password = args[3];

        String rootURL = RestApiConnectionUtils.buildRootUrl(hostname, port,false);

        // authenticate
        String token = RestApiConnectionUtils.loginServer(rootURL, username, password);

        // start the task
        CreateInventoryZoneForWindowsProbe task = new CreateInventoryZoneForWindowsProbe(rootURL);
        task.execute(token);
    }

    private void execute(String token) throws Exception {
        // check if new UI backend enabled
        RestApiConnectionUtils.ensureZoneBasedDiscoveryIsEnabled(rootURL, token);

        int count = 1;
        // create IP range
        String content = PayloadUtils.loadContent(this.getClass().getSimpleName(), count);
        count ++;
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode o = objectMapper.readValue(content, JsonNode.class);
        RestApiConnectionUtils.doPost(rootURL + "dataflowmanagement/probes/DataFlowProbe/ranges", token, o.toString(), "CREATE IP RANGE.");

        // create IP range group
        content = PayloadUtils.loadContent(this.getClass().getSimpleName(), count);
        count ++;
        objectMapper = new ObjectMapper();
        o = objectMapper.readValue(content, JsonNode.class);
        RestApiConnectionUtils.doPatch(rootURL + "discovery/iprangeprofiles", token, o.toString(), "CREATE IP RANGE PROFILE.");

        // create NTCMD credential
        content = PayloadUtils.loadContent(this.getClass().getSimpleName(), count);
        count ++;
        String credentialId = RestApiConnectionUtils.doPost(rootURL + "dataflowmanagement/credentials", token, content, "CREATE NTCMD CREADENTIAL.");
        credentialId = credentialId.substring(1,credentialId.length() - 1);

        // create Inventory credential group
        content = PayloadUtils.loadContent(this.getClass().getSimpleName(), count);
        count ++;
        objectMapper = new ObjectMapper();
        o = objectMapper.readValue(content, JsonNode.class);
        ((ObjectNode)o.get("credentials").get(0).get("protocols")).putArray("ntadminprotocol").add(credentialId);
        RestApiConnectionUtils.doPost(rootURL + "discovery/credentialprofiles", token, o.toString(), "CREATE CREADENTIAL GROUP.");


        // create Inventory job group
        content = PayloadUtils.loadContent(this.getClass().getSimpleName(), count);
        count ++;
        RestApiConnectionUtils.doPost(rootURL + "discovery/discoveryprofiles", token, content, "CREATE JOB GROUP.");

        // create Inventory discovery schedule
        content = PayloadUtils.loadContent(this.getClass().getSimpleName(), count);
        count ++;
        RestApiConnectionUtils.doPost(rootURL + "discovery/scheduleprofiles", token, content, "CREATE SCHEDULE.");

        // create the zone
        content = PayloadUtils.loadContent(this.getClass().getSimpleName(), count);
        count ++;
        RestApiConnectionUtils.doPost(rootURL + "discovery/managementzones", token, content, "CREATE ZONE.");

        // run the zone
        RestApiConnectionUtils.doPatch(rootURL + "discovery/managementzones/ProbeInventoryZone?operation=activate", token, null, "RUN THE ZONE.");

    }
}
