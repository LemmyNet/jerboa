# Contributing to Jerboa

<!-- TOC -->
* [Contributing to Jerboa](#contributing-to-jerboa)
  * [Ways to contribute](#ways-to-contribute)
  * [Application structure](#application-structure)
  * [Code contributions](#code-contributions)
    * [Kotlin](#kotlin)
    * [Code quality](#code-quality)
  * [Adding translations](#adding-translations)
<!-- TOC -->

## Ways to contribute
- Participate here and start answering questions.
- File GitHub issues for big reports from /c/Jerboa. Watch the project for duplicate reports and link them to the first report so devs can easily close dupes.
- File new bug reports for issues you find.
- Add missing translations
- Code contributions

## Application structure
- Basic Modern Android Development (https://developer.android.com/series/mad-skills) tech stack (Compose, Navigation, Coroutines, AndroidX)
- Guide to App Architecture (https://developer.android.com/topic/architecture), without domain layer. Basically, MVVM + Repositories for data access.
- Manual DI

## Code contributions
You can open Jerboa in AndroidStudio.

### Kotlin
This project is full Kotlin. Please do not write Java classes.

### Code quality
The code must be formatted to a common standard.

To check for violations
`./gradlew lintKotlin`
Or just run this to fix them
`./gradlew formatKotlin`

## Adding translations
You can find the translations in the `app/src/main/res/values-{locale}/strings.xml` file.
You can open it in android studio, right click and click open translations editor or you can directly edit the files.