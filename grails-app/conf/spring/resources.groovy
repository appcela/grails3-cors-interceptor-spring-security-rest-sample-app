import grails3.cors.interceptor.SpringSecurityCorsFilter
import org.springframework.boot.context.embedded.FilterRegistrationBean

// Place your Spring DSL code here
beans = {
    securityCorsFilter(SpringSecurityCorsFilter)

    myFilterDeregistrationBean(FilterRegistrationBean) {
        filter = ref('securityCorsFilter')
        enabled = false
    }
}
