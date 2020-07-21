/**
 * Copyright 2020 EntIT Software LLC, a Micro Focus company, L.P.
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
        String allIntegrationPoints = RestApiConnectionUtils.doGet(rootURL + "integration/integrationpoints", token, "Getting all sample points request.");
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
        return RestApiConnectionUtils.doGet(rootURL + "integration/integrationpoints/" + integrationPoint_name + "?detail=false",
                token, "Getting the " + integrationPoint_name + " sample point request.");
    }

    public static String activateOrDeactivateIntegrationPoint(String token, String rootURL, String integrationPoint_name, boolean enabled)throws IOException, JSONException, NoSuchAlgorithmException, KeyManagementException {
        String enableString = enabled ? "true" : "false";
        String activate = enabled ? "activate" : "deactivate";
        return RestApiConnectionUtils.doPatch(rootURL + "integration/integrationpoints/"+ integrationPoint_name + "?enabled=" + enableString,
                token, null, activate + " sample point request.");
    }

    public static String syncJob(String token, String rootURL, String integrationPoint_name, String job_name, String operation_type)throws IOException,JSONException,NoSuchAlgorithmException,KeyManagementException{
        JSONObject syncJson = new JSONObject();
        //there are four values for the operation_type parameter: PUSH_FULL / PUSH_DELTA / POPULATION_FULL / POPULATION_DELTA */
        syncJson.put("operationType",operation_type);
        return RestApiConnectionUtils.doPatch(rootURL + "integration/integrationpoints/" + integrationPoint_name + "/jobs/" + job_name + "?operationtype=" + operation_type,
                token, null, "Sync job request.");
    }

    public static String testConnectionWithIntegration(String token, String rootURL, String integrationPoint_name)throws IOException{
        return RestApiConnectionUtils.doGet(rootURL + "integration/integrationpoints/" + integrationPoint_name + "/connectionstatus",
                token, "Test connection with sample point request.");
    }

}
