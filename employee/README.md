~~# Employee Spring boot System

- Used Spring-boot for server handling, All API written in spring boot.
- Used Spring security and JWT authentication for security and Login purpose.
- Annotation applied only in spring-boot, NO XML code

- In Angular we developed Frontend pages i.e. Login, Home, Different functionalities
- User and Admin, Two type of roles are present.
- User is like an employee in department, and Admin is Administrator.


#### Angular-Client should be on http://localhost:4200
#### Spring-boot-server should run on http://localhost:8080


### MYSQL
- Data Will be store in Mysql-Database
- Database name is **emp_db**
- Give your username and password in application.properties file in spring boot
- Make sure your mysql is running on 3306 else you have to change the port in application.properties file in spring boot

### Mandatory Commands you have to run
#### Manual
- Open your Mysql-workbench or Mysql 9.4 Command line client
- if Database exists **Use emp_db;** else create database and run Spring-boot server application and then come to this point
- Insert Some Data into tables.
  /*
  insert into roles values(1, 'ROLE_USER');
  insert into roles values(2, 'ROLE_ADMIN');



#### Existed Commands
- Goto Spring boot -> hms.sql And run this file in workbench.

#### Commands

insert into roles values(1, 'ROLE_USER');
insert into roles values(2, 'ROLE_ADMIN');

insert into users values(1, '$2a$10$nuyV4m3SpBYuqzdRvQK4iO1PI9O0SkLyHQP2YB8CFksVJirzGrDT2', 'admin'); **Password : admin123** 
insert into users values(2, '$2a$10$ef4yjYkFjmAjnQk/GPecK.VxaNmpe6yPfBoZh9dcW1.uzf1zFYemu', 'user');' **Password : user123** 
**This is Encrypted Password**
<br>
<br>
insert into user_roles values(2, 1);
insert into user_roles values(1, 2);

**There is no signup option in Angular-client when it is running on chrome but you can go to signup page by writting this URL**
http://localhost:4200/signup
it will create only **User role** profile for you

### Access
Admin has all accesses <br>
User has all access except Department, Employee pages If you try using url then it will show **you are not authorised**

### ENV MYSQL SETUP CMD OR POWERSHELL USE ADMINISTRATOR
$env:MYSQL_DB_URL="jdbc:mysql://localhost:3306/test"
$env:MYSQL_DB_USERNAME="root"
$env:MYSQL_DB_PASSWORD="root"
$env:jwtSecret="appSecretKey"

echo $env:MYSQL_DB_USERNAME
echo $env:MYSQL_DB_PASSWORD
echo $env:jwtSecret
mysql -u $env:MYSQL_DB_USERNAME

### PENDING 
** SETUP OR CHANGE IDE PY XSS OWASP JUNIT TEST **

