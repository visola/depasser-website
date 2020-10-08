<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="base" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="r" uri="http://www.depasser.com.br/web/resources" %>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<r:resources>
	style/font.css
	style/all.css
</r:resources>
<script>
var ROOT = '${pageContext.request.contextPath}/';
</script>
<jsp:doBody />
</head>