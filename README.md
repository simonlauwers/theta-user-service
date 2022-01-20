# USER-SERVICE
This repository contains the identification of all the users in our Risk Game.
The user-service was one of my responibilities but my teammates did contribute to this repo too.
I decided to write this service in Kotlin because I like the null safety that Kotlin provides and the overall developer experience is great.

## ⚠️ Disclaimer
This is a micro-service of a bigger 3D Risk-game application. This project was imported from Gitlab where all our micro-service-repositories live.
This repository could be a great entry point for building a user-service because of its general endpoints such as: /reset-password, /login, /register etc.

## Messaging
To sync the other services with the User services, we decided to use RabbitMQ messaging. This makes the synchronization reliable between the different micro services.

## Json Web Token
All users are also identified by a JWT. All protected endpoints require that JSON Web Token inside of a cookie. Our API-gateway is responsible for authorizing all requests and routing it to the right service.

