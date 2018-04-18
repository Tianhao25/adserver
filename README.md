

# README

Some of students find it is hard to configure the original AdsServer project as it requires manual import Jars and some configuration settings. And some of you don't have web application development experience. So it may be hard for you to run the project. 

To solve this problem, I created a click-to-run version of our AdsServer. Basically, you **DON'T** need to set up **ANY** thing before you can run this server (only need to have a running Memcached). You even don't need a running MySQL server. I also included example ADs data and Campaign data into the project's _resources_. The only thing you need to do is to click RUN button in **Intellij** and you will have your running Ads Server.

You can still make changes to AdsEngine and AdsSelectors for furthur changes if you need.

This server is developed based on **Spring-Boot** and **Spring-Data**. You can look into related resources if you are interested.

## Import Project into Intellij 

1. ![Screen Shot 2018-04-17 at 9.30.20 PM](/Users/ross.wang/Desktop/Screen Shot 2018-04-17 at 9.30.20 PM.png)
2. ![Screen Shot 2018-04-17 at 9.31.38 PM](/Users/ross.wang/Desktop/Screen Shot 2018-04-17 at 9.31.38 PM.png)
3. ![Screen Shot 2018-04-17 at 9.32.49 PM](/Users/ross.wang/Desktop/Screen Shot 2018-04-17 at 9.32.49 PM.png)
4. Continue Clicking **Next** until you finish. Then, wait for the code got imported. In the mean time, make sure the **Memcached** is running.
5. Now, you should be able to see this little green **RUN** button in AdApp.java line29 : ![Screen Shot 2018-04-17 at 9.41.02 PM](/Users/ross.wang/Desktop/Screen Shot 2018-04-17 at 9.41.02 PM.png) 
6. If your Memcached is running locally on port 11211, just click the green button and run the server. If not, go to application.yml to configure the memcache.host and memcache.port.
7. After you click the run button, you should be able to see the server is starting up: ![Screen Shot 2018-04-17 at 9.43.57 PM](/Users/ross.wang/Desktop/Screen Shot 2018-04-17 at 9.43.57 PM.png)
8. When you see a log saying _server started_, the server is already running on port 12345! ![Screen Shot 2018-04-17 at 9.45.39 PM](/Users/ross.wang/Desktop/Screen Shot 2018-04-17 at 9.45.39 PM.png)
9. Now, use the browser to hit the server like: http://localhost:12345/SearchAds?q=life, you should be able to see ads showing! ![Screen Shot 2018-04-17 at 9.46.58 PM](/Users/ross.wang/Desktop/Screen Shot 2018-04-17 at 9.46.58 PM.png)

## Configurations

You can find all configurations in application.yml

```yaml
# server run on which port
server: 
  port: 12345

spring.h2.console.enabled: true
spring.h2.console.path: /h2

spring.datasource.url: jdbc:h2:file:~/test
spring.datasource.driver-class-name: org.h2.Driver
spring.datasource.username: sa

# Memcache configures
memcached.host: localhost
memcached.port: 11211
```

## H2 Database

I am using H2 in memory database to replace MySQL. H2 will start automatically with your server. You can login to http://localhost:12345/h2 to manage and see the data and tables inside.

