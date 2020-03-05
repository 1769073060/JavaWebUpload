<%--
  Created by IntelliJ IDEA.
  User: asus
  Date: 2020/2/28
  Time: 21:22
  To change this template use File | Settings | File Templates.
--%>

<%--

  通过表单上传文件
    get：上传文件大小有限制
    post：上传文件大小没有限制

--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>$Title$</title>
</head>
<body>
<form action="${pageContext.request.contextPath}/upload.do" enctype="multipart/form-data" method="post">
  上传用户:<input type="text" name="username"></br>
  <input type="file" name="file1"><br>
  <input type="file" name="file2"><br>
  <input type="submit"> |<input type="reset">
</form>
</body>
</html>
