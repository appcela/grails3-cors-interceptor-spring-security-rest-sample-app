package com.appcela.myrestapp.domain

import grails.plugin.springsecurity.annotation.Secured

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class AuthorController {

    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    @Secured(value=['ROLE_API_CLIENT'], httpMethod='GET')
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Author.list(params), model:[authorCount: Author.count()]
    }

    @Secured(value=['ROLE_API_CLIENT'], httpMethod='GET')
    def show(Author author) {
        respond author
    }

    @Transactional
    @Secured(value=['ROLE_API_CLIENT'], httpMethod='POST')
    def save(Author author) {
        if (author == null) {
            transactionStatus.setRollbackOnly()
            render status: NOT_FOUND
            return
        }

        if (author.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond author.errors, view:'create'
            return
        }

        author.save flush:true

        respond author, [status: CREATED, view:"show"]
    }

    @Transactional
    @Secured(value=['ROLE_API_CLIENT'], httpMethod='PUT')
    def update(Author author) {
        if (author == null) {
            transactionStatus.setRollbackOnly()
            render status: NOT_FOUND
            return
        }

        if (author.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond author.errors, view:'edit'
            return
        }

        author.save flush:true

        respond author, [status: OK, view:"show"]
    }

    @Transactional
    @Secured(value=['ROLE_API_CLIENT'], httpMethod='DELETE')
    def delete(Author author) {

        if (author == null) {
            transactionStatus.setRollbackOnly()
            render status: NOT_FOUND
            return
        }

        author.delete flush:true

        render status: NO_CONTENT
    }
}
