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
A DataAccessExcepton occured.
</h1>
<h2><span id="error" class="error">${exception.message}</span></h2>
<a href="start.html">Start page</a>
</dv>
</body>
</html>