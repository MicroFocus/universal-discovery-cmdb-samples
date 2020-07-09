package com.hp.ucmdb.rest.sample.discovery; 

import org.junit.Test; 
import org.junit.Before; 
import org.junit.After; 

/** 
* RerunFailedTriggers Tester. 
* 
* @author <Authors name> 
* @since <pre>Jul 9, 2020</pre> 
* @version 1.0 
*/ 
public class RerunFailedTriggersTest { 

    @Test
    public void testMain() throws Exception {
        RerunFailedTriggers.main(new String[]{"16.187.188.130","admin","Admin_1234", "myzone"});
    } 

} 
