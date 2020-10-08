<%@ tag language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="app" tagdir="/WEB-INF/tags/content" %>
<%@ taglib prefix="base" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="head" fragment="true" %>
<c:set var="ROOT" value="${pageContext.servletContext.contextPath}" scope="application" />
<html>
<base:header>
<jsp:invoke fragment="head" />
</base:header>
<app:body>
	<jsp:doBody />
</app:body>
</html>