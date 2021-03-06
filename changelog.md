# ChangeLog

## Version 1.0.0 - [Unreleased]
### Changed
- Exclude the classes which were present in the package com.causecode.user

## Version 1.0.0 - [23-05-2018]
### Changed
- Upgraded the plugin to support grails 3.3.5

## Version 0.5.5 - [09-04-2018]

### Fixed
- Duplication issue while adding users in development mode.

## Version 0.5.4 - [02-04-2018]

### Added
- Dependency versions for default dependencies to resolve https://github.com/grails/grails-core/issues/9276

## Version 0.5.3 - [02-04-2018]

### Added
- Removed 'user-data-lib' plugin and moved the classes back to nucleus.
- CircleCI and code climate configurations.

### Removed
- gradle-code-quality plugin.

## Version 0.5.2 - [14-12-2017]

### Added
1. A separate plugin 'user-data-lib' for user related classes.

## Version 0.5.1

### Changed
1. Upgraded `gradle-code-quality` version to `1.0.0`.
2. Updated `maven` server url in `build.gradle`.
3. Updated Gradle Wrapper version from 3.0 to 3.4.1.

### Added
* ####CircleCI configuration
    -  `.circleci/config.yml` for build automation using `CircleCI`.
    - `mavenCredsSetup.sh` for generating `gradle.properties` during the CircleCI build.

## v0.5.1 - [01-11-2017]

### Fixed
1. Dependencies - changed compile dependencies to provided dependency for common dependencies which will be
present in the installing app. (For ex - spring security core and spring security rest, json views etc.)

### Removed
1. org.grails.plugins:export:2.0.0 dependency
2. Removed duplicate dependencies.
3. EmUser class. This should be added in the installing app.
4. Google re-captcha server side validation. A new gradle plugin will be created to support this funnctionality.
card - https://trello.com/c/PpzKgczh
5. Removed mongodb dependencies.

## Version 0.3.4
1. Migrated from Grails version 2.5.0 to 3.1.4.
2. Changed package name from `cc` to > `causecode`.

## Version 0.2

### New Feature

1. Added user management screen.

## Version 0.1.9 - 0.1.9.2

### Database changes

1. Gender field made nullable in User domain,
2. Removed table mapping from all address related domains.

### Fixes

1. Fixed support for multiple error classes on jquery validation,
2. Some fixes for showing alert messages.

## Version 0.4.7
1. Changed utility method name from `camelCaseToTile` to `camelCaseToTitle` in NucleusBootStrap.groovy file.
