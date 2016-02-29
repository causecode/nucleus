<g:applyLayout name="emailMain">
    <html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    </head>
    <body>
        <div>
            Hello,
        </div>
        <br>
        <div>
            Internal server error occurred
            <g:if test="${userInstance }">for ${userInstance }</g:if>
            <g:if test="${requestURL }">while processing request at <strong>${requestURL }</strong></g:if> 
            <g:if test="${angularURL }">with angular app URL <strong>${angularURL }</strong></g:if>
            <g:if test="${codeExceutionFor }">while "${codeExceutionFor }"</g:if>
        </div>
        <br>
        <g:each in="${exceptions }" var="exception">
            <nucleus:renderException exception="${exception }" noCodeSnippet="${noCodeSnippet }" />
        </g:each>
        <br>
        From,<br>
        ${appName } Server
    </body>
    </html>
</g:applyLayout>