<%@ page isErrorPage="true" language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="base" tagdir="/WEB-INF/tags/content/" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<base:template>
	<jsp:attribute name="head">
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>ERROR</title>
		<style>
			#errorPage {
				color: #545454;
				font-family: arial, sans-serif;
				font-size: 10pt;
				margin: 100px 25px 0px;
				text-align: justify;
			}
		</style>
	</jsp:attribute>
	<jsp:body>
		<div id="errorPage">
			<c:choose>
				<c:when test="${pageContext.errorData != null}">
					<h2>Erro - ${pageContext.errorData.statusCode}</h2>
					<p>
						Um erro ocorreu ao tentar acessar a página: 
						<br />
						${pageContext.errorData.requestURI}
					</p>
					<c:choose>
						<c:when test="${pageContext.errorData.statusCode == 404}">
							<p>
								Parece que a página que você está tentando acessar não existe. 
								Você tem certeza que o endereço está certo?
								<br />
								<br />
								Se sim, por favor entre em contato conosco para que possamos arrumar isso - através do email contato@depasser.com.br 
								ou de nosso formulário de contato.
								<br />
								<br />
								Se não, tente um dos links no nosso menu acima.
							</p>
						</c:when>
						<c:otherwise>
							<p>
								Por favor reporte este erro para que possamos arrumar - através do email contato@depasser.com.br 
								ou de nosso formulário de contato.
							</p>
						</c:otherwise>
					</c:choose>
					<c:if test="${pageContext.errorData.throwable != null}">
						<p>
							<jsp:useBean id="today" class="java.util.Date" scope="page" />
							Data: <f:formatDate value="${today}" pattern="dd/MM/yyyy HH:mm:ss.SSS" />
							<br />
							Erro: ${pageContext.errorData.throwable.class.name}
							<br />
							Mensagem: ${pageContext.errorData.throwable.message}
						</p>
					</c:if>
				</c:when>
				<c:otherwise>
					<h2>Erro Desconhecido</h2>
					<p>Desculpe, um erro inesperado e desconhecido ocorreu.</p>
				</c:otherwise>
			</c:choose>
		</div>
	</jsp:body>
</base:template>