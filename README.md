# occurrence-annotation
Experimental: Rule based annotation store.

Run the application in IDEA and then:

```
curl -X POST http://localhost:8080/v1/occurrence/annotation/project -H "Content-Type: application/json" \
  -d '{"name":"LegumeData.org", "creator":"tim", "description":"Annotations from the Legumedata.org group"}'

curl -X POST http://localhost:8080/v1/occurrence/annotation/rule -H "Content-Type: application/json" \
  -d '{"Comment":"Terrestrial records in the sea", "creator":"tim", "geometry":"[ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ]", "contextType":"TAXON", "contextKey":"2435099","project": {"id":1}, "errorType":"LOCATION"}'
```

And then visit http://localhost:8080/v1/occurrence/annotation/rule to receive: 

``` 
[
   {
      "id":2,
      "contextType":"TAXON",
      "contextKey":"2435099",
      "geometry":"[ [100.0, 0.0], [101.0, 0.0], [101.0, 1.0], [100.0, 1.0], [100.0, 0.0] ]",
      "enrichmentType":null,
      "comment":null,
      "project":{
         "id":1,
         "name":"LegumeData.org",
         "description":"Annotations from the Legumedata.org group",
         "creator":"tim",
         "created":"2023-03-17T13:52:32.106+0000"
      },
      "creator":"tim",
      "created":"2023-03-17T13:52:48.955+0000",
      "errorType":"LOCATION"
   }
]
```

To list the projects: http://localhost:8080/v1/occurrence/annotation/project 