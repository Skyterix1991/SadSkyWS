## SadSkyWS
SadSky is a cross-platform health monitoring REST web service.

Requirements:
- MySQL/MariaDB (only for production)
- OpenJDK 15
- Maven

## Can I add more databases?
Of course you can, just add its driver to pom.xml file and select its driver and dialect in application.properties.

Documentation: https://skyterix1991.github.io/SadSkyWS/apidocs/

REST endpoints: https://documenter.getpostman.com/view/9259933/TVYF7dkw

Working example usage: https://skyterix1991.github.io/SadSkyWeb/

## Compile project
You can compile project yourself using following instruction.
Note that those modifications are only required if you plan to use it in production environment.

1. Make modifications to application-prod.properties located in resources folder.

You need to set/add following properties:

`token.secret` - Token secret used in generating user JWT token. My recommendation is to generate long token using random characters.

`spring.jpa.properties.hibernate.dialect` - Dialect used in communicating with database, for mysql there are following options: `MySQL8Dialect`, `MySQL5Dialect`, `MySQL55Dialect`, `MySQL57Dialect`. Use dialect corresponding to your MySQL version.

`spring.datasource.url` - Url to connect to database. You can create one from example below:
`jdbc:mysql://ip:port/database?serverTimezone=UTC&autoReconnect=true&characterEncoding=utf8&useUnicode=true` - Just replace ip, port, database with your own credentials.

`spring.datasource.username` - Username used when logging to database.

`spring.datasource.password` - Password used when logging to database.

2. Change `<packaging>war</packaging>` value in pom.xml to version you would like to get jar or war.

3. Run maven command: `mvn clean compile package -Dspring.profiles.active=test`.

5. Generated file will be located in `/target` folder.

6. To run your project navigate to "Run" section in README.md 

## Run Development profile
Project consists of development profile.
If one is selected default user and filled prediction will be created and console will display useful information regarding requests and database usage.

Email: admin@admin.pl

Password: admin

To run project in development profile you can use following command: `java -jar sadsky-0.0.1.jar -Dspring.profiles.active=test`.

## Run Production profile
To run project in production profile you can use following command: `java -jar sadsky-0.0.1.jar -Dspring.profiles.active=prod` or just (only if compiled manually) `java -jar sadsky-0.0.1.jar`.

## Tests
Whole project is covered by tests using JUnit 5.
Note that running tests requires test profile or else there will be thrown exceptions as default profile is production.

To run those you can use Maven command: `mvn clean compile test -Dspring.profiles.active=test`