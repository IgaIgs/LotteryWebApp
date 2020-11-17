# LotteryWebApp

![](https://github.com/IgaIgs/LotteryWebApp/blob/master/AppCaptures/home_page.png)

## Table of contents
* [Introduction](#introduction)
* [Idea](#idea)
* [Functionality](#functionality)
* [Security and programming paradigms](#security-and-programming-paradigms)
* [Launch](#launch)
  - [IntelliJ setup](#intellij-setup)
  - [Docker setup](#docker-setup)
  - [Access the web app](#access-the-web-app)
  - [Shut down Docker](#shut-down-docker)
* [Developement info](#developement-info)

## Introduction

This is a university project developed as part of the *Security and Programming Paradigms* module studied during undegraduate Computer Science degree.

## Idea

A mock scenario in which the University has decided to set up a Lottery scheme to raise money for local charities and has started to develop a prototype Lottery Web Application. 
The university has asked me, as a *software security specialist*, to help develop their prototype by designing and implementing some basic functionality and security elements.

Since the emphasis of this module is on the security and functionality aspects of the app, the GUI is indeed a very basic one.

## Functionality

This web app allows the users to:
* Create an account:
  - The public account allows the user to take part in the lottery
  - The admin account allows the user to check the data of other users
* Easily log in and log out of their accounts
* View the user's data on their account page
* Submit lottery draws:
  - by choosing their own numbers or
  - by using the lucky dip functionality
* Viewing their submitted draws

![](https://github.com/IgaIgs/LotteryWebApp/blob/master/AppCaptures/home_page.png)

* Check for a win

![](https://github.com/IgaIgs/LotteryWebApp/blob/master/AppCaptures/checkforwin.png)


## Security and programming paradigms

In this project I have learnt about and implemented the following:
* Data input from user 
* Read and write to a database
* Read and write to a text file
* Data transit between Client/Server
* Error handling
* Input validation using JavaScript/JQuery

![](https://github.com/IgaIgs/LotteryWebApp/blob/master/AppCaptures/CLI%20validation.png)  ![](https://github.com/IgaIgs/LotteryWebApp/blob/master/AppCaptures/CLI.png)

* Input validation using a filter
* Hashing and matching with a hashed password
* Encrypting/decrypting data
* Secure random number generation
* Session management
* Limiting user login attempts

![](https://github.com/IgaIgs/LotteryWebApp/blob/master/AppCaptures/error%20login.png)

* Role based access control

## Launch

Because the web application is just a prototype, to enable the university to test it the final version of the program runs using Docker.

Before you start:
1) Make sure you have a working Java IDE downloaded. I am using Intellij and it's also the recommended IDE for Java.
2) Download Docker and create a free account to be able to run images.

### IntelliJ setup

1. Click File -> Project Structure -> Project Settings -> Libraries
2. Click + then Java 
3. Locate the **mysql-connector-java-8.0.21.jar** and click on it (or highlight and click open) to add it.

You might find the file there already in red (the file path is wrong) delete this one by clicking -.

In project settings look at problems and see if any are highlighted. Click each one and select 'fix'. The likely problem is the mysql-connector-java-8.0.21.jar needs to be added to the artifacts.

If running Tomcat server and MySQL database server on Docker when developing, each time you modify the project and want to test it you will need to rebuild **LotteryWebApp_war.war**. 

In intellij:
1. Select Build -> Build Artifacts -> All Artifacts -> Build
2. Copy LotteryWebApp_war.war to the folder tomcat -> web apps
3. Run Docker (instructions below)

### Docker setup

1. Make sure this URL is used in all your Java Servlets which are connecting to the mysql database:
```String DB_URL = "jdbc:mysql://db:3306/lottery";```
2. Make sure the latest **LotteryWebApp_war.war** is copied to the folder tomcat -> web apps.

The docker-compose.yml will set up both Tomcat Server and MySql database server in one go and create a Docker network. To run the file open a terminal in the CSC2031 Coursework folder (containing this README file) and simply run:

```>> docker-compose up ```

You should see a lot of commands appearing in the terminal as this are set up. Once you see something like below everything should be good to go:

``` >> ready for connections. Version: '8.0.21'  socket: '/var/run/mysqld/mysqld.sock'  port: 3306  MySQL Community Server - GPL.```

### Access the web app

Open a browser and head to http://localhost:44444

### Shut down Docker
When you have finished using Docker it is best to stop it. 

1. On the terminal hit CTRL-C to get a prompt
2. ``` >> docker-compose down ```

## Developement info

The web application consists of the following elements:

* Java web application
* Apache Tomcat Server
* MySqL Database

