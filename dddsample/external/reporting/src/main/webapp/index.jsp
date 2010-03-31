<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
  <script type="text/javascript" src="jquery-1.3.2.js"></script>
  <script type="text/javascript" src="reporting.js"></script>
  <link rel="stylesheet" type="text/css" href="main.css"/> 
  <title>DDDSample Reporting</title>
</head>
<body>
  <img id="background" src="background.png" alt=""/>
  <div id="content">
    <div id="header">
      <img src="dddsample.png" alt="dddsample"/>
    </div>
    <div id="form">
      <form action="" method="get" onsubmit="search(); return false;">
        Track cargo or voyage:
        <input id="query" name="query" type="text" value="ABC"/>
        <input id="submit" name="submit" type="submit" value="Track"/>
      </form>
    </div>
    <div id="result"></div>
    <div id="footer">
      Created by <a href="http://www.citerus.se">Citerus</a> and <a href="http://www.domainlanguage.com">Domain Language</a>
    </div>
  </div>
  <script type="text/javascript">
    $('#query').focus();
  </script>
</body>
</html>

