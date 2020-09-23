/**
 * © Copyright 2011 - 2020 Micro Focus or one of its affiliates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.microfocus.ucmdb.rest.sample.integration;
import com.microfocus.ucmdb.rest.sample.utils.RestApiConnectionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/*
    This scenario is to run full sync and then delta sync of all jobs under inactive sample points.
 */
public class JobSyncScenarioSample {
    public static void main(String[] args) throws Exception {
        String hostname;
        String port;
        String username;
        String password;

        if (args.length < 4) {
            Scanner sc = new Scanner(System.in);
            System.out.print("Please enter hostname/IP of UCMDB Server: ");
            hostname = sc.hasNextLine() ? sc.nextLine() : "";
            System.out.print("Please enter port of UCMDB Server: ");
            port = sc.hasNext() ? sc.next() : "";
            System.out.print("Please enter username for UCMDB: ");
            username = sc.hasNext() ? sc.next() : "";
            Console console = System.console();
            password = new String(console.readPassword("Please enter password for UCMDB: "));
        } else {
            hostname = args[0];
            port = args[1];
            username = args[2];
            password = args[3];
        }

        String rootURL = RestApiConnectionUtils.buildRootUrl(hostname, port,false);

        // authenticate
        String token = RestApiConnectionUtils.loginServer(rootURL, username, password);

        //get details of all sample points
        String getResult = IntegrationCommonConnectionUtils.getAllIntegrationPoints(token, rootURL);
        JSONObject allIntegrationPoints = new JSONObject(getResult);
        if(allIntegrationPoints == null){
            System.out.println("Can not get details of all sample points!");
            return;
        }

        //get names of all inactive sample points
        List<String> inactiveList = getAllInactiveIntegrationPoints(allIntegrationPoints);
        if(inactiveList.size() == 0){
            System.out.println("No inactive sample point found!");
            return;
        }
        System.out.println("The list of inactive sample points is " + inactiveList.toString());

        System.out.println();
        System.out.println("Now, start to full sync!");

        //full sync all the jobs of inactive sample points
        for (String integration_name: inactiveList) {
            //activate an inactive sample point
            if(!activeIntegrationPoint(token, rootURL, integration_name)){
                System.out.println("Failed to activate the " + integration_name + " sample point.");
                inactiveList.remove(integration_name);// remove the sample point if activation failed
                continue;
            }

            //get all the job in this sample point
            List<Map<String,String>> jobList = getAllJobs(allIntegrationPoints, integration_name);

            if(jobList.size() > 0){
                syncAllJobs(jobList,token,rootURL,integration_name,"FULL");//"FULL" means full sync
            }else{
                System.out.println("No jobs found in the " + integration_name + " sample point.");
            }
        }

        System.out.println();
        System.out.println("Now, start to delta sync!");

        //delta sync all the jobs of inactive
        for (String integration_name: inactiveList) {

            //get all the jobs in this sample point
            List<Map<String,String>> jobList = getAllJobs(allIntegrationPoints, integration_name);

            if(jobList.size() > 0){
                syncAllJobs(jobList,token,rootURL,integration_name,"DELTA");//"DELTA" means delta sync
            }else{
                System.out.println("No jobs found in the " + integration_name + " sample point.");
            }
        }
        System.out.println("Done!");
    }

    //activate one inactive sample point
    private static boolean activeIntegrationPoint(String token, String rootURL, String integrationPointName)throws IOException, JSONException, NoSuchAlgorithmException, KeyManagementException, InterruptedException{

        String result = IntegrationCommonConnectionUtils.activateOrDeactivateIntegrationPoint(token, rootURL, integrationPointName, true);
        boolean tag = false;
        if(result != null && result.indexOf("200") != -1){
        // waiting for the activating process done
            tag = waitingProcessEnd(15, token, rootURL, integrationPointName, null, 0);
        }
        if (!tag){
            System.out.println("Failed to activate the " + integrationPointName + " sample point.");
        }
        return tag;
    }

    // sync all jobs in one sample point
    /* there are four values for syncType: PUSH_FULL / PUSH_DELTA / POPULATION_FULL / POPULATION_DELTA*/
    private static void syncAllJobs(List<Map<String,String>> jobList, String token, String rootURL, String integrationPointName, String syncType)throws InterruptedException, JSONException, NoSuchAlgorithmException, KeyManagementException, IOException{
        for (Map<String,String> map: jobList) {
            String jobCategory = map.get("category");

            // sync population jobs
            if("POPULATION".equals(jobCategory)){
                String jobName = map.get("name");

                //sync the job
                String operationType = "POPULATION_" + syncType;
                System.out.println("full population job syncing...");
                String result = IntegrationCommonConnectionUtils.syncJob(token, rootURL, integrationPointName, jobName, operationType);
                System.out.println("the request of population " + syncType + " sync is " + result);

                //waiting for the syncing process done
                if(!waitingProcessEnd(15, token, rootURL, integrationPointName, jobName, 1)){
                    System.out.println("Can not get the sync job status! The job name is (" + jobName + ") and sample name is (" + integrationPointName + ")");
                    continue;
                }
            }else if("PUSH".equals(jobCategory)){//sync push job
                String jobName = map.get("name");

                //sync the job
                String operationType = "PUSH_"+ syncType;
                System.out.println("full push job syncing...");
                String result = IntegrationCommonConnectionUtils.syncJob(token, rootURL, integrationPointName, jobName, operationType);
                System.out.println("the result of push " + syncType + " sync is " + result);

                //waiting for the syncing process done
                if(!waitingProcessEnd(15, token, rootURL, integrationPointName, jobName, 2)){
                    System.out.println("Can not get the sync job status! The job name is (" + jobName + ") and sample name is (" + integrationPointName + ")");
                    continue;
                }

            }
        }
    }

