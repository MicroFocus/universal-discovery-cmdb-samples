# Sample code for CMS RESTAPI

## Table of Contents

- [Background](#background)
- [Install](#install)
- [Usage](#usage)
	- [Quick Start](#quick-start)
	- [Discovery](#discovery)
	- [Integration](#integration)

## Background

This is a project for users who are using UCMDB and want to know how to manipulate Discovery through REST API. 



## Install

This project requires jdk1.8 or above and maven 3. Go check them out if you don't have them locally installed. 

After you cloned the project to your local machine, open the command console and go to project root folder, compile the project.

```sh
mvn compile
```

Make sure you can see 'BUILD SUCCESS' at the end.



## Usage

In order to run sample codes correctly, make sure that you have enabled the zone based discovery in UCMDB.

### Quick Start

In order to have a quick look at the REST API usage, we are going to test a simplest operation: Login. 

First, we need to modify JUnit test: 

src\test\java\com\microfocus\ucmdb\rest\sample\quickstart\LogInToUCMDBTest.java.

It may look like this:

  ```java
public class LogInToUCMDBTest {

    @Test
    public void testMain() throws Exception {
        LogInToUCMDB.main(new String[]{"127.0.0.1", "8443", "admin", "admin"});
    }
}
  ```
We need to update the IP of UCMDB, port, username and password. After that, we open a console and go to project root folder, type this command to run JUnit test:

```sh
mvn test -Dtest=com.microfocus.ucmdb.rest.sample.quickstart.LogInToUCMDBTest
```

It will output the token if executed successfully.


### Discovery
* Create AWS Zone  
    In this case, we will create a zone for AWS discovery. 

  First, we need to modify JUnit test: 

  src\test\java\com\microfocus\ucmdb\rest\sample\discovery\CreateAWSZoneTest.java

  update the IP of UCMDB, port, username and password.

  Second, we update the protocol_username and protocol_password in data\payload\CreateAWSZone_1.json.
  
  Then we run this test:
  
    ```sh
    mvn test -Dtest=com.microfocus.ucmdb.rest.sample.discovery.CreateAWSZoneTest
    ```
  
    
  
* Create Inventory Zone  
    In this case, we will create a zone which can do inventory discovery on a windows probe.
First, we need to modify JUnit test: src\test\java\com\microfocus\ucmdb\rest\sample\discovery\CreateInventoryZoneForWindowsProbeTest.java

    update the IP of UCMDB, port, username and password.

    Second, modify configuration of the zone. Including these files:

    data\payload\CreateInventoryZoneForWindowsProbe_1.json, update the range segment.

    data\payload\CreateInventoryZoneForWindowsProbe_2.json, update the ipRanges segment.
    
    data\payload\CreateInventoryZoneForWindowsProbe_3.json, update the credential.
    
    Then run this test:
    
    ```sh
    mvn test -Dtest=com.microfocus.ucmdb.rest.sample.discovery.CreateInventoryZoneForWindowsProbeTest
    ```



* Get communication log on trigger  
    In this case, we will re-run a trigger of zone, wait until it finishes, then retrieve communication log.
First, we need to modify JUnit test: 
  src\test\java\com\microfocus\ucmdb\rest\sample\discovery\GetComlogOnTriggerTest.java
  update the IP of UCMDB, port, username, password and zone name.
Then run this test:
  
    ```sh
    mvn test -Dtest=com.microfocus.ucmdb.rest.sample.discovery.GetComlogOnTriggerTest
    ```
  
  
  
* Get zone result  
    In this case, we will activate a zone, then get the statistics and result of the zone.
First, we need to modify JUnit test: 
  src\test\java\com\microfocus\ucmdb\rest\sample\discovery\GetZoneResultTest.java
  Then run this test:

    ```sh
    mvn test -Dtest=com.microfocus.ucmdb.rest.sample.discovery.GetZoneResultTest
    ```
  
  
  
* Rerun failed triggers  
    In this case, we will activate a zone, then rerun the trigger if status is error. 
First, we need to modify JUnit test: 
  src\test\java\com\microfocus\ucmdb\rest\sample\discovery\RerunFailedTriggersTest.java
  Then run this test:

    ```sh
    mvn test -Dtest=com.microfocus.ucmdb.rest.sample.discovery.RerunFailedTriggersTest
    ```
  
    
  
* Delete zone  
    In this case, we will delete a zone, if the used profile is not oob and only refered by this zone, also delete it.

  First, we need to modify JUnit test: 
  src\test\java\com\microfocus\ucmdb\rest\sample\discovery\DeleteZoneWithAllReferenceTest.java
  
  Then run this test:
  
  ```sh
  mvn test -Dtest=com.microfocus.ucmdb.rest.sample.discovery.DeleteZoneWithAllReferenceTest
  ```
  
  

### Integration
* JobSyncScenarioSample  
    This case is to run full sync and then delta sync of all jobs under inactive integration points.
    First, we need to create an integration point with push and population jobs in it. But do not activate it immediately.
    Second, we need to modify JUnit test: 
    src\test\java\com\microfocus\ucmdb\rest\sample\integration\JobSyncScenarioSampleTest.java
    Then run this test:
    
    ```sh
    mvn test -Dtest=com.microfocus.ucmdb.rest.sample.integration.JobSyncScenarioSampleTest
    ```
    
    
    
* SimpleTroubleshootingScenarioSample  
    This case is to perform a simple troubleshooting by getting connection status and job status.
    
    First, we need to modify JUnit test: 
    
    src\test\java\com\microfocus\ucmdb\rest\sample\integration\SimpleTroubleshootingScenarioSampleTest.java
    
    update the IP of UCMDB, port, username, password, integration point, job and job category. 
    
    ```sh
    mvn test -Dtest=com.microfocus.ucmdb.rest.sample.integration.SimpleTroubleshootingScenarioSampleTest
    ```
    
    
    
* ViewCIStatisticsScenarioSample  
    This case is to view the CI statistics for a specific job in a specific integration point.
    
    First, we need to modify JUnit test: 
    
    src\test\java\com\microfocus\ucmdb\rest\sample\integration\ViewCIStatisticsScenarioSampleTest.java
    
    update the IP of UCMDB, port, username, password, integration point, job and job category. 
    
    ```sh
    mvn test -Dtest=com.microfocus.ucmdb.rest.sample.integration.ViewCIStatisticsScenarioSampleTest
    ```
    
    
    
* ViewIntegrationPointsScenarioSample  
    This case is to get the following information of specific sample points: status, statistics, details for each sample point. Then you can view the job list and status of each job.
    
    First, we need to modify JUnit test: 
    
    src\test\java\com\microfocus\ucmdb\rest\sample\integration\ViewIntegrationPointsScenarioSampleTest.java
    
    ```sh
    mvn test -Dtest=com.microfocus.ucmdb.rest.sample.integration.ViewIntegrationPointsScenarioSampleTest
    ```
    
    