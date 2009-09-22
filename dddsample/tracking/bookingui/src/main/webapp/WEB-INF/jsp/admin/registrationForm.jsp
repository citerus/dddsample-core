<html>
<head>
  <title>Cargo Administration</title>
    <!--CSS file (default YUI Sam Skin) -->
	<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.7.0/build/calendar/assets/skins/sam/calendar.css"/>

	<!-- Dependencies -->
	<script type="text/javascript" src="http://yui.yahooapis.com/2.7.0/build/yahoo-dom-event/yahoo-dom-event.js"></script>

	<!-- Source file -->
	<script type="text/javascript" src="http://yui.yahooapis.com/2.7.0/build/calendar/calendar-min.js"></script>
    <script type="text/javascript">

        var cal
        function pickDate() {
        }

        function datePicked(type,args,obj) {
            var dates = args[0];
            var date = dates[0];
            var year = date[0], month = date[1], day = date[2];

            var arrivalDeadline = document.getElementById("arrivalDeadline");
            arrivalDeadline.value = month + "/" + day + "/" + year;

            cal.dispose();
        }
    </script>
    <style type="text/css">
        td {
          align: left;
        }
        .yui-calcontainer {
            font-size: 9pt;
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
        <tr>
          <td>Arrival deadline:</td>
          <td>
            <input name="arrivalDeadline" type="text" size="10" id="arrivalDeadline" value="${param.arrivalDeadline}"/>&nbsp;
          </td>
        </tr>
        <tr>
            <td></td>
            <td class="yui-skin-sam">
                <div id="cal1Container"></div>
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
<script type="text/javascript">
    cal = new YAHOO.widget.Calendar('cal1Container');
    cal.render();
    cal.selectEvent.subscribe(datePicked)
</script>
</body>
</html>