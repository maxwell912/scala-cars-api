# Cars API

Framework: Play2 <br />
Database: PostgreSQL.

## Start service

```shell
$ docker-compose up
```

## API methods
-  GET
   -  /cars/all - Return all inserted cars
   -  /cars/statistic - return map with statistic values
-  POST
   -  /cars/add - insert car with specified in json values
      -  number(String) Unique Pattern: A333AA33 / A333AA133
      -  model(String) Car model
      -  kind(String)
      -  color(String)
      -  year(Int) Production year 
-  DELETE
   - /cars/delete/{carNumber} - delete car with specified number
