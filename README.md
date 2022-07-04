# TransferApi

Design and implement a RESTful API for doing transfers between accounts. That should include data models and service implementation.
Minimum model definition
o Account
  ▪ Account number
  ▪ Balance
o Transaction
  ▪ Source account number
  ▪ Destination
  
**API Instructions**
======================================
- Checkout code to you system, its in public repository.
- Import transfer-api as maven based project in your preferred IDE.
- Perform mvn clean install for transfer-api project(Project with parent POM)

- IMPORTANT: Ensure to start applications in given order only.

  1. Start Config Service: Service to keep configurations are a centralized location, configured to read properties from Github.
  2. Start Discovery Service: Source service performs lookup at Discovery Service to get information[IP:PORT] about Target service.
  3. Start Account Service: Service maintains account related information[account-number,account-balance], configured to maintain data in in-memmory h2 database. This service is also pre-loaded with sample account numbers and their account balances.
  4. Start Event Service: Service to record transactions in in-memory database.
  5. Start Transfer Service: Service to perform acutal fund transfer, works as orchestrator between Account and Event Service.
  6. Start Gateway Service: Works as gateway to Transfer Service, configured to balance load across multiple instances of Tansfer Service.

**Instruction to test API**
======================================
- Gateway is configured to listen on port: 8085
- Do a HTTP POST request at: http://localhost:8085/transfer-api
- Sample JSON request:
  {
    "sourceAccountNumber":"sa1001",
    "destinationAccountNumber":"sa1002",
     "transferAmount": 200.00
  }
  
**Important URLs**
======================================
- Account Service in-memory Database: http://localhost:8082/h2-console
- Event Service in-memory Database: http://localhost:8083/h2-console
- Discovery Service/Server URL: http://localhost:8081/
- Config Service/Server URL: http://localhost:8080/service-name/profile-name
- Gateway Service URL: http://localhost:8085/transfer-api
  
**System Design**
======================================
![image](https://user-images.githubusercontent.com/4828778/177153726-c2874b1f-741a-49f8-b8fa-fe1c9ceace1c.png)

 
