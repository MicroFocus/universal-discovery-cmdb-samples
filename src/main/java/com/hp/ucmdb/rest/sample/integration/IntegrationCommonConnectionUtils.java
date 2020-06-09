package com.hp.ucmdb.rest.sample.integration;

import com.hp.ucmdb.rest.sample.utils.RestApiConnectionUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IntegrationCommonConnectionUtils {

    public static String getAllIntegrationPoints(String token, String serverIP)throws IOException {
        String domainURLAndApiType="https://" + serverIP + ":8443/rest-api/";
        System.out.print("getting all sample points request : ");
        String allIntegrationPoints = RestApiConnectionUtils.doGet(domainURLAndApiType + "sample/integrationpoints", token);
        System.out.println( "all sample points details : " + allIntegrationPoints);
        return allIntegrationPoints;
    }

    public static List<String> getAllIntegrationPointNames(JSONObject allIntegrationPoints){
        List<String> allIntegrationPointNames = new ArrayList<>();
        if (allIntegrationPoints != null) {
            Iterator iter = allIntegrationPoints.keys();
            while (iter.hasNext()) {
                Object integrationPoint = iter.next();
                allIntegrationPointNames.add((String)integrationPoint);
            }
        }
        return allIntegrationPointNames;
    }

    public static String getSingleIntegrationPoint(String token, String serverIP, String integrationPoint_name)throws IOException{
        String domainURLAndApiType="https://" + serverIP + ":8443/rest-api/";
        System.out.print("getting the " + integrationPoint_name + " sample point request : ");
        return RestApiConnectionUtils.doGet(domainURLAndApiType + "sample/integrationpoints/" + integrationPoint_name + "?detail=false", token);
    }

    public static String activateOrDeactivateIntegrationPoint(String token, String serverIP, String integrationPoint_name, boolean enabled)throws IOException, JSONException, NoSuchAlgorithmException, KeyManagementException {
        String domainURLAndApiType="https://" + serverIP + ":8443/rest-api/";
        JSONObject activeJson = new JSONObject();
//        activeJson.put("name", integrationPoint_name);
//        activeJson.put("enabled", enabled);// true: means active the sample point, false: means deactive it
        String enableString = enabled ? "true" : "false";
        String activate = enabled ? "activate" : "deactivate";
        System.out.print( activate + " sample point request : ");
        return RestApiConnectionUtils.doPatch(domainURLAndApiType + "sample/integrationpoints/"+ integrationPoint_name + "?enabled=" + enableString, token, activeJson.toString());
    }

    public static String syncJob(String token, String serverIP, String integrationPoint_name, String job_name, String operation_type)throws IOException,JSONException,NoSuchAlgorithmException,KeyManagementException{
        String domainURLAndApiType="https://" + serverIP + ":8443/rest-api/";

        String job_id = integrationPoint_name + "_" + job_name;
        JSONObject syncJson = new JSONObject();
        syncJson.put("operationType",operation_type);//there are four values for the operation_type parameter: PUSH_FULL / PUSH_DELTA / POPULATION_FULL / POPULATION_DELTA */
        System.out.print("sync job request : ");
        return RestApiConnectionUtils.doPatch(domainURLAndApiType + "sample/integrationpoints/" + integrationPoint_name + "/jobs/" + job_name + "?operationtype=" + operation_type, token, syncJson.toString());
    }

    public static String testConnectionWithIntegration(String token, String serverIP, String integrationPoint_name)throws IOException{
        String domainURLAndApiType="https://" + serverIP + ":8443/rest-api/";
        System.out.print("test connection with sample point request : ");
        return RestApiConnectionUtils.doGet(domainURLAndApiType + "sample/integrationpoints/" + integrationPoint_name + "/connectionstatus", token);
    }

}
