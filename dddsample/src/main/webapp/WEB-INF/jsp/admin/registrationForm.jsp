<html>
<head>
  <title>Cargo Administration</title>
  <style type="text/css">
    td {
      align: left;
    }
  </style>
</head>
<body>
<div id="container">
  <form action="<c:url value="/admin/register.html"/>" method="post">
  <table>
    <caption>Book new cargo</caption>
    <tbody>
      <tr>
        <td>Origin</td>
        <td>
          <select name="originUnlocode">
            <c:forEach items="${unlocodes}" var="u">
            <option value="${u}">${u}</option>
            </c:forEach>
          </select>
        </td>
      </tr>
      <tr>
        <td>Destination</td>
        <td>
          <select name="destinationUnlocode">
            <c:forEach items="${unlocodes}" var="u">
            <option value="${u}">${u}</option>
            </c:forEach>
          </select>
        </td>
      </tr>
    </tbody>
    <tfoot>
      <tr>
        <td> </td>
        <td>
          <input type="submit" value="Book"/>
        </td>
      </tr>
    </tfoot>
  </table>
  </form>
</div>
</body>
</html>