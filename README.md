# Nucleus Plugin (Latest 1.0.1)

Nucleus > 1.0.0 supports Grails 3.3.x
Nucleus < 0.5.5 supports Grails 3.2.x (Tested upto 3.2.5)

[![Maintainability](https://api.codeclimate.com/v1/badges/14b3b2d953e6e78c4439/maintainability)](https://codeclimate.com/repos/5abde0edb72b63029e000ee6/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/14b3b2d953e6e78c4439/test_coverage)](https://codeclimate.com/repos/5abde0edb72b63029e000ee6/test_coverage)

## By CauseCode Technologies Pvt. Ltd.

* This plugin is used as the base plugin of every project.

## ChangeLog

See [changelog.md](https://github.com/causecode/nucleus/blob/master/changelog.md) file.

## Running test cases

Use command: `grails test-app`

## Architecture Overview

### Domains

Note: To use nucleus 1.0.1, you need to create User, Role and UserRole domain classes in the package `com.causecode.user` similar
to present in the plugin in your app. Based on the requirement, you can use type of Id to ObjectId in case of MongoDB or Long in case of SQL.

- **User**, **Role**, **UserRole**:
A default domain generated using spring security core plugin to use standard user lookup, Customized according to
`CauseCode` application requirement.
- **Country** :
Used for storing country name and code.
- **PhoneCountryCode** :
Used for storing country code with `Country`.
- **Phone** :
Used for storing phone number with `PhoneCountryCode`.
- **City** :
Used for storing city related information with `Country`.
- **Location** :
Used for storing location related information with `City`.
- **Currency** :
Used for storing currency code and name.
- **Contact** :
A generic domain used to store `User` social network contact ID's, `Location` and `Phone`.
- **Url** :
Used in conjunction with Sitemap.
- **Sitemap** :
Used for tracking request URL for which search engine discover these application.

### Controllers/Trait

- **Currency** :
Provides default CRUD end point for Admin.
- **Sitemap** :
Provides end point to `generate` sitemap.
- **RestfulController** :
This controller extends the Grails default RestfulController and overrides the index and delete actions.
- **BaseController**
A trait to act as the BaseController for all controllers. It contains some generic methods and exception handlers
that are required by all the controllers in the App.

### Utility Classes

- **SitemapMarshaller** :
Used to render sitemap in sitemap.xml pattern.
- **CustomValidationErrorMarshaller** :
Used to render generic customizable validation error messages for domains.
- **CustomUserDetailsService** :
Provides methods `loadUserByUsername` accepts String argument as username and returns user by username or email.
**Note**: If User not found throws `UsernameNotFoundException` exception.
- **DateUtil** :
Provides multiple methods which transforms Dates.
- **NucleusTagLib** :
    - Provides `pagerInfo` tagLib with parameters max, offset and total.
    - Used to render HTML elements showing response for list pages record status.

## Detailed Documentation

### Required Modules

1. A module named **angular** is required in order to user user management screen.

### TagLib pagerInfo
Generic tagLib used to render pagination information for list pages.

```
    <pagerInfo total="{total}" max="{max}" offset="{offset}"></pagerInfo>
```
> total: (REQUIRED) total number of instances
> max: (OPTIONAL) max number of instances
> offset: (OPTIONAL) actual number of instances rendered

### Generate Groovy Documentation
To generate groovy document use following command and protect document using spring security static rules. Read more [here](http://www.gradle.org/docs/current/dsl/org.gradle.api.tasks.javadoc.Groovydoc.html) about groovydoc.
```
    groovydoc [options] [packagenames] [sourcefiles]
```
