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
        DeleteZoneWithAllReference.main(new String[]{"${UCMDB_IP_ADDRESS}", "${UCMDB_PORT}", "${UCMDB_USERNAME}", "${UCMDB_PASSWORD}", "${ZONE_NAME}"});
    } 


} 
