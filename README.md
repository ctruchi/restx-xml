restx-xml
=========
Simple restx-module to handle mime "text/xml" and "application/xml".

Example :

```java
@Component
@RestxResource
public class HelloWorldResource {

    @POST("/hello")
    @Produces("text/xml; charset=utf-8")
    public HelloWorld sayHello() {
        return new HelloWorld();
    }

    public static class HelloWorld {
        private String hello = "World!";

        public String getHello() {
            return hello;
        }

        public void setHello(String hello) {
            this.hello = hello;
        }
    }
```
