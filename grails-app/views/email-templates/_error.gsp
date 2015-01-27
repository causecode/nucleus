<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="main"/>
    <title></title>
</head>
<body>
    <div>
        Hello,
    </div>
    <br>
    <div>
        Internal server error occurred <strong>${exception}</strong> for <strong>${userInstance}</strong>.
        <br>Error Source: <a href="${requestURL }">${requestURL }</a>
        <br>
        <g:if test="${stackTrace }">
            <p>
                <strong>Stack Trace</strong>
                <br>
                <pre>
                    ${stackTrace }
                </pre>
            </p>
        </g:if>
        <g:if test="${exception?.cause }">
            <p>
                <strong>Cause:</strong>
                <br>
                <pre>${exception.cause.dump() }</pre>
            </p>
        </g:if>
        <br>
    </div>
    Team,<br>
    BillAway Server
</body>
</html>