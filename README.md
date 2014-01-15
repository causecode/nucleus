# Nucleus Plugin (Latest 0.1.9.1)

## By CauseCode Technologies Pvt. Ltd.

* This plugin is used as the base plugin of every project.

## Login via Email / Username

To add facility to login via email / username add following snippet to your `/grails-app/conf/spring/resources.groovy` file:

```
beans = {
    userDetailsService(CustomUserDetailsService)
}
```