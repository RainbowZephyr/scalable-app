# Load Balancer
Implemented using [Nginx-Clojure](https://github.com/nginx-clojure/nginx-clojure)
### Before Running makesure that RabbitMq is running & Nginx is installed.
#### To run it, please include the nginx.edited as the config file for nginx (it includes paths to JVM and ClassPath that needs to be adjusted for every machine)
1. compile the load_balancer/Nginx and its dependencies, and run Nginx (if on linux >sudo Nginx)
2. For test start sending http requests with JSON load of the app, check [JSONs](https://github.com/RainbowZephyr/scalable-app/tree/inter_apps_messags_structures) for reference.

To test interactions with other apps, run [TestApplicationCommands](https://github.com/RainbowZephyr/scalable-app/blob/load-balancer/src/main/java/TestApplicationCommands.java) to emulate another app listening on a queue (configure the name of the queue & mqServerAddress from within)
To test interaction with the controller, run [TestControllerCommands](https://github.com/RainbowZephyr/scalable-app/blob/load-balancer/src/main/java/TestControllerCommands.java)

Integration with the actual apps on the repo can be found in branch [Integration](https://github.com/RainbowZephyr/scalable-app/tree/integration)
