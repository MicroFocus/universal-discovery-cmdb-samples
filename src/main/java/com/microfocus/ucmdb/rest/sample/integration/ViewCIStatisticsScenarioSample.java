package com.microfocus.ucmdb.rest.sample.integration;
import com.microfocus.ucmdb.rest.sample.utils.RestApiConnectionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.util.Scanner;

/*
      This scenario is to view the CI statistics for a specific job in a specific sample point.
 */
public class ViewCIStatisticsScenarioSample {
    //the parameters you need to provide are: serverIp, userName, password, integrationPointName, jobName and jobCategory
    public static void main(String[] args) throws Exception {
        String hostname;
        String port;
        String username;
        String password;
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
            password = new String(console.readPassword("Please enter password for UCMDB: "));
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
            password = args[3];
            integrationPointName = args[4];
            jobName = args[5];
            jobCategoryName = args[6];
        }

        String rootURL = RestApiConnectionUtils.buildRootUrl(hostname, port,false);

        // authenticate
        String token = RestApiConnectionUtils.loginServer(rootURL, username, password);

        //get a specific sample point
        String integrationPointDetail = IntegrationCommonConnectionUtils.getSingleIntegrationPoint(token,rootURL,integrationPointName);
        System.out.println( "the sample point (" + integrationPointName + ") detail is: " + integrationPointDetail);

        //(1 is 'POPULATION' and 2 is 'PUSH')
        int jobCategory = 2;
        if ("POPULATION".equals(jobCategoryName)) {
            jobCategory = 1;
        }

        //get a specific job in the sample point
        JSONObject integrationJson = new JSONObject(integrationPointDetail);
        String jobDetail = getSingleJob(integrationJson, jobName, jobCategory);
        if(jobDetail == null){
            System.out.println("Error getting the job details!");
            return;
        }
        System.out.println("the job(" + jobName + ") detail  is " + jobDetail);

        //get ci statistics
        String jobStatisticJson = getJobStatistics(new JSONObject(jobDetail), jobCategory);
        System.out.println("The CI Statistics is " + jobStatisticJson);
        System.out.println("Done!");

    }

    // get details of a single job
    public static String getSingleJob(JSONObject integrationPointDetail, String jobName, int jobCategory) throws JSONException{
        if(!integrationPointDetail.has("dataPopulationJobs") || !integrationPointDetail.has("dataPushJobs")){
            return null;
        }
        if(jobCategory == 1){//get population jobs
            JSONArray populationJobList = integrationPointDetail.getJSONArray("dataPopulationJobs");
            for(int i = 0; i < populationJobList.length(); i++){
                JSONObject tmp_population = populationJobList.getJSONObject(i);
                if(jobName.equals(tmp_population.getString("displayID"))){
                    return tmp_population.toString();
                }
            }
        }else if(jobCategory == 2){//get push jobs
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

    // get CI statistics for the job
    private static String getJobStatistics(JSONObject jobDetail, int jobCategory)throws JSONException{
        if(jobDetail != null){
            if(jobCategory == 1){
                JSONObject statJson = jobDetail.getJSONObject("jobStatistics");
                Object result = null;
                try{
                    result = statJson.get("citStats");
                }catch (Throwable e){
                    e.printStackTrace();
                }
                return result != null ? result.toString() : null;
            }
            if(jobCategory == 2){
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
