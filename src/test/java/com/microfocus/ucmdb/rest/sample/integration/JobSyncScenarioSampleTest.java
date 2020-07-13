package com.microfocus.ucmdb.rest.sample.integration;

import org.junit.Test;

/** 
* JobSyncScenarioSample Tester. 
* 
* @author <Authors name> 
* @since <pre>Jul 9, 2020</pre> 
* @version 1.0 
*/ 
public class JobSyncScenarioSampleTest { 

    @Test
    public void testMain() throws Exception {
        JobSyncScenarioSample.main(new String[]{"127.0.0.1","8443","admin","admin"});
    } 

} 
