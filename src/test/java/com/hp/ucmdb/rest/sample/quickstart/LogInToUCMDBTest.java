package com.hp.ucmdb.rest.sample.quickstart; 

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
        LogInToUCMDB.main(new String[]{"16.187.188.130","admin","Admin_1234"});
    }


} 
