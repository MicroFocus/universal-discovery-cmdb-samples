package com.microfocus.ucmdb.rest.sample.integration;

import org.junit.Test;

/** 
* ViewIntegrationPointsScenarioSample Tester. 
* 
* @author <Authors name> 
* @since <pre>Jul 9, 2020</pre> 
* @version 1.0 
*/ 
public class ViewIntegrationPointsScenarioSampleTest { 

    @Test
    public void testMain() throws Exception {
        ViewIntegrationPointsScenarioSample.main(new String[]{"127.0.0.1","8443","admin","admin", "integrationPoint"});
    } 


} 
