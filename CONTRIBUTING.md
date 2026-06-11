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
  * [Theming guide](#theming-guide)
  * [Updating the instance list](#updating-the-instance-list)
  * [Generate compose compiler metrics](#generate-compose-compiler-metrics)
  * [Testing migrations](#testing-migrations)
  * [Updating the Baseline profiles](#updating-the-baseline-profiles)
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

You can open Jerboa in AndroidStudio, version 2022.3.1 or later (Giraffe).

Use Java 11+, preferably Java 17

### Kotlin

This project is full Kotlin. Please do not write Java classes.

### Code quality

The code must be formatted to a [common standard](https://pinterest.github.io/ktlint/0.49.1/rules/standard/).

To check for violations

```shell
./gradlew lintKotlin
```

Or just run this to fix them

```shell
./gradlew formatKotlin
```

Markdown and yaml files are formatted according to prettier.

You can install prettier either through the plugin, or globally using npm `npm install -g prettier`

To check for violations

```shell
prettier -c "*.md" "*.yml"
```

To fix the violations

```shell
prettier --write "*.md" "*.yml"
```

## Adding translations

Translations via [weblate](https://weblate.join-lemmy.org/projects/lemmy/jerboa/):

[![Translation status](https://weblate.join-lemmy.org/widget/lemmy/jerboa/multi-auto.svg)](https://weblate.join-lemmy.org/projects/lemmy/jerboa/)

## Theming guide

To add a custom theme:

- Use google's [Material 3 custom theme generator](https://m3.material.io/theme-builder#/custom), and save your `Theme.kt` file.
- Copy the theme into `Color.kt` like the others.
- Add the theme to the `ThemeColor` enum in `Types.kt`
- Add a translation string in `strings.xml` for your theme name.
- Add the theme to the `colorPairs` in `Theme.kt`

## Updating the instance list

There is a custom gradle task that generates all the lemmy instances that this app directly supports.
It updates the lemmy instances list in DefaultInstances.kt and the AndroidManifest.
It uses the fediverse api and filters on the monthly users.
You can run it by doing

```shell
 ./gradlew app:updateInstances
```

## Generate compose compiler metrics

You can generate the compose compiler metrics by executing the following gradle task.

```shell
./gradlew assembleRelease --rerun-tasks -P enableComposeCompilerReports=true
```

Then you will find the metrics in `app/build/compose_metrics` directory.
See [this link for more information on these metrics](https://github.com/androidx/androidx/blob/androidx-main/compose/compiler/design/compiler-metrics.md)

## Testing migrations

If you add a migration to the DB, test it with this gradle task

```shell
./gradlew app:connectAndroidTest
```

## Updating the Baseline profiles

You can generate the latest baseline profiles by executing the following gradle task.
It will generate the baseline profiles and the startup profiles.

The baseline profile code is located in the Benchmarks module.

Be warned that this task will take a long time to run (+30m) and can fail.

The variant for `app` needs to be set to `benchmarkRelease`.
There should be no device connected and it should start an emulator with the correct settings.

```shell
./gradlew :app:generateBaselineProfile
```

see https://developer.android.com/topic/performance/baselineprofiles/overview
