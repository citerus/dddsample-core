<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
  <title><decorator:title/></title>
  <script type="text/javascript" src="${rc.contextPath}/js/jquery-1.3.2.js"></script>
  <script type="text/javascript" src="${rc.contextPath}/js/jquery-ui-1.7.2.custom.min.js"></script>
  <link rel="stylesheet" type="text/css" href="${rc.contextPath}/css/jqueryui/jquery-ui-1.7.2.custom.css"/>
  <link rel="stylesheet" type="text/css" href="${rc.contextPath}/css/admin.css"/>
  <decorator:head/>
</head>
<body>
  <div id="outer">
    <img id="logotype" src="<c:url value="/images/dddsample.png"/>" alt=""/>
    <h1>Cargo Booking and Routing</h1>
    <ul id="menu">
      <li>
        <a href="${rc.contextPath}/admin/list.html">List all cargos</a>
      </li>
      <li>
        <a href="${rc.contextPath}/admin/cargoBookingForm.html">Book new cargo</a>
      </li>
      <li>
        <a href="${rc.contextPath}/admin/voyageDelayedForm.html">Report delay</a>
      </li>
    </ul>
    <div id="body">
      <h4><decorator:title/></h4>
      <decorator:body/>
    </div>
  </div>
</body>
</html>