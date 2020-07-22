# Sample Code for CMS REST API

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

1. Prerequisite: This project requires JDK 1.8 (or later) and maven 3. Go check them out if you don't have them locally installed. 
2. Clone the project so that you have a local copy. 
3. Go to your local project root folder, open the command console, change the path to project root folder and run the following command to build the project into a JAR file:
   ```sh
   mvn package
   ```
   Make sure you see 'BUILD SUCCESS' at the end.



## Usage

In order to run sample codes correctly, make sure that you have enabled the new zone-based discovery solution in CMS.

### Quick Start

Let's have a quick look at the REST API usage by testing a simple operation: Login. 

After building the project into a JAR file, open a command console and go to the project root folder, run the case like this:

```sh
java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.quickstart.LogInToUCMDB
```

After we provided necessary information, it will output the token as the result.


### Discovery
* Create Zone for AWS Discovery  
    In this case, we will create a zone for AWS discovery. 
First, we need to modify the AWS credential in this file:
    data\payload\CreateAWSZone_1.json
    
    Then run this command:
```sh
java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.discovery.CreateAWSZone
```

* Create Zone for Inventory Discovery  
    In this case, we will create a zone which can do inventory discovery on a windows probe.
First, we need to modify configuration of the zone. Including these files:

    data\payload\CreateInventoryZoneForWindowsProbe_1.json, update the range segment.

    data\payload\CreateInventoryZoneForWindowsProbe_2.json, update the ipRanges segment.

    data\payload\CreateInventoryZoneForWindowsProbe_3.json, update the credential.

    Then run this command:
    
```sh
java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.discovery.CreateInventoryZoneForWindowsProbe
```


* Get communication log on trigger  
    In this case, we will re-run a trigger of zone, wait until it finishes, then retrieve communication log.

    Run this command:

```sh
java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.discovery.GetComlogOnTrigger
```

  

* Get zone result  
    In this case, we will activate a zone, then get the statistics and result of the zone.

    Run this command:

```sh
java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.discovery.GetZoneResult
```

  

* Rerun failed triggers  
    In this case, we will activate a zone, then rerun the trigger if status is error. 

    Run this command:

```sh
java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.discovery.RerunFailedTriggers
```

​    

* Delete zone  
    In this case, we will delete a zone, if the used profile is not oob and only refered by this zone, also delete it.

  Run this command:
  
```sh
java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.discovery.DeleteZoneWithAllReference
```

  

### Integration
* Job sync  
    This case is to run full sync and then delta sync of all jobs under inactive integration points.
    First, we need to create an integration point with push and population jobs in it. But do not activate it immediately.
    
    Run this command:
    
```sh
java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.integration.JobSyncScenarioSample
```


​    
* Troubleshooting  
    This case is to perform a simple troubleshooting by getting connection status and job status.
    
    Run this command:
    
```sh
java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.integration.SimpleTroubleshootingScenarioSample
```


​    
* Viewing CI statistics  
    This case is to view the CI statistics for a specific job in a specific integration point.
    
    Run this command:
    
```sh
java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.integration.ViewCIStatisticsScenarioSample
```


​    
* Viewing IntegrationPoints  
    This case is to get the following information of specific sample points: status, statistics, details for each sample point. Then you can view the job list and status of each job.
    
    Run this command:
    
```sh
java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.integration.ViewIntegrationPointsScenarioSample
```

​    
