# Context and security extension for RestEasy REST API 
Basic request context and security filtering implementation to be extended and utilized with any RestEasy REST API.

Supports:
* transformation of request into Java object 
* implements role based REST API security check

## Setup
```xml
 <dependency>      
      <groupId>com.zandero</groupId>      
      <artifactId>rest.context</artifactId>      
      <version>1.2</version>      
 </dependency>
 ```
 
## Request context

### Step 1 - create authorization / request context
By extending the `BaseRequestContext` we create our own Request context.  
We can then extract data provided in the request (for instance session id in a cookie) and fill up all necessary fields.  
The Request context is **@RequestScoped** - unique for each request hitting the REST.
 
```java
@RequestScoped
public class MyRequestContext extends BaseRequestContext {

    private final Session session; // our custom session object

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

#### Bind correct @RequestScope 
Make sure you your @RequestScope properly bound.  

Using either: 
1. an existing plugin: `import org.jboss.resteasy.plugins.guice.RequestScoped;` and bind new org.jboss.resteasy.plugins.guice.ext.RequestScopeModule();
1. or implement `GuiceServletContextListener` and bind it

### Step 2 - bind Authorization filter and request context

public class MyRestModule extends AbstractModule {

```java
	@Override
	protected void configure() {

		bind(AuthorizationFilter.class);
		bind(RequestContext.class).to(MyRequestContext.class);
	}
```

### Step 3 - annotate REST with roles
Once the request/security context is in place we can annotate the REST with `@RolesAllowed` annotation.
The annotated `role` is provided in the `public boolean isUserInRole(String role)` context call, when checking access. 

If the `public boolean isUserInRole(String role)` returns **true** the REST is executed.   
In case **false** is returned a **403 FORBIDDEN** response is returned.

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

### Step 4 - provide request context into REST
We can access and utilize the Request context if needed.  

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
    
        return ctxProvider.get().getPrincipal(); // resolved for each request ... is unique for request
    }
   
```

[Additional info](https://github.com/zandero/rest.context/wiki/Home)