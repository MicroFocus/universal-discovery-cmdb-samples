# Sample code for CMS RESTAPI
## 1, Discovery
1. CreateAWSZone
	Create a management zone for AWS discovery
2. CreateInventoryZoneForWindowsProbe
	Create a management zone which can do inventory discovery on a windows probe.
3. CreateQuickRunZone
	Create a management zone and activate to do ping sweep only, which don't need credential
4, DeleteZoneWithAllReference
	Delete a management zone, if the used block is not oob and only refered by this zone, also delete it.
5, GetComlogOnTrigger
	Run a zone and check result, rerun the first trigger and retrieve communicationlog
6, GetZoneResult
	Run a zone, get the first bulk of result and print
7, RerunFailedTriggers
	Run a zone, go through all trigger, rerun the trigger if status is error. 

## 2, Integration
1. JobSyncScenarioSample
	This scenario is to run full sync and then delta sync of all jobs under inactive sample points
2. SimpleTroubleshootingScenarioSample
	This scenario is to perform a simple troubleshooting when there is an sample error.
3. ViewCIStatisticsScenarioSample
	This scenario is to view the CI statistics for a specific job in a specific sample point.
4. ViewIntegrationPointsScenarioSample
	This scenario is to get the following information of specific sample points: status, statistics, details for each sample point. Then you can view the job list and status of each job.