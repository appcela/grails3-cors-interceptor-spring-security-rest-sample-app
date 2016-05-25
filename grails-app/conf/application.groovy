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
	// block all other URL access
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
	[pattern: '/api/login',      filters: 'securityCorsFilter,restAuthenticationFilter'],
	//Stateless chain
	[
			pattern: '/api/**',
			filters: 'JOINED_FILTERS,-securityCorsFilter,-anonymousAuthenticationFilter,-exceptionTranslationFilter,-authenticationProcessingFilter,-securityContextPersistenceFilter,-rememberMeAuthenticationFilter'
	],

	//Traditional chain
	[
			pattern: '/**',
			filters: 'JOINED_FILTERS,-securityCorsFilter,-restTokenValidationFilter,-restExceptionTranslationFilter'
	]
]

// Optimistic approach (restrict access by URL only) to allow 'OPTIONS' access for CORS
grails.plugin.springsecurity.rejectIfNoRule = false
grails.plugin.springsecurity.fii.rejectPublicInvocations = false
