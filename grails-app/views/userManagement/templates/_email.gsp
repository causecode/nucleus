<%@ page contentType="text/html"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <title><%=email?.title%></title>
</head>
<body>
    <div class="main-container">
        <div class="main-title-container" style="">
            <img alt="" src="" class="logo">
            <span style="margin-left: 13px; color: #DDD; font-size: 20px; position: relative;top: -5px;font-weight: bold;"
                class="main-title">
                <%=email?.title%>
            </span>
        </div>
        <div style="padding: 10px;">
            <%=email?.body%>
        </div>
    </div>
</body>
</html>