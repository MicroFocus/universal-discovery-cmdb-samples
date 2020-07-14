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
        RerunFailedTriggers.main(new String[]{"${UCMDB_IP_ADDRESS}", "${UCMDB_PORT}", "${UCMDB_USERNAME}", "${UCMDB_PASSWORD}", "${ZONE_NAME}"});
    } 

} 
