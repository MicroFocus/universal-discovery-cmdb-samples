package com.microfocus.ucmdb.rest.sample.discovery;

import org.junit.Test;

/** 
* GetComlogOnTrigger Tester. 
* 
* @author <Authors name> 
* @since <pre>Jul 8, 2020</pre> 
* @version 1.0 
*/ 
public class GetComlogOnTriggerTest { 

    @Test
    public void testMain() throws Exception {
        GetComlogOnTrigger.main(new String[]{"127.0.0.1","8443","admin","admin", "ProbeInventoryZone"});
    } 

} 
