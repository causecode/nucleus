import org.apache.log4j.DailyRollingFileAppender

grails.app.context = "/" 

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

grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
    xml: ['text/xml', 'application/xml'],
    text: 'text/plain',
    js: 'text/javascript',
    rss: 'application/rss+xml',
    atom: 'application/atom+xml',
    css: 'text/css',
    csv: 'text/csv',
    pdf: 'application/pdf',
    rtf: 'application/rtf',
    excel: 'application/vnd.ms-excel',
    ods: 'application/vnd.oasis.opendocument.spreadsheet',
    all: '*/*',
    json: ['application/json','text/json'],
    form: 'application/x-www-form-urlencoded',
    multipartForm: 'multipart/form-data',
]

log4j = {
    appender new DailyRollingFileAppender(name: 'debugFile', datePattern: "'.'MM-dd-yyyy",
        fileName: './logs/nucleus/debugLog', layout: pattern(conversionPattern: '%-5p %d %c{2} %x - %m%n%n'))

    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'

    warn   'org.mortbay.log'
    debug  debugFile: ['com.cc']
}