# occurrence-annotation

**Experimental** : Rule based annotation store. 

A **rule** is a combination of geographic, taxonomic, or other information that facilitates data cleaning or analysis of occurrence data. 

## Summary of current features :

* Create rules based on location, taxonKey, and datasetKey. 
* Upvote and downvote rules. 
* Comment on rules. 
* Optionally associate rules with rulesets or projects.
* Create rulesets, which are groups of rules. 
* Create projects, which are groups of rulesets. 
* Rules, rulesets, and projects are only editable by members invited by the creator.  
* Collaborate or projects and rulesets with other invited GBIF users. 
* Rule data is open to all.

**Projects** and **rulesets** are logical groupings of rules that allow a group of editors to collaborate and view their results without outside interference. They may, for example, publish their rules as part of their cleaning process for their research. That being said, all rules are available to view for all. 

## Demo UI 

Link to demo. 

## R package interface 

There is an R interface being developed named `gbifan`. It can found [here](https://github.com/jhnwllr/gbifan).  

## Longer description and road map  

https://github.com/jhnwllr/doc-rule-based-annotations/blob/main/index.adoc

## Build and run locally 

Add the following to your `.m2/settings.xml` file.

```xml
<mirrors>
  <mirror>
    <id>gbif-public-mirror</id>
    <mirrorOf>*,!gbif-all,!gbif-mirror,!gbif-thirdparty</mirrorOf>
    <name>GBIF mirror of public repositories</name>
    <url>https://repository.gbif.org/content/groups/public/</url>
  </mirror>
</mirrors>
```

You might also need to have a running `postgres` instance with a database named "annotation". 

```shell 
sudo -u postgres psql
postgres=# CREATE DATABASE annotation;
```

Finally, run spring boot. 

```shell
mvn spring-boot:run
```

Run the [schema.sql](https://github.com/gbif/occurrence-annotation/blob/main/src/main/resources/schema.sql) to create the needed tables. 

Try it out locally using `curl`. Where `"$GBIF_USER:$GBIF_PWD"` are your GBIF username and password.

```shell
# Create a project 
curl -u "$GBIF_USER:$GBIF_PWD" -X POST http://localhost:8080/v1/occurrence/annotation/project -H "Content-Type: application/json" \
  -d '{"name":"LegumeData.org Annotation Project", "description":"Annotation rules from the Legumedata.org group"}'

# Create a ruleset for that project 
curl -u "username:password" -X POST http://localhost:8080/v1/occurrence/annotation/ruleset -H "Content-Type: application/json" \
  -d '{"name":"LegumeData.org", "description":"Annotations from the Legumedata.org group"}'

# Create a rule for the ruleset and project 
curl -u "username:password" -X POST http://localhost:8080/v1/occurrence/annotation/rule -H "Content-Type: application/json" \
  -d '{"taxonKey":2435099, "geometry":"POLYGON((24.67406 48.90016,24.7533 48.90016,24.7533 48.94698,24.67406 48.94698,24.67406 48.90016))", "rulesetId": 1, "annotation":"INTRODUCED"}'

# Add a comment to a rule  
curl -u "username:password" -X POST http://localhost:8080/v1/occurrence/annotation/rule/1/comment -H "Content-Type: application/json" \
  -d '{"comment":"Terrestrial species in the sea"}'

# Upvote a rule 
curl -u "username:password" -X POST http://localhost:8080/v1/occurrence/annotation/rule/1/support 
```
The API is documented on http://localhost:8080/swagger-ui/index.html 

## Tests

This project uses [test containers](https://testcontainers.com/) to run unit tests, so you will need to have docker running in the background for the tests to work.   
