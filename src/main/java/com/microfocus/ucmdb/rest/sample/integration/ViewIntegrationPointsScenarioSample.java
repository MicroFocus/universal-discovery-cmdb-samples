package com.microfocus.ucmdb.rest.sample.integration;
import com.microfocus.ucmdb.rest.sample.utils.RestApiConnectionUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Console;
import java.util.*;
/*
     This scenario is to get the following information of specific sample points: status, statistics, details for each sample point.
     Then you can view the job list and status of each job.
 */
public class ViewIntegrationPointsScenarioSample {
    //the parameters you need to provide are: serverIp, userName, password and integrationPointName
    public static void main(String[] args) throws Exception {
        String hostname;
        String port;
        String username;
        String password;
        String integrationPointName;

        if (args.length < 5) {
            Scanner sc = new Scanner(System.in);
            System.out.print("Please enter hostname/IP of UCMDB Server: ");
            hostname = sc.hasNextLine() ? sc.nextLine() : "";
            System.out.print("Please enter port of UCMDB Server: ");
            port = sc.hasNext() ? sc.next() : "";
            System.out.print("Please enter username for UCMDB: ");
            username = sc.hasNext() ? sc.next() : "";
            Console console = System.console();
            password = new String(console.readPassword("Please enter password for UCMDB: "));
            System.out.print("Please enter integration point name: ");
            integrationPointName = sc.hasNext() ? sc.next() : "";
        } else {
            hostname = args[0];
            port = args[1];
            username = args[2];
            password = args[3];
            integrationPointName = args[4];
        }

        String rootURL = RestApiConnectionUtils.buildRootUrl(hostname, port,false);

        // authenticate
        String token = RestApiConnectionUtils.loginServer(rootURL, username, password);

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
