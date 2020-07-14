package com.microfocus.ucmdb.rest.sample.integration;

import org.junit.Test;

/** 
* SimpleTroubleshootingScenarioSample Tester. 
* 
* @author <Authors name> 
* @since <pre>Jul 9, 2020</pre> 
* @version 1.0 
*/ 
public class SimpleTroubleshootingScenarioSampleTest { 

    @Test
    public void testMain() throws Exception {
        SimpleTroubleshootingScenarioSample.main(new String[]{"${UCMDB_IP_ADDRESS}", "${UCMDB_PORT}", "${UCMDB_USERNAME}", "${UCMDB_PASSWORD}", "${INTEGRATION_POINT}", "${JOB_NAME}", "${JOB_CATEGORY}"});
    }

} 
