package com.microfocus.ucmdb.rest.sample.quickstart;

import org.junit.Test;

/** 
* LoginUCMDB Tester. 
* 
* @author <Authors name> 
* @since <pre>Jul 7, 2020</pre> 
* @version 1.0 
*/ 
public class LogInToUCMDBTest {

    @Test
    public void testMain() throws Exception {
        LogInToUCMDB.main(new String[]{"127.0.0.1", "8443", "admin", "admin"});
    }
}
