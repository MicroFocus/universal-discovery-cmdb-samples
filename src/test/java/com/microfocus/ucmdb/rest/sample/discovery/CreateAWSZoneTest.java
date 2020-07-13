package com.microfocus.ucmdb.rest.sample.discovery;

import org.junit.Test;

/** 
* CreateAWSZone Tester. 
* 
* @author <Authors name> 
* @since <pre>Jun 24, 2020</pre> 
* @version 1.0 
*/ 
public class CreateAWSZoneTest { 

    @Test
    public void testMain() throws Exception {
        CreateAWSZone.main(new String[]{"127.0.0.1","8443","admin","admin"});
    }

} 
