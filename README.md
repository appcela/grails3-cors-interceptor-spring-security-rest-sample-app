# grails3-cors-interceptor-spring-security-rest-sample-app
Sample Grails app to demo how to use the [grails3-cors-interceptor](https://github.com/appcela/grails3-cors-interceptor) with [Spring Security Core](https://grails-plugins.github.io/grails-spring-security-core/v3/index.html) or [Spring Security REST plugin](http://alvarosanchez.github.io/grails-spring-security-rest/2.0.0.M2/docs/index.html).

- [Working with Spring Security Core Plugin](#working-with-spring-security-core-plugin)
- [Working with Spring Security REST Plugin](#working-with-spring-security-rest-plugin)

## Working with Spring Security Core Plugin

### 1. Add Plugin Dependency

*build.gradle*

```
    compile "org.grails.plugins:spring-security-core:3.0.4"
    compile "org.grails.plugins:grails3-cors-interceptor:1.0.0"
```

### 2. Add HTTP OPTIONS Method URL Mapping 

To support the preflight CORS request with HTTP OPTIONS method, url mappings for OPTIONS method must be added explicitly.

*UrlMappings.groovy*

```
        "/api/authors"(resources: 'author')
        "/api/authors/$id?"(controller:'author', method: 'OPTIONS') // explicitly map OPTIONS method to "author" REST controller
        "/api/books"(resources: 'book')
        "/api/books/$id?"(controller:'book', method: 'OPTIONS') // explicitly map OPTIONS method to "book" REST controller

```

### 3. Configuring Request Mappings to Secure URLs

#### 3.1 Disable Pessimistic Lockdown

By default, Spring Security Core uses [pessmimistic lockdown](https://grails-plugins.github.io/grails-spring-security-core/v3/index.html#pessimistic-lockdown), which denies access to all URLs 
that do not have an applicable URL <==> Role request mapping. As a result, All preflight CORS 'OPTIONS' requests will be denied.

Add the following settings to disable this behavior to allow 'OPTIONS' requests to go through.

```
// application.groovy

grails.plugin.springsecurity.rejectIfNoRule = false
grails.plugin.springsecurity.fii.rejectPublicInvocations = false
```

And with this new optimistic approach, any un-mapped URLs will be available without protection. So we should protect the un-mapped URLs
with the following static rules,

```
// application.groovy

  // EDIT: block all other URL access
	[pattern: '/**', access: ['denyAll'], httpMethod: 'GET'],
	[pattern: '/**', access: ['denyAll'], httpMethod: 'POST'],
	[pattern: '/**', access: ['denyAll'], httpMethod: 'PUT'],
	[pattern: '/**', access: ['denyAll'], httpMethod: 'DELETE']
```

Here's what your final mappings should look like.

*application.groovy*

```
// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'com.appcela.myrestapp.security.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'com.appcela.myrestapp.security.UserRole'
grails.plugin.springsecurity.authority.className = 'com.appcela.myrestapp.security.Role'
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
	[pattern: '/',               access: ['permitAll']],
	[pattern: '/500',            access: ['permitAll']],
	[pattern: '/404',            access: ['permitAll']],
	[pattern: '/error',          access: ['permitAll']],
	[pattern: '/index',          access: ['permitAll']],
	[pattern: '/index.gsp',      access: ['permitAll']],
	[pattern: '/shutdown',       access: ['permitAll']],
	[pattern: '/assets/**',      access: ['permitAll']],
	[pattern: '/**/js/**',       access: ['permitAll']],
	[pattern: '/**/css/**',      access: ['permitAll']],
	[pattern: '/**/images/**',   access: ['permitAll']],
	[pattern: '/**/favicon.ico', access: ['permitAll']],
	// EDIT: block all other URL access
	[pattern: '/**', access: ['denyAll'], httpMethod: 'GET'],
	[pattern: '/**', access: ['denyAll'], httpMethod: 'POST'],
	[pattern: '/**', access: ['denyAll'], httpMethod: 'PUT'],
	[pattern: '/**', access: ['denyAll'], httpMethod: 'DELETE']
]

grails.plugin.springsecurity.filterChain.chainMap = [
	[pattern: '/assets/**',      filters: 'none'],
	[pattern: '/**/js/**',       filters: 'none'],
	[pattern: '/**/css/**',      filters: 'none'],
	[pattern: '/**/images/**',   filters: 'none'],
	[pattern: '/**/favicon.ico', filters: 'none'],
	[pattern: '/**',             filters: 'JOINED_FILTERS']
]

// EDIT: Optimistic approach (restrict access by URL only) to allow 'OPTIONS' access for CORS
grails.plugin.springsecurity.rejectIfNoRule = false
grails.plugin.springsecurity.fii.rejectPublicInvocations = false
```

#### 3.2 Define Secured Annotations

The [@Secured annotation](https://grails-plugins.github.io/grails-spring-security-core/v3/index.html#securedAnnotations) by default applies to all HTTP methods including 'OPTIONS'. This means the preflight CORS 'OPTIONS' request is secured and requires authentication and authorization to access.

*Secure Controller at Class Level Example*

```
@Secured('ROLE_ADMIN')
class SecureAnnotatedController {

   def index() {
      render 'you have ROLE_ADMIN'
   }
   ...
```   

*Secure Controller at Method Level Example*

```
class SecureAnnotatedController {

   @Secured('ROLE_ADMIN')
   def index() {
      render 'you have ROLE_ADMIN'
   }
   ...
```   

Since CORS preflight 'OPTIONS' requests from the browser will not include any authentication headers, we'll have to open up 'OPTIONS' access by explicitly define the secured annotions with the exact HTTP methods required. 

*Secure Controller with Required Http Methods*

```
@Transactional(readOnly = true)
class AuthorController {

    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    @Secured(value=['ROLE_API_CLIENT'], httpMethod='GET')
    def index(Integer max) {
        ...
    }

    @Secured(value=['ROLE_API_CLIENT'], httpMethod='GET')
    def show(Author author) {
        ...
    }
    
    @Transactional
    @Secured(value=['ROLE_API_CLIENT'], httpMethod='PUT')
    def update(Author author) {
        ...
    }
    
    @Transactional
    @Secured(value=['ROLE_API_CLIENT'], httpMethod='DELETE')
    def delete(Author author) {
        ...
    }
    ...
}
```

That's all, you should be all set now.


## Working with Spring Security REST Plugin

The Spring Security REST plugin is based on Spring Security Core plugin, so the required settings to get grails3-cores-interceptor working are basically the same with just few differences. 

### 1. Add Plugin Dependency*

*build.gradle*

```
    compile "org.grails.plugins:spring-security-core:3.0.4"
    compile "org.grails.plugins:spring-security-rest:2.0.0.M2"
    compile "org.grails.plugins:grails3-cors-interceptor:1.0.0"
```

### 2. Add HTTP OPTIONS Method URL Mapping 

To support the preflight CORS request with HTTP OPTIONS method, url mappings for OPTIONS method must be added explicitly.

*UrlMappings.groovy*

```
        "/api/authors"(resources: 'author')
        "/api/authors/$id?"(controller:'author', method: 'OPTIONS') // explicitly map OPTIONS method to "author" REST controller
        "/api/books"(resources: 'book')
        "/api/books/$id?"(controller:'book', method: 'OPTIONS') // explicitly map OPTIONS method to "book" REST controller

```

### 3. Configuring Request Mappings to Secure URLs

#### 3.1 Disable Pessimistic Lockdown

By default, Spring Security Core uses [pessmimistic lockdown](https://grails-plugins.github.io/grails-spring-security-core/v3/index.html#pessimistic-lockdown), which denies access to all URLs 
that do not have an applicable URL <==> Role request mapping. As a result, All preflight CORS 'OPTIONS' requests will be denied.

Add the following settings to disable this behavior to allow 'OPTIONS' requests to go through.

```
// application.groovy

grails.plugin.springsecurity.rejectIfNoRule = false
grails.plugin.springsecurity.fii.rejectPublicInvocations = false
```

And with this new optimistic approach, any un-mapped URLs will be available without protection. So we should protect the un-mapped URLs
with the following static rules,

```
// application.groovy

  // EDIT: block all other URL access
	[pattern: '/**', access: ['denyAll'], httpMethod: 'GET'],
	[pattern: '/**', access: ['denyAll'], httpMethod: 'POST'],
	[pattern: '/**', access: ['denyAll'], httpMethod: 'PUT'],
	[pattern: '/**', access: ['denyAll'], httpMethod: 'DELETE']
```

#### 3.2 Plugin Configuration*

Configure the Spring Security filter chains to support stateless, token based access. See [Spring Security REST - Plugin configuration](http://alvarosanchez.github.io/grails-spring-security-rest/2.0.0.M2/docs/index.html#_plugin_configuration) for more details.

```
// application.groovy

grails.plugin.springsecurity.filterChain.chainMap = [
	[pattern: '/assets/**',      filters: 'none'],
	[pattern: '/**/js/**',       filters: 'none'],
	[pattern: '/**/css/**',      filters: 'none'],
	[pattern: '/**/images/**',   filters: 'none'],
	[pattern: '/**/favicon.ico', filters: 'none'],
	//Stateless chain
	[
			pattern: '/api/**',
			filters: 'JOINED_FILTERS,-anonymousAuthenticationFilter,-exceptionTranslationFilter,-authenticationProcessingFilter,-securityContextPersistenceFilter,-rememberMeAuthenticationFilter'
	],

	//Traditional chain
	[
			pattern: '/**',
			filters: 'JOINED_FILTERS,-restTokenValidationFilter,-restExceptionTranslationFilter'
	]
]
```

Here's what your final mappings should look like.

*application.groovy*

```
// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'com.appcela.myrestapp.security.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'com.appcela.myrestapp.security.UserRole'
grails.plugin.springsecurity.authority.className = 'com.appcela.myrestapp.security.Role'
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
	[pattern: '/',               access: ['permitAll']],
	[pattern: '/500',            access: ['permitAll']],
	[pattern: '/404',            access: ['permitAll']],
	[pattern: '/error',          access: ['permitAll']],
	[pattern: '/index',          access: ['permitAll']],
	[pattern: '/index.gsp',      access: ['permitAll']],
	[pattern: '/shutdown',       access: ['permitAll']],
	[pattern: '/assets/**',      access: ['permitAll']],
	[pattern: '/**/js/**',       access: ['permitAll']],
	[pattern: '/**/css/**',      access: ['permitAll']],
	[pattern: '/**/images/**',   access: ['permitAll']],
	[pattern: '/**/favicon.ico', access: ['permitAll']],
	// EDIT: block all other URL access
	[pattern: '/**', access: ['denyAll'], httpMethod: 'GET'],
	[pattern: '/**', access: ['denyAll'], httpMethod: 'POST'],
	[pattern: '/**', access: ['denyAll'], httpMethod: 'PUT'],
	[pattern: '/**', access: ['denyAll'], httpMethod: 'DELETE']
]

grails.plugin.springsecurity.filterChain.chainMap = [
	[pattern: '/assets/**',      filters: 'none'],
	[pattern: '/**/js/**',       filters: 'none'],
	[pattern: '/**/css/**',      filters: 'none'],
	[pattern: '/**/images/**',   filters: 'none'],
	[pattern: '/**/favicon.ico', filters: 'none'],
	//Stateless chain
	[
			pattern: '/api/**',
			filters: 'JOINED_FILTERS,-anonymousAuthenticationFilter,-exceptionTranslationFilter,-authenticationProcessingFilter,-securityContextPersistenceFilter,-rememberMeAuthenticationFilter'
	],

	//Traditional chain
	[
			pattern: '/**',
			filters: 'JOINED_FILTERS,-restTokenValidationFilter,-restExceptionTranslationFilter'
	]
]

// EDIT: Optimistic approach (restrict access by URL only) to allow 'OPTIONS' access for CORS
grails.plugin.springsecurity.rejectIfNoRule = false
grails.plugin.springsecurity.fii.rejectPublicInvocations = false
```

#### 3.3 Define Secured Annotations

The [@Secured annotation](https://grails-plugins.github.io/grails-spring-security-core/v3/index.html#securedAnnotations) by default applies to all HTTP methods including 'OPTIONS'. This means the preflight CORS 'OPTIONS' request is secured and requires authentication and authorization to access.

*Secure Controller at Class Level Example*

```
@Secured('ROLE_ADMIN')
class SecureAnnotatedController {

   def index() {
      render 'you have ROLE_ADMIN'
   }
   ...
```   

*Secure Controller at Method Level Example*

```
class SecureAnnotatedController {

   @Secured('ROLE_ADMIN')
   def index() {
      render 'you have ROLE_ADMIN'
   }
   ...
```   

Since CORS preflight 'OPTIONS' requests from the browser will not include any authentication headers, we'll have to open up 'OPTIONS' access by explicitly define the secured annotions with the exact HTTP methods required. 

*Secure Controller with Required Http Methods*

```
@Transactional(readOnly = true)
class AuthorController {

    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    @Secured(value=['ROLE_API_CLIENT'], httpMethod='GET')
    def index(Integer max) {
        ...
    }

    @Secured(value=['ROLE_API_CLIENT'], httpMethod='GET')
    def show(Author author) {
        ...
    }
    
    @Transactional
    @Secured(value=['ROLE_API_CLIENT'], httpMethod='PUT')
    def update(Author author) {
        ...
    }
    
    @Transactional
    @Secured(value=['ROLE_API_CLIENT'], httpMethod='DELETE')
    def delete(Author author) {
        ...
    }
    ...
}
```

That's all, now you should be all set.


