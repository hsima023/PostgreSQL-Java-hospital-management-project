# PostgreSQL-Java-hospital-management-project

In this project, my team and I create a model and build a hospital database management sytem using PostgreSQL. In the last phase, we implement PostgreSQL into the skeleton code in Java that is provided by the professor to construct a client application.

This project is being done over Spring 2021 quarter.

## How to initialize and stop PSQL environment
Go to /code/postgresql/
1. Execute the following command to initialize the PSQL environment.
```
source ./startPostgreSQL.sh
```
2. Execute the following command to create the database server.
```
source ./createPostgreDB.sh
```
3. DO NOT FORGET! Execute the following command to stop the server and shutdown the database.
```
source ./stopPostgreDB.sh
```

## How to compile and run the client App
Go to /code/java/
1. Execute the following command to compile the client application.
```
source ./compile.sh
```
2. Execute the following command to start running the application.
```
source ./run.sh
```
