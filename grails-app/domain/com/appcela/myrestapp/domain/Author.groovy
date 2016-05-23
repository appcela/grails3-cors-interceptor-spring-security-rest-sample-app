package com.appcela.myrestapp.domain


import grails.rest.*

class Author {
    String firstName
    String lastName

    static hasMany = [books: Book]
}