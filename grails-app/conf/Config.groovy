grails.app.context = "/" //url will be :localhost:8080

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'com.cc.user.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'com.cc.user.UserRole'
grails.plugin.springsecurity.authority.className = 'com.cc.user.Role'

grails.plugin.springsecurity.password.algorithm = 'SHA-256'
grails.plugin.springsecurity.password.hash.iterations = 1
grails.plugin.springsecurity.logout.postOnly = false
grails.plugin.springsecurity.rejectIfNoRule = false
grails.plugin.springsecurity.securityConfigType = "Annotation"

// Add this spring security static rule for documentation in App
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
    '/': ["permitAll"],
    '/**': ["permitAll"]
]
