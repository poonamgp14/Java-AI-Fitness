#AI powered Microservices based Java Fitness APP
Each microservice is regiesterd into Eureka server. API-gateway is the main entrypoint where Keycloak is added as an Identity provider for OAuth2 authorization.
Configs for different microservices are loaded via a config server.
Publishing a message to RabbitMQ queue on adding a new fitness activity
Messgae is consumed by Gemini based AI-service which gets recommendations based on the activity.


###Steps
1. Edit configuration under AI service and set env variables as
GEMINI_API_KEY=<KEY GOES HERE>;GEMINI_API_URL=<URL GOES HERE>
2. Run KeyCloak server via Docker
```
docker run -p 127.0.0.1:8181:8080 -e KC_BOOTSTRAP_ADMIN_USERNAME=admin -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin quay.io/keycloak/keycloak:26.5.2 start-dev
```
3. Run RabbitMQ instance via Docker
```
docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:4-management
```
4. UI is build using Vite. Cd into `cd fitness-app-UI` and run `npm run dev` 
