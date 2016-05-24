package com.appcela.myrestapp.domain

import grails.plugin.springsecurity.annotation.Secured

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class BookController {

    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    @Secured(value=['ROLE_API_CLIENT'], httpMethod='GET')
    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Book.list(params), model:[bookCount: Book.count()]
    }

    @Secured(value=['ROLE_API_CLIENT'], httpMethod='GET')
    def show(Book book) {
        respond book
    }

    @Transactional
    @Secured(value=['ROLE_API_CLIENT'], httpMethod='POST')
    def save(Book book) {
        if (book == null) {
            transactionStatus.setRollbackOnly()
            render status: NOT_FOUND
            return
        }

        if (book.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond book.errors, view:'create'
            return
        }

        book.save flush:true

        respond book, [status: CREATED, view:"show"]
    }

    @Transactional
    @Secured(value=['ROLE_API_CLIENT'], httpMethod='PUT')
    def update(Book book) {
        if (book == null) {
            transactionStatus.setRollbackOnly()
            render status: NOT_FOUND
            return
        }

        if (book.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond book.errors, view:'edit'
            return
        }

        book.save flush:true

        respond book, [status: OK, view:"show"]
    }

    @Transactional
    @Secured(value=['ROLE_API_CLIENT'], httpMethod='DELETE')
    def delete(Book book) {

        if (book == null) {
            transactionStatus.setRollbackOnly()
            render status: NOT_FOUND
            return
        }

        book.delete flush:true

        render status: NO_CONTENT
    }
}
