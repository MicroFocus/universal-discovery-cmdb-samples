package com.microfocus.ucmdb.rest.sample.integration;

import org.junit.Test;

/** 
* ViewCIStatisticsScenarioSample Tester. 
* 
* @author <Authors name> 
* @since <pre>Jul 9, 2020</pre> 
* @version 1.0 
*/ 
public class ViewCIStatisticsScenarioSampleTest { 

    @Test
    public void testMain() throws Exception {
        ViewCIStatisticsScenarioSample.main(new String[]{"127.0.0.1","8443","admin","admin", "integrationPoint", "pushJob", "PUSH"});
        ViewCIStatisticsScenarioSample.main(new String[]{"127.0.0.1","8443","admin","admin", "integrationPoint", "populationJob", "POPULATION"});

    }

} 
