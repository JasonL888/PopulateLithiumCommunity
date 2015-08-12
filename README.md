# PopulateLithiumCommunity
Sample tool to populate Lithium Community using public REST API

## Pre-requisite Setup
### Community.java
* Create RestApi Role on your Lithium Community
	* For this RestApi role, ensure it has the following user permissions on admin console
    	* User Management > switch to another user
    	* Accepted Solution > Accept or retract solutions
    	* REST API > make REST API calls with read access, write access
 * Create user RestApi
 * Assign following roles to user RestApi
 	* RestApi Role
 	* BlogAuthor Role
* Verify in Community.java
	* userPassword is the password for the RestApi User
	* communityHost is the hostname of your Lithium Platform, eg. "apac.stage.lithium.com"
* Create at least 2 normal users and enter their username, passwords in userCreds
* Create a blog author user with BlogAuthor Role assigned

### PopulateCommunity.java
* Update formBoardIdList with the BoardIds of the Forum Boards to populate
* Update blogBoardIdList with the BoardIds of the Blog Boards to populate
* (Optional) change the numTopics to populate