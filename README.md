<h1 align="center"> USER-SERVICE</h1> <br>

<p align="center">
    This repository contains the identification of all the users in our game. 
    We decided to write the User service in Kotlin because we found it interesting to explore this language.
</p>

## Introduction

We, at Thèta, are proud developers of our very first multiplayer game. We've developed an online version of the popular game "Risk", using microservices, 3D technologies and much more.
 
We've reached a total of 5 services, which are:
- Ai service
- Analytics service
- Chat service
- Game service
- User service

Besides that, there is the web client written in React and an admin panel written in Vue.

This was a fun and educational experience for everyone here at Thèta. 
We couldn't have made this possible without the help of our coaches:
- Bart Vochten
- Herwig De Smet
- Geert De Paepe
- Jan Van Overveldt

## Connection

### Messaging

To sync the other services with the User services, we decided to use RabbitMQ messaging. This makes the synchronization reliable between the different micro services.

## Technologies

### Json Web Token

To identify the user and give it the flow that is required, we decided to use JWT. This allows the users to stay logged in and so play the game we designed for them.

### Exception handler

We decided to use the exception handler from Spring to make a formal and complete exception message, for every exception that could occur.