    //waiting for the activating process or syncing process (interval in seconds)
    private static boolean waitingProcessEnd(int intervalSeconds, String token, String rootURL, String integrationPointName, String jobName, int jobCategory)throws JSONException,IOException{
        boolean loop = false;
        int count = 0;
        while (!loop){
            try{
                Thread.sleep(intervalSeconds*1000);
                count++;
                String printInfo = jobName == null ? "sample point name is " + integrationPointName : "sample point name is " + integrationPointName +" and job name is " + jobName;
                System.out.println("Checking status. Attempt " + count + " ..." + "     (" + printInfo + ")");

                String status = jobName == null ? getIntegrationPointActiveStatus(token, rootURL, integrationPointName) : getJobStatus(token, rootURL, integrationPointName, jobName, jobCategory);
                System.out.println("The status is " + status);
                if(status == null){
                    System.out.println("Check the server connection!");
                    break;
                }

                if( status.indexOf("SUCCESS") != -1 || status.indexOf("SUCESS") != -1){
                    System.out.println("Checking over with successful!");
                    System.out.println();
                    loop = true;
                    break;
                }
                if(count >= 60){
                    System.out.println("Checking status failed. You have exceeded the maximum attempts allowed.");
                    break;
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        return loop;
    }

    //get the sample point activation status
    private static String getIntegrationPointActiveStatus(String token, String rootURL, String integrationPointName)throws IOException, JSONException{
        //get the special sample point
        String integrationPointDetail = IntegrationCommonConnectionUtils.getSingleIntegrationPoint(token,rootURL,integrationPointName);

        //get special job in one sample point
        JSONObject integrationJson = new JSONObject(integrationPointDetail);
        String result = integrationJson.getBoolean("enabled") ? "SUCCESS" : "DISABLE";
        return result;
    }


    //get the running job's status
    private static String getJobStatus(String token, String rootURL, String integrationPointName, String jobName, int jobCategory)throws IOException, JSONException{
        //get the specific sample point
        String integrationPointDetail = IntegrationCommonConnectionUtils.getSingleIntegrationPoint(token, rootURL, integrationPointName);

        if(integrationPointDetail != null){
            String status = null;
            if(jobCategory == 1)
                status = getPopulationJobStatus(new JSONObject(integrationPointDetail), jobName);//get statuses of population jobs
            if(jobCategory == 2)
                status = getPushJobStatus(new JSONObject(integrationPointDetail), jobName);//get statuses of push jobs
            if(status == null){
                System.out.println("Failed to get statuses of jobs!");
                return null;
            }
            return status;
        }
        return null;
    }

    //get statuses of population jobs
    private static String getPopulationJobStatus(JSONObject integrationDetail, String jobName)throws JSONException{
        if(integrationDetail != null && integrationDetail.has("dataPopulationJobs")){
            JSONArray populationJobList = integrationDetail.getJSONArray("dataPopulationJobs");
            for(int i = 0; i < populationJobList.length(); i++){
                JSONObject tmp = populationJobList.getJSONObject(i);
                if(tmp.getString("displayID").equals(jobName));
                JSONObject statusJson = tmp.getJSONObject("jobStatistics");
                return statusJson.getString("jobStatus");
            }
        }
        return null;
    }

    //get statuses of push jobs
    private static String getPushJobStatus(JSONObject integrationDetail, String jobName)throws JSONException{
        if(integrationDetail != null && integrationDetail.has("dataPushJobs")){
            JSONArray pushJobList = integrationDetail.getJSONArray("dataPushJobs");
            for (int i = 0; i < pushJobList.length(); i++) {
                JSONObject tmp = pushJobList.getJSONObject(i);
                if(tmp.getString("displayID").equals(jobName)){
                    String status = "UNKNOWN";
                    boolean enabled = integrationDetail.getBoolean("enabled");
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
                    return status;
                }
            }
        }
        return null;
    }

    private static List<String> getAllInactiveIntegrationPoints(JSONObject allIntegrationPoints) throws JSONException{
        List<String> list = new ArrayList<>();
        if (allIntegrationPoints != null) {
            Iterator iter = allIntegrationPoints.keys();
            while (iter.hasNext()) {
                String integrationPoint = (String)iter.next();
                if(!"HistoryDataSource".equals(integrationPoint) && !"UCMDBDiscovery".equals(integrationPoint)
                        && !allIntegrationPoints.getJSONObject(integrationPoint).getBoolean("enabled")){
                    list.add(integrationPoint);
                }
            }
        }
        return list;
    }

    private static List<Map<String,String>> getAllJobs(JSONObject allIntegrationPoints, String integrationPointName)throws JSONException{
        List<Map<String,String>> jobList = new ArrayList<>();
        JSONObject singleIntegration = allIntegrationPoints.getJSONObject(integrationPointName);
        if(singleIntegration == null) return null;

        //add population jobs
        JSONArray populationJobArray = singleIntegration.getJSONArray("dataPopulationJobs");
        for(int i = 0; i < populationJobArray.length(); i++){
            Map<String, String> tmp = new HashMap<>();
            String jobName = populationJobArray.getJSONObject(i).getString("displayID");
            tmp.put("name",jobName);
            tmp.put("category","POPULATION");
            jobList.add(tmp);
        }

        //add push jobs
        JSONArray pushJobArray = singleIntegration.getJSONArray("dataPushJobs");
        for(int i = 0; i < pushJobArray.length(); i++){
            Map<String, String> tmp = new HashMap<>();
            String jobName = pushJobArray.getJSONObject(i).getString("displayID");
            tmp.put("name",jobName);
            tmp.put("category","PUSH");
            jobList.add(tmp);
        }

        return jobList;
    }



}
