# Context and security extension for RestEasy REST API 
Basic request context and security filtering implementation to be extended and utilized with any RestEasy REST API.

Supports:
* transformation of request into java object 
* implements role based REST API security check

## Setup
```xml
 <dependency>      
      <groupId>com.zandero</groupId>      
      <artifactId>rest.context</artifactId>      
      <version>1.0</version>      
 </dependency>
 ```
 
## Request context

### Step 1 - create authorization / request context
By extending the `BaseRequestContext` we 
 
```java
@RequestScoped
public class MyRequestContext extends BaseRequestContext {

    /**
     * use request to find out the context (is user logged in?)
     * this might be a request header or query string parameter ...
     */
    @Inject    
    public TestRequestContext(HttpServletRequest servletRequest) {
    
        super(servletRequest);
    
        // create session object from request
        session = resolveSession(servletRequest);
    }
    
    /**
    * @return user making the request or null if not known
    */
    @Override
    public Principal getUserPrincipal() {
        return () -> session.getUser();
    }
    
    /**
    * Checks if user is in role
    * @param role as provided in @RolesAllowed(role) annotation
    * @return true if user is in role, false if not
    */
    @Override
    public boolean isUserInRole(String role) {
        return session.isUserInRole(role);
    }
    
    /**
     * @return true if call is secure, false if not
     */
    @Override
    public boolean isSecure() {
    
        return session != null;
    }
    
    @Override
    public String getAuthenticationScheme() {
    
        return session.getScheme();
    }
}
```

## Step 2 - annotate REST with roles
Once the request/security context is in place we 

```java
/**
* Only accessible if user is in given "User" role
*/
@GET
@RolesAllowed("User")
@Path("/private")
public String getUserInfo() {

    return null;
}
```

## Step 3 - provide request context into REST
```java
@Path("/api")
@Singleton
public class MyRestApi {

    private final Provider<MyRequestContext> ctxProvider;
    
    @Inject
    public TestRestApi(Provider<MyRequestContext> contextProvider) {
    
        ctxProvider = contextProvider;
    }
    
    @GET
    @RolesAllowed("User")
    @Path("/private")
    public String getUserInfo() {
    
        return ctxProvider.get().getPrincipal();
    }
   
```

[Additional info](https://github.com/zandero/rest.context/wiki/Home)