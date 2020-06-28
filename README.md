# Order Service
This project includes an example implementation of order service using technologies 
Spring Boot, Docker. Customer and Product information are assumed to be located in 
different services. Only customerId and productCode are used as references. 

# Requirements
this project requires
* Java 8
* Maven
* docker
* docker-compose

# Installation
In order to install the project execute `mvn clean install` under the project folder.

# Configurations
Configurations are located externally under "config" folder. One of the following spring profiles must be selected:
* development
* test
* prod

# Execution
In order to run application on docker following commands must be executed:
* development: `docker-compose -f docker-compose.yml -f docker-compose-development.yml up --build`
* test: `docker-compose -f docker-compose.yml -f docker-compose-test.yml up --build`
* prod: `docker-compose -f docker-compose.yml -f docker-compose-prod.yml up --build`

# Test
While the application is deployed, API Guide can be accessed through path [/api-guide.html](http://localhost:8080/api-guide.html) 
