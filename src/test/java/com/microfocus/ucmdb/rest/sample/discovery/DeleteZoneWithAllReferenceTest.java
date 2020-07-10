package com.microfocus.ucmdb.rest.sample.discovery;

import org.junit.Test;

/** 
* DeleteZoneWithAllReference Tester. 
* 
* @author <Authors name> 
* @since <pre>Jul 6, 2020</pre> 
* @version 1.0 
*/ 
public class DeleteZoneWithAllReferenceTest { 

    @Test
    public void testMain() throws Exception {
        DeleteZoneWithAllReference.main(new String[]{"16.187.188.130","admin","Admin_1234", "ProbeInventoryZone"});
    } 


} 
