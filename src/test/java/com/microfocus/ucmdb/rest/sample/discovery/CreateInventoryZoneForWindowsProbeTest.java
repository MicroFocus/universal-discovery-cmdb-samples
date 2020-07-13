package com.microfocus.ucmdb.rest.sample.discovery;

import org.junit.Test;

/** 
* CreateInventoryZoneForWindowsProbe Tester. 
* 
* @author <Authors name> 
* @since <pre>Jul 5, 2020</pre> 
* @version 1.0 
*/ 
public class CreateInventoryZoneForWindowsProbeTest { 

    /** 
    * 
    * Method: main(String[] args) 
    * 
    */ 
    @Test
    public void testMain() throws Exception {
        CreateInventoryZoneForWindowsProbe.main(new String[]{"127.0.0.1","8443","admin","admin"});
    } 



} 
