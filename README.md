ING Assignment:

Background
Your task is to create a simple Android app that allows users to search for recipes and view 
details about each recipe. The app should use the MVVM architecture pattern and be 
structured with clean separation between Model, Domain, Data, and UI layers.

Introduction:

So I decided to to try for a nice challenge and potential learnings, but still demonstrating a
Android focused project, with MvvM,  a Kotlin multiplatform project.
I tried to leverage the robustness of Kotlin Multiplatform to share business logic across iOS and Android.
My choice of technologies were driven by the need for a scalable, maintainable, and testable codebase.

A fun page i found to really learn about all the new libraries available for KMM , i came across this,
https://github.com/terrakok/kmp-awesome

Architecture Components:

Kotlin Multiplatform: Used to share code between iOS and Android platforms. This includes domain logic, data models, and network calls.
Ktor: Selected for networking due to its lightweight nature and the ability to seamlessly integrate with Kotlin Multiplatform.
StateFlow: Chosen for state management within our ViewModel to efficiently handle state updates with the benefits of flow and coroutines.
Model-View-ViewModel (MVVM): Implemented as the design pattern for the presentation layer to promote separation of concerns and easier unit testing.
SQLDelight: A Kotlin Multiplatform library for managing local databases. It compiles SQL to native code, allowing for type-safe SQL queries with the convenience of object mapping.

Design Decisions:

Common Module: I encapsulated shared logic within a common module, which includes the data repository, use cases, and shared utilities.

Platform-Specific Modules: I created platform-specific modules to handle UI rendering and platform-specific APIs.

Dependency Injection: I used Koin for dependency injection within the common module to provide decoupling and easier mock implementation for testing.

Networking Layer: Within the Ktor client, i implemented a robust error handling and retry mechanism to handle transient network issues.

Shared Database Logic: I leveraged SQLDelight in the common module to handle local data storage and retrieval, allowing for a consistent database schema and queries across platforms.

Platform-Specific Database Drivers: SQLDelight requires platform-specific drivers, so i implemented a SQL driver for Android and a Native driver for iOS, both configured in their respective platform-specific modules.

Assumptions:
I used a RESTful API with JSON payloads for network communications.
I assumed that both Android and iOS platforms have similar feature sets and user interface requirements.

Potential Improvements
Error Handling: I can improve error handling by implementing a more sophisticated error mapping strategy to provide more informative feedback to the user.
Refinement of State Management: While StateFlow works well, integrating a more advanced state management library like MVIKotlin could provide additional benefits in terms of predictability and debugging.
Strings: for speed of development , i did hard code strings, but i do know to use relevant string files for both platforms.
Compose views: make more components re-usable for a bigger project. Seperate into smaller composable classes.
Tests: Write more comprehensive test coverage.

Conclusion:
The architecture designed for my application provides a solid foundation for a scalable and maintainable mobile application across multiple platforms(iOS and Android). By using Kotlin Multiplatform, it can reduce the overhead of maintaining separate codebases and ensure consistency in business logic across platforms. 
The application of MVVM facilitates testing and separation of concerns, particularly in conjunction with StateFlow for reactive state management. Continuous iteration and refinement will be crucial to help the application evolve, and if more time was spent on development.

Instructions:
Prerequisites:
Before you start, ensure you have the following installed:

Git
JDK (Java Development Kit)
Android Studio (latest version recommended as it provides the best support for KMM)
Xcode (if you are on macOS and plan to run the iOS application)

Steps:
Clone the Repository:

git clone https://github.com/aicameron10/INGredients.git

Open the Project:

Open Android Studio and select "Open an existing project". Navigate to the directory where you cloned the project and select the project directory.

Sync Gradle:

After opening the project, Android Studio will attempt to sync the project with Gradle files. If it doesn't start automatically, you can trigger a Gradle sync manually by clicking on the "Sync Project with Gradle Files" button in the toolbar.

Check Configuration:

Ensure that the Kotlin plugin is up-to-date. You can check this by going to Android Studio > Preferences > Plugins and looking for the Kotlin plugin.

Run the Android Application:

Make sure you have an Android emulator set up or a physical Android device connected.
Select the Android application module in the configuration dropdown menu in the toolbar.

Run the iOS Application:
(Skip this step if you're not on macOS or not interested in running the iOS app.)

Open the iosApp directory within the KMM project in Xcode.
Select an iOS simulator or a connected iOS device.
Press Cmd + R to build and run the iOS application.
Troubleshooting:

Build Failures:
For any build errors, check the error log in Android Studio or Xcode and address the issues. They might be due to version incompatibilities or missing SDK components.

Gradle Wrapper:
If there are problems with the Gradle wrapper, you may need to check if the wrapper is included in the repository or if you need to adjust Gradle settings.

Design Inspiration:
Tasty, Cookpad, Recipe Keeper
