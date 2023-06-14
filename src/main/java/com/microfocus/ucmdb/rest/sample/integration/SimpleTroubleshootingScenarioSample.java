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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

public class SimpleTroubleshootingScenarioSample {
    //the parameters user need to input are (serverIp, userName, password, integrationPointName, jobName, jobCategory)
    public static void main(String[] args) throws Exception {
        String hostname;
        String port;
        String username;
        char[] password;
        String integrationPointName;
        String jobName;
        String jobCategoryName;

        if (args.length < 7) {
            Scanner sc = new Scanner(System.in);
            System.out.print("Please enter hostname/IP of UCMDB Server: ");
            hostname = sc.hasNextLine() ? sc.nextLine() : "";
            System.out.print("Please enter port of UCMDB Server: ");
            port = sc.hasNext() ? sc.next() : "";
            System.out.print("Please enter username for UCMDB: ");
            username = sc.hasNext() ? sc.next() : "";
            Console console = System.console();
            password = console.readPassword("Please enter password for UCMDB: ");
            System.out.print("Please enter integration point name: ");
            integrationPointName = sc.hasNext() ? sc.next() : "";
            System.out.print("Please enter job name: ");
            jobName = sc.hasNext() ? sc.next() : "";
            System.out.print("Please enter job category name(POPULATION/PUSH): ");
            jobCategoryName = sc.hasNext() ? sc.next() : "";
        } else {
            hostname = args[0];
            port = args[1];
            username = args[2];
            password = args[3].toCharArray();
            integrationPointName = args[4];
            jobName = args[5];
            jobCategoryName = args[6];
        }

        String rootURL = RestApiConnectionUtils.buildRootUrl(hostname, port,false);

        // authenticate
        String token = RestApiConnectionUtils.loginServer(rootURL, username, password);

        //test connection with an sample point /*it takes around 40s if the connection is failed*/
        String result = IntegrationCommonConnectionUtils.testConnectionWithIntegration(token, rootURL, integrationPointName);

        if(result != null){
            boolean isConnected = false;
            JSONObject resultJson = new JSONObject(result);
            //test connection successful, the result contain the key-value {"connectionStatus": true}
            if(resultJson!=null){
                if(resultJson.has("connectionStatus")) isConnected = resultJson.getBoolean("connectionStatus");
            }
            if(!isConnected){
                System.out.println("Can not connect to the sample point(" + integrationPointName + ")");
                return;
            }
        }

        //inactive the sample point
        String integrationPointString = IntegrationCommonConnectionUtils.getSingleIntegrationPoint(token, rootURL, integrationPointName);
        JSONObject integrationPoint = new JSONObject(integrationPointString);
        System.out.println("The details of the sample point are " + integrationPoint);
        if(integrationPoint != null && integrationPoint.has("enabled") && integrationPoint.getBoolean("enabled")){
            String inactiveResult = IntegrationCommonConnectionUtils.activateOrDeactivateIntegrationPoint(token, rootURL, integrationPointName, false);
            if(inactiveResult.indexOf("200") == -1){
                System.out.println("Failed to inactivate the sample point");
            }
            System.out.println("the result of the inactive sample point is: " + inactiveResult);

            //waiting for the inactivating process is over
            if(!waitingProcessEnd(15, token, rootURL, integrationPointName, null, 0, true)){
                System.out.println("Can not get the inactivation status!");
                return;
            }

            //activate the sample point
            String activeResult = IntegrationCommonConnectionUtils.activateOrDeactivateIntegrationPoint(token, rootURL, integrationPointName, true);
            if(activeResult.indexOf("200") == -1){// failed to active
                System.out.println("Failed to activate the sample point");
            }
            System.out.println("the result of active sample point is: " + activeResult);

            //waiting for the activating process is over
            if(!waitingProcessEnd(15, token, rootURL, integrationPointName, null, 0, false)){
                System.out.println("Can not get the activation status!");
                return;
            }
        }


        //(1 is 'POPULATION' and 2 is 'PUSH')
        int jobCategory = 2;
        if ("POPULATION".equals(jobCategoryName)) {
            jobCategory = 1;
        }

        //(1 is "FULL" and 2 is "DELTA")
        int syncType = 1;
        /* there are four values for sync job: PUSH_FULL / PUSH_DELTA / POPULATION_FULL / POPULATION_DELTA*/
        String operationType = jobCategory == 1 ? "POPULATION" : "PUSH";
        operationType = syncType == 1 ? operationType+"_"+"FULL" : operationType+"_"+"DELTA";
        System.out.println("Syncing...");
        String syncResult = IntegrationCommonConnectionUtils.syncJob(token, rootURL, integrationPointName, jobName, operationType);
        System.out.println("The result of sync job is: " + syncResult);

        //waiting for the syncing process is done
        if(!waitingProcessEnd(15, token, rootURL, integrationPointName, jobName, jobCategory, false)){
            System.out.println("Can not get the sync job status!");
            return;
        }

        //check the query status
        String queryStatus = getQueryStatus(token, rootURL, integrationPointName, jobName, jobCategory);
        System.out.println("The query status is " + queryStatus);
        System.out.println("Done!");
    }

