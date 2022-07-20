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

Added Community and Site 

# Jerboa v0.0.3-alpha Release (2022-01-18)

This is the very first alpha release of Jerboa, a native android Lemmy client.

**This is not a finished client, so don't expect most things to be working**

If anything isn't working correctly, open up an [issue on the repo.](https://github.com/dessalines/jerboa/issues)
