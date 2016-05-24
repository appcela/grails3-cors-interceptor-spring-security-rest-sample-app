package myrestapp

class UrlMappings {

    static mappings = {

        "/"(controller: 'application', action:'index')
        "/500"(view: '/error')
        "/404"(view: '/notFound')

        "/api/authors"(resources: 'author')
        "/api/authors/$id?"(controller:'author', method: 'OPTIONS') // explicitly map OPTIONS method to "author" REST controller
        "/api/books"(resources: 'book')
        "/api/books/$id?"(controller:'book', method: 'OPTIONS') // explicitly map OPTIONS method to "book" REST controller
    }
}
