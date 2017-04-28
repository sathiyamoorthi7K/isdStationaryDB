<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@ page import="com.test.isd.dto.RequestorDTO" %>
    <%@ page import="com.test.isd.dto.ItemDescriptionDTO" %>
    <%@ page import="java.util.Map" %>
    <%@ page import="java.util.List" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<title>Insert title here</title>

<script type="text/javascript">
$(document).ready(function() {
	$('#items').on('change',function() {
		alert("Hello");
	});
});
</script>
s</head>
<body>

<%
String userName = request.getParameter("username");
RequestorDTO requestorDTO = (RequestorDTO)request.getAttribute("requestor");
Map<String, List<ItemDescriptionDTO>> itemsDescMap = ( Map<String, List<ItemDescriptionDTO>>) request.getAttribute("itemsMap");

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
				
				<tr>
				<td><select id="items">
							<option selected=selected>--select--</option>
				<% for(Map.Entry<String, List<ItemDescriptionDTO>> entrySet : itemsDescMap.entrySet()) { %>
							<option><%= entrySet.getKey()%></option>
					<%
					}
					%>
				</select></td>
					
					<td><select id="itemdesc">
							<option selected=selected>--select--</option>
					</select></td>
				</tr>
			</table>
		</form>

	</div>


</body>
</html>