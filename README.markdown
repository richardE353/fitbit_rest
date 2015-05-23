_fitbit_rest_ is a REST interface to the Fitbit API written in Scala using Spray and Spray Client.  It uses Oauth2 for authentication.

### Setup
 * install SBT from http://www.scala-sbt.org
 * copy the src/main/resources/sample_application.conf to src/main/resources/application.conf
 * modify application.conf to have your apps clientId, secret, and target

### Run
To start up the service, just go to the top directory, and execute "sbt run"

### Postman Setup
Googles Postman REST Client is a great tool for exercising REST interfaces. To use it to explore the app, do the following:

 * Import the Fitbit.json.postman_collection file into Postman
 * Set the Postman RestSvc environment var to your server protocol, host, and port  ex: http://192.160.1.104:8080
 * Create another Postman environment var, FitbitToken.  When you have your token from fitbit, set this variable to the token value
  
### Using Postman
Presuming you have started up your service, and have setup Postman, to start using the service, do the following:

 * Run the "authorize" command - this will get you a URL for getting authentication from Fitbit to access data
 * Paste that URL into a browser, and permit access
 * Take the access_token value out of the browser response, and use it to set the Postman FitbitToken variable
 
That is it.  You should now be able to access all the activity data, using the Fitbit commands
 
### License

_fitbit_rest_ is licensed under [APL 2.0](http://www.apache.org/licenses/LICENSE-2.0).
