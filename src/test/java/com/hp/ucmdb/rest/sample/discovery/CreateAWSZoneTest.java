package com.hp.ucmdb.rest.sample.discovery; 

import org.junit.Test; 
import org.junit.Before; 
import org.junit.After; 

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
        CreateAWSZone.main(new String[]{"16.187.188.130","admin","Admin_1234"});
    }

} 
