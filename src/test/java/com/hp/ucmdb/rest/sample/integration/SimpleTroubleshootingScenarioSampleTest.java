package com.hp.ucmdb.rest.sample.integration; 

import org.junit.Test; 
import org.junit.Before; 
import org.junit.After; 

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
        SimpleTroubleshootingScenarioSample.main(new String[]{"16.187.188.130","admin","Admin_1234", "test", "push1", "PUSH"});
    }

} 
