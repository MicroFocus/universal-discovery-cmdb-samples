package com.hp.ucmdb.rest.sample.integration;
import com.hp.ucmdb.rest.sample.utils.RestApiConnectionUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
/*
     This scenario is to get the following information of specific sample points: status, statistics, details for each sample point.
     Then you can view the job list and status of each job.
 */
public class ViewIntegrationPointsScenarioSample {
    //the parameters you need to provide are: serverIp, userName, password and integrationPointName
    public static void main(String[] args) throws Exception {

        if(args.length < 3 ){
            System.out.println("Parameters: hostname username password integrationPointName");
            System.exit(0);
        }

        String hostname = args[0];
        String username = args[1];
        String password = args[2];
        String integrationPointName = args[3];
        String port = "8443";

        String rootURL = RestApiConnectionUtils.buildRootUrl(hostname, port,false);

        String token = RestApiConnectionUtils.loginServer(rootURL, username, password);
        if(token == null || token.length() == 0){
            System.out.println("Can not log in to the UCMDB server. Check your serverIp, userName or password!");
            return;
        }
        System.out.println(token);

        //get details of all sample points
        JSONObject allIntegrationPoints = new JSONObject(IntegrationCommonConnectionUtils.getAllIntegrationPoints(token, rootURL));
        if(allIntegrationPoints == null){
            System.out.println("Can not get details of all sample points!");
            return;
        }
        System.out.println("Sample points list is " + IntegrationCommonConnectionUtils.getAllIntegrationPointNames(allIntegrationPoints));
        
        //get a specific sample point
        String integrationPointDetail = allIntegrationPoints.getJSONObject(integrationPointName).toString();
        System.out.println( "Details of the sample point (" + integrationPointName + ") are: " + integrationPointDetail);

        //get job list of a specific sample point (including job status)
        List<String> allIntegrationPointNames = IntegrationCommonConnectionUtils.getAllIntegrationPointNames(allIntegrationPoints);
        for (String ipName : allIntegrationPointNames) {
            JSONObject detail = allIntegrationPoints.getJSONObject(ipName);
            System.out.println(ipName + " : ");
            //get population jobs
            JSONArray populationJobList = detail.getJSONArray("dataPopulationJobs");
            System.out.print("population job list size is " + populationJobList.length() + " ; [ ");
            for(int i = 0; i < populationJobList.length(); i++){
                JSONObject tmp = populationJobList.getJSONObject(i);
                String jobName = tmp.getString("displayID");
                System.out.print(jobName + "");
                JSONObject statusJson = tmp.getJSONObject("jobStatistics");
                String status = statusJson.getString("jobStatus");
                System.out.print(":{"+status+"} ");
            }
            System.out.print("]");
            System.out.println();

            //get push jobs
            JSONArray pushJobList = detail.getJSONArray("dataPushJobs");
            System.out.print("push job list size is " + pushJobList.length() + " ; [ ");
            for (int i = 0; i < pushJobList.length(); i++) {
                JSONObject tmp = pushJobList.getJSONObject(i);
                String jobName = tmp.getString("displayID");
                System.out.print(jobName + " ");

                String status = "UNKNOWN";
                boolean enabled = detail.getBoolean("enabled");
                if(!enabled){//disabled
                    status = "DISABLED";
                }else {
                    JSONObject statusJson = tmp.getJSONObject("jobRunCurrentStatus");
                    if(statusJson != null && "RUNNING".equals(statusJson.getString("status"))){//running
                        status = "RUNNING";
                    }else {//other status
                        statusJson = tmp.getJSONArray("jobRunHistory").getJSONObject(0);
                        if(statusJson != null) status = statusJson.getString("status");
                    }
                }
                System.out.print(":{"+status+"} ");
            }
            System.out.print("]");
            System.out.println();
        }
        System.out.println("Done!");
    }

}
