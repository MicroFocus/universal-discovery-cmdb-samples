# Code samples for CMS REST API

## Table of Contents

- [Introduction](#introduction)
- [Install](#install)
- [Usage](#usage)
	- [Quick Start](#quick-start)
	- [Discovery](#discovery)
	- [Integration](#integration)
	- [Probesetup](#probesetup)

## Introduction

This project provides CMS REST API code samples for UCMDB users who would like to perform zone-based discovery and integration through REST API. 
You can access CMS REST API documentation from one of the following:
- To view the interactive REST API Reference documentation from the product, access CMS UI, and go to **Help > REST API Reference**.
- To view the static REST API Reference documentation, go to [CMS REST API Reference](https://docs.microfocus.com/UCMDB/2020.11/ucmdb-docs/docs/eng/APIs/UCMDB_RestAPI/index.html). 

**Important:** 
- In a classic version of UCMDB, make sure you add **/rest-api** to the beginning of the URL, so that it becomes **/rest-api/authenticate**.
- In a suite version, make sure you add **/ucmdb-server/rest-api** to the beginning of the URL, so that it becomes **/ucmdb-server/rest-api/authenticate**. 

  For SMAX suite, a further root context **/cms** needs to be added, which results in **/cms/ucmdb-server/rest-api/authenticate**.



## Set up the sample project

1. **Prerequisite:** This project requires JDK 1.8 (or later) and Maven 3. 
   Make sure you already have them on your local machine. 
   
2. Clone the project so that you have a local copy. 

3. Go to the project root folder on your local mahine, open Command Prompt and run the following command to build the project into a JAR file:

   ```sh
   mvn package
   ```
   When the building process completes, you should see the `BUILD SUCCESS` message at the end.
   
4. Go to the **target** folder, you should find the JAR file you just built.



## Usage

This section explains how to use CMS REST API code samples. 

In order to run the code samples properly, make sure you have enabled the new zone-based discovery solution in CMS UI.

### Quick start

This use case shows the easy usage of CMS REST API with a simple operation: Login. 

1. Go to the project root folder on your local mahine, open Command Prompt and run the following command:

   ```sh
   java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.quickstart.LogInToUCMDB
   ```
2. Provide necessary information when prompted, for example, hostname/IP of the UCMDB server, port of the UCMDB server, username, and password.
3. The response shows that the sample code has connected to the UCMDB server successfully, and you should see the `LOG IN TO SERVER` message as well as the token retured. 
   Then you can use this token for REST API operations.


### Discovery
* **Create a zone for AWS Discovery** 

  This sample case shows how to create a zone for AWS discovery with REST API.  
  
  1. Go to the project root folder on your local mahine, and open the following file: 
     **data\payload\CreateAWSZone_1.json**
     
  2. Modify the AWS credential parameters' values as necessary, and save the file.
  
  3. From the project root folder, run the following command:
  
     ```sh
     java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.discovery.CreateAWSZone
     ```
  4. Provide necessary information when prompted.
  
  5. When the sample code execution is completed, you should be able to see in the log what steps the sample has executed as well as execution result for each step: 
  
     a. `LOG IN TO SERVER`
     
     b. `ENSURE ZONE BASED DISCOVERY IS ENABLED`
     
     c. `CREATE AWS CREDENTIAL`
     
     d. `CREATE AWS CREDENTIAL GROUP`
     
     e. `CREATE AWS JOB GROUP`
     
     f. `CERATE AWS ZONE`
     
  6. Go to the Discovery module in CMS UI, you should see an AWSZONE is created successfully.  
  
    

* **Create a zone for Inventory Discovery**

  This sample case shows how to create a zone for inventory discovery on a Windows probe with REST API.
  
  1. Open each of the following files and modify values of zone configuration parameters as necessary:

     **data\payload\CreateInventoryZoneForWindowsProbe_1.json** - update the `range` section

     **data\payload\CreateInventoryZoneForWindowsProbe_2.json** - update the `ipRanges` section

     **data\payload\CreateInventoryZoneForWindowsProbe_3.json** - update the `credential` section

  2. From the project root folder on your local machine, run the following command:
    
     ```sh
     java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.discovery.CreateInventoryZoneForWindowsProbe
     ```

* **Copy a zone from an existing zone for troubleshooting**

  This sample case shows how to copy a zone for troubleshooting.

  1. From the project root folder on your local machine, run the following command:
     ```sh
     java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.discovery.CopyZone
     ```
     It will copy a new zone from existing AWSZone, there is an example, you can use any existing zone id here.
  

* **Get communication log on trigger**

  This sample case shows how to re-run a trigger of zone, wait until it finishes, and then retrieve communication log.

  From the project root folder on your local machine, run the following command:

  ```sh
  java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.discovery.GetComlogOnTrigger
  ```

  

* **Get zone result**

  This sample case shows how to activate a zone, then get the statistics and result of the zone.

  From the project root folder on your local machine, run the following command:

  ```sh
  java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.discovery.GetZoneResult
  ```

  

* **Rerun failed triggers**  

  This sample case shows how to activate a zone, and then rerun the trigger if its status is Error. 

  From the project root folder on your local machine, run the following command:

  ```sh
  java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.discovery.RerunFailedTriggers
  ```

    

* **Delete a zone**
  
  This sample case shows how to delete a zone. If IP range group, schedule, credential group, or job group specified for the zone is not an out-of-the-box (OOTB) resource and is only referenced by this zone, also deletes it.

  From the project root folder on your local machine, run the following command:
  
  ```sh
  java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.discovery.DeleteZoneWithAllReference
  ```

  

### Integration

* **Job sync**

  This sample case runs full sync and then delta sync of all jobs under inactive integration points.
  
  1. Create an integration point that includes both push and population jobs in it. Do not activate it immediately.
  
  2. From the project root folder on your local machine, run the following command:
    
     ```sh
     java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.integration.JobSyncScenarioSample
     ```



* **Troubleshooting**  
  
  This sample case performs a simple troubleshooting by getting connection status and job status.
    
  From the project root folder on your local machine, run the following command:
  
  ```sh
  java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.integration.SimpleTroubleshootingScenarioSample
  ```



* **View CI statistics**  

  This sample case views CI statistics for a specific job in a specific integration point.
    
  From the project root folder on your local machine, run the following command:
  
  ```sh
  java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.integration.ViewCIStatisticsScenarioSample
  ```



* **View Integration Points** 

  This sample case shows how to get the following information of specific integration points: status, statistics, details for each sample integration point. Then you can view the job list and status of each job.
    
  From the project root folder on your local machine, run the following command:
    
  ```sh
  java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.integration.ViewIntegrationPointsScenarioSample
  ```

### Probesetup
* **Export Ranges** 

  This sample case shows how to export ranges.  
  
  1. Go to the project root folder on your local mahine, and open the following file: 
     **data\payload\ExportRange_1.json**  is a request payload example which to export all ranges.
     **data\payload\ExportRange_2.json**  is a request payload example which to export ranges from specified domains and probes.
     **data\payload\ExportRange_3.json**  is a request payload example which to export ranges from specified groupNames and rangeTypes.
     **data\payload\ExportRange_4.json**  is a request payload example which to export ranges from specified domains,probes,groupNames and rangeTypes.
     **data\payload\ExportRange_5.json**  is a request payload example which to export ranges from specified ranges.
     
  2. Modify the domainNames' values, probeNames' values, rangeTypes' values and groupNames's values as necessary, and save the file.
  
  3. From the project root folder, run the following command:
  
     ```sh
     java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.probesetup.ExportRange
     ```
  4. Provide necessary information when prompted.
  
  5. When the sample code execution is completed, you should be able to see in the log what steps the sample has executed as well as execution result for each step: 
  
     a. `LOG IN TO SERVER`
     
     b. `Export ALL ranges and save as CSV`
     
     c. `Export ALL ranges and save as PDF`
     
     d. `Export ALL ranges and save as XLS`
     
     e. `Export ALL ranges and save as XLSX`
     
     f. `Export SELECTED ranges and save as CSV`
     
  6. In the log , you will also see which folder is file stored at , and go to the folder, you should see these exported files.  


* **Import Ranges** 

  This sample case shows how to import ranges from a CSV range file.  
  
  1. Go to the project root folder on your local mahine, and open the following file: 
     **data\payload\Export_Data_1686548768110.CSV**  
        
  2. Modify the values of 'Probe Name' column, values of 'Domain' column as necessary, and save the file.
  
  3. From the project root folder, run the following command:
  
     ```sh
     java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.probesetup.ImportRange
     ```
  4. Provide necessary information when prompted.
  
  5. When the sample code execution is completed, you should be able to see in the log what steps the sample has executed as well as execution result for each step: 
  
     a. `LOG IN TO SERVER`
     
     b. `Import ranges from CSV with allowOverlap true`
         
     c. `Import ranges from CSV with allowOverlap false`  
  
     In step b, the range import will succeed anyway, as overlapping ranges (if any) will be ignored and other ranges can be properly imported.       
     In step c, the range import will fail if there is any range overlap with the existing ranges in UCMDB. 
  6. Then login CMS UI, you should see ranges have been imported to your specified domains and probes.  

* **Define the network scope for the credential**

  This sample case shows how to define the network scope for the credential.

    1. Go to the project root folder on your local mahine, and open the following file:
       **data\payload\NetworkScopeConfigurationForCredential_1.json**  is a request payload example which to define the network scope (range level) for the credential.
       **data\payload\NetworkScopeConfigurationForCredential_2.json**  is a request payload example which to disable the network scope (probe level) for the credential.
       **data\payload\NetworkScopeConfigurationForCredential_3.json**  is a request payload example which to define the network scope (probe level) for the credential.
       **data\payload\NetworkScopeConfigurationForCredential_4.json**  is a request payload example which to define the network scope (both probe level and range level) for the credential.

    2. Modify the protocol_netaddress' value and domainName' value as necessary, and save the file.

    3. From the project root folder, run the following command:

       ```sh
       java -cp target/sample-1.0-SNAPSHOT-jar-with-dependencies.jar com.microfocus.ucmdb.rest.sample.probesetup.NetworkScopeConfigurationForCredential
       ```
    4. Provide necessary information when prompted.

    5. Go to the credential page in CMS UI, you should see a credential with defined the network scope is created successfully.
