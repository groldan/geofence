# Read Me First
The following was discovered as part of building this project:

* The following dependencies are not known to work with Spring Native: 'Eureka Discovery Client, Cloud Bootstrap, Spring Configuration Processor, Spring Boot DevTools, Spring LDAP, Embedded LDAP Server, Cloud Bus'. As a result, your application may not work as expected.

# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.4.4/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.4.4/maven-plugin/reference/html/#build-image)
* [Spring Native Reference Guide](https://docs.spring.io/spring-native/docs/current/reference/htmlsingle/)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/2.4.4/reference/htmlsingle/#boot-features-jpa-and-spring-data)
* [Config Client Quick Start](https://docs.spring.io/spring-cloud-config/docs/current/reference/html/#_client_side_usage)
* [Eureka Discovery Client](https://docs.spring.io/spring-cloud-netflix/docs/current/reference/html/#service-discovery-eureka-clients)
* [Cloud Bootstrap](https://spring.io/projects/spring-cloud-commons)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.4.4/reference/htmlsingle/#boot-features-developing-web-applications)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/docs/2.4.4/reference/htmlsingle/#configuration-metadata-annotation-processor)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/2.4.4/reference/htmlsingle/#using-boot-devtools)
* [Spring Security](https://docs.spring.io/spring-boot/docs/2.4.4/reference/htmlsingle/#boot-features-security)
* [Spring LDAP](https://docs.spring.io/spring-boot/docs/2.4.4/reference/htmlsingle/#boot-features-ldap)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/2.4.4/reference/htmlsingle/#production-ready)
* [Embedded LDAP Server](https://docs.spring.io/spring-boot/docs/2.4.4/reference/html/boot-features-nosql.html#boot-features-ldap-embedded)

### Guides
The following guides illustrate how to use some features concretely:

* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Service Registration and Discovery with Eureka and Spring Cloud](https://spring.io/guides/gs/service-registration-and-discovery/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/bookmarks/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)
* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)

### Additional Links
These additional references should also help you:

* [Configure the Spring AOT Plugin](https://docs.spring.io/spring-native/docs/0.9.1/reference/htmlsingle/#spring-aot-maven)

## Spring Native

This project has been configured to let you generate a lightweight container running a native executable.
Docker should be installed and configured on your machine prior to creating the image, see [the Getting Started section of the reference guide](https://docs.spring.io/spring-native/docs/0.9.1/reference/htmlsingle/#getting-started-buildpacks).

To create the image, run the following goal:

```
$ ./mvnw spring-boot:build-image
```

Then, you can run the app like any other container:

```
$ docker run --rm -p 8080:8080 geofence-service:0.0.1-SNAPSHOT
```
