# occurrence-annotation
Experimental: Rule based annotation store.

The gist of this project:

- A GBIF user can view some data such as a map of a Taxon or a Dataset (the context) on a map
- The user can then draw a polygon on the map and create a rule to say either the data contained is erroneous or can offer an enrichment
- When stating data is erroneous, the user will be able to optionally qualify the error e.g. coordinates are incorrect or the identification is wrong
- When enriching, the user is noting that the data is not incorrect, but should be supplemented with additional data e.g. the records represent an organism in captivity, or it is an observation of a vagrant organism etc
- Logged-in users will be able to offer support or context rules (i.e. thumbs up or thumbs down)
- Comments can be added by logged-in users to a rule
- Optionally, a rule can be associated with a project when it is created if the user is a registered editor of that project.
- All rules are open to everybody, optionally limited to a project view
- Projects allow a group of editors to collaborate and view their results without outside interference. They may e.g. publish their rules as part of their cleaning process for a their research.
- A leaderboard of annotators will be available

Run the application in IDEA and then:

```
curl -u "username:password" -X POST http://localhost:8080/v1/occurrence/annotation/project -H "Content-Type: application/json" \
  -d '{"name":"LegumeData.org", "description":"Annotations from the Legumedata.org group"}'

curl -u "username:password" -X POST http://localhost:8080/v1/occurrence/annotation/rule -H "Content-Type: application/json" \
  -d '{"contextType":"TAXON", "contextKey":"2435099", "geometry":"[ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ]", "projectId": 1, "errorType":"LOCATION"}'
  
curl -u "username:password" -X POST http://localhost:8080/v1/occurrence/annotation/rule/1/comment -H "Content-Type: application/json" \
  -d '{"comment":"Terrestrial species in the sea"}'
  
curl -u "username:password" -X POST http://localhost:8080/v1/occurrence/annotation/rule/1/support 
```

The API is documented on http://localhost:8080/swagger-ui/index.html 