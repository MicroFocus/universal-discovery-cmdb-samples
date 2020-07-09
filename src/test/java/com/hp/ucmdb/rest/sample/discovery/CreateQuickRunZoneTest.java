package com.hp.ucmdb.rest.sample.discovery; 

import org.junit.Test; 
import org.junit.Before; 
import org.junit.After; 

/** 
* CreateQuickRunZone Tester. 
* 
* @author <Authors name> 
* @since <pre>Jul 6, 2020</pre> 
* @version 1.0 
*/ 
public class CreateQuickRunZoneTest { 

    /** 
    * 
    * Method: main(String[] args) 
    * 
    */ 
    @Test
    public void testMain() throws Exception { 
        CreateQuickRunZone.main(new String[]{"16.187.188.130","admin","Admin_1234"});
    } 

} 
