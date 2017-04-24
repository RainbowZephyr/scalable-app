# scalable-app

### Please Pull [Integration](https://github.com/RainbowZephyr/scalable-app/tree/integration) to get started

### To be able to run please download the following
For Linux & Unix
1. Message Queue [RabbitMQ](https://www.rabbitmq.com/releases/rabbitmq-server/v3.6.9/rabbitmq-server-generic-unix-3.6.9.tar.xz).
2. Load Balancer [Nginx-Clojure](https://nginx-clojure.github.io/quickstart.html). figure it out till I get a docker image running.
3. To Be Able To Run Search **SEARCH TEAM ONLY** , Download [Titan](http://s3.thinkaurelius.com/downloads/titan/titan-1.0.0-hadoop1.zip).

#### Run Nginx using the configuration file [Nginx.Config](https://github.com/RainbowZephyr/scalable-app/blob/integration/loadbalancer/nginx.edited).
`sudo nginx` & to stop it `sudo nginx -s stop`
##### make sure that the config file is edited to match your machine
so edit the following.
1. at line 2: it should point to ngx_http_clojure_module on your device.
2. at line 19: it should point to jvm on your device.
3. at line 20: this points to the target folder where loadbalancer .classes are stored `scalable-app/loadbalancer/target/classes/` ,
and the libraries installed by maven on your device separated by `:`
#### NOTE: paths needs to be absolute 
#### libraries needed in the config file (these are the folders you need to add to the config file).
1. com/squareup/javapoet/1.5.1
2. net/openhft/chronicle-values/1.5.5/
3. net/openhft/chronicle-bytes/1.7.34/
4. net/openhft/chronicle-core/1.7.15/
5. net/openhft/chronicle-algorithms/1.1.8/
6. net/openhft/chronicle-map/3.13.0/
7. io/netty/netty-all/4.1.9.Final/
8. nginx-clojure/nginx-clojure/0.4.4/
9. com/rabbitmq/amqp-client/4.1.0/
10. org/slf4j/slf4j-api/1.7.25/
11. com/google/code/gson/gson/2.8.0/
12. commons-lang/commons-lang/2.6/

### I will try to put a docker image that includes all that (but no promises)

#### Run RabbitMQ by doing the following
Inside the rabbitMQ archive Downloaded (from a terminal)
`cd sbin`
`./rabbitmq-server start` to start the server


#### For ** Search Team ** , To run TitanDB do the following
Inside the titan archive downloaded (from a terminal)
`cd bin`
`./titan.sh` this starts titan with the default cassandra db & elasticsearch provided with titan in the archive you downloaded. (check link above)

***

## Testing

Each sub app has a separate folder, to run an app make sure that all the above are running first
To run an app, run the main method in the main class in the default package.
#### Running Instance of Each App
Example to run one instance of user app
1. Compile Nginx.java & reload nginx service by running the command `sudo nginx -s reload` from a terminal.
2. Run main.main (main method inside main class of the app).
3. Send a post request using curl, Insomnia or postman, the jsons are included within each folder under JSONs directory
4. By default the controller is waiting for AdminRequests on port 4001, so send AdminRequests on `http://127.0.0.1:4001/`
5. By default the load balancer is waiting for requests on Port 80, so send ApplicationRequests on `http://127.0.0.1:80/java`
#### Running Multiple Instances of Each App

Example to run two instances of user app.
1. Add the instance name in [apps_instances](https://github.com/RainbowZephyr/scalable-app/blob/integration/loadbalancer/config/apps_instances.properties)
2. Add the instance location & port where it should be listening [APP_CONFIG](https://github.com/RainbowZephyr/scalable-app/blob/integration/controller/config/APP_CONFIG_FILE.config)  #### NOTE: for each instance added 2 queues are created, for example adding `wall5` create `wall5_InboundQueue` & `wall5_OutboundQueue`
3. Change the message queues name to match the new one in [message_queues](https://github.com/RainbowZephyr/scalable-app/blob/integration/userapp/config/message_queues.properties) #### NOTE: they must match the following instanceName`_InboundQueue` for the ConsumerQueue & instanceName`_OutboundQueue` for the ProducerQueue
4. Compile Nginx.java (to include the newInstance) & reload nginx service by running the command `sudo nginx -s reload` from a terminal.
5. inside main method [main.java](https://github.com/RainbowZephyr/scalable-app/blob/integration/messageapp/src/main/java/main.java) change `Constants.setAppId("user1");` to `Constants.setAppId("user2");`
6. Run main.main (main method inside main class of the app) to get the new instance running.
7. Send a post request using curl, Insomnia or postman, the jsons are included within each folder under JSONs directory
8. By default the controller is waiting for AdminRequests on port 4001, so send AdminRequests on `http://127.0.0.1:4001/`
9. By default the load balancer is waiting for requests on Port 80, so send ApplicationRequests on `http://127.0.0.1:80/java`
