# grails3-cors-interceptor-spring-security-rest-sample-app
Sample Grails app to demo how to use the grails3-cors-interceptor with Spring Security Core or Spring Security REST plugin.

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


// EDIT: Optimistic approach (restrict access by URL only) to allow 'OPTIONS' access for CORS
grails.plugin.springsecurity.rejectIfNoRule = false
grails.plugin.springsecurity.fii.rejectPublicInvocations = false
```

...

## Working with Spring Security REST Plugin

...

```
// build.gradle

    compile "org.grails.plugins:spring-security-core:3.0.4"
    compile "org.grails.plugins:spring-security-rest:2.0.0.M2"
    compile "org.grails.plugins:grails3-cors-interceptor:1.0.0"
```
