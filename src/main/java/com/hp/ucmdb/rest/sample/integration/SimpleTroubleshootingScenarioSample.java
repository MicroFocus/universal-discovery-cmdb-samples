package com.hp.ucmdb.rest.sample.integration;
import com.hp.ucmdb.rest.sample.utils.RestApiConnectionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
/*
     This scenario is to perform a simple troubleshooting when there is an sample error.
 */
public class SimpleTroubleshootingScenarioSample {
    //the parameters user need to input are (serverIp, userName, password, integrationPoint_name, job_name, job_category)
    public static void main(String[] args) throws Exception {

        //get token for authentication
        String serverIp = "127.0.0.1";//**********input************
        String userName = "";//************input**********
        String password = "";//************input**********
        String token = RestApiConnectionUtils.loginServer(serverIp, userName, password);
        if(token == null || token.length() == 0){
            System.out.println("Can not log in to the UCMDB server. Check your serverIp, userName or password!");
            return;
        }
        System.out.println(token);

        String integrationPoint_name = "sun";//***********input***********

        //test connection with an sample point /*it takes around 40s if the connection is failed*/
        String result = IntegrationCommonConnectionUtils.testConnectionWithIntegration(token, serverIp, integrationPoint_name);
        System.out.println("the result of test connection is: " + result);
        if(result != null){
            boolean isConnected = false;
            JSONObject resultJson = new JSONObject(result);
            if(resultJson!=null){//test connection successful, the result contain the key-value {"connectionStatus": true}
                if(resultJson.has("connectionStatus")) isConnected = resultJson.getBoolean("connectionStatus");
            }
            if(!isConnected){
                System.out.println("Can not connect to the sample point(" + integrationPoint_name + ")");
                return;
            }
        }

        //inactive the sample point
        String integrationPointString = IntegrationCommonConnectionUtils.getSingleIntegrationPoint(token, serverIp, integrationPoint_name);
        JSONObject integrationPoint = new JSONObject(integrationPointString);
        System.out.println("The details of the sample point are " + integrationPoint);
        if(integrationPoint != null && integrationPoint.has("enabled") && integrationPoint.getBoolean("enabled")){
            String inactive_result = IntegrationCommonConnectionUtils.activateOrDeactivateIntegrationPoint(token, serverIp, integrationPoint_name, false);
            if(inactive_result.indexOf("200") == -1){// failed to inactive
                System.out.println("Failed to inactivate the sample point");
            }
            System.out.println("the result of the inactive sample point is: " + inactive_result);

            //waiting for the inactivating process is over
            if(!waitingProcessEnd(3, token, serverIp, integrationPoint_name, null, 0, true)){
                System.out.println("Can not get the inactivation status!");
                return;
            }

            //activate the sample point
            String active_result = IntegrationCommonConnectionUtils.activateOrDeactivateIntegrationPoint(token, serverIp, integrationPoint_name, true);
            if(active_result.indexOf("200") == -1){// failed to active
                System.out.println("Failed to activate the sample point");
            }
            System.out.println("the result of active sample point is: " + active_result);

            //waiting for the activating process is over
            if(!waitingProcessEnd(3, token, serverIp, integrationPoint_name, null, 0, false)){
                System.out.println("Can not get the activation status!");
                return;
            }
        }

        String job_name = "push2";//***********input***********
        int job_category = 2;//**********input************ (1 is 'POPULATION' and 2 is 'PUSH')

        //sync job
        int sync_type = 1; //***********input*********** (1 is "FULL" and 2 is "DELTA")
        /* there are four values for sync job: PUSH_FULL / PUSH_DELTA / POPULATION_FULL / POPULATION_DELTA*/
        String operation_type = job_category == 1 ? "POPULATION" : "PUSH";
        operation_type = sync_type == 1 ? operation_type+"_"+"FULL" : operation_type+"_"+"DELTA";
        System.out.println("syncing...");//it may take several seconds
        String syncResult = IntegrationCommonConnectionUtils.syncJob(token, serverIp, integrationPoint_name, job_name, operation_type);
        System.out.println("the result of sync job is: " + syncResult);

        //waiting for the syncing process is done
        if(!waitingProcessEnd(3, token, serverIp, integrationPoint_name, job_name, job_category, false)){
            System.out.println("Can not get the sync job status!");
            return;
        }

        //check the query status
        String queryStatus = getQueryStatus(token, serverIp, integrationPoint_name, job_name, job_category);
        System.out.println("the query status is " + queryStatus);
        System.out.println("Done!");
    }

