# Mercury Code Challenge


This is a Java/SpringBoot implementation of the Mercury code challenge.

Configuration is provided in a YAML file ```resources/application.yml```

The application is organised in the following layers that can be tested indepenently:


- **REST API**
	- Runs an embeded webserver on **localhost:8080**
	- Exposes the WithdrawBalance API as well as GetUser and Ping API useful for testing and health checks.
- **Service Layer**
    - Here lives the ```RiskEngineServiceImpl``` service.
    - The service exposes the WithdrawBalance API as defined in the interface ```RiskEngineService``` 
    - It connects to the **Kafka** cluser on **localhost:9092** and handles ```TradeSettlementMessage``` messages sent to topic **mercury.t**
    - I've added an ```orderId``` field to. ```WithdrawBalanceRequest``` and ```TradeSettlementMessage``` messages to allow to reconcile the sell token balance once a trade gets settled.
- **Business/Model Layer**
    - This is where the application business logic lives.
    - the component ```CachedUserRepository``` implements an LRU cache of ```User``` objects limited to 300 items as per specifications (log statements are printed for cache hit and cache miss events).
    - CachedUserRepository extends a ```BaseUserRepository```` where the business logic is implemented.
 
- **Persistance Layer**
    -  ```BaseUserRepository``` uses an implemenation of the ```DataSource``` interface, ```CSVFileDataSource```, that provides user data from the resource file ```user-balances.csv```
    -   ``CSVFileDataSource`` stores in memory data for all users but this data will be accessed only when data for a specific user is not found in the LRU cache.
    - Ideally  ```BaseUserRepository``` would use an implementation of ```DataSource``` interface that can be efficiently persisted (e.g. a RDBMS or NoSQL database)




## Build & Run

### Build

```mvn clean package```

build artifact
``` target/mercury-microservice-0.0.1-SNAPSHOT.jar ```

### Run

```mvn spring-boot:run ```


## Tests

Run all Tests (Unit + Integration)

```mvn test```

- Unit tests
   - Live under ```com.blockchain.mercury.test.unit``` and test individual layers of the application mocking their dependencies.
- Integration tests 
   -  Live under ```com.blockchain.mercury.test.integration```, cover the full application worflow across all application layers.
   -  Use an embedded Kafka instance started on a random port.


## REST API

### Health check

```  curl -i http://localhost:8080/api/ping ```

### Fetch User
```  curl -i -H "Accept: application/json" http://localhost:8080/api/user/100 ```

### Withdraw Balance

``` 
curl --header "Content-Type: application/json" \
     --request POST \
     --data '{"orderId": 1, "userId": 100, "token": "USD", "requestedQuantity": 1000}' \ 
     http://localhost:8080/api/withdraw    
```  

## Improvements
- I focused on clean design, implementaton of the core functionality and unit/integration testing
- Error handling has not been addressed
- High availability concerns are also not addressed