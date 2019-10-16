# Yet Another Banking System

## Build
```bash
mvn clean install
```

## Run app
```bash
java -jar target/yabs.jar
```

## Diferences from real live system

* **Security** - API should be secured, also each transfer request should require two factor authorization (e.g. one time use sms codes, or secret send by push notification)
* **Health check** - endpoint return system health, needed for container orechestration / culuster management to replace unhealthy instances
* **Metrics** - system should publish all possible metrics, like number of transaction, request times, etc.
* **Logs**  - ther should be structured logs, containg details information about all processes
* **Caching** - real system shoudl cache some information, like e.g. account balance value, but this will require queueing balance changes for creditor account since locking both creditor and debtor account mqight cause dead lock.