    // get details of the single job
    public static String getSingleJob(JSONObject integrationPointDetail, String job_name, int job_category) throws JSONException{
        if(job_category == 1){//population
            JSONArray populationJobList = integrationPointDetail.getJSONArray("dataPopulationJobs");
            for(int i = 0; i < populationJobList.length(); i++){
                JSONObject tmp_population = populationJobList.getJSONObject(i);
                if(job_name.equals(tmp_population.getString("displayID"))){
                    return tmp_population.toString();
                }
            }
        }else if(job_category == 2){//push
            JSONArray pushJobList = integrationPointDetail.getJSONArray("dataPushJobs");
            for(int i = 0; i < pushJobList.length(); i++){
                JSONObject tmp_push = pushJobList.getJSONObject(i);
                if(job_name.equals(tmp_push.getString("displayID"))){
                    return tmp_push.toString();
                }
            }
        }
        return null;
    }

    //waiting for the activating process or syncing process (interval in seconds)
    private static boolean waitingProcessEnd(int intervalSeconds, String token, String serverIP, String integrationPoint_name, String job_name, int job_category, boolean isInactive)throws JSONException,IOException, InterruptedException{
        boolean loop = false;
        int count = 0;
        while (!loop){
            try{
                Thread.sleep(intervalSeconds*1000);
                count++;
                String printInfo = job_name == null ? "sample name is " + integrationPoint_name : "sample name is " + integrationPoint_name +" and job name is " + job_name;
                System.out.println("Checking with times " + count + " ..." + "     (" + printInfo + ")");

                String status = job_name == null ? getIntegrationPointActiveStatus(token, serverIP, integrationPoint_name) : getJobStatus(token, serverIP, integrationPoint_name, job_name, job_category);
                System.out.println("The status is " + status);
                if(status == null){
                    System.out.println("Check the server connection!");
                    break;
                }


                if(job_name != null){//judge the sync status
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
    private static String getIntegrationPointActiveStatus(String token, String serverIP, String integrationPoint_name)throws IOException, JSONException{
        //get the special sample point
        String integrationPointDetail = IntegrationCommonConnectionUtils.getSingleIntegrationPoint(token,serverIP,integrationPoint_name);

        //get special job in one sample point
        JSONObject integrationJson = new JSONObject(integrationPointDetail);
        String result = integrationJson.getBoolean("enabled") ? "SUCCESS" : "DISABLE";
        return result;
    }


    //get the running job's status
    private static String getJobStatus(String token, String serverIP, String integrationPoint_name, String job_name, int job_category)throws IOException, JSONException{
        //get the
        String integrationPointDetail = IntegrationCommonConnectionUtils.getSingleIntegrationPoint(token, serverIP, integrationPoint_name);
        if(integrationPointDetail != null){
            String status = null;
            if(job_category == 1)
                status = getPopulationJobStatus(new JSONObject(integrationPointDetail), job_name);
            if(job_category == 2)
                status = getPushJobStatus(new JSONObject(integrationPointDetail), job_name);
            if(status == null){
                System.out.println("Failed to get the job status!");
                return null;
            }
            return status;
        }
        return null;
    }

    private static String getPopulationJobStatus(JSONObject integrationDetail, String job_name)throws JSONException{
        if(integrationDetail != null && integrationDetail.has("dataPopulationJobs")){
            JSONArray populationJobList = integrationDetail.getJSONArray("dataPopulationJobs");
            for(int i = 0; i < populationJobList.length(); i++){
                JSONObject tmp = populationJobList.getJSONObject(i);
                if(tmp.getString("displayID").equals(job_name)){
                    JSONObject statusJson = tmp.getJSONObject("jobStatistics");
                    return statusJson.getString("jobStatus");
                }
            }
        }
        return null;
    }

    private static String getPushJobStatus(JSONObject integrationDetail, String job_name)throws JSONException{
        if(integrationDetail != null && integrationDetail.has("dataPushJobs")){
            JSONArray pushJobList = integrationDetail.getJSONArray("dataPushJobs");
            for (int i = 0; i < pushJobList.length(); i++) {
                JSONObject tmp = pushJobList.getJSONObject(i);
                if(tmp.getString("displayID").equals(job_name)){
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
    private static String getQueryStatus(String token, String serverIP, String integrationPoint_name, String job_name, int job_category)throws IOException, JSONException{
        //get the sample point
        String integrationPointDetail = IntegrationCommonConnectionUtils.getSingleIntegrationPoint(token,serverIP,integrationPoint_name);
        System.out.println("the sample point detail is " + integrationPointDetail);

        //get details of a specific job in a specific sample point
        JSONObject integrationJson = new JSONObject(integrationPointDetail);
        String jobDetail = getSingleJob(integrationJson, job_name, job_category);
        if(jobDetail == null){
            System.out.println("Error getting the job!");

        }
        System.out.println("the job(" + job_name + ") detail  is " + jobDetail);
        return parseJob(new JSONObject(jobDetail), job_category);
    }

    // parse the job structure to get the query status
    private static String parseJob(JSONObject jobDetail, int job_category)throws JSONException{
        if(jobDetail != null){
            //get population jobs
            if(job_category == 1){
                return jobDetail.getJSONObject("jobStatistics").getJSONObject("queryStatus").toString();
            }
            //get push jobs
            if(job_category == 2){
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
