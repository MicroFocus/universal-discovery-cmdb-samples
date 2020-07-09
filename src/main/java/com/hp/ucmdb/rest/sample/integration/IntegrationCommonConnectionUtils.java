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

    public static String getAllIntegrationPoints(String token, String rootURL)throws IOException {
        System.out.println("Getting all sample points request.");
        String allIntegrationPoints = RestApiConnectionUtils.doGet(rootURL + "integration/integrationpoints", token);
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

    public static String getSingleIntegrationPoint(String token, String rootURL, String integrationPoint_name)throws IOException{
        System.out.println("Getting the " + integrationPoint_name + " sample point request.");
        return RestApiConnectionUtils.doGet(rootURL + "integration/integrationpoints/" + integrationPoint_name + "?detail=false", token);
    }

    public static String activateOrDeactivateIntegrationPoint(String token, String rootURL, String integrationPoint_name, boolean enabled)throws IOException, JSONException, NoSuchAlgorithmException, KeyManagementException {
        String enableString = enabled ? "true" : "false";
        String activate = enabled ? "activate" : "deactivate";
        System.out.println( activate + " sample point request.");
        return RestApiConnectionUtils.doPatch(rootURL + "integration/integrationpoints/"+ integrationPoint_name + "?enabled=" + enableString, token, null);
    }

    public static String syncJob(String token, String rootURL, String integrationPoint_name, String job_name, String operation_type)throws IOException,JSONException,NoSuchAlgorithmException,KeyManagementException{
        JSONObject syncJson = new JSONObject();
        //there are four values for the operation_type parameter: PUSH_FULL / PUSH_DELTA / POPULATION_FULL / POPULATION_DELTA */
        syncJson.put("operationType",operation_type);
        System.out.println("Sync job request.");
        return RestApiConnectionUtils.doPatch(rootURL + "integration/integrationpoints/" + integrationPoint_name + "/jobs/" + job_name + "?operationtype=" + operation_type, token, null);
    }

    public static String testConnectionWithIntegration(String token, String rootURL, String integrationPoint_name)throws IOException{
        System.out.println("Test connection with sample point request.");
        return RestApiConnectionUtils.doGet(rootURL + "integration/integrationpoints/" + integrationPoint_name + "/connectionstatus", token);
    }

}
