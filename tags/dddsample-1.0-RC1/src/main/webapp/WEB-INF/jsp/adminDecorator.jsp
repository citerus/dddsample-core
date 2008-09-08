<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
  <title><decorator:title/></title>
  <script type="text/javascript" charset="UTF-8"></script>
  <style type="text/css" title="style" media="screen">
    @import "${rc.contextPath}/admin.css";
  </style>
  <decorator:head/>
</head>
<body>
  <div id="outer">
    <img id="logotype" src="<c:url value="/images/dddsample_logotype_small.png"/>" alt=""/>
    <h1>Cargo Booking and Routing</h1>
    <ul id="menu">
      <li>
        <a href="${rc.contextPath}/admin/list.html">
          List all cargos
        </a>
      </li>
      <li>
        <a href="${rc.contextPath}/admin/registrationForm.html">
          Book new cargo
        </a>
      </li>
    </ul>
    <div id="body">
      <decorator:body/>
    </div>
  </div>
</body>
</html>