# Jerboa v0.0.37-alpha Release (2023-06-29)

## What's Changed

- Nav scoped view models by @nahwneeth in https://github.com/dessalines/jerboa/pull/817
- Fix downvote on post in postview doing a upvote by @MV-GH in https://github.com/dessalines/jerboa/pull/893
- Fix .zoomable functionality in ImageViewerDialog and add "swipe up to close" by @sockenklaus in https://github.com/dessalines/jerboa/pull/894
- Add blur option, fix non blurred community icons/banners by @MV-GH in https://github.com/dessalines/jerboa/pull/896
- Fix edit comment view text is being empty by @MV-GH in https://github.com/dessalines/jerboa/pull/897
- Update baseline profiles by @MV-GH in https://github.com/dessalines/jerboa/pull/906
- Change app display name by @Undearius in https://github.com/dessalines/jerboa/pull/904

## New Contributors

- @Undearius made their first contribution in https://github.com/dessalines/jerboa/pull/904

**Full Changelog**: https://github.com/dessalines/jerboa/compare/0.0.36...0.0.37

# Jerboa v0.0.36-alpha Release (2023-06-26)

## What's Changed

- Fixing incorrect dk language code. The correct one is da. by @dessalines in https://github.com/dessalines/jerboa/pull/819
- Fix: Add Account fails when full server uri is used by @sockenklaus in https://github.com/dessalines/jerboa/pull/805
- Enable swiping on comments to go back to posts by @CharlieGitDB in https://github.com/dessalines/jerboa/pull/785
- Fix passwords in plaintext on logcat by @CharlieGitDB in https://github.com/dessalines/jerboa/pull/834
- Make Search Page have consistent back button by @scme0 in https://github.com/dessalines/jerboa/pull/849
- Fix for scrolling up past images in the comments jerks you back down by @nahwneeth in https://github.com/dessalines/jerboa/pull/845
- Added switch to mark new posts as NSFW / toggle tag on existing posts by @sockenklaus in https://github.com/dessalines/jerboa/pull/833
- Update French translation by @julroy67 in https://github.com/dessalines/jerboa/pull/853
- Fix default sort and listing type to read from db by @beatgammit in https://github.com/dessalines/jerboa/pull/854
- Copy post and comment information to clipboard by @Noxor11 in https://github.com/dessalines/jerboa/pull/850
- Convert the groovy gradle files to kotlin DSL by @MV-GH in https://github.com/dessalines/jerboa/pull/858
- Bottom nav bar and screen transitions by @nahwneeth in https://github.com/dessalines/jerboa/pull/855
- Switch inbox & profile tabs with swipe gesture even whey they are empty. by @nahwneeth in https://github.com/dessalines/jerboa/pull/861
- Update bug report to include detailed ways to get logs by @MV-GH in https://github.com/dessalines/jerboa/pull/864
- Improved Spanish translation by @Noxor11 in https://github.com/dessalines/jerboa/pull/870
- Catch network errors and show a toast by @beatgammit in https://github.com/dessalines/jerboa/pull/874
- Include taglines with the scrollable post view by @beatgammit in https://github.com/dessalines/jerboa/pull/875
- Add totp field to login by @MV-GH in https://github.com/dessalines/jerboa/pull/868
- Add backwards compatible language picker by @MV-GH in https://github.com/dessalines/jerboa/pull/873
- Consolidate composables for CreatePost and PostEdit by @sockenklaus in https://github.com/dessalines/jerboa/pull/860
- QOL Remove unnecessary clickable areas from PostListingList by @lbenedetto in https://github.com/dessalines/jerboa/pull/710
- Reworked PickImage() composable by @sockenklaus in https://github.com/dessalines/jerboa/pull/877
- Bump dependencies by @MV-GH in https://github.com/dessalines/jerboa/pull/881
- Add generate lemmy instance list gradle task by @MV-GH in https://github.com/dessalines/jerboa/pull/884

## New Contributors

- @CharlieGitDB made their first contribution in https://github.com/dessalines/jerboa/pull/785
- @scme0 made their first contribution in https://github.com/dessalines/jerboa/pull/849
- @julroy67 made their first contribution in https://github.com/dessalines/jerboa/pull/853
- @Noxor11 made their first contribution in https://github.com/dessalines/jerboa/pull/850

**Full Changelog**: https://github.com/dessalines/jerboa/compare/0.0.35...0.0.36

# Jerboa v0.0.35-alpha Release (2023-06-22)

## What's Changed

