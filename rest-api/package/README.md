
This is the OpenShift packgaing folder require for deployment of the WAR file.

-----------
 
 Files:
 
 * Tomcat & Environment initialisation (i.e. set cisco.life, classpaths, etc ...): 
    * package/repo/.openshift/action_hooks/pre_start_jbossews  - contains shell commands to be executed on Tomcat start
    * package/repo/.openshift/action_hooks/pre_restart_jbossews  - contains shell commands to be executed on Tomcat re-start (currently simply delegates to above)
    
 * Application configuration:
    * package/repo/.openshift/config/server.xml  - configure Tomcat server, including context path for the application and WAR location. 
    * package/repo/.openshift/config/context.xml  - configure application-level resources, such as DataSource pools, etc...
       
 * WAR 
    * package/dependencies/jbossews/webapps  - this directory will contain the created WAR file ready to be assembled into the Openshift TAR.GZ archive. (do not delete this or add its contents to SCM)       

-----------

Also see other README files inside
