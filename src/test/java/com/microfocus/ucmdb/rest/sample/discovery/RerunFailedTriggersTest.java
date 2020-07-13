package com.microfocus.ucmdb.rest.sample.discovery;

import org.junit.Test;

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
        RerunFailedTriggers.main(new String[]{"127.0.0.1","8443","admin","admin", "ProbeInventoryZone"});
    } 

} 
