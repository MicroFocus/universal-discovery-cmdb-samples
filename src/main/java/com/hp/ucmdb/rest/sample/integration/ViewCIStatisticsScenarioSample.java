package com.hp.ucmdb.rest.sample.integration;
import com.hp.ucmdb.rest.sample.utils.RestApiConnectionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
      This scenario is to view the CI statistics for a specific job in a specific sample point.
 */
public class ViewCIStatisticsScenarioSample {
    //the parameters you need to provide are: serverIp, userName, password, integrationPoint_name, job_name and job_category
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

        String integrationPoint_name = "test_sun";//***********input***********

        //get a specific sample point
        String integrationPointDetail = IntegrationCommonConnectionUtils.getSingleIntegrationPoint(token,serverIp,integrationPoint_name);
        System.out.println( "the sample point (" + integrationPoint_name + ") detail is: " + integrationPointDetail);


        String job_name = "pop1";//***********input***********
        int job_category = 1;//**********input************ (1 is 'POPULATION' / 2 is 'PUSH')

        //get a specific job in the sample point
        JSONObject integrationJson = new JSONObject(integrationPointDetail);
        String jobDetail = getSingleJob(integrationJson, job_name, job_category);
        if(jobDetail == null){
            System.out.println("Error getting the job details!");
            return;
        }
        System.out.println("the job(" + job_name + ") detail  is " + jobDetail);

        //get ci statistics
        String jobStatisticJson = getJobStatistics(new JSONObject(jobDetail), job_category);
        System.out.println("The CI Statistics is " + jobStatisticJson);
        System.out.println("Done!");

    }

    // get details of a single job
    public static String getSingleJob(JSONObject integrationPointDetail, String job_name, int job_category) throws JSONException{
        if(!integrationPointDetail.has("dataPopulationJobs") || !integrationPointDetail.has("dataPushJobs")){
            return null;
        }
        if(job_category == 1){//get population jobs
            JSONArray populationJobList = integrationPointDetail.getJSONArray("dataPopulationJobs");
            for(int i = 0; i < populationJobList.length(); i++){
                JSONObject tmp_population = populationJobList.getJSONObject(i);
                if(job_name.equals(tmp_population.getString("displayID"))){
                    return tmp_population.toString();
                }
            }
        }else if(job_category == 2){//get push jobs
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

    // get CI statistics for the job
    private static String getJobStatistics(JSONObject jobDetail, int job_category)throws JSONException{
        if(jobDetail != null){
            if(job_category == 1){
                JSONObject statJson = jobDetail.getJSONObject("jobStatistics");
                JSONArray result = null;
                try{
                    result = statJson.getJSONArray("citStats");
                }catch (Throwable e){
                    e.printStackTrace();
                }
                return result != null ? result.toString() : null;
            }
            if(job_category == 2){
                JSONObject jobRunHistory = jobDetail.getJSONArray("jobRunHistory").getJSONObject(0);
                JSONArray statArray = jobRunHistory.getJSONArray("queryRunHistory");
                JSONObject result = new JSONObject();
                for(int i = 0; i < statArray.length(); i++){
                    JSONObject pushStaJson = statArray.getJSONObject(i);
                    result.put("queryName",pushStaJson.getString("queryName"));
                    result.put("addedCIs",pushStaJson.getInt("addedCIs"));
                    result.put("updatedCIs",pushStaJson.getInt("updatedCIs"));
                    result.put("failedCIs",pushStaJson.getInt("removeFailures") + pushStaJson.getInt("updateFailures") + pushStaJson.getInt("addFailures"));
                    result.put("removedCIs",pushStaJson.getInt("removedCIs"));
                }
                return result.toString();
            }
        }
        return "No result";
    }
}
