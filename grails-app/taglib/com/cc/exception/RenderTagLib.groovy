package com.cc.exception

import org.codehaus.groovy.grails.support.encoding.CodecLookup
import org.codehaus.groovy.grails.support.encoding.Encoder
import org.codehaus.groovy.grails.web.errors.ErrorsViewStackTracePrinter
import org.codehaus.groovy.grails.web.errors.ExceptionUtils
import org.springframework.util.StringUtils

/**
 * A taglib used for various rendering purposes.
 * 
 * @author Shashank Agrawal
 * @since 0.3.3
 */
class RenderTagLib {

    static namespace = "nucleus"
    static defaultEncodeAs = "raw"

    CodecLookup codecLookup
    ErrorsViewStackTracePrinter errorsViewStackTracePrinter

    /**
     * Renders an exception irrespective of a current web request.
     * Can be used within an job unlike Grails renderException taglib.<br>
     *
     * Example:<br/>
     * 
     * <pre>
     *   <code>
     *      &lt;nucleus:renderException exception="${exception}" /&gt;<br/>
     *      &lt;nucleus:renderException exception="${exception}" noCodeSnippet="true" /&gt;<br/>
     *   </code>
     * </pre>
     * 
     * @attr exception REQUIRED The exception to render
     * @attr noCodeSnippet OPTIONAL To avoid writing code snippet.
     */
    Closure renderException = { Map attrs ->
        if (!(attrs?.exception instanceof Throwable)) {
            return
        }

        Throwable exception = (Throwable) attrs.exception

        Encoder htmlEncoder = codecLookup.lookupEncoder("HTML")

        out << """<div style="background: #F5F5F5; padding: 10px; border-radius: 5px; margin-bottom: 10px;">"""

        def root = ExceptionUtils.getRootCause(exception)
        out << "<div><strong>Class</strong> &nbsp; ${root?.getClass()?.name ?: exception.getClass().name}</div>"
        out << "<div><strong>Message</strong> &nbsp; ${htmlEncoder.encode(exception.message)}</div>"

        if (root != null && root != exception && root.message != exception.message) {
            out << "<div><strong>Caused by</strong> &nbsp; ${htmlEncoder.encode(root.message)}</div>"
        }
        out << "<br>"

        if (!attrs.noCodeSnippet) {
            String codeSnippet = errorsViewStackTracePrinter.prettyPrintCodeSnippet(exception)

            // Browsers does not accepts CSS classes and its styles
            codeSnippet = codeSnippet.replaceAll("<code", "<code style=\"display: block\"")
                    .replaceAll("<h2", "<strong style=\"display: block\"")
                    .replaceAll("</h2", "</strong")

            out << codeSnippet
        }

        def trace = errorsViewStackTracePrinter.prettyPrint(exception.cause ?: exception)
        if (StringUtils.hasText(trace.trim())) {
            out << "<div><strong>Trace</strong></div>"
            out << '<pre>'
            out << htmlEncoder.encode(trace)
            out << '</pre>'
        }

        out << "</div>"
    }
}