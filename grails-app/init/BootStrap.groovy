import com.appcela.myrestapp.domain.Author
import com.appcela.myrestapp.domain.Book

class BootStrap {

    def init = { servletContext ->
        Author ivan = new Author(
                firstName: "Ivan",
                lastName: "Chang"
        )
        Book gameOfThrones = new Book(
                name: 'Game of Thrones',
                description: 'Long long time ago...',
                authors: [ivan]
        )
        ivan.addToBooks(gameOfThrones)
        ivan.save()
    }
    def destroy = {
    }
}
