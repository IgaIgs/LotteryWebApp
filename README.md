# LotteryWebApp

## Table of contents
* [Introduction](#introduction)
* [Idea](#idea)
* [Functionality](#functionality)
* [Security and programming paradigms](#security-and-programming-paradigms)
* [Launch](#launch)
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
* Check for a win

## Security and programming paradigms

* Data input from user 
* Read and write to a database
* Read and write to a text file
* Data transit between Client/Server
* Error handling
* Input validation using JavaScript/JQuery
* Input validation using a filter
* Hashing and matching with a hashed password
* Encrypting/decrypting data
* Secure random number generation
* Session management
* Limiting user login attempts
* Role based access control

## Launch

## Developement info

The web application consists of the following elements:

* Java web application
* Apache Tomcat Server
* MySqL Database

Because the web application is just a prototype, to enable the university to test it the final version of the program runs using Docker.
