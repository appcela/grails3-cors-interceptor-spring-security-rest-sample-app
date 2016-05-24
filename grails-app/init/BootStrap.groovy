import com.appcela.myrestapp.domain.Author
import com.appcela.myrestapp.domain.Book
import com.appcela.myrestapp.security.Role
import com.appcela.myrestapp.security.User
import com.appcela.myrestapp.security.UserRole

class BootStrap {

    def init = { servletContext ->
        Author georgeRRMartin = new Author(
                firstName: "George",
                lastName: "Martin"
        )
        Book gameOfThrones = new Book(
                name: 'A Game of Thrones',
                description: 'A Game of Thrones is the first novel in A Song of Ice and Fire, a series of high fantasy novels by American author George R. R. Martin. It was first published on August 6, 1996. ',
                authors: [georgeRRMartin]
        )
        georgeRRMartin.addToBooks(gameOfThrones)
        georgeRRMartin.save()

        Role roleRestAPIClient = new Role(
                authority: "ROLE_API_CLIENT"
        )
        roleRestAPIClient.save()

        User apiUser = new User(
                username: "api",
                password: "test123",
                enabled: true,
                accountLocked: false,
                accountExpired: false
        )
        apiUser.save()

        UserRole.create(apiUser, roleRestAPIClient, true)

    }
    def destroy = {
    }
}
