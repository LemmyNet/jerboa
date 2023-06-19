# Contributing to Jerboa

<!-- prettier-ignore-start -->

<!-- TOC -->
* [Contributing to Jerboa](#contributing-to-jerboa)
  * [Ways to contribute](#ways-to-contribute)
  * [Application structure](#application-structure)
  * [Code contributions](#code-contributions)
    * [Kotlin](#kotlin)
    * [Code quality](#code-quality)
  * [Adding translations](#adding-translations)
<!-- TOC -->

<!-- prettier-ignore-end -->

## Ways to contribute

- Participate here and start answering questions.
- File GitHub issues for bug reports from [/c/Jerboa](https://lemmy.ml/c/jerboa). Watch the project for duplicate reports and link them to the first report so devs can easily close dupes.
- File new bug reports for issues you find.
- Add missing translations
- Code contributions

## Application structure

- Basic [Modern Android Development](https://developer.android.com/series/mad-skills) tech stack (Compose, Navigation, Coroutines, AndroidX)
- Guide to [App Architecture](https://developer.android.com/topic/architecture), without domain layer. Basically, MVVM + Repositories for data access.
- Manual DI

## Code contributions

You can open Jerboa in AndroidStudio.

### Kotlin

This project is full Kotlin. Please do not write Java classes.

### Code quality

The code must be formatted to a [common standard](https://pinterest.github.io/ktlint/0.49.1/rules/standard/).

To check for violations

```shell
./gradlew lintKotlin`
```

Or just run this to fix them

```shell
./gradlew formatKotlin
```

Markdown and yaml files are formatted according to prettier.

You can install prettier either through the plugin, or globally using npm `npm install -g prettier`

To check for violations

```shell
prettier -c "*.md" "*.yml"`
```

To fix the violations

```shell
prettier --write "*.md" "*.yml"`
```

## Adding translations

You can find the translations in the `app/src/main/res/values-{locale}/strings.xml` file.
You can open it in android studio, right click and click open translations editor or you can
directly edit the files.