    // get details of the single job
    public static String getSingleJob(JSONObject integrationPointDetail, String jobName, int jobCategory) throws JSONException{
        if(jobCategory == 1){//population
            JSONArray populationJobList = integrationPointDetail.getJSONArray("dataPopulationJobs");
            for(int i = 0; i < populationJobList.length(); i++){
                JSONObject tmp_population = populationJobList.getJSONObject(i);
                if(jobName.equals(tmp_population.getString("displayID"))){
                    return tmp_population.toString();
                }
            }
        }else if(jobCategory == 2){//push
            JSONArray pushJobList = integrationPointDetail.getJSONArray("dataPushJobs");
            for(int i = 0; i < pushJobList.length(); i++){
                JSONObject tmp_push = pushJobList.getJSONObject(i);
                if(jobName.equals(tmp_push.getString("displayID"))){
                    return tmp_push.toString();
                }
            }
        }
        return null;
    }

    //waiting for the activating process or syncing process (interval in seconds)
    private static boolean waitingProcessEnd(int intervalSeconds, String token, String rootURL, String integrationPointName, String jobName, int jobCategory, boolean isInactive)throws JSONException,IOException, InterruptedException{
        boolean loop = false;
        int count = 0;
        while (!loop){
            try{
                Thread.sleep(intervalSeconds*1000);
                count++;
                String printInfo = jobName == null ? "sample name is " + integrationPointName : "sample name is " + integrationPointName +" and job name is " + jobName;
                System.out.println("Checking with times " + count + " ..." + "     (" + printInfo + ")");

                String status = jobName == null ? getIntegrationPointActiveStatus(token, rootURL, integrationPointName) : getJobStatus(token, rootURL, integrationPointName, jobName, jobCategory);
                System.out.println("The status is " + status);
                if(status == null){
                    System.out.println("Check the server connection!");
                    break;
                }


                if(jobName != null){//judge the sync status
                    if( status.indexOf("SUCCESS") != -1 || status.indexOf("SUCESS") != -1 ){
                        System.out.println("Checking over with successful!");
                        loop = true;
                        break;
                    }
                }else{//judge the active or inactive sample point status
                    if(isInactive && "DISABLE".equals(status)){
                        System.out.println("Checking over with successful!");
                        loop = true;
                        break;
                    }
                    if(!isInactive && "SUCCESS".equals(status)){
                        System.out.println("Checking over with successful!");
                        loop = true;
                        break;
                    }
                }

                if(count >= 20){
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
        //get the
        String integrationPointDetail = IntegrationCommonConnectionUtils.getSingleIntegrationPoint(token, rootURL, integrationPointName);
        if(integrationPointDetail != null){
            String status = null;
            if(jobCategory == 1)
                status = getPopulationJobStatus(new JSONObject(integrationPointDetail), jobName);
            if(jobCategory == 2)
                status = getPushJobStatus(new JSONObject(integrationPointDetail), jobName);
            if(status == null){
                System.out.println("Failed to get the job status!");
                return null;
            }
            return status;
        }
        return null;
    }

    private static String getPopulationJobStatus(JSONObject integrationDetail, String jobName)throws JSONException{
        if(integrationDetail != null && integrationDetail.has("dataPopulationJobs")){
            JSONArray populationJobList = integrationDetail.getJSONArray("dataPopulationJobs");
            for(int i = 0; i < populationJobList.length(); i++){
                JSONObject tmp = populationJobList.getJSONObject(i);
                if(tmp.getString("displayID").equals(jobName)){
                    JSONObject statusJson = tmp.getJSONObject("jobStatistics");
                    return statusJson.getString("jobStatus");
                }
            }
        }
        return null;
    }

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

    //get the result of query status
    private static String getQueryStatus(String token, String rootURL, String integrationPointName, String jobName, int jobCategory)throws IOException, JSONException{
        //get the sample point
        String integrationPointDetail = IntegrationCommonConnectionUtils.getSingleIntegrationPoint(token,rootURL,integrationPointName);
        System.out.println("the sample point detail is " + integrationPointDetail);

        //get details of a specific job in a specific sample point
        JSONObject integrationJson = new JSONObject(integrationPointDetail);
        String jobDetail = getSingleJob(integrationJson, jobName, jobCategory);
        if(jobDetail == null){
            System.out.println("Error getting the job!");

        }
        System.out.println("the job(" + jobName + ") detail  is " + jobDetail);
        return parseJob(new JSONObject(jobDetail), jobCategory);
    }

    // parse the job structure to get the query status
    private static String parseJob(JSONObject jobDetail, int jobCategory)throws JSONException{
        if(jobDetail != null){
            //get population jobs
            if(jobCategory == 1){
                return jobDetail.getJSONObject("jobStatistics").getJSONObject("queryStatus").toString();
            }
            //get push jobs
            if(jobCategory == 2){
                JSONObject jobRunHistory = jobDetail.getJSONArray("jobRunHistory").getJSONObject(0);
                JSONArray queryArray = jobRunHistory.getJSONArray("queryRunHistory");
                List<JSONObject> queryResult = new ArrayList<>();
                for(int i = 0; i < queryArray.length(); i++){
                    JSONObject result = new JSONObject();
                    JSONObject pushStaJson = queryArray.getJSONObject(i);
                    SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date startDate = new Date();
                    startDate.setTime(pushStaJson.getLong("startTime"));
                    Date stopDate = new Date();
                    stopDate.setTime(pushStaJson.getLong("stopTime"));

                    result.put("queryName",pushStaJson.getString("queryName"));
                    result.put("startTime",format.format(startDate.getTime()));
                    result.put("stopTime",format.format(stopDate.getTime()));
                    result.put("queryPushStatus",pushStaJson.getString("queryPushStatus"));

                    queryResult.add(result);
                }
                return queryResult.toString();
            }
        }
        return "No result";
    }
}
