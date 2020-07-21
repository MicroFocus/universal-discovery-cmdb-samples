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

After you cloned the project to your local machine, open the command console and go to project root folder, build the project into a jar.

```sh
mvn package
```

Make sure you can see 'BUILD SUCCESS' at the end.



## Usage

In order to run sample codes correctly, make sure that you have enabled the zone based discovery in UCMDB.

### Quick Start

Let's have a quick look at the REST API usage by testing a simplest operation: Login. 

After we built the project into a jar, we can open a console and go to project root folder, run the case like this:

```sh
java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.quickstart.LogInToUCMDB
```

After we provided necessary information, it will output the token as the result.


### Discovery
* Create Zone for AWS  
    In this case, we will create a zone for AWS discovery. 
First, we need to modify the AWS credential in this file:
    data\payload\CreateAWSZone_1.json
    
    Then run this command:
```sh
java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.discovery.CreateAWSZone
```

* Create Zone for Inventory  
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
* Sample of job sync scenario  
    This case is to run full sync and then delta sync of all jobs under inactive integration points.
    First, we need to create an integration point with push and population jobs in it. But do not activate it immediately.
    
    Run this command:
    
```sh
java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.integration.JobSyncScenarioSample
```


​    
* Sample of troubleshooting scenario  
    This case is to perform a simple troubleshooting by getting connection status and job status.
    
    Run this command:
    
```sh
java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.integration.SimpleTroubleshootingScenarioSample
```


​    
* Sample of viewing CI statistics scenario  
    This case is to view the CI statistics for a specific job in a specific integration point.
    
    Run this command:
    
```sh
java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.integration.ViewCIStatisticsScenarioSample
```


​    
* sample of viewing IntegrationPoints scenario  
    This case is to get the following information of specific sample points: status, statistics, details for each sample point. Then you can view the job list and status of each job.
    
    Run this command:
    
```sh
java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.integration.ViewIntegrationPointsScenarioSample
```

​    