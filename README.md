# Nucleus Plugin (Latest 0.5.0)
Supported Grails 3.2.0

## By CauseCode Technologies Pvt. Ltd.

* This plugin is used as the base plugin of every project.

## ChangeLog

See [ChangeLog.md](https://bitbucket.org/causecode/nucleus/src/d9be8242b8cc37260eac82ea157d1eebe49b71be/ChangeLog.md?at=master) file.

## Running test cases

To run the test cases, uncomment the hibernate plugin dependency in BuildConfig.groovy file. Just make sure you do
not commit that file.

## Architecture Overview

### Domains

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

### Controllers

- **Currency** : 
Provides default CRUD end point for Admin.
- **Sitemap** : 
Provides end point to `generate` sitemap.
- **UserManagement** : 
Provides End point for `index`, `list` , `modifyRoles`, `makeUserActiveInactive` and `exportUserReport`.

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

### Login via Email / Username

To add facility to login via email / username add following snippet to your `/grails-app/conf/spring/resources.groovy` file:

```
beans = {
    userDetailsService(CustomUserDetailsService)
}
```

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
