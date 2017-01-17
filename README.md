
### Fulltext Search - seed app 

##### App setup and running the tests

1) Create Postgres database 
2) Configure access to database in `flyway-[env].properties` and execute the Flyway script `V1.0.0__Initial_Schema_Create.sql` to create the structure, including all fulltext search related code
3) Configure access to database in `config/ftsearch.properties` 
4) Update `dbunit/dbunit.properties` and run the tests in `com.cisco.ccit.ftsearch.service.FulltextSearchIT`    

   
      
  
