package com.hp.ucmdb.rest.sample.integration;
import com.hp.ucmdb.rest.sample.utils.RestApiConnectionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/*
    This scenario is to run full sync and then delta sync of all jobs under inactive sample points.
 */
public class JobSyncScenarioSample {
    //the parameters you need to provide are serverIp, userName and password
    public static void main(String[] args) throws Exception {

        //get token for authentication
        String serverIp = "127.0.0.1";//**********input************
        String userName = "";//************input**********
        String password = "";//************input**********
        String token = RestApiConnectionUtils.loginServer(serverIp, userName, password);//login server
        if(token == null || token.length() == 0){
            System.out.println("Can not log in to the UCMDB server. Check your serverIp, userName or password!");
            return;
        }
        System.out.println(token);

        //get details of all sample points
        String getResult = IntegrationCommonConnectionUtils.getAllIntegrationPoints(token, serverIp);
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
            if(!activeIntegrationPoint(token, serverIp, integration_name)){
                System.out.println("Failed to activate the " + integration_name + " sample point.");
                inactiveList.remove(integration_name);// remove the sample point if activation failed
                continue;
            }

            //get all the job in this sample point
            List<Map<String,String>> jobList = getAllJobs(allIntegrationPoints, integration_name);

            if(jobList.size() > 0){
                syncAllJobs(jobList,token,serverIp,integration_name,"FULL");//"FULL" means full sync
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
                syncAllJobs(jobList,token,serverIp,integration_name,"DELTA");//"DELTA" means delta sync
            }else{
                System.out.println("No jobs found in the " + integration_name + " sample point.");
            }
        }
        System.out.println("Done!");
    }

    //activate one inactive sample point
    private static boolean activeIntegrationPoint(String token, String serverIP, String integrationPoint_name)throws IOException, JSONException, NoSuchAlgorithmException, KeyManagementException, InterruptedException{

        String result = IntegrationCommonConnectionUtils.activateOrDeactivateIntegrationPoint(token, serverIP, integrationPoint_name, true);
        boolean tag = false;
        if(result != null && result.indexOf("200") != -1){
        // waiting for the activating process done
            tag = waitingProcessEnd(3, token, serverIP, integrationPoint_name, null, 0);
        }
        if (!tag){
            System.out.println("Failed to activate the " + integrationPoint_name + " sample point.");
        }
        return tag;
    }

    // sync all jobs in one sample point
    /* there are four values for sync_type: PUSH_FULL / PUSH_DELTA / POPULATION_FULL / POPULATION_DELTA*/
    private static void syncAllJobs(List<Map<String,String>> jobList, String token, String serverIP, String integrationPoint_name, String sync_type)throws InterruptedException, JSONException, NoSuchAlgorithmException, KeyManagementException, IOException{
        for (Map<String,String> map: jobList) {
            String jobCategory = map.get("category");

            // sync population jobs
            if("POPULATION".equals(jobCategory)){
                String job_name = map.get("name");

                //sync the job
                String operation_type = "POPULATION_" + sync_type;
                System.out.println("full population job syncing...");
                String result = IntegrationCommonConnectionUtils.syncJob(token, serverIP, integrationPoint_name, job_name, operation_type);
                System.out.println("the request of population " + sync_type + " sync is " + result);

                //waiting for the syncing process done
                if(!waitingProcessEnd(3, token, serverIP, integrationPoint_name, job_name, 1)){
                    System.out.println("Can not get the sync job status! The job name is (" + job_name + ") and sample name is (" + integrationPoint_name + ")");
                    continue;
                }
            }else if("PUSH".equals(jobCategory)){//sync push job
                String job_name = map.get("name");

                //sync the job
                String operation_type = "PUSH_"+ sync_type;
                System.out.println("full push job syncing...");
                String result = IntegrationCommonConnectionUtils.syncJob(token, serverIP, integrationPoint_name, job_name, operation_type);
                System.out.println("the result of push " + sync_type + " sync is " + result);

                //waiting for the syncing process done
                if(!waitingProcessEnd(3, token, serverIP, integrationPoint_name, job_name, 1)){
                    System.out.println("Can not get the sync job status! The job name is (" + job_name + ") and sample name is (" + integrationPoint_name + ")");
                    continue;
                }

            }
        }
    }

    //waiting for the activating process or syncing process (interval in seconds)
    private static boolean waitingProcessEnd(int intervalSeconds, String token, String serverIP, String integrationPoint_name, String job_name, int job_category)throws JSONException,IOException{
        boolean loop = false;
        int count = 0;
        while (!loop){
            try{
                Thread.sleep(intervalSeconds*1000);
                count++;
                String printInfo = job_name == null ? "sample point name is " + integrationPoint_name : "sample point name is " + integrationPoint_name +" and job name is " + job_name;
                System.out.println("Checking status. Attempt " + count + " ..." + "     (" + printInfo + ")");

                String status = job_name == null ? getIntegrationPointActiveStatus(token, serverIP, integrationPoint_name) : getJobStatus(token, serverIP, integrationPoint_name, job_name, job_category);
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
        //get the specific sample point
        String integrationPointDetail = IntegrationCommonConnectionUtils.getSingleIntegrationPoint(token, serverIP, integrationPoint_name);

        if(integrationPointDetail != null){
            String status = null;
            if(job_category == 1)
                status = getPopulationJobStatus(new JSONObject(integrationPointDetail), job_name);//get statuses of population jobs
            if(job_category == 2)
                status = getPushJobStatus(new JSONObject(integrationPointDetail), job_name);//get statuses of push jobs
            if(status == null){
                System.out.println("Failed to get statuses of jobs!");
                return null;
            }
            return status;
        }
        return null;
    }

    //get statuses of population jobs
    private static String getPopulationJobStatus(JSONObject integrationDetail, String job_name)throws JSONException{
        if(integrationDetail != null && integrationDetail.has("dataPopulationJobs")){
            JSONArray populationJobList = integrationDetail.getJSONArray("dataPopulationJobs");
            for(int i = 0; i < populationJobList.length(); i++){
                JSONObject tmp = populationJobList.getJSONObject(i);
                if(tmp.getString("displayID").equals(job_name));
                JSONObject statusJson = tmp.getJSONObject("jobStatistics");
                return statusJson.getString("jobStatus");
            }
        }
        return null;
    }

    //get statuses of push jobs
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

    private static List<Map<String,String>> getAllJobs(JSONObject allIntegrationPoints, String integrationPoint_name)throws JSONException{
        List<Map<String,String>> jobList = new ArrayList<>();
        JSONObject singleIntegration = allIntegrationPoints.getJSONObject(integrationPoint_name);
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
