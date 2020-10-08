<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="app" tagdir="/WEB-INF/tags/content" %>
<%@ taglib prefix="base" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="r" uri="http://www.depasser.com.br/web/resources" %>
<app:template>
	<jsp:attribute name="head">
		<link rel="shortcut icon" type="image/png" href="${pageContext.request.contextPath}/favicon.png" />
		<link rel="alternate" type="application/rss+xml" title="John Bokma RSS" href="${pageContext.request.contextPath}/blog/rss.do">
		<r:resources>
			style/index.css
			script/Names.js
			script/mootools/Core.js
			script/JSON.js
			script/util/Logger.js
			script/util/Logger/ConsoleLogger.js
			script/util/EventManager.js
			script/util/Action.js
			script/util/Executor.js
			script/web/XHR.js
			script/web/Loader.js
			script/web/Dependency.js
			script/web/Tracker.js
			script/all.js
			script/index.js
		</r:resources>
		<title>Dépasser - Escola de Tecnologia</title>
	</jsp:attribute>
	<jsp:body>
		<div id="universe"></div>
	</jsp:body>
</app:template>