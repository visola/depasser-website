<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="app" tagdir="/WEB-INF/tags/content" %>
<body>
	<div id="header">
		<img src="${pageContext.servletContext.contextPath}/img/logo.png" alt="Logo Depasser"/>
		<app:menu />
		<%--
		<span>
			<input type="text" id="search" />
			<img src="${pageContext.servletContext.contextPath}/img/zoom.png" alt="busca"/>
		</span>
		 --%>
	</div>
	<div id="container">
		<jsp:doBody />
	</div>
	<div id="footer">
		<p>Copyright © 2010 - Dépasser - Todos os Direitos Reservados</p>
	</div>
</body>