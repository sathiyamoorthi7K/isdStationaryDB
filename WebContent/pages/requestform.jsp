<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="com.test.isd.dto.RequestorDTO" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

<%
String userName = request.getParameter("username");
RequestorDTO requestorDTO = (RequestorDTO)request.getAttribute("requestor");

if(userName == null) {
	response.sendRedirect("login.jsp");
}
%>
Welcome <%=userName %>, Login Successful !!!

	<div align="center">

		<form>
			<table>
				<tr>
					<td>Staff ID :</td>
					<td><input type="text" value=<%= userName%> /></td>
				</tr>
				<tr>
					<td>Requester Name :</td>
					<td><input type="text" value=<%= requestorDTO.getRequestorName()%> /></td>
				</tr>
				<tr>
					<td>Telephone :</td>
					<td><input type="text" value=<%= requestorDTO.getTelephone()%> /></td>
				</tr>
				<tr>
					<td>Group :</td>
					<td><input type="text" value=<%= requestorDTO.getGroup()%> /></td>
				</tr>
				<tr>
					<td>Department :</td>
					<td><input type="text" value=<%= requestorDTO.getDepartment()%> /></td>
				</tr>
				<tr>
					<td>Section :</td>
					<td><input type="text" value=<%= requestorDTO.getSection()%> /></td>
				</tr>
			</table>
		</form>

	</div>


</body>
</html>