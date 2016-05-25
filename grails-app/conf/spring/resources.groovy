import grails3.cors.interceptor.SpringSecurityCorsFilter

// Place your Spring DSL code here
beans = {
    securityCorsFilter(SpringSecurityCorsFilter)
}
