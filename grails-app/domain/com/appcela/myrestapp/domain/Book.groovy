package com.appcela.myrestapp.domain


import grails.rest.*

class Book {
    String name
    String description

    static belongsTo = Author
    static hasMany = [authors: Author]
}