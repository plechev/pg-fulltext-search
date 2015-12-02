
##Template Summary##

   Multi-module Openshift-ready Maven project with the following stack:
    
   * REST API 
      * CXF Front-End (JAX-RS Compliant)
      * Spring Security (optional)
      * Integration Testing with [Jmockring](https://github.com/plechev/jmockring) 
   * Services
      * Spring DI 
      * Spring Transactional Support
   * DB Access
      * JPA/Hibernate 
      * Spring Data 
      * Optional Embedded Database (H2)
   * DB Migrations Management
      * [Flyway](http://flywaydb.org)
    
## Modules ##

###Business Modules###
   
   This project takes the vertical-first approach in application composition. 
   What this means in practice is that you have multiple coherent Maven modules (JARs) each encapsulating the following:
    
   * Domain model specific to this vertical
   * Business logic specific to this vertical, and 
   * External data access (database, other APIs, Messaging, etc ...) 

   This template contains one vertical **business-1**, but you can create as many as required by your project.
   Some examples of vertical business modules:
    
   * *users* - has all classes and logic pertaining to users 
    * domain: User 
    * business: UserService 
    * data access: UserRepository
   * *orders* - has all classes and logic for processing user orders - this will naturally depend on 'user' module 
   * *scheduler* - background tasks for order fulfillment - naturally depends on 'orders' (and transitively on 'users') 
    
###Delivery mechanism###
    
   The public face of your application (the "delivery mechanism") is separated in its own module.  
   In this case this is the **rest-api** which is the link between the client and your business modules. 
   This module has natural dependency on all of your business modules. It is here that we need to write the most exhaustive integration tests which simulate client interactions.
   It is the responsibility of this module to translate the client's call (typically HTTP Request) into a business method call and convert the business response to client-friendly message (typically JSON).
   
   Since this is the application's public face, it is also where we implement the security constraints (enable & configure Spring Security)
   
####Deployment####
   
   Because this module already aggregates all dependencies in your project it will normally be used to produce your deployable artifact, i.e. the WAR file. 
   To meet the OpenShift (LAE) deployment prerequisites, it bundles-in the __*/package*__ hierarchy with Tomcat configurations.  

   
###Database migrations###
   
   This template features a special module called **db-migrations** which contains all database evolution scripts. The migration task is configured to execute on each build. 

##Configuration##

   The configuration in this project uses the following file naming conventions:
   
   * Business module configuration
     * Main Spring context file: {module_name}-context.xml - Example: **business1-context.xml**
     * Additional Spring context files: {module_name}-{any_name}-config.xml - these are files normally included in the main context. Example: **<import resource=''business-1-repository-config.xml''/>**      
     * Properties files 
        * default: {module_name}.properties - example **business1.properties**
        * environment-specific override: {module_name}-{env_name}.properties - example **business1-dev.properties**  

   * REST module configuration 
     The conventions here are similar to the business modules, however there are 3 files with special functions:
     * **cxf-config.xml** - configures the CXFServlet Spring context. Typically, this will reference all Spring bean classes which are declared in the reset module. These include JAX-RS Resource classes, providers, interceptors, etc ..   
     * **bootstrap-rest-context.xml** - configures the business Spring context, typically by including the main context files from all business modules. 
     * **security-config.xml** - enables and configures Cisco SSO idiomatic Spring Security context. If required this must be included in bootstrap-rest-context.xml via **<import resource=''security-config.xml''/>**

   * Flyway configuration
     * Database connection properties are located in __*db-migrations/flyway*__   
     * Properties files must be named  **flyway-{env_name}.properties**
     * Schema evolution scripts are located in **db-migrations/src/main/resources/migrations** and follow the [Flyway naming conventions](http://flywaydb.org/documentation/migration/sql.html) 


###Note on local (developer-only) files:###
    
   In order to facilitate the isolation of the development work, configuration settings specific to the developer's machine are kept in separate .properties files which map to the fictitious environment **lcl**. 
   Since these files are not versioned and must not be committed to the Git repository, the developer is required to create them manually.  
   The easiest way to do so is by copying the corresponding CI file (ending with *-jenkins.properties*) and replace *jenkins* with *lcl* in the file name. For example, in the case of FlyWay Db config, the file *flyway-jenkins.properties*
   will be copied to *flyway-lcl.properties* and then database configuration updated accordingly. 
   Please note that the pattern ***-lcl.*** is added to the *.gitignore*. Therefore, all local files must be maintained independently by all team members.    
    
##Bootstrapping your project##
  
   1. Fork the project from Stash (https://gitscm.cisco.com/projects/ASF/repos/tmpl-rest-cxf-spring-hibernate?fork)
   2. Rename the following modules to reflect your project:
    * business-1 - this is a single vertical module including domain, repository and service layers. 
    * rest-api - the REST module representing the delivery mechanism (REST API)
   3. Rename all configuration files starting with 'business-1' to reflect your project (do global search in IDE to locate them) 
   4. Configure DB access in:
    * db-migrations/flyway/flyway-[env].properties 
   5. Search globally for the pattern $CHANGEME$ and update the relevant settings accordingly
   6. Run *mvn clean verify* to check the build for errors
   7. Start coding. Use the existing classes and tests to adapt to your requirements. Update the class and package names as appropriate.   

##Building the OpenShift/LAE deployable WAR##
      
   To build the TAR.GZ file required by Openshift, execute the Maven build with the profile **lae**, e.g. *mvn -Plae clean package*. This will create the archive in the target folder of the rest-api module. 
   The **lae** profile must be activated when building the project in Jenkins for uDeploy deployments.  
   
      
##Reccommended IDE Plugins##
 
   * [Generate Test Cases](https://wiki.openmrs.org/display/docs/Generate+Test+Case+Plugin)
   * [Git Ignore plugin (IntelliJ)](https://plugins.jetbrains.com/plugin/7495?pr=idea)
   * [Markdown (IntelliJ)](https://plugins.jetbrains.com/plugin?id=5970)
   
   
      
  
