Barney
======

Barney is the security manager, it provides access to checks and crud to the hashed and salted passwords. It uses shadow files so the
passwords are not stored in the database but outside of the database to make leaking of them harder. Barney can only be accessed using 
the ServiceInterface.

1) Check out Barney in Eclipse
2) Build a war using the 'deploy-war' task with the provided build.xml
3) Deploy the war on a Tomcat server

Used by other Springfield services for login, password mails, confirm mails and cookie control.