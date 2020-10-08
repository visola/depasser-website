<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ attribute name="file" required="true" %>
<%@ attribute name="alt" required="true" %>
<img src="${pageContext.servletContext.contextPath}/img/${file}" alt="${alt}" />