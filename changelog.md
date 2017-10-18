# ChangeLog

## Version 0.5.0

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
