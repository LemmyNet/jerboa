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
  * [Updating the instance list](#updating-the-instance-list)
  * [Generate compose compiler metrics](#generate-compose-compiler-metrics)
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

## Updating the instance list

There is a custom gradle task that generates all the lemmy instances that this app directly supports.
It updates the lemmy instances list in DefaultInstances.kt and the AndroidManifest.
It uses the fediverse api and filters on the monthly users.
You can run it by doing

```shell
 ./gradlew app:updateInstances --no-configuration-cache
```

## Generate compose compiler metrics

You can generate the compose compiler metrics by executing the following gradle task.

```shell
./gradlew assembleRelease --rerun-tasks -P com.jerboa.enableComposeCompilerReports=true
```

Then you will find the metrics in `app/build/compose_metrics` directory.
See [this link for more information on these metrics](https://github.com/androidx/androidx/blob/androidx-main/compose/compiler/design/compiler-metrics.md)
