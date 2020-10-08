<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ attribute name="href" required="true" %>
<a href="${pageContext.request.contextPath}${href}"><jsp:doBody /></a>