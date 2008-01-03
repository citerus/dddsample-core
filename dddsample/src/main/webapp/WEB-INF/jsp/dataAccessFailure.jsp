<%@ include file="/WEB-INF/jspf/include.jspf" %>

<html>
<head>
	<meta http-equiv="content-type" content="text/html; charset=iso-8859-1" />
	<script type="text/javascript"></script>
	<style type="text/css" title="style" media="screen">
		@import "${rc.contextPath}/style.css";
	</style>
</head>
<body>
<div id="container">
<h1>
An DataAccessExcepton occured.
</h1>
<div id="error">
${exception.message}
</div>
<br/>
<a href="start.html">Start page</a>
</dv>
</body>
</html>