- Revised and added some german translations by @sabieber in https://github.com/dessalines/jerboa/pull/611
- Fix typo labeling saved as search by @twizmwazin in https://github.com/dessalines/jerboa/pull/608
- Add more markwon plugins by @twizmwazin in https://github.com/dessalines/jerboa/pull/610
- Support android 13's per-app language selection, fixes #603 by @twizmwazin in https://github.com/dessalines/jerboa/pull/609
- Add option to use private custom tabs when available by @twizmwazin in https://github.com/dessalines/jerboa/pull/613
- Fix accompanist deprecations by @twizmwazin in https://github.com/dessalines/jerboa/pull/475
- Account Settings: Improves paddings and small layout details by @sabieber in https://github.com/dessalines/jerboa/pull/612
- Add vertical scroll to about screen by @marcellogalhardo in https://github.com/dessalines/jerboa/pull/618
- Use image previewer when clicking on images in small cards and list view by @twizmwazin in https://github.com/dessalines/jerboa/pull/614
- add bottom bar labels for swedish locale by @vijaykramesh in https://github.com/dessalines/jerboa/pull/623
- Show action bar by default by @beatgammit in https://github.com/dessalines/jerboa/pull/634
- improve manifest readability of supported instances by @thebino in https://github.com/dessalines/jerboa/pull/656
- Added comment sorting functionality by @a1studmuffin in https://github.com/dessalines/jerboa/pull/495
- Polish translation by @OrginalS in https://github.com/dessalines/jerboa/pull/655
- Added spanish translations by @ArkoSammy12 in https://github.com/dessalines/jerboa/pull/659
- add danish translations by @vijaykramesh in https://github.com/dessalines/jerboa/pull/666
- Update Italian translation and sort baseline strings lexicographically by @andreaippo in https://github.com/dessalines/jerboa/pull/630
- Edit German translation by @femoto in https://github.com/dessalines/jerboa/pull/650
- Fixing rest of build warnings. by @dessalines in https://github.com/dessalines/jerboa/pull/641
- Added unit tests for easy-to-test utils by @beatgammit in https://github.com/dessalines/jerboa/pull/629
- don't have translucent background on image post thumbnail click by @vijaykramesh in https://github.com/dessalines/jerboa/pull/625
- Fix image upload cancel crash by @noloman in https://github.com/dessalines/jerboa/pull/671
- Fix numbers and text@text turning into links (#635) by @MV-GH in https://github.com/dessalines/jerboa/pull/677
- Navigation enhancements: Bottom Navigation, screen transitions, back button by @nahwneeth in https://github.com/dessalines/jerboa/pull/670
- Handle /c/community@host URLs by @beatgammit in https://github.com/dessalines/jerboa/pull/583
- Open lemmy-verse links in Jerboa by @twizmwazin in https://github.com/dessalines/jerboa/pull/692
- Preserving state on orientation change. by @nahwneeth in https://github.com/dessalines/jerboa/pull/690
- Small Accesibility Tweaks by @scottmmjackson in https://github.com/dessalines/jerboa/pull/674
- Fix markdown formatting in bug report template by @gxtu in https://github.com/dessalines/jerboa/pull/743
- Update bug_report.md to include logs by @MV-GH in https://github.com/dessalines/jerboa/pull/702
- Auto-generate Lemmy-API types. by @dessalines in https://github.com/dessalines/jerboa/pull/657
- add strings.xml for French by @perepepepa in https://github.com/dessalines/jerboa/pull/638
- Added Japanese Translation Strings by @ShinyLuxray in https://github.com/dessalines/jerboa/pull/686
- Handle errors when updating changelog by @marcellogalhardo in https://github.com/dessalines/jerboa/pull/617
- Add setting (off by default) to prevent screenshots. by @camporter in https://github.com/dessalines/jerboa/pull/685
- #539 PrettyTime Missing Unit Workaround by @lbenedetto in https://github.com/dessalines/jerboa/pull/687
- #602 Change post button to be more obvious by @Chris-Kropp in https://github.com/dessalines/jerboa/pull/691
- Update nl translations by @MV-GH in https://github.com/dessalines/jerboa/pull/706
- Fix URL in pictrsImageThumbnail by @APraxx in https://github.com/dessalines/jerboa/pull/720
- Fixes image saving crashing on Android 9 and below by @MV-GH in https://github.com/dessalines/jerboa/pull/696
- voteToggle unused resource removed from string resources by @scottmmjackson in https://github.com/dessalines/jerboa/pull/699
- missing monochrome tags for adaptive icons by @JosephGaiser in https://github.com/dessalines/jerboa/pull/728
- Added close on click functionality to ImageViewerDialog. Solves #736 by @sockenklaus in https://github.com/dessalines/jerboa/pull/738
- Fix links not working in markdown tables by @MV-GH in https://github.com/dessalines/jerboa/pull/744
- Fixing test failure. by @dessalines in https://github.com/dessalines/jerboa/pull/762
- Added blur for Android version older than 12 by @sockenklaus in https://github.com/dessalines/jerboa/pull/746
- App settings should be visible if the user is not logged in by @yate in https://github.com/dessalines/jerboa/pull/764
- More Themes (Dark + Light) by @bynatejones in https://github.com/dessalines/jerboa/pull/750
- Account Settings: Use Alorma Compose Settings Components by @sabieber in https://github.com/dessalines/jerboa/pull/731
- Add rough rudimentary CONTRIBUTING.md by @MV-GH in https://github.com/dessalines/jerboa/pull/753
- Add CI dependency caching in between steps by @dessalines in https://github.com/dessalines/jerboa/pull/763
- fix: Comment replies icon appears in profile #533 by @LufyCZ in https://github.com/dessalines/jerboa/pull/726
- Remove remaining voteToggle unused translations by @Eskuero in https://github.com/dessalines/jerboa/pull/776
- Add benchmarks for TypicalUserJourney, Enable baselineprofile generator, and some metrics by @MV-GH in https://github.com/dessalines/jerboa/pull/601
- Quick comment navigation by @yate in https://github.com/dessalines/jerboa/pull/749
- Changed icons for "Go to comment" and "Copy permalink" in CommmentOptionsDialog by @sockenklaus in https://github.com/dessalines/jerboa/pull/781
- Fix bug with pictrs url query param rewriting by @beatgammit in https://github.com/dessalines/jerboa/pull/735
- Russian translation & Fixes of non-localized strings by @Snow4DV in https://github.com/dessalines/jerboa/pull/794
- Adds nsfw tag to nsfw posts by @Chris-Kropp in https://github.com/dessalines/jerboa/pull/786
- Expands german translations by @sabieber in https://github.com/dessalines/jerboa/pull/801
- Add dynamic splash screen colors by @ddmetz in https://github.com/dessalines/jerboa/pull/800
- Fix links not working when exclamation mark before it by @MV-GH in https://github.com/dessalines/jerboa/pull/802
- Check server version against app minimum api version by @beatgammit in https://github.com/dessalines/jerboa/pull/737
- Fix thumbnail links being treated as images. Fixes #815 by @dessalines in https://github.com/dessalines/jerboa/pull/816

## New Contributors

- @sabieber made their first contribution in https://github.com/dessalines/jerboa/pull/611
- @marcellogalhardo made their first contribution in https://github.com/dessalines/jerboa/pull/618
- @vijaykramesh made their first contribution in https://github.com/dessalines/jerboa/pull/623
- @thebino made their first contribution in https://github.com/dessalines/jerboa/pull/656
- @OrginalS made their first contribution in https://github.com/dessalines/jerboa/pull/655
- @ArkoSammy12 made their first contribution in https://github.com/dessalines/jerboa/pull/659
- @femoto made their first contribution in https://github.com/dessalines/jerboa/pull/650
- @noloman made their first contribution in https://github.com/dessalines/jerboa/pull/671
- @nahwneeth made their first contribution in https://github.com/dessalines/jerboa/pull/670
- @scottmmjackson made their first contribution in https://github.com/dessalines/jerboa/pull/674
- @gxtu made their first contribution in https://github.com/dessalines/jerboa/pull/743
- @perepepepa made their first contribution in https://github.com/dessalines/jerboa/pull/638
- @ShinyLuxray made their first contribution in https://github.com/dessalines/jerboa/pull/686
- @lbenedetto made their first contribution in https://github.com/dessalines/jerboa/pull/687
- @Chris-Kropp made their first contribution in https://github.com/dessalines/jerboa/pull/691
- @APraxx made their first contribution in https://github.com/dessalines/jerboa/pull/720
- @JosephGaiser made their first contribution in https://github.com/dessalines/jerboa/pull/728
- @sockenklaus made their first contribution in https://github.com/dessalines/jerboa/pull/738
- @yate made their first contribution in https://github.com/dessalines/jerboa/pull/764
- @bynatejones made their first contribution in https://github.com/dessalines/jerboa/pull/750
- @LufyCZ made their first contribution in https://github.com/dessalines/jerboa/pull/726
- @Eskuero made their first contribution in https://github.com/dessalines/jerboa/pull/776
- @Snow4DV made their first contribution in https://github.com/dessalines/jerboa/pull/794
- @ddmetz made their first contribution in https://github.com/dessalines/jerboa/pull/800

**Full Changelog**: https://github.com/dessalines/jerboa/compare/0.0.34...0.0.35

# Jerboa v0.0.34-alpha Release (2023-06-13)

## What's Changed

- QoL: Allow debug version installed next to release version by @MV-GH in https://github.com/dessalines/jerboa/pull/501
- Added Italian localization by @andreaippo in https://github.com/dessalines/jerboa/pull/533
- Highlight the current screen in BottomAppBar by @ironveil in https://github.com/dessalines/jerboa/pull/531
- Fix stale user profile data showing momentarily on PersonProfileActivity during fetch operations by @a1studmuffin in https://github.com/dessalines/jerboa/pull/518
- Prevent multiple line entry for the name of an instance on login. by @camporter in https://github.com/dessalines/jerboa/pull/520
- Add Brazilian Portuguese localization by @somehare in https://github.com/dessalines/jerboa/pull/540
- Fix big font size cutting off settings options by @calincara in https://github.com/dessalines/jerboa/pull/534
- Hide downvote button on comments and posts when disabled by @lsim in https://github.com/dessalines/jerboa/pull/502
- Added default community icon where appropriate by @a1studmuffin in https://github.com/dessalines/jerboa/pull/549
- Add a user agent by @camporter in https://github.com/dessalines/jerboa/pull/519
- If login fails, don't leave the current lemmy instance invalid by @camporter in https://github.com/dessalines/jerboa/pull/521
- Fix some issues with the unread counts not being accurate by @camporter in https://github.com/dessalines/jerboa/pull/537
- Fix the missing 'Old' default sort account setting. by @camporter in https://github.com/dessalines/jerboa/pull/517
- Added "Go to (user)" option in more places by @a1studmuffin in https://github.com/dessalines/jerboa/pull/515
- Update AndroidManifest.xml to match instances added in #505 by @shombando in https://github.com/dessalines/jerboa/pull/552
- Add contentDescription to all relevant components by @pipe01 in https://github.com/dessalines/jerboa/pull/470
- Added Swedish localisation by @JasBC in https://github.com/dessalines/jerboa/pull/569
- Fix typos in German strings by @tribut in https://github.com/dessalines/jerboa/pull/564
- #345 respect avatar settings by @igarshep in https://github.com/dessalines/jerboa/pull/554
- Increased NSFW blur by @XanderV2001 in https://github.com/dessalines/jerboa/pull/576
- Localization of user tabs by @kuroi-usagi in https://github.com/dessalines/jerboa/pull/563
- Add communities list to sidebar, fixes #510 by @twizmwazin in https://github.com/dessalines/jerboa/pull/512
- feat/launcher icon by @seamuslowry in https://github.com/dessalines/jerboa/pull/528
- Bash isn't portable, use POSIX sh instead by @7heo in https://github.com/dessalines/jerboa/pull/560
- Implement animated gifs when clicking on an image by @beatgammit in https://github.com/dessalines/jerboa/pull/580
- Add strings.xml for locale `ko` by @meinside in https://github.com/dessalines/jerboa/pull/586
- Copy paste bugfix for bottom bar highlight by @a1studmuffin in https://github.com/dessalines/jerboa/pull/592
- Swedish localisation revised; strings.xml-file fixed. by @JasBC in https://github.com/dessalines/jerboa/pull/594
- Images updated 2 by @dessalines in https://github.com/dessalines/jerboa/pull/595
- Comment action bar fixes by @a1studmuffin in https://github.com/dessalines/jerboa/pull/593
- Remove unnecessary check by @7heo in https://github.com/dessalines/jerboa/pull/600
- Show default icon for community links in sidebar by @a1studmuffin in https://github.com/dessalines/jerboa/pull/590
- Update Italian Translations by @andscape-dev in https://github.com/dessalines/jerboa/pull/591
- Revamped BottomBar to match MD3 by @ironveil in https://github.com/dessalines/jerboa/pull/567

## New Contributors

- @andreaippo made their first contribution in https://github.com/dessalines/jerboa/pull/533
- @ironveil made their first contribution in https://github.com/dessalines/jerboa/pull/531
- @camporter made their first contribution in https://github.com/dessalines/jerboa/pull/520
- @somehare made their first contribution in https://github.com/dessalines/jerboa/pull/540
- @calincara made their first contribution in https://github.com/dessalines/jerboa/pull/534
- @JasBC made their first contribution in https://github.com/dessalines/jerboa/pull/569
- @tribut made their first contribution in https://github.com/dessalines/jerboa/pull/564
- @igarshep made their first contribution in https://github.com/dessalines/jerboa/pull/554
- @XanderV2001 made their first contribution in https://github.com/dessalines/jerboa/pull/576
- @seamuslowry made their first contribution in https://github.com/dessalines/jerboa/pull/528
- @beatgammit made their first contribution in https://github.com/dessalines/jerboa/pull/580
- @meinside made their first contribution in https://github.com/dessalines/jerboa/pull/586
- @andscape-dev made their first contribution in https://github.com/dessalines/jerboa/pull/591

**Full Changelog**: https://github.com/dessalines/jerboa/compare/0.0.33...0.0.34

# Jerboa v0.0.33-alpha Release (2023-06-10)

This is a big one, thank you to all our new contributors! 🥳 Jerboa is still very much an alpha-level app, but its wonderful to see its pace accelerating rapidly thanks to others working hard to make it better.

- Special thanks to @twizmwazin, for joining the Jerboa team, making great contributions, and helping out with code reviews.
- Jerboa is also now internationalized, thanks to the excellent work by @kuroi-usagi .
  - You can begin adding strings to a `values-{2_LETTER_LANG_ID}/strings.xml` , like [this one](https://github.com/dessalines/jerboa/blob/main/app/src/main/res/values-de/strings.xml)

## What's Changed

- Use inner function to reduce boilerplate in LookAndFeelActivity by @twizmwazin in https://github.com/dessalines/jerboa/pull/417
- Fix the flashing between transitions (#371) by @AntmanLFEz in https://github.com/dessalines/jerboa/pull/428
- Fix #421 by @7heo in https://github.com/dessalines/jerboa/pull/422
- Expand area tappable by user to collapse a comment by @twizmwazin in https://github.com/dessalines/jerboa/pull/436
- Aesthetics improvements by @pipe01 in https://github.com/dessalines/jerboa/pull/424
- Allow signed-out users to interact with showMoreOptions by @tuxiqae in https://github.com/dessalines/jerboa/pull/450
- Enable autofill on login form by @pipe01 in https://github.com/dessalines/jerboa/pull/451
- Add option to show contents of collapsed comments by @twizmwazin in https://github.com/dessalines/jerboa/pull/438
- Markdown renderer replacement by @AntmanLFEz in https://github.com/dessalines/jerboa/pull/432
- Comment action bar improvements by @twizmwazin in https://github.com/dessalines/jerboa/pull/453
- Fix show more children button not being animated when collapsed by @twizmwazin in https://github.com/dessalines/jerboa/pull/461
- Truncates too long passwords by @MV-GH in https://github.com/dessalines/jerboa/pull/462
- In-app image viewer by @pipe01 in https://github.com/dessalines/jerboa/pull/444
- Change badge component from Badge to Box by @mxmvncnt in https://github.com/dessalines/jerboa/pull/459
- update DEFAULT_LEMMY_INSTANCES by @taitsmith in https://github.com/dessalines/jerboa/pull/466
- Change color of account name to adapt to light/dark mode by @mitchellss in https://github.com/dessalines/jerboa/pull/478
- started localization for the app and added first german local by @kuroi-usagi in https://github.com/dessalines/jerboa/pull/447
- Fixed up ThemeMode enum to use strings in UI by @a1studmuffin in https://github.com/dessalines/jerboa/pull/469
- Add voting arrows and score to posts in "List" view mode by @a1studmuffin in https://github.com/dessalines/jerboa/pull/472
- Add linkify plugin to markwon by @twizmwazin in https://github.com/dessalines/jerboa/pull/487
- Add custom tab support by @twizmwazin in https://github.com/dessalines/jerboa/pull/474
- Close account switcher when drawer is closed by @vishalbiswas in https://github.com/dessalines/jerboa/pull/490
- Simple filter to display matching instances in the dropdown. by @onowrouzi in https://github.com/dessalines/jerboa/pull/489
- Fix show voting arrows in list view option not updating by @twizmwazin in https://github.com/dessalines/jerboa/pull/493
- Remove check to allow replying to own comments by @lsim in https://github.com/dessalines/jerboa/pull/499
- Add dutch translations by @MV-GH in https://github.com/dessalines/jerboa/pull/504
- Fix CommentNode previews by @lsim in https://github.com/dessalines/jerboa/pull/503
- Fixing markdown line height issue. by @dessalines in https://github.com/dessalines/jerboa/pull/500
- Expanded default lemmy instances list to active users/mo > 100 by @shombando in https://github.com/dessalines/jerboa/pull/505
- Add bottom bar to community list by @abluescarab in https://github.com/dessalines/jerboa/pull/497
- Fixing broken DB. by @dessalines in https://github.com/dessalines/jerboa/pull/511

## New Contributors

- @AntmanLFEz made their first contribution in https://github.com/dessalines/jerboa/pull/428
- @7heo made their first contribution in https://github.com/dessalines/jerboa/pull/422
- @pipe01 made their first contribution in https://github.com/dessalines/jerboa/pull/424
- @tuxiqae made their first contribution in https://github.com/dessalines/jerboa/pull/450
- @MV-GH made their first contribution in https://github.com/dessalines/jerboa/pull/462
- @mxmvncnt made their first contribution in https://github.com/dessalines/jerboa/pull/459
- @taitsmith made their first contribution in https://github.com/dessalines/jerboa/pull/466
- @mitchellss made their first contribution in https://github.com/dessalines/jerboa/pull/478
- @kuroi-usagi made their first contribution in https://github.com/dessalines/jerboa/pull/447
- @vishalbiswas made their first contribution in https://github.com/dessalines/jerboa/pull/490
- @onowrouzi made their first contribution in https://github.com/dessalines/jerboa/pull/489
- @lsim made their first contribution in https://github.com/dessalines/jerboa/pull/499
- @shombando made their first contribution in https://github.com/dessalines/jerboa/pull/505
- @abluescarab made their first contribution in https://github.com/dessalines/jerboa/pull/497

**Full Changelog**: https://github.com/dessalines/jerboa/compare/0.0.32...0.0.33

# Jerboa v0.0.32-alpha Release (2023-06-05)

## What's Changed

- Adding more CI commands. by @dessalines in https://github.com/dessalines/jerboa/pull/367
- Show settings in sidebar even when user is not logged in by @twizmwazin in https://github.com/dessalines/jerboa/pull/375
- Added Blue theme color by @a1studmuffin in https://github.com/dessalines/jerboa/pull/392
- Roll back AGP to `8.0.2` by @oscarnylander in https://github.com/dessalines/jerboa/pull/404
- Fixing CI ntfy.sh notification link. by @dessalines in https://github.com/dessalines/jerboa/pull/406
- Remove Splash Screen by @oscarnylander in https://github.com/dessalines/jerboa/pull/383
- Add padding to SwipeRefresh for PostListings (for #400) by @russjr08 in https://github.com/dessalines/jerboa/pull/408
- Ktlint 2 by @dessalines in https://github.com/dessalines/jerboa/pull/409
- Add `.editorconfig` by @oscarnylander in https://github.com/dessalines/jerboa/pull/405
- Save listing and sort type-preferences in app DB by @oscarnylander in https://github.com/dessalines/jerboa/pull/407
- Added Black and System Black theme modes, fixes https://github.com/dessalines/jerboa/issues/376 by @a1studmuffin in https://github.com/dessalines/jerboa/pull/393
- Fix dropdowns on Account Settings-screen not working by @oscarnylander in https://github.com/dessalines/jerboa/pull/387
- Collapse comments by @dessalines in https://github.com/dessalines/jerboa/pull/419
- Collapse comments by tapping directly on the comment body by @a1studmuffin in https://github.com/dessalines/jerboa/pull/398
- Fix navigating to other instance communities and users by @Anna-log7 in https://github.com/dessalines/jerboa/pull/413
- Add option to disable showing the bottom navigation bar by @twizmwazin in https://github.com/dessalines/jerboa/pull/412
- Fixed showNavBar issue. by @dessalines in https://github.com/dessalines/jerboa/pull/420

## New Contributors

- @twizmwazin made their first contribution in https://github.com/dessalines/jerboa/pull/375
- @a1studmuffin made their first contribution in https://github.com/dessalines/jerboa/pull/392
- @oscarnylander made their first contribution in https://github.com/dessalines/jerboa/pull/404
- @Anna-log7 made their first contribution in https://github.com/dessalines/jerboa/pull/413

**Full Changelog**: https://github.com/dessalines/jerboa/compare/0.0.31...0.0.32

# Jerboa v0.0.31-alpha Release (2023-06-01)

## What's Changed

- Fix padding bug by @dessalines in https://github.com/dessalines/jerboa/pull/366

**Full Changelog**: https://github.com/dessalines/jerboa/compare/0.0.30...0.0.31

# Jerboa v0.0.30-alpha Release (2023-04-25)

## What's Changed

- Upgrade deps 11 by @dessalines in https://github.com/dessalines/jerboa/pull/355
- Adjust the status bar while using the system dark theme to not use dark icons by @russjr08 in https://github.com/dessalines/jerboa/pull/358
- Upgrading deps. by @dessalines in https://github.com/dessalines/jerboa/pull/359
- Use default bottom app bar. by @dessalines in https://github.com/dessalines/jerboa/pull/360

## New Contributors

- @russjr08 made their first contribution in https://github.com/dessalines/jerboa/pull/358

**Full Changelog**: https://github.com/dessalines/jerboa/compare/0.0.29...0.0.30

# Jerboa v0.0.29-alpha Release (2023-03-23)

## What's Changed

- Adding a translucent statusbar. Fixes #347 by @dessalines in https://github.com/dessalines/jerboa/pull/348
- Upgrading deps by @dessalines in https://github.com/dessalines/jerboa/pull/349
- Fixing IME padding issues. Fixes #350 by @dessalines in https://github.com/dessalines/jerboa/pull/351
- Making icon thumbnails smaller. by @dessalines in https://github.com/dessalines/jerboa/pull/353

**Full Changelog**: https://github.com/dessalines/jerboa/compare/0.0.28...0.0.29

# Jerboa v0.0.28-alpha Release (2023-03-01)

## What's Changed

- Making icons larger, adding node keys by @dessalines in https://github.com/dessalines/jerboa/pull/338
- Upgrade from kapt to ksp. by @dessalines in https://github.com/dessalines/jerboa/pull/342
- Add report user by @dessalines in https://github.com/dessalines/jerboa/pull/343
- Adding comment mentions to inbox. Fixes #339 by @dessalines in https://github.com/dessalines/jerboa/pull/344

**Full Changelog**: https://github.com/dessalines/jerboa/compare/0.0.27...0.0.28

# Jerboa v0.0.27-alpha Release (2023-02-19)

## What's Changed

- Use muted profile names, better colors, and align post header bar. by @dessalines in https://github.com/dessalines/jerboa/pull/328
- Downgrade from gradle RC to gradle 8.0 by @dessalines in https://github.com/dessalines/jerboa/pull/329
- Make default font size 16. Fixes #330 @dessalines in https://github.com/dessalines/jerboa/pull/334
- Changing the post action bar. Fixes #324 by @dessalines in https://github.com/dessalines/jerboa/pull/332
- Adding instant post and comment voting. Fixes #299 by @dessalines in https://github.com/dessalines/jerboa/pull/335

**Full Changelog**: https://github.com/dessalines/jerboa/compare/0.0.26...0.0.27

# Jerboa v0.0.26-alpha Release (2023-02-06)

## What's Changed

- Fixing bookmark style. Fixes #297 by @dessalines in https://github.com/dessalines/jerboa/pull/304
- Softer post card color. Fixes #296 by @dessalines in https://github.com/dessalines/jerboa/pull/305
- Show post score next to time. Fixes #303 by @dessalines in https://github.com/dessalines/jerboa/pull/306
- Make icons squircle. Fixes #301 by @dessalines in https://github.com/dessalines/jerboa/pull/307
- Remove @ sign for usernames. Fixes #295 by @dessalines in https://github.com/dessalines/jerboa/pull/308
- Better more comments button. Fixes #292 by @dessalines in https://github.com/dessalines/jerboa/pull/309
- Adding nsfw image blurring. Fixes #291 by @dessalines in https://github.com/dessalines/jerboa/pull/310
- Reply node by @dessalines in https://github.com/dessalines/jerboa/pull/311
- Adding vote icons. Fixes #302 by @dessalines in https://github.com/dessalines/jerboa/pull/313
- Adding new comments indicator. Fixes #283 by @dessalines in https://github.com/dessalines/jerboa/pull/314
- Better subscribe button. Fixes #273 by @dessalines in https://github.com/dessalines/jerboa/pull/315
- Comment reply links now go to parent for context. Fixes #155 by @dessalines in https://github.com/dessalines/jerboa/pull/316
- Adding taglines. Fixes #286 by @dessalines in https://github.com/dessalines/jerboa/pull/317
- Adding post view modes: Card, Small Card, and List. Fixes #278 by @dessalines in https://github.com/dessalines/jerboa/pull/318

**Full Changelog**: https://github.com/dessalines/jerboa/compare/0.0.25...0.0.26

# Jerboa v0.0.25-alpha Release (2023-02-02)

## What's Changed

- Fix not showing federated comments. Fixes #290 by @dessalines in https://github.com/dessalines/jerboa/pull/294

**Full Changelog**: https://github.com/dessalines/jerboa/compare/0.0.24...0.0.25

# Jerboa v0.0.24-alpha Release (2023-02-01)

## What's Changed

- Fix font sizes by @dessalines in https://github.com/dessalines/jerboa/pull/28
- Upgrade to lemmy version 0.17.0 . Fixes #277 by @dessalines in https://github.com/dessalines/jerboa/pull/289

**Full Changelog**: https://github.com/dessalines/jerboa/compare/0.0.23...0.0.24

# Jerboa v0.0.23-alpha Release (2022-12-29)

## What's Changed

- Fix material 3 crash on android 11 and below devices. Fixes #264 by @dessalines in https://github.com/dessalines/jerboa/pull/268
- Add donation link by @dessalines in https://github.com/dessalines/jerboa/pull/269
- Fix bad_url for torrent magnet links. Fixes #270 by @dessalines in https://github.com/dessalines/jerboa/pull/271

**Full Changelog**: https://github.com/dessalines/jerboa/compare/0.0.22...0.0.23

# Jerboa v0.0.22-alpha Release (2022-12-22)

## What's Changed

- Material v3 by @dessalines in https://github.com/dessalines/jerboa/pull/263
- Dynamic Themes, and Green and Pink.
- Adds Scrolling app bars.
- Better post listing header layout.
- Various UI improvements.

**Full Changelog**: https://github.com/dessalines/jerboa/compare/0.0.21...0.0.22

# Jerboa v0.0.21-alpha Release (2022-12-21)

## What's Changed

- Change sidebar to info by @dessalines in https://github.com/dessalines/jerboa/pull/245
- Remove top route. by @dessalines in https://github.com/dessalines/jerboa/pull/246
- Adding copy post link. Fixes #168 by @dessalines in https://github.com/dessalines/jerboa/pull/247
- Organizing imports. by @dessalines in https://github.com/dessalines/jerboa/pull/248
- More sidebar stats by @dessalines in https://github.com/dessalines/jerboa/pull/249
- Fixing siFormat issue when 0. Fixes #170 by @dessalines in https://github.com/dessalines/jerboa/pull/252
- Adding a settings page. by @dessalines in https://github.com/dessalines/jerboa/pull/253
- Adding light and dark theme options. Fixes #254 by @dessalines in https://github.com/dessalines/jerboa/pull/259
- Add about page by @dessalines in https://github.com/dessalines/jerboa/pull/260
- Smaller action bars by @dessalines in https://github.com/dessalines/jerboa/pull/261

# Jerboa v0.0.20-alpha Release (2022-10-18)

## What's Changed

- Fix prettytime crash. Fixes #238 by @dessalines in https://github.com/dessalines/jerboa/pull/239

**Full Changelog**: https://github.com/dessalines/jerboa/compare/0.0.19...0.0.20

# Jerboa v0.0.18-alpha Release (2022-10-10)

## What's Changed

- Coil upgrade v2 by @dessalines in https://github.com/dessalines/jerboa/pull/234
- Fix comment scrolling bug. Fixes #231 by @dessalines in https://github.com/dessalines/jerboa/pull/235
- Fix create post bug. Fixes #230 by @dessalines in https://github.com/dessalines/jerboa/pull/236
- Fix account bug. Fixes #229 by @dessalines in https://github.com/dessalines/jerboa/pull/237

**Full Changelog**: https://github.com/dessalines/jerboa/compare/0.0.17...0.0.18

# Jerboa v0.0.17-alpha Release (2022-10-04)

## What's Changed

- Running lint, updating deps. by @dessalines in https://github.com/dessalines/jerboa/pull/197
- Moving to kotlinter-gradle by @dessalines in https://github.com/dessalines/jerboa/pull/198
- Fixing unit tests. by @dessalines in https://github.com/dessalines/jerboa/pull/199
- Some items fixes. by @dessalines in https://github.com/dessalines/jerboa/pull/204
- Upgrade accompanist by @dessalines in https://github.com/dessalines/jerboa/pull/208
- Fix comment header with flowrow. Fixes #207 by @dessalines in https://github.com/dessalines/jerboa/pull/209
- Make comment slightly larger. Fixes #213 by @dessalines in https://github.com/dessalines/jerboa/pull/214
- Make comment icon smaller. Fixes #212 by @dessalines in https://github.com/dessalines/jerboa/pull/215
- Changing star to bookmark. Fixes #210 by @dessalines in https://github.com/dessalines/jerboa/pull/216
- Fix comment indent. Fixes #211 by @dessalines in https://github.com/dessalines/jerboa/pull/217
- Make post pictures wider. Fixes #196 by @dessalines in https://github.com/dessalines/jerboa/pull/218
- Deduplicate scrolling posts. Fixes #219 by @dessalines in https://github.com/dessalines/jerboa/pull/220
- Adding a login first message. Fixes #206 by @dessalines in https://github.com/dessalines/jerboa/pull/221
- Lazycolumn 2 by @dessalines in https://github.com/dessalines/jerboa/pull/223
- Fix deleted item header spacing. Fixes #222 by @dessalines in https://github.com/dessalines/jerboa/pull/224
- Adding deep links. by @dessalines in https://github.com/dessalines/jerboa/pull/228

**Full Changelog**: https://github.com/dessalines/jerboa/compare/0.0.16...0.0.17

# Jerboa v0.0.16-alpha Release (2022-07-20)

- Adding instance name to accounts. Fixes #164 by @dessalines in https://github.com/dessalines/jerboa/pull/166
- Removing Site creator by @dessalines in https://github.com/dessalines/jerboa/pull/172
- Enable minify. Fixes #171 by @dessalines in https://github.com/dessalines/jerboa/pull/173
- fix signout by @LunaticHacker in https://github.com/dessalines/jerboa/pull/180
- Add deeplinks for createPost by @LunaticHacker in https://github.com/dessalines/jerboa/pull/178
- fix a bug i introduced in #180 by @LunaticHacker in https://github.com/dessalines/jerboa/pull/185
- Add api settings by @LunaticHacker in https://github.com/dessalines/jerboa/pull/183
- add fab for create post from community page by @LunaticHacker in https://github.com/dessalines/jerboa/pull/184
- Make MarkdownTextField generic by @LunaticHacker in https://github.com/dessalines/jerboa/pull/195

## New Contributors

- Special thanks to @LunaticHacker for their work on Jerboa!

**Full Changelog**: https://github.com/dessalines/jerboa/compare/0.0.15...0.0.16

# Jerboa v0.0.15-alpha Release (2022-04-19)

- Reverting back to old markdown renderer. Was much better overall. by @dessalines in https://github.com/dessalines/jerboa/pull/156
- Debounce search box input. Fixes #154 by @dessalines in https://github.com/dessalines/jerboa/pull/157
- Ability to delete posts and comments. Fixes #152 by @dessalines in https://github.com/dessalines/jerboa/pull/161
- Comment tree rework 1 by @dessalines in https://github.com/dessalines/jerboa/pull/162

# Jerboa v0.0.13-alpha Release (2022-03-29)

- Fix animation direction. Fixes [#114](https://github.com/dessalines/jerboa/issues/114) ([#140](https://github.com/dessalines/jerboa/issues/140))
- Adding refresh button, and scroll to top on sort change. ([#139](https://github.com/dessalines/jerboa/issues/139))
- Adding new sorts ([#138](https://github.com/dessalines/jerboa/issues/138))
- Catch malformed URL exception. ([#135](https://github.com/dessalines/jerboa/issues/135))
- Upgrade deps ([#134](https://github.com/dessalines/jerboa/issues/134))

# Jerboa v0.0.12-alpha Release (2022-03-13)

- Fixing slow create post. Fixes [#117](https://github.com/dessalines/jerboa/issues/117) ([#126](https://github.com/dessalines/jerboa/issues/126))
- Fix image height in landscape mode. Fixes [#122](https://github.com/dessalines/jerboa/issues/122) ([#125](https://github.com/dessalines/jerboa/issues/125))
- Remove instant voting. Fixes [#123](https://github.com/dessalines/jerboa/issues/123) ([#124](https://github.com/dessalines/jerboa/issues/124))
- Trying to fix builds 1 ([#121](https://github.com/dessalines/jerboa/issues/121))

# Jerboa v0.0.11-alpha Release (2022-03-09)

- Adding deploy to device script.
- Adding signing config to release.
- Upgrading some deps. ([#120](https://github.com/dessalines/jerboa/issues/120))
- Various Light theme fixes. ([#118](https://github.com/dessalines/jerboa/issues/118))

# Jerboa v0.0.10-alpha Release (2022-02-25)

- Adding link from text selection. Fixes [#105](https://github.com/dessalines/jerboa/issues/105) ([#107](https://github.com/dessalines/jerboa/issues/107))
- Remember saved text for markdown areas. Fixes [#104](https://github.com/dessalines/jerboa/issues/104)

# Jerboa v0.0.9-alpha Release (2022-02-20)

- Adding a markdown helper. Fixes [#38](https://github.com/dessalines/jerboa/issues/38)
- Show comment upvote count when there are downvotes. Fixes [#97](https://github.com/dessalines/jerboa/issues/97)
- Fix muted colors for titles, top app bars. Fixes [#98](https://github.com/dessalines/jerboa/issues/98)
- Smoother comment scrolling. Fixes [#99](https://github.com/dessalines/jerboa/issues/99)
- Mute post title color on read. Fixes [#100](https://github.com/dessalines/jerboa/issues/100)
- Add link to Jerboa on F-Droid ([#96](https://github.com/dessalines/jerboa/issues/96))

# Jerboa v0.0.8-alpha Release (2022-02-17)

- Showing stickied / locked. Fixes [#61](https://github.com/dessalines/jerboa/issues/61) ([#95](https://github.com/dessalines/jerboa/issues/95))
- Add no-background post listing. Fixes [#91](https://github.com/dessalines/jerboa/issues/91) ([#94](https://github.com/dessalines/jerboa/issues/94))
- Don't show block person on your own profile. Fixes [#93](https://github.com/dessalines/jerboa/issues/93)
- Adding scrollbars to lazycolumns. Fixes [#87](https://github.com/dessalines/jerboa/issues/87) ([#90](https://github.com/dessalines/jerboa/issues/90))

# Jerboa v0.0.7-alpha Release (2022-02-06)

- Merge pull request [#86](https://github.com/dessalines/jerboa/issues/86) from dessalines/bottom_bar_highlight
- Darkblue statusbar color. Fixes [#79](https://github.com/dessalines/jerboa/issues/79) ([#84](https://github.com/dessalines/jerboa/issues/84))
- Downgrade compose to fix liststate bug. Fixes [#81](https://github.com/dessalines/jerboa/issues/81)

# Jerboa v0.0.6-alpha Release (2022-02-03)

- Dont resort comments ([#77](https://github.com/dessalines/jerboa/issues/77))
- Adding user and community blocking. Fixes [#71](https://github.com/dessalines/jerboa/issues/71) Fixes [#58](https://github.com/dessalines/jerboa/issues/58) ([#75](https://github.com/dessalines/jerboa/issues/75))
- Saved page fix ([#74](https://github.com/dessalines/jerboa/issues/74))
- Adding saved page. Fixes [#20](https://github.com/dessalines/jerboa/issues/20) ([#73](https://github.com/dessalines/jerboa/issues/73))
- Forwarding error messages from lemmy. Fixes [#66](https://github.com/dessalines/jerboa/issues/66)
- Removing unit defaults. Fixes [#67](https://github.com/dessalines/jerboa/issues/67) ([#69](https://github.com/dessalines/jerboa/issues/69))
- Addin comment and post reporting. Fixes [#59](https://github.com/dessalines/jerboa/issues/59) ([#68](https://github.com/dessalines/jerboa/issues/68))
- Adding black background. Fixes [#56](https://github.com/dessalines/jerboa/issues/56)
- Adding comment hot sorting, and a few API stubs. Fixes [#62](https://github.com/dessalines/jerboa/issues/62)

# Jerboa v0.0.5-alpha Release (2022-01-26)

- Fix null checks and crash. Fixes [#60](https://github.com/dessalines/jerboa/issues/60) . Fixes [#37](https://github.com/dessalines/jerboa/issues/37)
- Adding loading indicators and disables. Fixes [#57](https://github.com/dessalines/jerboa/issues/57)
- Adding comment header long click collapse. Fixes [#51](https://github.com/dessalines/jerboa/issues/51)
- Fixing subnode actions. Fixes [#55](https://github.com/dessalines/jerboa/issues/55)
- Changing thumbs to arrows.
- Adding some better spacing. Fixes [#54](https://github.com/dessalines/jerboa/issues/54)
- Adding surface for splashscreen. Fixes [#56](https://github.com/dessalines/jerboa/issues/56)
- Adding Fastlane structure
- Adding fetch suggested title.

# Jerboa v0.0.4-alpha Release (2022-01-20)

- Adding community sidebars. Fixes [#44](https://github.com/dessalines/jerboa/issues/44)
- Fix private message reply bug.
- CommentReplyView refactored on its own viewmodel. Added mark replied message as read. Fixes [#45](https://github.com/dessalines/jerboa/issues/45)
- Adding modified times. Fixes [#46](https://github.com/dessalines/jerboa/issues/46)
- Add bottom app bar padding.
- Added Community and Site

# Jerboa v0.0.3-alpha Release (2022-01-18)

This is the very first alpha release of Jerboa, a native android Lemmy client.

**This is not a finished client, so don't expect most things to be working**

If anything isn't working correctly, open up an [issue on the repo.](https://github.com/dessalines/jerboa/issues)
