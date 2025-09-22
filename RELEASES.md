## What's Changed in 0.0.84

- Restore deprecated apk post processing config by @MV-GH in [#1969](https://github.com/LemmyNet/jerboa/pull/1969)
- Regenerate baseline profiles by @MV-GH in [#1968](https://github.com/LemmyNet/jerboa/pull/1968)
- Fixing build tools to version 36.0.0 by @dessalines in [#1967](https://github.com/LemmyNet/jerboa/pull/1967)
- Bump to Android SDK 36 by @MV-GH in [#1933](https://github.com/LemmyNet/jerboa/pull/1933)
- Fix too large images in comments being cutoff by @MV-GH in [#1944](https://github.com/LemmyNet/jerboa/pull/1944)
- Add option to disable video auto play by @MV-GH in [#1936](https://github.com/LemmyNet/jerboa/pull/1936)
- Fix #1934 some urls being wrongly interpreted as video by @MV-GH in [#1941](https://github.com/LemmyNet/jerboa/pull/1941)
- Merge branch 'main' of https://github.com/LemmyNet/jerboa by @dessalines
- Update strings.xml by @jwkwshjsjsj in [#1928](https://github.com/LemmyNet/jerboa/pull/1928)
- Update README.md by @jwkwshjsjsj in [#1926](https://github.com/LemmyNet/jerboa/pull/1926)

## New Contributors

- @weblate made their first contribution
- @jwkwshjsjsj made their first contribution in [#1928](https://github.com/LemmyNet/jerboa/pull/1928)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.83...0.0.84

## What's Changed in 0.0.83

- Add Video screen viewer, FeedVideoPlayer, plus support for popular non OGP videohosts. by @MV-GH in [#1922](https://github.com/LemmyNet/jerboa/pull/1922)
- Fix #1884, rare case markdown actions can cause crashes by @MV-GH in [#1889](https://github.com/LemmyNet/jerboa/pull/1889)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.80...0.0.83

## What's Changed in 0.0.80

- Fix post read not being applied in smallcard viewmode #1870 by @MV-GH in [#1872](https://github.com/LemmyNet/jerboa/pull/1872)
- Fix crashes on Android 9 due to compose bump by @MV-GH in [#1871](https://github.com/LemmyNet/jerboa/pull/1871)
- Fix LemmyAPI build by @MV-GH in [#1865](https://github.com/LemmyNet/jerboa/pull/1865)
- Better ntfy notifs. by @dessalines in [#1853](https://github.com/LemmyNet/jerboa/pull/1853)
- Fix edgecase saveImage failing causing crash by @MV-GH in [#1846](https://github.com/LemmyNet/jerboa/pull/1846)

## New Contributors

- @flipflop97 made their first contribution
- @ryanho made their first contribution
- @Tmpod made their first contribution
- @panosalevropoulos made their first contribution

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.79...0.0.80

## What's Changed in 0.0.79

- Update baseline profiles by @MV-GH in [#1845](https://github.com/LemmyNet/jerboa/pull/1845)
- Fixing changelog script. by @dessalines in [#1844](https://github.com/LemmyNet/jerboa/pull/1844)
- Add "Copy Image" action to Image context menu by @MV-GH in [#1840](https://github.com/LemmyNet/jerboa/pull/1840)
- Fix deprecation zoomable constructor by @MV-GH in [#1841](https://github.com/LemmyNet/jerboa/pull/1841)
- Adding weblate translations url. by @dessalines in [#1830](https://github.com/LemmyNet/jerboa/pull/1830)
- Add ko-fi by @dessalines in [#1826](https://github.com/LemmyNet/jerboa/pull/1826)
- Fixes #1755 Keyboard still open after creating comment by @MV-GH in [#1825](https://github.com/LemmyNet/jerboa/pull/1825)
- Fixes #1748 Extra padding above IME keyboard by @MV-GH in [#1824](https://github.com/LemmyNet/jerboa/pull/1824)
- Add Android 15 SDK support, Configure Android lint, deprecation fixes by @MV-GH in [#1823](https://github.com/LemmyNet/jerboa/pull/1823)
- Add Lemmy Donation Dialog by @iByteABit256 in [#1813](https://github.com/LemmyNet/jerboa/pull/1813)
- Add Image Proxy endpoint support to Share/Download actions by @MV-GH in [#1814](https://github.com/LemmyNet/jerboa/pull/1814)
- Fixes: Webp images using image_proxy can't be saved in Android 10+ by @MV-GH in [#1801](https://github.com/LemmyNet/jerboa/pull/1801)
- Fix rare crash on switching account #1757 by @MV-GH in [#1758](https://github.com/LemmyNet/jerboa/pull/1758)
- Fixing bug with torrent magnet link posts. by @dessalines in [#1724](https://github.com/LemmyNet/jerboa/pull/1724)
- Adding an image upload button for custom thumbnails. by @dessalines in [#1725](https://github.com/LemmyNet/jerboa/pull/1725)
- Create strings.xml (zh) by @BingoKingo in [#1722](https://github.com/LemmyNet/jerboa/pull/1722)
- Redesign for blocks screen by @rodrigo-fm in [#1718](https://github.com/LemmyNet/jerboa/pull/1718)
- Bump LemmyApi to Support Lemmy 0.19.7 Features by @MV-GH in [#1719](https://github.com/LemmyNet/jerboa/pull/1719)
- Fixing signing config. by @dessalines in [#1708](https://github.com/LemmyNet/jerboa/pull/1708)
- Update Norwegian Nynorsk translation by @huftis in [#1695](https://github.com/LemmyNet/jerboa/pull/1695)
- Adding the ability to export / import the database. by @dessalines in [#1685](https://github.com/LemmyNet/jerboa/pull/1685)
- Running renovate every weekend. by @dessalines in [#1686](https://github.com/LemmyNet/jerboa/pull/1686)
- Updating git cliff. by @dessalines in [#1682](https://github.com/LemmyNet/jerboa/pull/1682)

## New Contributors

- @BingoKingo made their first contribution in [#1722](https://github.com/LemmyNet/jerboa/pull/1722)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.77...0.0.79

## What's Changed in 0.0.77

- Fix community search not relaunching after process death by @MV-GH in [#1681](https://github.com/LemmyNet/jerboa/pull/1681)
- Fix popups disappearing on orientation change. by @MV-GH in [#1680](https://github.com/LemmyNet/jerboa/pull/1680)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.75...0.0.77

## What's Changed in 0.0.75

- Fix 'Swipe right to navigate back' to go back multiple times by @MV-GH in [#1660](https://github.com/LemmyNet/jerboa/pull/1660)
- Fix legacy score behaviour by @MV-GH in [#1659](https://github.com/LemmyNet/jerboa/pull/1659)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.74...0.0.75

## What's Changed in 0.0.74

- Fix missing parent comment navigation by @MV-GH in [#1656](https://github.com/LemmyNet/jerboa/pull/1656)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.73...0.0.74

## What's Changed in 0.0.73

- Fixes wrong height Navigation bar when descriptions where hidden. by @MV-GH in [#1650](https://github.com/LemmyNet/jerboa/pull/1650)
- Fixes mostly systembars colors not matching with application by @MV-GH in [#1651](https://github.com/LemmyNet/jerboa/pull/1651)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.72...0.0.73

## What's Changed in 0.0.72

- Upgrading compose-bom to 2024.08 by @dessalines in [#1646](https://github.com/LemmyNet/jerboa/pull/1646)
- Temp fix for incorrect coil image sizing. by @dessalines in [#1647](https://github.com/LemmyNet/jerboa/pull/1647)
- Adding edge to edge support by @dessalines in [#1607](https://github.com/LemmyNet/jerboa/pull/1607)
- Showing scores in post listing like they were before. by @dessalines in [#1641](https://github.com/LemmyNet/jerboa/pull/1641)
- Make sup/subscript more strict by @MV-GH in [#1632](https://github.com/LemmyNet/jerboa/pull/1632)
- Move time ago and score to a flowrow layout by @dessalines in [#1615](https://github.com/LemmyNet/jerboa/pull/1615)
- Some material 3 cleanups. by @dessalines in [#1614](https://github.com/LemmyNet/jerboa/pull/1614)
- Adding a few resources. by @dessalines in [#1617](https://github.com/LemmyNet/jerboa/pull/1617)
- Adding post divider back. by @dessalines in [#1616](https://github.com/LemmyNet/jerboa/pull/1616)
- Deduplicate posts in the feed by @MV-GH in [#1613](https://github.com/LemmyNet/jerboa/pull/1613)
- Bettering torrent support. by @dessalines in [#1612](https://github.com/LemmyNet/jerboa/pull/1612)
- Fix dialog for switching keyboards/languages by @MV-GH in [#1611](https://github.com/LemmyNet/jerboa/pull/1611)
- Adding renovate automerge. by @dessalines in [#1606](https://github.com/LemmyNet/jerboa/pull/1606)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.71...0.0.72

## What's Changed in 0.0.71

- Fix subscribed feed paging not working by @MV-GH in [#1597](https://github.com/LemmyNet/jerboa/pull/1597)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.70...0.0.71

## What's Changed in 0.0.70

- Fixing build script. by @dessalines in [#1595](https://github.com/LemmyNet/jerboa/pull/1595)
- Fix show back button when clicking on tagged user by @Tyoda in [#1579](https://github.com/LemmyNet/jerboa/pull/1579)
- Fix multiple spoiler blocks displaying incorrectly (#1577) by @Tyoda in [#1578](https://github.com/LemmyNet/jerboa/pull/1578)
- Bump compose bom by @MV-GH in [#1576](https://github.com/LemmyNet/jerboa/pull/1576)
- Update lemmy api to beta by @MV-GH in [#1573](https://github.com/LemmyNet/jerboa/pull/1573)
- update-tr-language by @mikropsoft in [#1575](https://github.com/LemmyNet/jerboa/pull/1575)
- Remove renovate bot from git cliff changelogs. by @dessalines in [#1563](https://github.com/LemmyNet/jerboa/pull/1563)
- Removing renovate schedule. by @dessalines in [#1555](https://github.com/LemmyNet/jerboa/pull/1555)
- Fix initial page_cursor by @MV-GH in [#1553](https://github.com/LemmyNet/jerboa/pull/1553)

## New Contributors

- @Tyoda made their first contribution in [#1579](https://github.com/LemmyNet/jerboa/pull/1579)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.69...0.0.70

## What's Changed in 0.0.69

- Improve baseline documentation + update baseline profiles by @MV-GH in [#1551](https://github.com/LemmyNet/jerboa/pull/1551)
- Fix edgecase with legacy show_scores by @MV-GH in [#1552](https://github.com/LemmyNet/jerboa/pull/1552)
- Restore legacy 'Show Scores' option by @MV-GH in [#1544](https://github.com/LemmyNet/jerboa/pull/1544)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.68...0.0.69

## What's Changed in 0.0.68

- Add blocks screen by @MV-GH in [#1545](https://github.com/LemmyNet/jerboa/pull/1545)
- Fix Loading bar clipping behind content by @MV-GH in [#1540](https://github.com/LemmyNet/jerboa/pull/1540)
- Reduce recompositions due to TopAppBar by @MV-GH in [#1542](https://github.com/LemmyNet/jerboa/pull/1542)
- Increase fontsize votes slightly by @MV-GH in [#1549](https://github.com/LemmyNet/jerboa/pull/1549)
- Hide Metadata outline when no metadata is shown by @MV-GH in [#1548](https://github.com/LemmyNet/jerboa/pull/1548)
- Use AsyncPainter where possible by @MV-GH in [#1543](https://github.com/LemmyNet/jerboa/pull/1543)
- Update the default instance list by @MV-GH in [#1550](https://github.com/LemmyNet/jerboa/pull/1550)
- Rename Activity nomenclature to Screen by @MV-GH in [#1546](https://github.com/LemmyNet/jerboa/pull/1546)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.67...0.0.68

## What's Changed in 0.0.67

- Adding LocalUserVoteDisplayMode for saving, and displaying votes. by @dessalines in [#1529](https://github.com/LemmyNet/jerboa/pull/1529)
- Add baseline profile info + update baseline profile by @MV-GH in [#1538](https://github.com/LemmyNet/jerboa/pull/1538)
- Refactor deprecated M1 SwipeToDismiss, use M3 SwipeToDismissBox by @MV-GH in [#1537](https://github.com/LemmyNet/jerboa/pull/1537)
- Show when banned on view-votes screen. by @dessalines in [#1536](https://github.com/LemmyNet/jerboa/pull/1536)
- Performance refactors for Posts feed by @MV-GH in [#1527](https://github.com/LemmyNet/jerboa/pull/1527)
- Fix #1533 use M3 PullToRefresh by @MV-GH in [#1534](https://github.com/LemmyNet/jerboa/pull/1534)
- Adding alt_text and custom_thumbnail to post form. Fixes #1513 by @dessalines in [#1528](https://github.com/LemmyNet/jerboa/pull/1528)
- Use chris banes compose-bom. by @dessalines in [#1530](https://github.com/LemmyNet/jerboa/pull/1530)
- Adding post hiding. by @dessalines in [#1517](https://github.com/LemmyNet/jerboa/pull/1517)
- Revert "Set specific versions for runtime-livedata." by @dessalines in [#1526](https://github.com/LemmyNet/jerboa/pull/1526)
- Perf improvement: enable Strong Skipping by @MV-GH in [#1524](https://github.com/LemmyNet/jerboa/pull/1524)
- chore(deps): update kotlin monorepo to v2 (major) by @MV-GH in [#1523](https://github.com/LemmyNet/jerboa/pull/1523)
- Fix content description for upvotes by @MV-GH in [#1516](https://github.com/LemmyNet/jerboa/pull/1516)
- Set specific versions for runtime-livedata. by @dessalines in [#1502](https://github.com/LemmyNet/jerboa/pull/1502)
- Removing ben-names_versions. by @dessalines in [#1501](https://github.com/LemmyNet/jerboa/pull/1501)
- Update LemmyApi by @MV-GH in [#1486](https://github.com/LemmyNet/jerboa/pull/1486)
- Use Compose BOM by @MV-GH in [#1485](https://github.com/LemmyNet/jerboa/pull/1485)
- Upgrading deps. by @dessalines in [#1482](https://github.com/LemmyNet/jerboa/pull/1482)
- chore: fix some comments by @TechVest in [#1480](https://github.com/LemmyNet/jerboa/pull/1480)

## New Contributors

- @renovate[bot] made their first contribution in [#1535](https://github.com/LemmyNet/jerboa/pull/1535)
- @TechVest made their first contribution in [#1480](https://github.com/LemmyNet/jerboa/pull/1480)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.66...0.0.67

## What's Changed in 0.0.66

- Bump deps by @MV-GH in [#1475](https://github.com/LemmyNet/jerboa/pull/1475)
- Fix Saved comments footer empty space doesn't open post by @MV-GH in [#1472](https://github.com/LemmyNet/jerboa/pull/1472)
- Fix Saved comments header does nothing by @MV-GH in [#1473](https://github.com/LemmyNet/jerboa/pull/1473)
- Add tr locales by @mikropsoft in [#1477](https://github.com/LemmyNet/jerboa/pull/1477)
- Moving to new LemmyNet repo. by @dessalines in [#1470](https://github.com/LemmyNet/jerboa/pull/1470)
- Removing cardano, this was never used. by @dessalines in [#1467](https://github.com/LemmyNet/jerboa/pull/1467)

## New Contributors

- @mikropsoft made their first contribution in [#1477](https://github.com/LemmyNet/jerboa/pull/1477)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.65...0.0.66

## What's Changed in 0.0.65

- Fixing a few other top app bar title sizes. by @dessalines in [#1466](https://github.com/LemmyNet/jerboa/pull/1466)
- Rework fontsize, better defaults by @MV-GH in [#1452](https://github.com/LemmyNet/jerboa/pull/1452)
- Upgrading deps. by @dessalines in [#1464](https://github.com/LemmyNet/jerboa/pull/1464)
- Fix email required error when saving user settings when no email set on email required instance by @MV-GH in [#1463](https://github.com/LemmyNet/jerboa/pull/1463)
- Prevents posts from reloading when changing the comments sorting by @rodrigo-fm in [#1462](https://github.com/LemmyNet/jerboa/pull/1462)

## New Contributors

- @rodrigo-fm made their first contribution in [#1462](https://github.com/LemmyNet/jerboa/pull/1462)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.64...0.0.65

## What's Changed in 0.0.64

- Fixing woodpecker. by @dessalines in [#1460](https://github.com/LemmyNet/jerboa/pull/1460)
- Moving to a new preferences library. by @dessalines in [#1451](https://github.com/LemmyNet/jerboa/pull/1451)
- Subscriptions list ignores case when sorting by @MV-GH in [#1457](https://github.com/LemmyNet/jerboa/pull/1457)
- Add community mods and admins to sidebars. Fixes #1343 by @dessalines in [#1450](https://github.com/LemmyNet/jerboa/pull/1450)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.63...0.0.64

## What's Changed in 0.0.63

- Correcting a few wrong defaults. by @dessalines in [#1448](https://github.com/LemmyNet/jerboa/pull/1448)
- Fix migrations 1 by @dessalines in [#1446](https://github.com/LemmyNet/jerboa/pull/1446)
- Fix Strikethrough rendering by @MV-GH in [#1445](https://github.com/LemmyNet/jerboa/pull/1445)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.62...0.0.63

## What's Changed in 0.0.62

- Fix migration changing the the app settings by @MV-GH in [#1442](https://github.com/LemmyNet/jerboa/pull/1442)
- Fix issues caused by edge to edge by @MV-GH in [#1443](https://github.com/LemmyNet/jerboa/pull/1443)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.61...0.0.62

## What's Changed in 0.0.61

- Fix generate changelog line. by @dessalines
- Fixing donation line. by @dessalines in [#1435](https://github.com/LemmyNet/jerboa/pull/1435)
- Upgrading deps. by @dessalines in [#1434](https://github.com/LemmyNet/jerboa/pull/1434)
- Use edge to edge by @MV-GH in [#1351](https://github.com/LemmyNet/jerboa/pull/1351)
- Add non mirrored only votes swipe action by @MV-GH in [#1421](https://github.com/LemmyNet/jerboa/pull/1421)
- More horizontal line fixes. by @dessalines in [#1423](https://github.com/LemmyNet/jerboa/pull/1423)
- Fix saving webm videos by @MV-GH in [#1429](https://github.com/LemmyNet/jerboa/pull/1429)
- Update Ukrainian translation by @stanol in [#1432](https://github.com/LemmyNet/jerboa/pull/1432)
- Add first frame video support for comment videos by @MV-GH in [#1428](https://github.com/LemmyNet/jerboa/pull/1428)
- Update Bulgarian translation by @salif in [#1425](https://github.com/LemmyNet/jerboa/pull/1425)
- Removing bars on non-top level comments. Fixes #1388 by @dessalines in [#1422](https://github.com/LemmyNet/jerboa/pull/1422)
- Fix git cliff by @dessalines in [#1419](https://github.com/LemmyNet/jerboa/pull/1419)
- Remove v0.18.4 and less workaround for missing timezone in datetime by @MV-GH in [#1418](https://github.com/LemmyNet/jerboa/pull/1418)
- Fix super and sub script not being properly rendered by @MV-GH in [#1410](https://github.com/LemmyNet/jerboa/pull/1410)
- Add nlnet grant line in readme. by @dessalines in [#1415](https://github.com/LemmyNet/jerboa/pull/1415)
- Fix admin actions visibility for non admin mods by @MV-GH in [#1412](https://github.com/LemmyNet/jerboa/pull/1412)
- Fix that non admin mods can see messages report tab by @MV-GH in [#1404](https://github.com/LemmyNet/jerboa/pull/1404)
- Fix sidebar showing admin screen to all users by @MV-GH in [#1401](https://github.com/LemmyNet/jerboa/pull/1401)
- Adding additional vote display modes by @dessalines in [#1378](https://github.com/LemmyNet/jerboa/pull/1378)
- Show full error on post creation failing by @MV-GH in [#1396](https://github.com/LemmyNet/jerboa/pull/1396)
- Adding mod ability to distinguish comment. by @dessalines in [#1381](https://github.com/LemmyNet/jerboa/pull/1381)
- Delaying marquee for 4 seconds. #1390 by @dessalines in [#1391](https://github.com/LemmyNet/jerboa/pull/1391)
- Fix sizing by not using multipliers. Fixes #1385 by @dessalines in [#1386](https://github.com/LemmyNet/jerboa/pull/1386)
- Bump deps + Update baselines profiles by @MV-GH in [#1389](https://github.com/LemmyNet/jerboa/pull/1389)
- Adding legal / privacy policy info. by @dessalines in [#1382](https://github.com/LemmyNet/jerboa/pull/1382)
- Upgrading git-cliff init. by @dessalines in [#1387](https://github.com/LemmyNet/jerboa/pull/1387)

## New Contributors

- @stanol made their first contribution in [#1432](https://github.com/LemmyNet/jerboa/pull/1432)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.59...0.0.61

## What's Changed in 0.0.59

- Adding lintVitalRelease to CI by @dessalines in [#1383](https://github.com/LemmyNet/jerboa/pull/1383)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.58...0.0.59

## What's Changed in 0.0.58

- Adding ability to ban users from profile pages. by @dessalines in [#1380](https://github.com/LemmyNet/jerboa/pull/1380)
- Fixing view votes crash. Fixes #1357 by @dessalines in [#1379](https://github.com/LemmyNet/jerboa/pull/1379)
- Add report queue by @dessalines in [#1360](https://github.com/LemmyNet/jerboa/pull/1360)
- Upgrading deps. by @dessalines in [#1377](https://github.com/LemmyNet/jerboa/pull/1377)
- Adding asset to git. by @dessalines in [#1376](https://github.com/LemmyNet/jerboa/pull/1376)
- Remove twizmwazin from codeowners. by @dessalines in [#1375](https://github.com/LemmyNet/jerboa/pull/1375)
- Using muted federated names. by @dessalines in [#1368](https://github.com/LemmyNet/jerboa/pull/1368)
- Adding registration applications queue. by @dessalines in [#1339](https://github.com/LemmyNet/jerboa/pull/1339)
- Fix error not being displayed on post creation failing by @MV-GH in [#1374](https://github.com/LemmyNet/jerboa/pull/1374)
- Dont show metadata title if it matches the post title. by @dessalines in [#1370](https://github.com/LemmyNet/jerboa/pull/1370)
- Dont show comment content when its deleted or removed. by @dessalines in [#1371](https://github.com/LemmyNet/jerboa/pull/1371)
- Switch blurNSFW and postActionBarMode to use enums. by @dessalines in [#1369](https://github.com/LemmyNet/jerboa/pull/1369)
- Fixing comment bottom margin. Fixes #1365 by @dessalines in [#1367](https://github.com/LemmyNet/jerboa/pull/1367)
- Remove material1 LocalContentColor. by @dessalines in [#1366](https://github.com/LemmyNet/jerboa/pull/1366)
- Adding a locally generated changelog. by @dessalines in [#1355](https://github.com/LemmyNet/jerboa/pull/1355)
- Adding dependenciesInfo for f-droid builds. Fixes #1353 by @dessalines in [#1354](https://github.com/LemmyNet/jerboa/pull/1354)
- Adding release notes. by @dessalines

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.57...0.0.58

## What's Changed in 0.0.57

- Fixing community pages, used wrong ID_TYPE. Fixes #1348 by @dessalines in [#1350](https://github.com/LemmyNet/jerboa/pull/1350)
- Adding hack for swipe gesture Setting. #1338 by @dessalines in [#1349](https://github.com/LemmyNet/jerboa/pull/1349)
- Adding release notes. by @dessalines

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.56...0.0.57

## What's Changed in 0.0.56

- Making swipe colors softer. by @dessalines in [#1347](https://github.com/LemmyNet/jerboa/pull/1347)
- Swipe post/comment to upvote/downvote/reply/save by @Snow4DV in [#1327](https://github.com/LemmyNet/jerboa/pull/1327)
- Temp workaround for compose-settings height bug. by @dessalines in [#1345](https://github.com/LemmyNet/jerboa/pull/1345)
- Adding admin / mod view votes. by @dessalines in [#1331](https://github.com/LemmyNet/jerboa/pull/1331)
- Remove ImmutableList and add stability config file by @MV-GH in [#1342](https://github.com/LemmyNet/jerboa/pull/1342)
- InstantScores refactor + Int to Long changes by @MV-GH in [#1337](https://github.com/LemmyNet/jerboa/pull/1337)
- General changes by @MV-GH in [#1334](https://github.com/LemmyNet/jerboa/pull/1334)
- Upgrading deps. by @dessalines in [#1333](https://github.com/LemmyNet/jerboa/pull/1333)
- Add ability to Ban and Ban from Community for posts and comments. by @dessalines in [#1325](https://github.com/LemmyNet/jerboa/pull/1325)
- Feature post by @dessalines in [#1330](https://github.com/LemmyNet/jerboa/pull/1330)
- Adding ability to lock / unlock posts. #1182 by @dessalines in [#1328](https://github.com/LemmyNet/jerboa/pull/1328)
- Making botton app bar smaller when descriptions are hidden. by @dessalines in [#1329](https://github.com/LemmyNet/jerboa/pull/1329)
- Try removing meltwater cache. by @dessalines in [#1326](https://github.com/LemmyNet/jerboa/pull/1326)
- Post removing by @dessalines in [#1324](https://github.com/LemmyNet/jerboa/pull/1324)
- Adding ability to remove and restore comments. #1182 by @dessalines in [#1323](https://github.com/LemmyNet/jerboa/pull/1323)
- Fix loading more posts in community view for 0.18 instances by @MV-GH in [#1314](https://github.com/LemmyNet/jerboa/pull/1314)
- Fix initial default sort in community not being used by @MV-GH in [#1311](https://github.com/LemmyNet/jerboa/pull/1311)
- Woodpecker CI cache fixes by @Nutomic in [#1309](https://github.com/LemmyNet/jerboa/pull/1309)
- Adding release notes. by @dessalines

## New Contributors

- @Nutomic made their first contribution in [#1309](https://github.com/LemmyNet/jerboa/pull/1309)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.55...0.0.56

## What's Changed in 0.0.55

- Fix image upload by @MV-GH in [#1305](https://github.com/LemmyNet/jerboa/pull/1305)
- Fix Posts failed loading, retry by @MV-GH in [#1302](https://github.com/LemmyNet/jerboa/pull/1302)
- Improve Russian translations by @mittwerk in [#1300](https://github.com/LemmyNet/jerboa/pull/1300)
- Adding release notes. by @dessalines

## New Contributors

- @mittwerk made their first contribution in [#1300](https://github.com/LemmyNet/jerboa/pull/1300)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.54...0.0.55

## What's Changed in 0.0.54

- Fix logging in failing by @MV-GH in [#1295](https://github.com/LemmyNet/jerboa/pull/1295)
- Adding release notes. by @dessalines

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.53...0.0.54

## What's Changed in 0.0.53

- Fix empty url causing post creation failing by @MV-GH in [#1291](https://github.com/LemmyNet/jerboa/pull/1291)
- Better fix for API creation in Verification procedure by @MV-GH in [#1290](https://github.com/LemmyNet/jerboa/pull/1290)
- Updating releases.md by @dessalines

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.52...0.0.53

## What's Changed in 0.0.52

- Fix crash on opening profile anonymous by @MV-GH in [#1288](https://github.com/LemmyNet/jerboa/pull/1288)
- Fix crash on creating Post by @MV-GH in [#1287](https://github.com/LemmyNet/jerboa/pull/1287)
- Adding release notes. by @dessalines

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.51...0.0.52

## What's Changed in 0.0.51

- Increasing java memory to fix release build. by @dessalines in [#1286](https://github.com/LemmyNet/jerboa/pull/1286)
- Initial MVP integration with my BackwardsCompatibleAPI by @MV-GH in [#1284](https://github.com/LemmyNet/jerboa/pull/1284)
- v0.19 upgrade by @dessalines in [#1277](https://github.com/LemmyNet/jerboa/pull/1277)
- Fix edgecase crash when login fails causes crash by @MV-GH in [#1278](https://github.com/LemmyNet/jerboa/pull/1278)
- Upgrading kotlinter. by @dessalines in [#1275](https://github.com/LemmyNet/jerboa/pull/1275)
- Adding release notes. by @dessalines

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.50...0.0.51

## What's Changed in 0.0.50

- Upgrading deps and gradle to 8.4 by @dessalines in [#1271](https://github.com/LemmyNet/jerboa/pull/1271)
- Add BlurNsfwExceptFromNsfwCommunities to blur types by @MakcNmyc in [#1229](https://github.com/LemmyNet/jerboa/pull/1229)
- Fix missing deduplication guard on profile by @MV-GH in [#1269](https://github.com/LemmyNet/jerboa/pull/1269)
- Adding a theming guide. by @dessalines in [#1267](https://github.com/LemmyNet/jerboa/pull/1267)
- Upgrading deps. by @dessalines in [#1266](https://github.com/LemmyNet/jerboa/pull/1266)
- Fix CI instability by lowering gradle memory. by @dessalines in [#1265](https://github.com/LemmyNet/jerboa/pull/1265)
- Community: Add Share drop down in community view. by @lubosz in [#1255](https://github.com/LemmyNet/jerboa/pull/1255)
- Fix search list repopulating on return by @MV-GH in [#1260](https://github.com/LemmyNet/jerboa/pull/1260)
- Adding release notes. by @dessalines

## New Contributors

- @MakcNmyc made their first contribution in [#1229](https://github.com/LemmyNet/jerboa/pull/1229)
- @lubosz made their first contribution in [#1255](https://github.com/LemmyNet/jerboa/pull/1255)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.49...0.0.50

## What's Changed in 0.0.49

- Depend on upstream Cascade by @MV-GH in [#1256](https://github.com/LemmyNet/jerboa/pull/1256)
- Upgrading gradle to 8.3 by @dessalines in [#1253](https://github.com/LemmyNet/jerboa/pull/1253)
- Fixes missing comments causing `X more replies` to show instead the missing comments by @MV-GH in [#1240](https://github.com/LemmyNet/jerboa/pull/1240)
- Add Bulgarian translation by @salif in [#1252](https://github.com/LemmyNet/jerboa/pull/1252)
- Adding release notes. by @dessalines

## New Contributors

- @salif made their first contribution in [#1252](https://github.com/LemmyNet/jerboa/pull/1252)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.48...0.0.49

## What's Changed in 0.0.48

- Fix post delete not working from within PostActivity by @MV-GH in [#1249](https://github.com/LemmyNet/jerboa/pull/1249)
- Small refactor for shareLink and bump telephoto by @MV-GH in [#1246](https://github.com/LemmyNet/jerboa/pull/1246)
- Sort crash logs by new, and display elapsed time by @MV-GH in [#1244](https://github.com/LemmyNet/jerboa/pull/1244)
- Adding release notes. by @dessalines

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.47...0.0.48

## What's Changed in 0.0.47

- Move save to the topbar of user settings and deduplicate topbar code by @MV-GH in [#1243](https://github.com/LemmyNet/jerboa/pull/1243)
- Revert "Fix zooming difficulties" and temporary remove Swipe to exit in ImageViewer by @dessalines in [#1238](https://github.com/LemmyNet/jerboa/pull/1238)
- Fix datetime parse failures causing app crash by @MV-GH in [#1235](https://github.com/LemmyNet/jerboa/pull/1235)
- Fix zooming difficulties by @MV-GH in [#1237](https://github.com/LemmyNet/jerboa/pull/1237)
- Fix link not opening in Chrome If private tabs setting is on without CustomTab being on by @MV-GH in [#1234](https://github.com/LemmyNet/jerboa/pull/1234)
- Fix unread count not resetting when swapping to anon by @MV-GH in [#1233](https://github.com/LemmyNet/jerboa/pull/1233)
- Bump deps by @MV-GH in [#1236](https://github.com/LemmyNet/jerboa/pull/1236)
- Add Arabic Translations in [#1226](https://github.com/LemmyNet/jerboa/pull/1226)
- Refactor initiazable, Community navhost navigation and fix rare crash in inbox by @MV-GH in [#1210](https://github.com/LemmyNet/jerboa/pull/1210)
- Fix PullRefreshIndicator theming and placement by @MV-GH in [#1220](https://github.com/LemmyNet/jerboa/pull/1220)
- Adding release notes. by @dessalines

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.46...0.0.47

## What's Changed in 0.0.46

- Update profiles and instance list by @MV-GH in [#1219](https://github.com/LemmyNet/jerboa/pull/1219)
- Added dracula-theme, like in thumb-key by @0xfossman in [#1218](https://github.com/LemmyNet/jerboa/pull/1218)
- Support private tabs for FireFox by @dankeast in [#1216](https://github.com/LemmyNet/jerboa/pull/1216)
- Replace all relevant dialogs with Animated Center Popup menus by @MV-GH in [#1200](https://github.com/LemmyNet/jerboa/pull/1200)
- Add retry button when loading more posts fails by @MV-GH in [#1209](https://github.com/LemmyNet/jerboa/pull/1209)
- Fix wrong aligment sometimes for images in ImageViewer with RTL by @MV-GH in [#1215](https://github.com/LemmyNet/jerboa/pull/1215)
- Force HTTP links to HTTPS by @MV-GH in [#1214](https://github.com/LemmyNet/jerboa/pull/1214)
- Fix crash in share Media, fix failed download media by @MV-GH in [#1213](https://github.com/LemmyNet/jerboa/pull/1213)
- Add resource by @MV-GH in [#1207](https://github.com/LemmyNet/jerboa/pull/1207)
- Add connect on matrix by @MV-GH in [#1206](https://github.com/LemmyNet/jerboa/pull/1206)
- Sort drawer subscriptions on title instead by @MV-GH in [#1205](https://github.com/LemmyNet/jerboa/pull/1205)
- Add spoiler template action button by @MV-GH in [#1198](https://github.com/LemmyNet/jerboa/pull/1198)
- Make nav text smaller by @MV-GH in [#1201](https://github.com/LemmyNet/jerboa/pull/1201)
- Bump AGP by @MV-GH in [#1196](https://github.com/LemmyNet/jerboa/pull/1196)
- Bump deps by @MV-GH in [#1197](https://github.com/LemmyNet/jerboa/pull/1197)
- Fix read post on back by @MV-GH in [#1195](https://github.com/LemmyNet/jerboa/pull/1195)
- Fix broken Japanese string format for AppBars_users_month by @habbbe in [#1194](https://github.com/LemmyNet/jerboa/pull/1194)
- Controversial posts and comments by @iByteABit256 in [#1106](https://github.com/LemmyNet/jerboa/pull/1106)
- Adding release notes. by @dessalines

## New Contributors

- @0xfossman made their first contribution in [#1218](https://github.com/LemmyNet/jerboa/pull/1218)
- @dankeast made their first contribution in [#1216](https://github.com/LemmyNet/jerboa/pull/1216)
- @habbbe made their first contribution in [#1194](https://github.com/LemmyNet/jerboa/pull/1194)
- @iByteABit256 made their first contribution in [#1106](https://github.com/LemmyNet/jerboa/pull/1106)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.45...0.0.46

## What's Changed in 0.0.45

- Improve woodpecker build times by @MV-GH in [#1139](https://github.com/LemmyNet/jerboa/pull/1139)
- Update community block msg to display unblock if blocked already by @MV-GH in [#1185](https://github.com/LemmyNet/jerboa/pull/1185)
- Add setting to enable swipe to go back in Look and Feel settings by @sthomas727 in [#1191](https://github.com/LemmyNet/jerboa/pull/1191)
- Change the default of isCurrentlyConnected by @MV-GH in [#1181](https://github.com/LemmyNet/jerboa/pull/1181)
- Add long click link popup menu, including actions by @MV-GH in [#1189](https://github.com/LemmyNet/jerboa/pull/1189)
- Refactor sort type handling, and fetching initData/Posts, fixes default sort in community by @MV-GH in [#1166](https://github.com/LemmyNet/jerboa/pull/1166)
- Fix comment click deadzones by @camporter in [#1175](https://github.com/LemmyNet/jerboa/pull/1175)
- Update docs requirements by @MV-GH in [#1178](https://github.com/LemmyNet/jerboa/pull/1178)
- Update deps, notable update to kotlin 1.9 by @MV-GH in [#1173](https://github.com/LemmyNet/jerboa/pull/1173)
- Add option to auto play GIFs by @MV-GH in [#1164](https://github.com/LemmyNet/jerboa/pull/1164)
- Make accounts list in drawer scrollable by @MV-GH in [#1174](https://github.com/LemmyNet/jerboa/pull/1174)
- Update Norwegian Nynorsk translation by @huftis in [#1165](https://github.com/LemmyNet/jerboa/pull/1165)
- Updating releases.md by @dessalines

## New Contributors

- @sthomas727 made their first contribution in [#1191](https://github.com/LemmyNet/jerboa/pull/1191)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.44...0.0.45

## What's Changed in 0.0.44

- Fix appbar rework by @MV-GH in [#1160](https://github.com/LemmyNet/jerboa/pull/1160)
- Adding release notes. by @dessalines
- Post actionbar design rework and options for it. by @MV-GH in [#1155](https://github.com/LemmyNet/jerboa/pull/1155)
- Increase the zoom capability of the imageviewer by @MV-GH in [#1159](https://github.com/LemmyNet/jerboa/pull/1159)
- Allow post mode change in Community by @ZJouba in [#1158](https://github.com/LemmyNet/jerboa/pull/1158)
- Use M3 menu dropdown for inbox unread sort by @MV-GH in [#1152](https://github.com/LemmyNet/jerboa/pull/1152)
- Fixing woodpecker env vars. by @dessalines in [#1157](https://github.com/LemmyNet/jerboa/pull/1157)
- Update woodpecker config by @MV-GH in [#1138](https://github.com/LemmyNet/jerboa/pull/1138)
- Use listing type use M3 dropdown instead of dialog by @MV-GH in [#1150](https://github.com/LemmyNet/jerboa/pull/1150)
- Add 2FA user setting by @MV-GH in [#1137](https://github.com/LemmyNet/jerboa/pull/1137)
- Make app bar 'more' buttons use dropdowns rather than dialogs by @camporter in [#1146](https://github.com/LemmyNet/jerboa/pull/1146)
- Add strings for the validateUrl and validatePostName utils. by @camporter in [#1145](https://github.com/LemmyNet/jerboa/pull/1145)
- Add share functionality to imageviewer by @MV-GH in [#1144](https://github.com/LemmyNet/jerboa/pull/1144)
- Replace gson.tojson with Parcelable implementation by @MV-GH in [#1124](https://github.com/LemmyNet/jerboa/pull/1124)
- Small changes by @MV-GH in [#1133](https://github.com/LemmyNet/jerboa/pull/1133)
- Adding release notes. by @dessalines

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.42...0.0.44

## What's Changed in 0.0.42

- Enchance imageviewer: Downloadprogressbar, error placeholder + retry, large images subsampling, SVG support by @MV-GH in [#1111](https://github.com/LemmyNet/jerboa/pull/1111)
- Update drawer Subscriptions by @ZJouba in [#1131](https://github.com/LemmyNet/jerboa/pull/1131)
- Update moderator list to be a isModerator function by @MV-GH in [#1130](https://github.com/LemmyNet/jerboa/pull/1130)
- Go to comment when clicking on reply comment in inbox, fixes #1089 by @twizmwazin in [#1091](https://github.com/LemmyNet/jerboa/pull/1091)
- Mark as Read on Open and Scroll by @ZJouba in [#1025](https://github.com/LemmyNet/jerboa/pull/1025)
- Properly fix read more replies bug introduced in lemmy 18.3 by @MV-GH in [#1127](https://github.com/LemmyNet/jerboa/pull/1127)
- Use marquee for instance in topbar, and add it to communitysidebar by @MV-GH in [#1107](https://github.com/LemmyNet/jerboa/pull/1107)
- Remove account_approved and account_email_verified add account_banned checks by @MV-GH in [#1126](https://github.com/LemmyNet/jerboa/pull/1126)
- Respect show score user setting by @MV-GH in [#1122](https://github.com/LemmyNet/jerboa/pull/1122)
- Update deps, gradle and target SDK 34 by @MV-GH in [#1120](https://github.com/LemmyNet/jerboa/pull/1120)
- Add account verification, improve login error messages, fix "account removal" and more by @MV-GH in [#1099](https://github.com/LemmyNet/jerboa/pull/1099)
- Fix Inbox Spanish Translation, closes #1110 by @ogarcia in [#1118](https://github.com/LemmyNet/jerboa/pull/1118)
- Add direct message person option and screen by @MV-GH in [#1108](https://github.com/LemmyNet/jerboa/pull/1108)
- Temp fix for crash sometimes on read more replies to bug introduced in lemmy 18.3 by @MV-GH in [#1113](https://github.com/LemmyNet/jerboa/pull/1113)
- Add failure toast for creating comments by @MV-GH in [#1114](https://github.com/LemmyNet/jerboa/pull/1114)
- Add warning in instance file for auto generated instance list by @MV-GH in [#1112](https://github.com/LemmyNet/jerboa/pull/1112)
- Fix `Value X cannot be converted to json` in error messages by @MV-GH in [#1098](https://github.com/LemmyNet/jerboa/pull/1098)
- Add instance to community name by @MV-GH in [#1104](https://github.com/LemmyNet/jerboa/pull/1104)
- Add Norwegian Nynorsk translation by @huftis in [#1102](https://github.com/LemmyNet/jerboa/pull/1102)
- Add long-form ‘Top All’ string by @huftis in [#1103](https://github.com/LemmyNet/jerboa/pull/1103)
- Fetch unreadCount on refresh, make unreadCount use less API calls, fix mark unread reply bug by @MV-GH in [#1086](https://github.com/LemmyNet/jerboa/pull/1086)
- Scroll to top if homepage button pressed when on the home... (#1080) by @aaronkh in [#1092](https://github.com/LemmyNet/jerboa/pull/1092)
- Don't show posts as read in saved and profile views by @twizmwazin in [#1090](https://github.com/LemmyNet/jerboa/pull/1090)
- Add switch to anonymous option by @MV-GH in [#1084](https://github.com/LemmyNet/jerboa/pull/1084)
- Prevent duplicate accounts from logging in by @MV-GH in [#1082](https://github.com/LemmyNet/jerboa/pull/1082)
- Fix menubar icon when viewing someones profile by @MV-GH in [#1083](https://github.com/LemmyNet/jerboa/pull/1083)
- Adding release notes. by @dessalines

## New Contributors

- @ogarcia made their first contribution in [#1118](https://github.com/LemmyNet/jerboa/pull/1118)
- @huftis made their first contribution in [#1102](https://github.com/LemmyNet/jerboa/pull/1102)
- @aaronkh made their first contribution in [#1092](https://github.com/LemmyNet/jerboa/pull/1092)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.41...0.0.42

## What's Changed in 0.0.41

- Don't color posts as read in post activity by @twizmwazin in [#1076](https://github.com/LemmyNet/jerboa/pull/1076)
- Add spacer to bottom of comment lists by @twizmwazin in [#1074](https://github.com/LemmyNet/jerboa/pull/1074)
- Show comment divider inside the left side border by @twizmwazin in [#1075](https://github.com/LemmyNet/jerboa/pull/1075)
- Update strings.xml for `ko` by @meinside in [#1073](https://github.com/LemmyNet/jerboa/pull/1073)
- Move unExpandedComments and commentsWithToggledActionBar from PostActivity to PostViewModel by @twizmwazin in [#1068](https://github.com/LemmyNet/jerboa/pull/1068)
- Retain scroll position of feed after swapping navigation by @MV-GH in [#1072](https://github.com/LemmyNet/jerboa/pull/1072)
- Increase timeout of connections to 30s by @MV-GH in [#1071](https://github.com/LemmyNet/jerboa/pull/1071)
- Make images in markdown open in imageviewer by @MV-GH in [#1069](https://github.com/LemmyNet/jerboa/pull/1069)
- Fix links looking like lemmy links being used as one by @MV-GH in [#1048](https://github.com/LemmyNet/jerboa/pull/1048)
- Fix dropdown popup issue by @MV-GH in [#1063](https://github.com/LemmyNet/jerboa/pull/1063)
- Add error handling for saving images by @MV-GH in [#1066](https://github.com/LemmyNet/jerboa/pull/1066)
- Add caret to indicate thumbnails that open in the image viewer by @LilithSilver in [#932](https://github.com/LemmyNet/jerboa/pull/932)
- Add divider to section for report actions by @MV-GH in [#1064](https://github.com/LemmyNet/jerboa/pull/1064)
- Add contenttypes to lazy columns by @MV-GH in [#984](https://github.com/LemmyNet/jerboa/pull/984)
- Update bug report to include how to get the full logs step by @MV-GH in [#1059](https://github.com/LemmyNet/jerboa/pull/1059)
- Clean up strings.xml by @MV-GH in [#1056](https://github.com/LemmyNet/jerboa/pull/1056)
- Update Brazilian Portuguese translation by @Gustavo-Martins in [#1062](https://github.com/LemmyNet/jerboa/pull/1062)
- Update Azerbaijani translation by @Fish25op in [#1058](https://github.com/LemmyNet/jerboa/pull/1058)
- Create strings.xml by @Fish25op in [#1052](https://github.com/LemmyNet/jerboa/pull/1052)
- Fix unread inbox counter badge not properly updating by @MV-GH in [#1051](https://github.com/LemmyNet/jerboa/pull/1051)
- Fix thumbnail link not opening in SmallCard mode by @MV-GH in [#1055](https://github.com/LemmyNet/jerboa/pull/1055)
- Make datatypes Parcelable and Stable by @MV-GH in [#1044](https://github.com/LemmyNet/jerboa/pull/1044)
- Improve stableness by @MV-GH in [#1040](https://github.com/LemmyNet/jerboa/pull/1040)
- Change all issue templates to github issue forms by @bappitybup in [#1031](https://github.com/LemmyNet/jerboa/pull/1031)
- Fix image viewer adding unneeded padding on android 10 and below by @MV-GH in [#1046](https://github.com/LemmyNet/jerboa/pull/1046)
- Navigate to home tab after selecting feed type in sidebar by @twizmwazin in [#1049](https://github.com/LemmyNet/jerboa/pull/1049)
- Add missing and shorten some german translations (fixes #818) by @yjiang-yh135 in [#1047](https://github.com/LemmyNet/jerboa/pull/1047)
- Adding release notes. by @dessalines

## New Contributors

- @LilithSilver made their first contribution in [#932](https://github.com/LemmyNet/jerboa/pull/932)
- @Gustavo-Martins made their first contribution in [#1062](https://github.com/LemmyNet/jerboa/pull/1062)
- @Fish25op made their first contribution in [#1058](https://github.com/LemmyNet/jerboa/pull/1058)
- @bappitybup made their first contribution in [#1031](https://github.com/LemmyNet/jerboa/pull/1031)
- @yjiang-yh135 made their first contribution in [#1047](https://github.com/LemmyNet/jerboa/pull/1047)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.40...0.0.41

## What's Changed in 0.0.40

- Auto fill in community name for posts created in community by @MV-GH in [#1030](https://github.com/LemmyNet/jerboa/pull/1030)
- Add option to show/hide post previews by @twizmwazin in [#1033](https://github.com/LemmyNet/jerboa/pull/1033)
- Revert #767 should fix post opens the wrong one sometimes by @MV-GH in [#1029](https://github.com/LemmyNet/jerboa/pull/1029)
- Fix rare edgecase crash when deleting app DB by @MV-GH in [#1028](https://github.com/LemmyNet/jerboa/pull/1028)
- Change default of show_parent_comment_navigation_buttons by @MV-GH in [#1027](https://github.com/LemmyNet/jerboa/pull/1027)
- Fix thumbnail links not working with deeplinks by @MV-GH in [#1023](https://github.com/LemmyNet/jerboa/pull/1023)
- Add general small fixes by @MV-GH in [#1026](https://github.com/LemmyNet/jerboa/pull/1026)
- Update italian translations by @andscape-dev in [#1024](https://github.com/LemmyNet/jerboa/pull/1024)
- Add local crash tracking and viewing by @camporter in [#945](https://github.com/LemmyNet/jerboa/pull/945)
- db refactor by @yate in [#973](https://github.com/LemmyNet/jerboa/pull/973)
- Fix and update of the Ukrainian translation by @Digharatta in [#1022](https://github.com/LemmyNet/jerboa/pull/1022)
- Indicate persons that are bots by @camporter in [#1019](https://github.com/LemmyNet/jerboa/pull/1019)
- Adding spoiler tag support by @ZJouba in [#990](https://github.com/LemmyNet/jerboa/pull/990)
- Add confirmation on exit by @MV-GH in [#998](https://github.com/LemmyNet/jerboa/pull/998)
- Pull-to-refresh fix on post loading fail by @ZJouba in [#1014](https://github.com/LemmyNet/jerboa/pull/1014)
- Add separate screen for ImageViewer by @MV-GH in [#980](https://github.com/LemmyNet/jerboa/pull/980)
- Update baseline profiles by @MV-GH in [#1013](https://github.com/LemmyNet/jerboa/pull/1013)
- Adding release notes. by @dessalines

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.39...0.0.40

## What's Changed in 0.0.39

- Add drawer to all main tabs and general drawer changes by @MV-GH in [#991](https://github.com/LemmyNet/jerboa/pull/991)
- Add markdown preview by @MV-GH in [#1002](https://github.com/LemmyNet/jerboa/pull/1002)
- Fix inbox loading all pages on startup instead of set unread/all mode by @MV-GH in [#1007](https://github.com/LemmyNet/jerboa/pull/1007)
- Adding MV-GH to codeowners. by @dessalines
- Update instance list by @MV-GH in [#982](https://github.com/LemmyNet/jerboa/pull/982)
- Add toggle in settings to hide descriptions in navbar by @shtrophic in [#995](https://github.com/LemmyNet/jerboa/pull/995)
- Move more viewmodels into com.jerboa.model package by @twizmwazin in [#1000](https://github.com/LemmyNet/jerboa/pull/1000)
- Update CONTRIBUTION.md by @MV-GH in [#997](https://github.com/LemmyNet/jerboa/pull/997)
- Add Ukrainian Language Support to Jerboa - values-uk strings.xml by @Digharatta in [#996](https://github.com/LemmyNet/jerboa/pull/996)
- Move view models to com.jerboa.model by @twizmwazin in [#994](https://github.com/LemmyNet/jerboa/pull/994)
- Fix recompositions due to autofill by @MV-GH in [#975](https://github.com/LemmyNet/jerboa/pull/975)
- Improve login form by @MV-GH in [#953](https://github.com/LemmyNet/jerboa/pull/953)
- Add "..." for too long community names by @MV-GH in [#985](https://github.com/LemmyNet/jerboa/pull/985)
- Fix regression due to deprecations change by @MV-GH in [#986](https://github.com/LemmyNet/jerboa/pull/986)
- Fix clear backstack not working properly by @MV-GH in [#988](https://github.com/LemmyNet/jerboa/pull/988)
- Add DB migrations tests by @MV-GH in [#899](https://github.com/LemmyNet/jerboa/pull/899)
- Update strings.xml for `ko` by @meinside in [#965](https://github.com/LemmyNet/jerboa/pull/965)
- Fixes in Greek Translation by @sv1sjp in [#971](https://github.com/LemmyNet/jerboa/pull/971)
- Update subscribed communities in sidebar when user subscribes by @twizmwazin in [#948](https://github.com/LemmyNet/jerboa/pull/948)
- Use overloaded AndroidView in MarkdownHelper by @yate in [#767](https://github.com/LemmyNet/jerboa/pull/767)
- Fix deprecations by @MV-GH in [#962](https://github.com/LemmyNet/jerboa/pull/962)
- Updating releases.md by @dessalines

## New Contributors

- @shtrophic made their first contribution in [#995](https://github.com/LemmyNet/jerboa/pull/995)
- @Digharatta made their first contribution in [#996](https://github.com/LemmyNet/jerboa/pull/996)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.38...0.0.39

## What's Changed in 0.0.38

- Fix private messages being sent to yourself by @MV-GH in [#960](https://github.com/LemmyNet/jerboa/pull/960)
- Add gitattributes file by @MV-GH in [#956](https://github.com/LemmyNet/jerboa/pull/956)
- Show the users' federated name in drawer. by @camporter in [#937](https://github.com/LemmyNet/jerboa/pull/937)
- Replace "Posta in arrivo" with "Notifiche" by @andreaippo in [#826](https://github.com/LemmyNet/jerboa/pull/826)
- Fix loading indicator and loading bar issues by @MV-GH in [#809](https://github.com/LemmyNet/jerboa/pull/809)
- Fix some broken Dutch by @frankivo in [#961](https://github.com/LemmyNet/jerboa/pull/961)
- Add ability to share posts. by @camporter in [#543](https://github.com/LemmyNet/jerboa/pull/543)
- Add ability to share posts. by @camporter
- Add Greek Language Support on Jerboa - values-el strings.xml by @sv1sjp in [#944](https://github.com/LemmyNet/jerboa/pull/944)
- Show main sidebar content even when account switcher is shown by @twizmwazin in [#947](https://github.com/LemmyNet/jerboa/pull/947)
- Fix JWT from being logged by @MV-GH in [#946](https://github.com/LemmyNet/jerboa/pull/946)
- Update to compose 1.5.0-beta03 to fix crashes when animationDurationScale is disabled by @MV-GH in [#930](https://github.com/LemmyNet/jerboa/pull/930)
- Fix some FR translation typos by @MKabe in [#933](https://github.com/LemmyNet/jerboa/pull/933)
- When drawer is open, close it if back is pressed. by @camporter in [#941](https://github.com/LemmyNet/jerboa/pull/941)
- General small refactors by @MV-GH in [#942](https://github.com/LemmyNet/jerboa/pull/942)
- Fix jerboa icon resize. by @camporter in [#943](https://github.com/LemmyNet/jerboa/pull/943)
- Fix crash 0.37 due to missing SortTypes by @MV-GH in [#918](https://github.com/LemmyNet/jerboa/pull/918)
- Make updateInstances task compatible with configuration cache by @MV-GH in [#915](https://github.com/LemmyNet/jerboa/pull/915)
- Merge branch 'main' into config-cache by @MV-GH
- Adding Show Post Source button by @MV-GH in [#901](https://github.com/LemmyNet/jerboa/pull/901)
- Fixing lint issue by @IzakJoubert
- Merge branch 'issue/506' of https://github.com/ZJouba/jerboa into issue/506 by @IzakJoubert
- Merge branch 'main' into issue/506 by @ZJouba
- Remove `app_name` from strings.xml for `ko` by @MV-GH in [#924](https://github.com/LemmyNet/jerboa/pull/924)
- Remove `app_name` from strings.xml for `ko` by @meinside
- Changing button text with Boolean by @IzakJoubert
- Merge remote-tracking branch 'upstream/main' into issue/506 by @IzakJoubert
- Removing unknown translations by @IzakJoubert
- Fixing lint issues by @IzakJoubert
- Adding Show Post Source button by @IzakJoubert
- Make updateInstances task compatible with configuration cache by @AppearamidGuy
- Edit strings.xml for `ko` by @meinside in [#909](https://github.com/LemmyNet/jerboa/pull/909)
- Adding release notes. by @dessalines

## New Contributors

- @frankivo made their first contribution in [#961](https://github.com/LemmyNet/jerboa/pull/961)
- @sv1sjp made their first contribution in [#944](https://github.com/LemmyNet/jerboa/pull/944)
- @MKabe made their first contribution in [#933](https://github.com/LemmyNet/jerboa/pull/933)
- @IzakJoubert made their first contribution
- @ZJouba made their first contribution
- @AppearamidGuy made their first contribution

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.37...0.0.38

## What's Changed in 0.0.37

- Change app display name by @Undearius in [#904](https://github.com/LemmyNet/jerboa/pull/904)
- Update baseline profiles by @MV-GH in [#906](https://github.com/LemmyNet/jerboa/pull/906)
- Fix edit comment view text is being empty by @MV-GH in [#897](https://github.com/LemmyNet/jerboa/pull/897)
- Add blur option, fix non blurred community icons/banners by @MV-GH in [#896](https://github.com/LemmyNet/jerboa/pull/896)
- Fix .zoomable functionality in ImageViewerDialog and add "swipe up to close" in [#894](https://github.com/LemmyNet/jerboa/pull/894)
- Fix downvote on post in postview doing a upvote by @MV-GH in [#893](https://github.com/LemmyNet/jerboa/pull/893)
- Nav scoped view models by @nahwneeth in [#817](https://github.com/LemmyNet/jerboa/pull/817)
- Adding release notes. by @dessalines

## New Contributors

- @Undearius made their first contribution in [#904](https://github.com/LemmyNet/jerboa/pull/904)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.36...0.0.37

## What's Changed in 0.0.36

- Add generate lemmy instance list gradle task by @MV-GH in [#884](https://github.com/LemmyNet/jerboa/pull/884)
- Bump dependencies by @MV-GH in [#881](https://github.com/LemmyNet/jerboa/pull/881)
- Reworked PickImage() composable in [#877](https://github.com/LemmyNet/jerboa/pull/877)
- QOL Remove unnecessary clickable areas from PostListingList by @lbenedetto in [#710](https://github.com/LemmyNet/jerboa/pull/710)
- Consolidate composables for CreatePost and PostEdit in [#860](https://github.com/LemmyNet/jerboa/pull/860)
- Add backwards compatible language picker by @MV-GH in [#873](https://github.com/LemmyNet/jerboa/pull/873)
- Add totp field to login by @MV-GH in [#868](https://github.com/LemmyNet/jerboa/pull/868)
- Include taglines with the scrollable post view by @beatgammit in [#875](https://github.com/LemmyNet/jerboa/pull/875)
- Catch network errors and show a toast by @beatgammit in [#874](https://github.com/LemmyNet/jerboa/pull/874)
- Improved Spanish translation by @Noxor11 in [#870](https://github.com/LemmyNet/jerboa/pull/870)
- Update bug report to include detailed ways to get logs by @MV-GH in [#864](https://github.com/LemmyNet/jerboa/pull/864)
- Switch inbox & profile tabs with swipe gesture even whey they are empty. by @nahwneeth in [#861](https://github.com/LemmyNet/jerboa/pull/861)
- Bottom nav bar and screen transitions by @nahwneeth in [#855](https://github.com/LemmyNet/jerboa/pull/855)
- Convert the groovy gradle files to kotlin DSL by @MV-GH in [#858](https://github.com/LemmyNet/jerboa/pull/858)
- Copy post and comment information to clipboard by @Noxor11 in [#850](https://github.com/LemmyNet/jerboa/pull/850)
- Fix default sort and listing type to read from db by @beatgammit in [#854](https://github.com/LemmyNet/jerboa/pull/854)
- Update French translation by @julroy67 in [#853](https://github.com/LemmyNet/jerboa/pull/853)
- Added switch to mark new posts as NSFW / toggle tag on existing posts in [#833](https://github.com/LemmyNet/jerboa/pull/833)
- Fix for scrolling up past images in the comments jerks you back down by @nahwneeth in [#845](https://github.com/LemmyNet/jerboa/pull/845)
- Make Search Page have consistent back button by @scme0 in [#849](https://github.com/LemmyNet/jerboa/pull/849)
- Fix passwords in plaintext on logcat by @CharlieGitDB in [#834](https://github.com/LemmyNet/jerboa/pull/834)
- Enable swiping on comments to go back to posts by @CharlieGitDB in [#785](https://github.com/LemmyNet/jerboa/pull/785)
- Fix: Add Account fails when full server uri is used in [#805](https://github.com/LemmyNet/jerboa/pull/805)
- Fixing incorrect dk language code. The correct one is da. by @dessalines in [#819](https://github.com/LemmyNet/jerboa/pull/819)
- Adding release notes. by @dessalines

## New Contributors

- @Noxor11 made their first contribution in [#870](https://github.com/LemmyNet/jerboa/pull/870)
- @julroy67 made their first contribution in [#853](https://github.com/LemmyNet/jerboa/pull/853)
- @scme0 made their first contribution in [#849](https://github.com/LemmyNet/jerboa/pull/849)
- @CharlieGitDB made their first contribution in [#834](https://github.com/LemmyNet/jerboa/pull/834)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.35...0.0.36

## What's Changed in 0.0.35

- Fix thumbnail links being treated as images. Fixes #815 by @dessalines in [#816](https://github.com/LemmyNet/jerboa/pull/816)
- Check server version against app minimum api version by @beatgammit in [#737](https://github.com/LemmyNet/jerboa/pull/737)
- Fix links not working when exclamation mark before it by @MV-GH in [#802](https://github.com/LemmyNet/jerboa/pull/802)
- Add dynamic splash screen colors by @shezdy in [#800](https://github.com/LemmyNet/jerboa/pull/800)
- Expands german translations by @sabieber in [#801](https://github.com/LemmyNet/jerboa/pull/801)
- Adds nsfw tag to nsfw posts by @Chris-Kropp in [#786](https://github.com/LemmyNet/jerboa/pull/786)
- Russian translation & Fixes of non-localized strings by @Snow4DV in [#794](https://github.com/LemmyNet/jerboa/pull/794)
- Fix bug with pictrs url query param rewriting by @beatgammit in [#735](https://github.com/LemmyNet/jerboa/pull/735)
- Changed icons for "Go to comment" and "Copy permalink" in CommmentOptionsDialog in [#781](https://github.com/LemmyNet/jerboa/pull/781)
- Quick comment navigation by @yate in [#749](https://github.com/LemmyNet/jerboa/pull/749)
- Add benchmarks for TypicalUserJourney, Enable baselineprofile generator, and some metrics by @MV-GH in [#601](https://github.com/LemmyNet/jerboa/pull/601)
- Remove remaining voteToggle unused translations by @Eskuero in [#776](https://github.com/LemmyNet/jerboa/pull/776)
- fix: Comment replies icon appears in profile #533 by @LufyCZ in [#726](https://github.com/LemmyNet/jerboa/pull/726)
- Add CI dependency caching in between steps by @dessalines in [#763](https://github.com/LemmyNet/jerboa/pull/763)
- Add rough rudimentary CONTRIBUTING.md by @MV-GH in [#753](https://github.com/LemmyNet/jerboa/pull/753)
- Account Settings: Use Alorma Compose Settings Components by @sabieber in [#731](https://github.com/LemmyNet/jerboa/pull/731)
- More Themes (Dark + Light) by @bynatejones in [#750](https://github.com/LemmyNet/jerboa/pull/750)
- App settings should be visible if the user is not logged in by @yate in [#764](https://github.com/LemmyNet/jerboa/pull/764)
- Added blur for Android version older than 12 in [#746](https://github.com/LemmyNet/jerboa/pull/746)
- Fixing test failure. by @dessalines in [#762](https://github.com/LemmyNet/jerboa/pull/762)
- Fix links not working in markdown tables by @MV-GH in [#744](https://github.com/LemmyNet/jerboa/pull/744)
- Added close on click functionality to ImageViewerDialog. Solves #736 in [#738](https://github.com/LemmyNet/jerboa/pull/738)
- missing monochrome tags for adaptive icons by @JosephGaiser in [#728](https://github.com/LemmyNet/jerboa/pull/728)
- voteToggle unused resource removed from string resources by @scottmmjackson in [#699](https://github.com/LemmyNet/jerboa/pull/699)
- Fixes image saving crashing on Android 9 and below by @MV-GH in [#696](https://github.com/LemmyNet/jerboa/pull/696)
- Remove redundant curly braces. by @dessalines
- Fix URL in pictrsImageThumbnail by @dessalines in [#720](https://github.com/LemmyNet/jerboa/pull/720)
- Update nl translations by @MV-GH in [#706](https://github.com/LemmyNet/jerboa/pull/706)
- #602 Change post button to be more obvious by @dessalines in [#691](https://github.com/LemmyNet/jerboa/pull/691)
- #539 PrettyTime Missing Unit Workaround by @lbenedetto in [#687](https://github.com/LemmyNet/jerboa/pull/687)
- Add setting (off by default) to prevent screenshots. by @camporter in [#685](https://github.com/LemmyNet/jerboa/pull/685)
- Handle errors when updating changelog by @marcellogalhardo in [#617](https://github.com/LemmyNet/jerboa/pull/617)
- Added Japanese Translation Strings by @dessalines in [#686](https://github.com/LemmyNet/jerboa/pull/686)
- add strings.xml for French by @perepepepa in [#638](https://github.com/LemmyNet/jerboa/pull/638)
- Auto-generate Lemmy-API types. by @dessalines in [#657](https://github.com/LemmyNet/jerboa/pull/657)
- Update bug_report.md to include logs by @MV-GH in [#702](https://github.com/LemmyNet/jerboa/pull/702)
- Fix markdown formatting in bug report template by @gxtu in [#743](https://github.com/LemmyNet/jerboa/pull/743)
- changed local variable names context to ctx to better follow convention by @ShinyLuxray
- Merge branch 'main' into ja-language-support by @twizmwazin
- code spacing/formatting fixes by @ShinyLuxray
- Improved localization in Home, Inbox, Comment and Community views by @ShinyLuxray
- Added Japanese Translation Strings by @ShinyLuxray
- Cleans up selection of filled and outlined icon by @Chris-Kropp
- #602 Change post button to be more obvious by @Chris-Kropp
- Fix URL in pictrsImageThumbnail by @APraxx
- Revert "Navigation enhancements: Bottom Navigation, screen transitions, back button " by @dessalines
- Small Accesibility Tweaks by @scottmmjackson in [#674](https://github.com/LemmyNet/jerboa/pull/674)
- Preserving state on orientation change. by @nahwneeth in [#690](https://github.com/LemmyNet/jerboa/pull/690)
- Open lemmy-verse links in Jerboa by @twizmwazin in [#692](https://github.com/LemmyNet/jerboa/pull/692)
- Handle /c/community@host URLs by @beatgammit in [#583](https://github.com/LemmyNet/jerboa/pull/583)
- Navigation enhancements: Bottom Navigation, screen transitions, back button by @nahwneeth in [#670](https://github.com/LemmyNet/jerboa/pull/670)
- Fix numbers and text@text turning into links (#635) by @MV-GH in [#677](https://github.com/LemmyNet/jerboa/pull/677)
- Fix image upload cancel crash by @noloman in [#671](https://github.com/LemmyNet/jerboa/pull/671)
- don't have translucent background on image post thumbnail click in [#625](https://github.com/LemmyNet/jerboa/pull/625)
- Added unit tests for easy-to-test utils by @beatgammit in [#629](https://github.com/LemmyNet/jerboa/pull/629)
- Fixing rest of build warnings. by @dessalines in [#641](https://github.com/LemmyNet/jerboa/pull/641)
- Edit German translation by @femoto in [#650](https://github.com/LemmyNet/jerboa/pull/650)
- Update Italian translation and sort baseline strings lexicographically by @andreaippo in [#630](https://github.com/LemmyNet/jerboa/pull/630)
- add danish translations in [#666](https://github.com/LemmyNet/jerboa/pull/666)
- Added spanish translations by @ArkoSammy12 in [#659](https://github.com/LemmyNet/jerboa/pull/659)
- Polish translation by @OrginalS in [#655](https://github.com/LemmyNet/jerboa/pull/655)
- Added comment sorting functionality by @a1studmuffin in [#495](https://github.com/LemmyNet/jerboa/pull/495)
- improve manifest readability of supported instances by @thebino in [#656](https://github.com/LemmyNet/jerboa/pull/656)
- Show action bar by default by @beatgammit in [#634](https://github.com/LemmyNet/jerboa/pull/634)
- add bottom bar labels for swedish locale by @vijaykramesh in [#623](https://github.com/LemmyNet/jerboa/pull/623)
- Revert "Simple filter to display matching instances in the dropdown. " by @dessalines
- Use image previewer when clicking on images in small cards and list view by @twizmwazin in [#614](https://github.com/LemmyNet/jerboa/pull/614)
- Add vertical scroll to about screen by @marcellogalhardo in [#618](https://github.com/LemmyNet/jerboa/pull/618)
- Account Settings: Improves paddings and small layout details by @sabieber in [#612](https://github.com/LemmyNet/jerboa/pull/612)
- Fix accompanist deprecations by @twizmwazin in [#475](https://github.com/LemmyNet/jerboa/pull/475)
- Add option to use private custom tabs when available by @twizmwazin in [#613](https://github.com/LemmyNet/jerboa/pull/613)
- Support android 13's per-app language selection, fixes #603 by @twizmwazin in [#609](https://github.com/LemmyNet/jerboa/pull/609)
- Add more markwon plugins by @twizmwazin in [#610](https://github.com/LemmyNet/jerboa/pull/610)
- Fix typo labeling saved as search by @twizmwazin in [#608](https://github.com/LemmyNet/jerboa/pull/608)
- Revised and added some german translations by @sabieber in [#611](https://github.com/LemmyNet/jerboa/pull/611)

## New Contributors

- @shezdy made their first contribution in [#800](https://github.com/LemmyNet/jerboa/pull/800)
- @sabieber made their first contribution in [#801](https://github.com/LemmyNet/jerboa/pull/801)
- @Chris-Kropp made their first contribution in [#786](https://github.com/LemmyNet/jerboa/pull/786)
- @Snow4DV made their first contribution in [#794](https://github.com/LemmyNet/jerboa/pull/794)
- @yate made their first contribution in [#749](https://github.com/LemmyNet/jerboa/pull/749)
- @Eskuero made their first contribution in [#776](https://github.com/LemmyNet/jerboa/pull/776)
- @LufyCZ made their first contribution in [#726](https://github.com/LemmyNet/jerboa/pull/726)
- @bynatejones made their first contribution in [#750](https://github.com/LemmyNet/jerboa/pull/750)
- @JosephGaiser made their first contribution in [#728](https://github.com/LemmyNet/jerboa/pull/728)
- @scottmmjackson made their first contribution in [#699](https://github.com/LemmyNet/jerboa/pull/699)
- @lbenedetto made their first contribution in [#687](https://github.com/LemmyNet/jerboa/pull/687)
- @marcellogalhardo made their first contribution in [#617](https://github.com/LemmyNet/jerboa/pull/617)
- @perepepepa made their first contribution in [#638](https://github.com/LemmyNet/jerboa/pull/638)
- @gxtu made their first contribution in [#743](https://github.com/LemmyNet/jerboa/pull/743)
- @ShinyLuxray made their first contribution
- @APraxx made their first contribution
- @nahwneeth made their first contribution in [#690](https://github.com/LemmyNet/jerboa/pull/690)
- @noloman made their first contribution in [#671](https://github.com/LemmyNet/jerboa/pull/671)
- @femoto made their first contribution in [#650](https://github.com/LemmyNet/jerboa/pull/650)
- @ArkoSammy12 made their first contribution in [#659](https://github.com/LemmyNet/jerboa/pull/659)
- @OrginalS made their first contribution in [#655](https://github.com/LemmyNet/jerboa/pull/655)
- @thebino made their first contribution in [#656](https://github.com/LemmyNet/jerboa/pull/656)
- @vijaykramesh made their first contribution in [#623](https://github.com/LemmyNet/jerboa/pull/623)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.34...0.0.35

## What's Changed in 0.0.34

- Fix string issues. by @dessalines
- Adding release notes. by @dessalines
- Revamped BottomBar to match MD3 by @ironveil in [#567](https://github.com/LemmyNet/jerboa/pull/567)
- Update Italian Translations by @andscape-dev in [#591](https://github.com/LemmyNet/jerboa/pull/591)
- Show default icon for community links in sidebar by @a1studmuffin in [#590](https://github.com/LemmyNet/jerboa/pull/590)
- Remove unnecessary check by @7heo in [#600](https://github.com/LemmyNet/jerboa/pull/600)
- Comment action bar fixes by @a1studmuffin in [#593](https://github.com/LemmyNet/jerboa/pull/593)
- Images updated 2 by @dessalines in [#595](https://github.com/LemmyNet/jerboa/pull/595)
- Swedish localisation revised; strings.xml-file fixed. in [#594](https://github.com/LemmyNet/jerboa/pull/594)
- Copy paste bugfix for bottom bar highlight by @a1studmuffin in [#592](https://github.com/LemmyNet/jerboa/pull/592)
- Add strings.xml for locale `ko` by @meinside in [#586](https://github.com/LemmyNet/jerboa/pull/586)
- Implement animated gifs when clicking on an image by @beatgammit in [#580](https://github.com/LemmyNet/jerboa/pull/580)
- Bash isn't portable, use POSIX sh instead by @7heo in [#560](https://github.com/LemmyNet/jerboa/pull/560)
- feat/launcher icon by @seamuslowry in [#528](https://github.com/LemmyNet/jerboa/pull/528)
- Add communities list to sidebar, fixes #510 by @twizmwazin in [#512](https://github.com/LemmyNet/jerboa/pull/512)
- Localization of user tabs by @kuro-codes in [#563](https://github.com/LemmyNet/jerboa/pull/563)
- Increased NSFW blur by @XanderV2001 in [#576](https://github.com/LemmyNet/jerboa/pull/576)
- #345 respect avatar settings by @igarshep in [#554](https://github.com/LemmyNet/jerboa/pull/554)
- Fix typos in German strings by @tribut in [#564](https://github.com/LemmyNet/jerboa/pull/564)
- Added Swedish localisation in [#569](https://github.com/LemmyNet/jerboa/pull/569)
- Add contentDescription to all relevant components by @pipe01 in [#470](https://github.com/LemmyNet/jerboa/pull/470)
- Update AndroidManifest.xml to match instances added in #505 by @shombando in [#552](https://github.com/LemmyNet/jerboa/pull/552)
- Added "Go to (user)" option in more places by @a1studmuffin in [#515](https://github.com/LemmyNet/jerboa/pull/515)
- Fix the missing 'Old' default sort account setting. by @camporter in [#517](https://github.com/LemmyNet/jerboa/pull/517)
- Fix some issues with the unread counts not being accurate by @camporter in [#537](https://github.com/LemmyNet/jerboa/pull/537)
- If login fails, don't leave the current lemmy instance invalid by @camporter in [#521](https://github.com/LemmyNet/jerboa/pull/521)
- Add a user agent by @camporter in [#519](https://github.com/LemmyNet/jerboa/pull/519)
- Added default community icon where appropriate by @a1studmuffin in [#549](https://github.com/LemmyNet/jerboa/pull/549)
- Hide downvote button on comments and posts when disabled by @lsim in [#502](https://github.com/LemmyNet/jerboa/pull/502)
- Fix big font size cutting off settings options by @calincara in [#534](https://github.com/LemmyNet/jerboa/pull/534)
- Add Brazilian Portuguese localization by @somehare in [#540](https://github.com/LemmyNet/jerboa/pull/540)
- Prevent multiple line entry for the name of an instance on login. by @camporter in [#520](https://github.com/LemmyNet/jerboa/pull/520)
- Fix stale user profile data showing momentarily on PersonProfileActivity during fetch operations by @a1studmuffin in [#518](https://github.com/LemmyNet/jerboa/pull/518)
- Highlight the current screen in BottomAppBar by @ironveil in [#531](https://github.com/LemmyNet/jerboa/pull/531)
- Added Italian localization by @andreaippo in [#533](https://github.com/LemmyNet/jerboa/pull/533)
- QoL: Allow debug version installed next to release version by @MV-GH in [#501](https://github.com/LemmyNet/jerboa/pull/501)
- Adding release notes. by @dessalines

## New Contributors

- @ironveil made their first contribution in [#567](https://github.com/LemmyNet/jerboa/pull/567)
- @andscape-dev made their first contribution in [#591](https://github.com/LemmyNet/jerboa/pull/591)
- @meinside made their first contribution in [#586](https://github.com/LemmyNet/jerboa/pull/586)
- @beatgammit made their first contribution in [#580](https://github.com/LemmyNet/jerboa/pull/580)
- @seamuslowry made their first contribution in [#528](https://github.com/LemmyNet/jerboa/pull/528)
- @XanderV2001 made their first contribution in [#576](https://github.com/LemmyNet/jerboa/pull/576)
- @igarshep made their first contribution in [#554](https://github.com/LemmyNet/jerboa/pull/554)
- @tribut made their first contribution in [#564](https://github.com/LemmyNet/jerboa/pull/564)
- @camporter made their first contribution in [#517](https://github.com/LemmyNet/jerboa/pull/517)
- @calincara made their first contribution in [#534](https://github.com/LemmyNet/jerboa/pull/534)
- @somehare made their first contribution in [#540](https://github.com/LemmyNet/jerboa/pull/540)
- @andreaippo made their first contribution in [#533](https://github.com/LemmyNet/jerboa/pull/533)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.33...0.0.34

## What's Changed in 0.0.33

- Fixing broken DB. by @dessalines in [#511](https://github.com/LemmyNet/jerboa/pull/511)
- Add bottom bar to community list by @abluescarab in [#497](https://github.com/LemmyNet/jerboa/pull/497)
- Expanded default lemmy instances list to active users/mo > 100 by @shombando in [#505](https://github.com/LemmyNet/jerboa/pull/505)
- Fixing markdown line height issue. by @dessalines in [#500](https://github.com/LemmyNet/jerboa/pull/500)
- Fix CommentNode previews by @lsim in [#503](https://github.com/LemmyNet/jerboa/pull/503)
- Add dutch translations by @MV-GH in [#504](https://github.com/LemmyNet/jerboa/pull/504)
- Remove check to allow replying to own comments by @lsim in [#499](https://github.com/LemmyNet/jerboa/pull/499)
- Fix show voting arrows in list view option not updating by @twizmwazin in [#493](https://github.com/LemmyNet/jerboa/pull/493)
- Simple filter to display matching instances in the dropdown. in [#489](https://github.com/LemmyNet/jerboa/pull/489)
- Close account switcher when drawer is closed by @vishalbiswas in [#490](https://github.com/LemmyNet/jerboa/pull/490)
- Add custom tab support by @twizmwazin in [#474](https://github.com/LemmyNet/jerboa/pull/474)
- Add linkify plugin to markwon by @twizmwazin in [#487](https://github.com/LemmyNet/jerboa/pull/487)
- Add voting arrows and score to posts in "List" view mode by @a1studmuffin in [#472](https://github.com/LemmyNet/jerboa/pull/472)
- Fixed up ThemeMode enum to use strings in UI by @a1studmuffin in [#469](https://github.com/LemmyNet/jerboa/pull/469)
- Merge branch 'kuroi-usagi-localization' by @dessalines
- Merge branch 'main' into kuroi-usagi-localization by @dessalines
- Change color of account name to adapt to light/dark mode by @mitchellss in [#478](https://github.com/LemmyNet/jerboa/pull/478)
- update DEFAULT_LEMMY_INSTANCES by @taitsmith in [#466](https://github.com/LemmyNet/jerboa/pull/466)
- Change color of person name to adapt to light/dark mode by @mitchellss
- update DEFAULT_LEMMY_INSTANCES by @taitsmith
- started localization for the app and added first german local by @kuro-codes in [#447](https://github.com/LemmyNet/jerboa/pull/447)
- Merge branch 'main' into localization by @kuro-codes
- Add custom TextBadge() component by @mxmvncnt in [#459](https://github.com/LemmyNet/jerboa/pull/459)
- In-app image viewer by @pipe01 in [#444](https://github.com/LemmyNet/jerboa/pull/444)
- fixed typo in german translation by @kuro-codes
- format fixes by @kuro-codes
- Merge branch 'dessalines:main' into localization by @kuro-codes
- Adding twizmwazin to codeowners. by @dessalines
- Truncates too long passwords by @MV-GH in [#462](https://github.com/LemmyNet/jerboa/pull/462)
- Fix show more children button not being animated when collapsed by @twizmwazin in [#461](https://github.com/LemmyNet/jerboa/pull/461)
- Comment action bar improvements by @twizmwazin in [#453](https://github.com/LemmyNet/jerboa/pull/453)
- Markdown renderer replacement by @AntmanLFEz in [#432](https://github.com/LemmyNet/jerboa/pull/432)
- added missing localization strings by @kuro-codes
- Merge branch 'localization' of github.com:kuroi-usagi/jerboa into localization by @kuro-codes
- Merge branch 'main' into localization by @dessalines
- Add issue templates. by @dessalines
- added missing localization strings by @kuro-codes
- fixing formatting by @kuro-codes
- added translations for the Toasts by @kuro-codes
- fixed formatting again by @kuro-codes
- Merge branch 'main' into localization by @kuro-codes
- Add option to show contents of collapsed comments by @twizmwazin in [#438](https://github.com/LemmyNet/jerboa/pull/438)
- Merge branch 'dessalines-main' into localization by @kuro-codes
- fixing merge error in Login.kt by @kuro-codes
- Enable autofill on login form by @pipe01 in [#451](https://github.com/LemmyNet/jerboa/pull/451)
- Allow signed-out users to interact with showMoreOptions by @tuxiqae in [#450](https://github.com/LemmyNet/jerboa/pull/450)
- removed unecessary DB migration, did run fromatKotlin by @kuro-codes
- started localization for the app and added first german local by @kuro-codes
- Aesthetics improvements by @pipe01 in [#424](https://github.com/LemmyNet/jerboa/pull/424)
- Expand area tappable by user to collapse a comment by @twizmwazin in [#436](https://github.com/LemmyNet/jerboa/pull/436)
- Fix #421 by @7heo in [#422](https://github.com/LemmyNet/jerboa/pull/422)
- Fix the flashing between transitions (#371) by @AntmanLFEz in [#428](https://github.com/LemmyNet/jerboa/pull/428)
- Use inner function to reduce boilerplate in LookAndFeelActivity by @twizmwazin in [#417](https://github.com/LemmyNet/jerboa/pull/417)
- Adding release notes. by @dessalines

## New Contributors

- @abluescarab made their first contribution in [#497](https://github.com/LemmyNet/jerboa/pull/497)
- @shombando made their first contribution in [#505](https://github.com/LemmyNet/jerboa/pull/505)
- @lsim made their first contribution in [#503](https://github.com/LemmyNet/jerboa/pull/503)
- @MV-GH made their first contribution in [#504](https://github.com/LemmyNet/jerboa/pull/504)
- @ made their first contribution in [#489](https://github.com/LemmyNet/jerboa/pull/489)
- @vishalbiswas made their first contribution in [#490](https://github.com/LemmyNet/jerboa/pull/490)
- @mitchellss made their first contribution
- @taitsmith made their first contribution
- @kuro-codes made their first contribution in [#447](https://github.com/LemmyNet/jerboa/pull/447)
- @mxmvncnt made their first contribution in [#459](https://github.com/LemmyNet/jerboa/pull/459)
- @pipe01 made their first contribution in [#444](https://github.com/LemmyNet/jerboa/pull/444)
- @AntmanLFEz made their first contribution in [#432](https://github.com/LemmyNet/jerboa/pull/432)
- @tuxiqae made their first contribution in [#450](https://github.com/LemmyNet/jerboa/pull/450)
- @7heo made their first contribution in [#422](https://github.com/LemmyNet/jerboa/pull/422)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.32...0.0.33

## What's Changed in 0.0.32

- Fixed showNavBar issue. by @dessalines in [#420](https://github.com/LemmyNet/jerboa/pull/420)
- Add option to disable showing the bottom navigation bar by @twizmwazin in [#412](https://github.com/LemmyNet/jerboa/pull/412)
- Fix navigating to other instance communities and users by @Anna-log7 in [#413](https://github.com/LemmyNet/jerboa/pull/413)
- Collapse comments by @dessalines in [#419](https://github.com/LemmyNet/jerboa/pull/419)
- Collapse comments by tapping directly on the comment body by @dessalines in [#398](https://github.com/LemmyNet/jerboa/pull/398)
- Fix dropdowns on Account Settings-screen not working by @oscarnylander in [#387](https://github.com/LemmyNet/jerboa/pull/387)
- Added Black and System Black theme modes, fixes https://github.com/dessalines/jerboa/issues/376 by @dessalines in [#393](https://github.com/LemmyNet/jerboa/pull/393)
- Fixing lint. by @dessalines
- Save listing and sort type-preferences in app DB by @dessalines in [#407](https://github.com/LemmyNet/jerboa/pull/407)
- Ktlint 2 by @dessalines in [#409](https://github.com/LemmyNet/jerboa/pull/409)
- Merge branch 'main' into ktlint_2 by @dessalines
- Add padding to SwipeRefresh for PostListings (for #400) by @russjr08 in [#408](https://github.com/LemmyNet/jerboa/pull/408)
- Merge branch 'main' into ktlint_2 by @dessalines
- Running ktlint. by @dessalines
- Add `.editorconfig` by @oscarnylander in [#405](https://github.com/LemmyNet/jerboa/pull/405)
- Remove rule config from `kotlinter` by @oscarnylander
- Add `.editorconfig` by @oscarnylander
- Merge branch 'main' into save-listing-and-sort-type-in-app-db by @dessalines
- Remove Splash Screen by @oscarnylander in [#383](https://github.com/LemmyNet/jerboa/pull/383)
- Save listing and sort type-preferences in app DB by @oscarnylander
- Fixing CI ntfy.sh notification link. by @dessalines in [#406](https://github.com/LemmyNet/jerboa/pull/406)
- Roll back AGP to `8.0.2` by @oscarnylander in [#404](https://github.com/LemmyNet/jerboa/pull/404)
- Merge branch 'main' into black_theme_mode by @dessalines
- Added Blue theme color by @a1studmuffin in [#392](https://github.com/LemmyNet/jerboa/pull/392)
- Show settings in sidebar even when user is not logged in by @twizmwazin in [#375](https://github.com/LemmyNet/jerboa/pull/375)
- Added Black theme mode. Looks exactly like the Dark theme mode except near-black background colors are true black. Also added theme mode SystemBlack, which uses Light/Black instead of Light/Dark depending on system setting. by @a1studmuffin
- Don't pass onClick default for various components as per convention. by @a1studmuffin
- Users can now collapse comments by tapping anywhere on the comment body or header, and expand comments by tapping on the comment header. This replaces the old behaviour (tap and hold on comment headers), which wasn't as intuitive for new users. by @a1studmuffin
- Adding more CI commands. by @dessalines in [#367](https://github.com/LemmyNet/jerboa/pull/367)
- Adding release notes. by @dessalines

## New Contributors

- @twizmwazin made their first contribution in [#412](https://github.com/LemmyNet/jerboa/pull/412)
- @Anna-log7 made their first contribution in [#413](https://github.com/LemmyNet/jerboa/pull/413)
- @a1studmuffin made their first contribution
- @oscarnylander made their first contribution in [#387](https://github.com/LemmyNet/jerboa/pull/387)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.31...0.0.32

## What's Changed in 0.0.31

- Fix padding bug by @dessalines in [#366](https://github.com/LemmyNet/jerboa/pull/366)
- Adding izzy repo. by @dessalines
- Adding release notes. by @dessalines

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.30...0.0.31

## What's Changed in 0.0.30

- Use default bottom app bar. by @dessalines in [#360](https://github.com/LemmyNet/jerboa/pull/360)
- Upgrading deps. by @dessalines in [#359](https://github.com/LemmyNet/jerboa/pull/359)
- Adjust the status bar while using the system dark theme to not use dark icons by @russjr08 in [#358](https://github.com/LemmyNet/jerboa/pull/358)
- Upgrade deps 11 by @dessalines in [#355](https://github.com/LemmyNet/jerboa/pull/355)
- Adding to releases. by @dessalines

## New Contributors

- @russjr08 made their first contribution in [#358](https://github.com/LemmyNet/jerboa/pull/358)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.29...0.0.30

## What's Changed in 0.0.29

- Making icon thumbnails smaller. by @dessalines in [#353](https://github.com/LemmyNet/jerboa/pull/353)
- Adding woodpecker status badge. by @dessalines
- Adding woodpecker 1 by @dessalines in [#352](https://github.com/LemmyNet/jerboa/pull/352)
- Fixing IME padding issues. Fixes #350 by @dessalines in [#351](https://github.com/LemmyNet/jerboa/pull/351)
- Upgrading deps by @dessalines in [#349](https://github.com/LemmyNet/jerboa/pull/349)
- Adding a translucent statusbar. Fixes #347 by @dessalines in [#348](https://github.com/LemmyNet/jerboa/pull/348)
- Adding release notes. by @dessalines

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.28...0.0.29

## What's Changed in 0.0.28

- Adding comment mentions to inbox. Fixes #339 by @dessalines in [#344](https://github.com/LemmyNet/jerboa/pull/344)
- Add report user by @dessalines in [#343](https://github.com/LemmyNet/jerboa/pull/343)
- Upgrade from kapt to ksp. by @dessalines in [#342](https://github.com/LemmyNet/jerboa/pull/342)
- Making icons larger, adding node keys by @dessalines in [#338](https://github.com/LemmyNet/jerboa/pull/338)
- Adding release notes. by @dessalines

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.27...0.0.28

## What's Changed in 0.0.27

- Adding instant post and comment voting. Fixes #299 by @dessalines in [#335](https://github.com/LemmyNet/jerboa/pull/335)
- Fix font size round 2 by @dessalines in [#334](https://github.com/LemmyNet/jerboa/pull/334)
- Revert "Making font size 16 by default. Fixes #330" by @dessalines in [#333](https://github.com/LemmyNet/jerboa/pull/333)
- Changing the post action bar. Fixes #324 by @dessalines in [#332](https://github.com/LemmyNet/jerboa/pull/332)
- Making font size 16 by default. Fixes #330 by @dessalines in [#331](https://github.com/LemmyNet/jerboa/pull/331)
- Downgrade from gradle RC to gradle 8.0 by @dessalines in [#329](https://github.com/LemmyNet/jerboa/pull/329)
- Use muted profile names, better colors, and align post header bar. by @dessalines in [#328](https://github.com/LemmyNet/jerboa/pull/328)
- Fixing release version. by @dessalines
- Adding release notes. by @dessalines

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.26...0.0.27

## What's Changed in 0.0.26

- Upgrading version by @dessalines
- Adding post view modes: Card, Small Card, and List. Fixes #278 by @dessalines in [#318](https://github.com/LemmyNet/jerboa/pull/318)
- Adding taglines. Fixes #286 by @dessalines in [#317](https://github.com/LemmyNet/jerboa/pull/317)
- Comment reply links now go to parent for context. Fixes #155 by @dessalines in [#316](https://github.com/LemmyNet/jerboa/pull/316)
- Better subscribe button. Fixes #273 by @dessalines in [#315](https://github.com/LemmyNet/jerboa/pull/315)
- Some spacing fixes. by @dessalines
- Adding new comments indicator. Fixes #283 by @dessalines in [#314](https://github.com/LemmyNet/jerboa/pull/314)
- Adding vote icons. Fixes #302 by @dessalines in [#313](https://github.com/LemmyNet/jerboa/pull/313)
- Reply node by @dessalines in [#311](https://github.com/LemmyNet/jerboa/pull/311)
- Adding nsfw image blurring. Fixes #291 by @dessalines in [#310](https://github.com/LemmyNet/jerboa/pull/310)
- Better more comments button. Fixes #292 by @dessalines in [#309](https://github.com/LemmyNet/jerboa/pull/309)
- Remove @ sign for usernames. Fixes #295 by @dessalines in [#308](https://github.com/LemmyNet/jerboa/pull/308)
- Make icons squircle. Fixes #301 by @dessalines in [#307](https://github.com/LemmyNet/jerboa/pull/307)
- Show post score next to time. Fixes #303 by @dessalines in [#306](https://github.com/LemmyNet/jerboa/pull/306)
- Softer post card color. Fixes #296 by @dessalines in [#305](https://github.com/LemmyNet/jerboa/pull/305)
- Fixing bookmark style. Fixes #297 by @dessalines in [#304](https://github.com/LemmyNet/jerboa/pull/304)
- Add to RELEASES.md by @dessalines

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.25...0.0.26

## What's Changed in 0.0.25

- Upgrading version. by @dessalines
- Fix not showing federated comments. Fixes #290 by @dessalines in [#294](https://github.com/LemmyNet/jerboa/pull/294)
- Add to RELEASES.md by @dessalines

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.24...0.0.25

## What's Changed in 0.0.24

- Upgrading version. by @dessalines
- Upgrade to lemmy version 0.17.0 . Fixes #277 by @dessalines in [#289](https://github.com/LemmyNet/jerboa/pull/289)
- Blocking instances from login. Fixes #280 by @dessalines in [#282](https://github.com/LemmyNet/jerboa/pull/282)
- Fix font sizes 1 by @dessalines in [#281](https://github.com/LemmyNet/jerboa/pull/281)
- Trying to fix drone 3. by @dessalines
- Trying to fix drone 2. by @dessalines
- Trying to fix drone 1. by @dessalines
- Upgrading deps. by @dessalines
- Updating releases.md by @dessalines

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.23...0.0.24

## What's Changed in 0.0.23

- Fix bad_url for torrent magnet links. Fixes #270 by @dessalines in [#271](https://github.com/LemmyNet/jerboa/pull/271)
- Add donation link by @dessalines in [#269](https://github.com/LemmyNet/jerboa/pull/269)
- Fix material 3 crash on android 11 and below devices. Fixes #264 by @dessalines in [#268](https://github.com/LemmyNet/jerboa/pull/268)
- Release notes. by @dessalines

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.22...0.0.23

## What's Changed in 0.0.22

- Material v3 by @dessalines in [#263](https://github.com/LemmyNet/jerboa/pull/263)
- Updating releases.md by @dessalines

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.21...0.0.22

## What's Changed in 0.0.21

- Smaller action bars by @dessalines in [#261](https://github.com/LemmyNet/jerboa/pull/261)
- Add about page by @dessalines in [#260](https://github.com/LemmyNet/jerboa/pull/260)
- Adding light and dark theme options. Fixes #254 by @dessalines in [#259](https://github.com/LemmyNet/jerboa/pull/259)
- Adding a settings page. by @dessalines in [#253](https://github.com/LemmyNet/jerboa/pull/253)
- Fixing siFormat issue when 0. Fixes #170 by @dessalines in [#252](https://github.com/LemmyNet/jerboa/pull/252)
- More sidebar stats by @dessalines in [#249](https://github.com/LemmyNet/jerboa/pull/249)
- Organizing imports. by @dessalines in [#248](https://github.com/LemmyNet/jerboa/pull/248)
- Adding copy post link. Fixes #168 by @dessalines in [#247](https://github.com/LemmyNet/jerboa/pull/247)
- Remove top route. by @dessalines in [#246](https://github.com/LemmyNet/jerboa/pull/246)
- Change sidebar to info by @dessalines in [#245](https://github.com/LemmyNet/jerboa/pull/245)
- Adding release notes. by @dessalines

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.20...0.0.21

## What's Changed in 0.0.20

- Fix prettytime crash. Fixes #238 by @dessalines in [#239](https://github.com/LemmyNet/jerboa/pull/239)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.19...0.0.20

## What's Changed in 0.0.19

- Fix gradle release for f-droid by @dessalines
- Adding to releases. by @dessalines

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.18...0.0.19

## What's Changed in 0.0.18

- Fix account bug. Fixes #229 by @dessalines in [#237](https://github.com/LemmyNet/jerboa/pull/237)
- Fix create post bug. Fixes #230 by @dessalines in [#236](https://github.com/LemmyNet/jerboa/pull/236)
- Fix comment scrolling bug. Fixes #231 by @dessalines in [#235](https://github.com/LemmyNet/jerboa/pull/235)
- Coil upgrade v2 by @dessalines in [#234](https://github.com/LemmyNet/jerboa/pull/234)
- Updating Releases.md by @dessalines

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.17...0.0.18

## What's Changed in 0.0.17

- Adding deep links. by @dessalines in [#228](https://github.com/LemmyNet/jerboa/pull/228)
- Fix deleted item header spacing. Fixes #222 by @dessalines in [#224](https://github.com/LemmyNet/jerboa/pull/224)
- Lazycolumn 2 by @dessalines in [#223](https://github.com/LemmyNet/jerboa/pull/223)
- Adding a login first message. Fixes #206 by @dessalines in [#221](https://github.com/LemmyNet/jerboa/pull/221)
- Deduplicate scrolling posts. Fixes #219 by @dessalines in [#220](https://github.com/LemmyNet/jerboa/pull/220)
- Make post pictures wider. Fixes #196 by @dessalines in [#218](https://github.com/LemmyNet/jerboa/pull/218)
- Fix comment indent. Fixes #211 by @dessalines in [#217](https://github.com/LemmyNet/jerboa/pull/217)
- Changing star to bookmark. Fixes #210 by @dessalines in [#216](https://github.com/LemmyNet/jerboa/pull/216)
- Make comment icon smaller. Fixes #212 by @dessalines in [#215](https://github.com/LemmyNet/jerboa/pull/215)
- Make comment slightly larger. Fixes #213 by @dessalines in [#214](https://github.com/LemmyNet/jerboa/pull/214)
- Fix comment header with flowrow. Fixes #207 by @dessalines in [#209](https://github.com/LemmyNet/jerboa/pull/209)
- Upgrade accompanist by @dessalines in [#208](https://github.com/LemmyNet/jerboa/pull/208)
- Some items fixes. by @dessalines in [#204](https://github.com/LemmyNet/jerboa/pull/204)
- Fixing unit tests. by @dessalines in [#199](https://github.com/LemmyNet/jerboa/pull/199)
- Moving to kotlinter-gradle by @dessalines in [#198](https://github.com/LemmyNet/jerboa/pull/198)
- Running lint, updating deps. by @dessalines in [#197](https://github.com/LemmyNet/jerboa/pull/197)
- Adding 0.0.16-alpha release. by @dessalines

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.16...0.0.17

## What's Changed in 0.0.16

- Make MarkdownTextField generic by @LunaticHacker in [#195](https://github.com/LemmyNet/jerboa/pull/195)
- Upgrading android for jetbrains. by @dessalines in [#193](https://github.com/LemmyNet/jerboa/pull/193)
- add fab for create post from community page by @LunaticHacker in [#184](https://github.com/LemmyNet/jerboa/pull/184)
- Add api settings by @LunaticHacker in [#183](https://github.com/LemmyNet/jerboa/pull/183)
- fix a bug i introduced in #180 by @LunaticHacker in [#185](https://github.com/LemmyNet/jerboa/pull/185)
- Upgrading deps by @dessalines in [#182](https://github.com/LemmyNet/jerboa/pull/182)
- Upgrade gradle to 7.5 by @dessalines in [#181](https://github.com/LemmyNet/jerboa/pull/181)
- Add deeplinks for createPost by @LunaticHacker in [#178](https://github.com/LemmyNet/jerboa/pull/178)
- fix signout by @LunaticHacker in [#180](https://github.com/LemmyNet/jerboa/pull/180)
- Enable minify. Fixes #171 by @dessalines in [#173](https://github.com/LemmyNet/jerboa/pull/173)
- Removing Site creator by @dessalines in [#172](https://github.com/LemmyNet/jerboa/pull/172)
- Fix accompanist version. by @dessalines
- Upgrading deps. by @dessalines in [#167](https://github.com/LemmyNet/jerboa/pull/167)
- Adding instance name to accounts. Fixes #164 by @dessalines in [#166](https://github.com/LemmyNet/jerboa/pull/166)
- Adding release notes. by @dessalines

## New Contributors

- @LunaticHacker made their first contribution in [#195](https://github.com/LemmyNet/jerboa/pull/195)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.15...0.0.16

## What's Changed in 0.0.15

- Comment tree rework 1 by @dessalines in [#162](https://github.com/LemmyNet/jerboa/pull/162)
- Ability to delete posts and comments. Fixes #152 by @dessalines in [#161](https://github.com/LemmyNet/jerboa/pull/161)
- Debounce search box input. Fixes #154 by @dessalines in [#157](https://github.com/LemmyNet/jerboa/pull/157)
- Reverting back to old markdown renderer. Was much better overall. by @dessalines in [#156](https://github.com/LemmyNet/jerboa/pull/156)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.14...0.0.15

## What's Changed in 0.0.14

- Alt markdown 1 by @dessalines in [#150](https://github.com/LemmyNet/jerboa/pull/150)
- Adding round icon. Fixes #128 by @dessalines in [#151](https://github.com/LemmyNet/jerboa/pull/151)
- Alt markdown 1 by @dessalines in [#146](https://github.com/LemmyNet/jerboa/pull/146)
- Adding html metadata cards. Fixes #142 by @dessalines in [#143](https://github.com/LemmyNet/jerboa/pull/143)
- Adding release notes. by @dessalines

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.13...0.0.14

## What's Changed in 0.0.13

- Fix animation direction by @dessalines in [#140](https://github.com/LemmyNet/jerboa/pull/140)
- Scroll to top on resort by @dessalines in [#139](https://github.com/LemmyNet/jerboa/pull/139)
- Adding new sorts by @dessalines in [#138](https://github.com/LemmyNet/jerboa/pull/138)
- Catch url exception by @dessalines in [#135](https://github.com/LemmyNet/jerboa/pull/135)
- Upgrade deps by @dessalines in [#134](https://github.com/LemmyNet/jerboa/pull/134)
- Adding release notes. by @dessalines

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.12...0.0.13

## What's Changed in 0.0.12

- Fixing slow create post. Fixes #117 by @dessalines in [#126](https://github.com/LemmyNet/jerboa/pull/126)
- Fix image height in landscape mode. Fixes #122 by @dessalines in [#125](https://github.com/LemmyNet/jerboa/pull/125)
- Remove instant voting. Fixes #123 by @dessalines in [#124](https://github.com/LemmyNet/jerboa/pull/124)
- Trying to fix builds 1 by @dessalines in [#121](https://github.com/LemmyNet/jerboa/pull/121)
- Adding release notes. by @dessalines

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.11...0.0.12

## What's Changed in 0.0.11

- Adding deploy to device script. by @dessalines
- Adding signing config to release. by @dessalines
- Adding to gitignore. by @dessalines
- Upgrading some deps. by @dessalines in [#120](https://github.com/LemmyNet/jerboa/pull/120)
- Another fix. by @dessalines in [#119](https://github.com/LemmyNet/jerboa/pull/119)
- Various Light theme fixes. by @dessalines in [#118](https://github.com/LemmyNet/jerboa/pull/118)
- Adding release notes. by @dessalines

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.10...0.0.11

## What's Changed in 0.0.10

- Adding link from text selection. Fixes #105 by @dessalines in [#107](https://github.com/LemmyNet/jerboa/pull/107)
- Remember saved text for markdown areas. Fixes #104 by @dessalines in [#106](https://github.com/LemmyNet/jerboa/pull/106)
- Adding release notes. by @dessalines

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.9...0.0.10

## What's Changed in 0.0.9

- Adding a markdown helper. Fixes #38 by @dessalines in [#103](https://github.com/LemmyNet/jerboa/pull/103)
- Various fixes. by @dessalines in [#101](https://github.com/LemmyNet/jerboa/pull/101)
- Add link to Jerboa on F-Droid by @Adda0 in [#96](https://github.com/LemmyNet/jerboa/pull/96)
- Release notes. by @dessalines

## New Contributors

- @Adda0 made their first contribution in [#96](https://github.com/LemmyNet/jerboa/pull/96)

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.8...0.0.9

## What's Changed in 0.0.8

- Showing stickied / locked. Fixes #61 by @dessalines in [#95](https://github.com/LemmyNet/jerboa/pull/95)
- Add no-background post listing. Fixes #91 by @dessalines in [#94](https://github.com/LemmyNet/jerboa/pull/94)
- Don't show block person on your own profile. Fixes #93 by @dessalines
- Adding scrollbars to lazycolumns. Fixes #87 by @dessalines in [#90](https://github.com/LemmyNet/jerboa/pull/90)
- Adding release notes. by @dessalines

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.7...0.0.8

## What's Changed in 0.0.7

- Fix bottom bar correct screen. by @dessalines in [#86](https://github.com/LemmyNet/jerboa/pull/86)
- Darkblue statusbar color. Fixes #79 by @dessalines in [#84](https://github.com/LemmyNet/jerboa/pull/84)
- Downgrade compose to fix liststate bug. Fixes #81 by @dessalines in [#83](https://github.com/LemmyNet/jerboa/pull/83)
- Downgrade compose to fix liststate bug. Fixes #81 by @dessalines
- Adding release notes. by @dessalines

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.6...0.0.7

## What's Changed in 0.0.6

- Dont resort comments by @dessalines in [#77](https://github.com/LemmyNet/jerboa/pull/77)
- Adding user and community blocking. Fixes #71 Fixes #58 by @dessalines in [#75](https://github.com/LemmyNet/jerboa/pull/75)
- Saved page fix by @dessalines in [#74](https://github.com/LemmyNet/jerboa/pull/74)
- Adding saved page. Fixes #20 by @dessalines in [#73](https://github.com/LemmyNet/jerboa/pull/73)
- Upgrading deps due to dolphin release. by @dessalines in [#72](https://github.com/LemmyNet/jerboa/pull/72)
- Merge branch 'main' into upgrade_deps_2 by @dessalines
- Forwarding error messages from lemmy. Fixes #66 by @dessalines in [#70](https://github.com/LemmyNet/jerboa/pull/70)
- Merge branch 'main' into retrofit_errs by @dessalines
- Forwarding error messages from lemmy. Fixes #66 by @dessalines
- Upgrading deps due to dolphin release. by @dessalines
- Removing unit defaults. Fixes #67 by @dessalines in [#69](https://github.com/LemmyNet/jerboa/pull/69)
- Addin comment and post reporting. Fixes #59 by @dessalines in [#68](https://github.com/LemmyNet/jerboa/pull/68)
- Upgrading deps. by @dessalines in [#64](https://github.com/LemmyNet/jerboa/pull/64)
- add schemas. by @dessalines in [#65](https://github.com/LemmyNet/jerboa/pull/65)
- Incorrect format check by @dessalines in [#63](https://github.com/LemmyNet/jerboa/pull/63)
- Add codeowners. by @dessalines
- Trying to add drone, 4. by @dessalines
- Trying to add drone, 3. by @dessalines
- Trying to add drone, 2. by @dessalines
- Trying to add drone, 1. by @dessalines
- Updating readme. by @dessalines
- Removing .idea folder. by @dessalines
- Adding black background. Fixes #56 by @dessalines
- Adding comment hot sorting, and a few API stubs. Fixes #62 by @dessalines
- Adding release notes. by @dessalines

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.5...0.0.6

## What's Changed in 0.0.5

- Fix null checks and crash. Fixes #60 . Fixes #37 by @dessalines
- Adding loading indicators and disables. Fixes #57 by @dessalines
- Adding comment header long click collapse. Fixes #51 by @dessalines
- Adding to gitignore. by @dessalines
- Fixing subnode actions. Fixes #55 by @dessalines
- Changing thumbs to arrows. by @dessalines
- Adding some better spacing. Fixes #54 by @dessalines
- Adding surface for splashscreen. Fixes #56 by @dessalines
- initial Fastlane structure by @dessalines in [#50](https://github.com/LemmyNet/jerboa/pull/50)
- initial Fastlane structure by @IzzySoft
- Adding fetch suggested title. by @dessalines
- Updating releases.md by @dessalines

## New Contributors

- @IzzySoft made their first contribution

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.4...0.0.5

## What's Changed in 0.0.4

- Adding community sidebars. Fixes #44 by @dessalines
- Fix private message reply bug. by @dessalines
- CommentReplyView refactored on its own viewmodel. Added mark replied message as read. Fixes #45 by @dessalines
- Adding modified times. Fixes #46 by @dessalines
- Add bottom app bar padding. by @dessalines
- A few fixes. by @dessalines
- Adding releases.md by @dessalines

**Full Changelog**: https://github.com/LemmyNet/jerboa/compare/0.0.3...0.0.4

## What's Changed in 0.0.3

- Up version. by @dessalines
- Fix SI formatter. by @dessalines
- Adding site sidebar. by @dessalines
- Add post editing. Fixes #39 by @dessalines
- Fix initial site fetch. Fixes #40 by @dessalines
- Adding comment editing. Fixes #25 by @dessalines
- Adding moderator, admin, and banned tags. Fixes #2 by @dessalines
- Add default lemmy instances. by @dessalines
- Adding anonymous browsing. Fixes #34 by @dessalines
- Sort and label deleted and removed comments. Fixes #33 by @dessalines
- Add image Uploads. by @dessalines
- Adding bottom app bar for other activities. by @dessalines
- Updating icon. by @dessalines
- Adding bottom app bar for home. Fixes #9 by @dessalines
- Instant comment and post voting. Fixes #31 by @dessalines
- Adding qualified names. Fixes #32 by @dessalines
- Trim body and null check. Fixes #30 by @dessalines
- Adding create post, community list, and searching. by @dessalines
- Add post metadata card. Fixes #28 by @dessalines
- Upgrading icon. by @dessalines
- Adding subscribe. Fixes #3 by @dessalines
- Adding unread counts. Fixes #17 by @dessalines
- Adding a splash screen. Fixes #19 by @dessalines
- Got private message replies working. by @dessalines
- Starting to add private messages. by @dessalines
- Adding mark all as read. Fixes #16 by @dessalines
- Adding mark comment as read. Fixes #15 by @dessalines
- Upgrading compose. by @dessalines
- Adding ktlint by @dessalines
- Starting to add inbox. by @dessalines
- Fixing account change bug. by @dessalines
- Fixing some padding. by @dessalines
- Added tabs and comments to person profile view. by @dessalines
- Adding more person links by @dessalines
- Adding readme 3. by @dessalines
- Adding readme 2. by @dessalines
- Adding readme. by @dessalines
- Adding jerboa icon 2. by @dessalines
- Adding jerboa icon. by @dessalines
- Adding profile pages. by @dessalines
- Starting to add community view. by @dessalines
- Starting to work on sidebar by @dessalines
- Added saving posts and comments. by @dessalines
- Adding site view model, default sort options. by @dessalines
- Starting to get sort options by @dessalines
- Image posts working. by @dessalines
- Adding post links. by @dessalines
- Adding create comments by @dessalines
- Got comment tree and voting working. by @dessalines
- Starting to work on comments. by @dessalines
- State hoisting reorg. by @dessalines
- First failed try at voting. by @dessalines
- Basic account switcher done. by @dessalines
- Creating a room repository by @dessalines
- Starting to add accounts. by @dessalines
- Reworking PostListing by @dessalines
- Initial commit. by @dessalines

## New Contributors

- @dessalines made their first contribution

<!-- generated by git-cliff -->
