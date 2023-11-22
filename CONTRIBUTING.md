# Project Documentation

## Backend

### Procedural Style and Dependency Injection
The backend is developed in a procedural style, primarily due to the natural alignment with the Ktor framework. We haven't implemented dependency injection yet, as it wasn't necessary for our current needs. We're open to evolving towards an object-oriented approach and incorporating dependency injection if it benefits the project. Feedback and contributions in this area are welcome.

### Kotlin Multi-platform - Backend Targets
Our backend includes JVM and native targets. The native target is an experimental aspect of our project. Contributors can suggest changes or even recommend dropping this target if it aligns better with the project's direction.

## Frontend

### ViewModel-based Navigation
In the frontend, navigation is handled by ViewModels, specifically through the MainViewModel which manages the content. Other ViewModels signal the MainViewModel to switch content, which is then rendered by the Composables. This approach, while simpler than using navigation frameworks, has served our needs effectively. We're interested in your experiences and opinions on this method.

### Dependency Injection in Frontend
Currently, the frontend does not use a formal dependency injection framework. Instead, we have a global object managing singletons and their lazy initialization, similar to a basic form of dependency injection. Implementing a more structured dependency injection framework could be beneficial, given the frontend's complexity, but this hasn't been addressed yet. Contributions in this area are especially welcome.

### Kotlin Multi-platform - Frontend Targets
Currently, the frontend targets Android, iOS, and JVM desktop platforms. We are also exploring the possibility of expanding to browser support through WebAssembly in the future, pending the stability and compatibility of the necessary libraries.

## Contributing
We welcome contributions from anyone interested in improving the project. Whether it's enhancing existing features, suggesting new ones, or refactoring the code, your input is valuable. Feel free to fork the repository, make changes, and submit pull requests. If you have any questions or ideas, don't hesitate to open an issue or contact us.

### Tasks of your own fancy
You are missing some feature? You don't like the style? You want to add a language? Feel free to do that.

### Issues
There are some open issues that you can work on if you feel so. They are very different in size and complexity. Feel free to ask me, should you have any questions.

### TASK comments
There are some TASK comments littered in the code, for things that aren't really an issue but could be fixed at some point. 
