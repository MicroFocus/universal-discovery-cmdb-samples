package com.microfocus.ucmdb.rest.sample.discovery;

import org.junit.Test;

/** 
* GetZoneResult Tester. 
* 
* @author <Authors name> 
* @since <pre>Jul 8, 2020</pre> 
* @version 1.0 
*/ 
public class GetZoneResultTest { 

    @Test
    public void testMain() throws Exception {
        GetZoneResult.main(new String[]{"${UCMDB_IP_ADDRESS}", "${UCMDB_PORT}", "${UCMDB_USERNAME}", "${UCMDB_PASSWORD}", "${ZONE_NAME}"});
    }

} 
