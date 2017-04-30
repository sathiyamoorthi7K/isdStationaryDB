<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ page import="com.test.isd.dto.RequestorDTO"%>
<%@ page import="com.test.isd.dto.ItemDescriptionDTO"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	
<title>Insert title here</title>

<!-- <script type="text/javascript">
$(document).ready(function() {
	$('#items').on('change',function() {
		alert("Hello");
	});
});
</script>  -->

<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
  <link rel="stylesheet" href="/resources/demos/style.css">
  <script src="https://code.jquery.com/jquery-1.12.4.js"></script>
  <script src="https://code.jquery.com/ui/1.12.1/jquer y-ui.js"></script>
  <script>
  $( function() {
    $( "#datepicker" ).datepicker();
  } );
  </script>
  
</head>
<body>

	<%
String userName = request.getParameter("username");
RequestorDTO requestorDTO = (RequestorDTO)request.getAttribute("requestor");
Map<String, List<ItemDescriptionDTO>> itemsDescMap = ( Map<String, List<ItemDescriptionDTO>>) request.getAttribute("itemsMap");
String redirectPage = "";
if(request.getAttribute("requestservlet") != null)
{
	userName = "";
}
if(userName == null) response.sendRedirect("./pages/login.jsp");
	

%>
	Welcome
	<% if(userName.equals("")) {
		
		%>
		<%=requestorDTO.getRequestorName()%>
		<%
	} else {
		%>
		<%=userName%>
		<%} %>
		, Login Successful !!!


	<div align="center">
		
		<form method="post" action="ISDStationaryServlet">
		<table>
		<td><input type = "submit" value = "Search Requests"/>
		<input type="hidden" value="searchrequest" name="searchrequest"/>
		<input type="hidden" value=<%=requestorDTO.getRequestorName() %> name="usernameforsearch"/>
		</td>
		</table>
		</form>
		

		<form method="post" action="ISDStationaryServlet">
			<table>
				<tr>
					<td>Staff ID :</td>
					<td><input type="text" value=<%= requestorDTO.getRequestorName()%> name="staffID"/></td>
				</tr>
				<tr>
					<td>Requester Name :</td>
					<td><input type="text"
						value=<%= requestorDTO.getRequestorName()%> name="requestorName"/></td>
				</tr>
				<tr>
					<td>Telephone :</td>
					<td><input type="text" value=<%= requestorDTO.getTelephone()%> name="telephone"/></td>
				</tr>
				<tr>
					<td>Group :</td>
					<td><input type="text" value=<%= requestorDTO.getGroup()%> name="group"/></td>
				</tr>
				<tr>
					<td>Department :</td>
					<td><input type="text"
						value=<%= requestorDTO.getDepartment()%> name="dept"/></td>
				</tr>
				<tr>
					<td>Section :</td>
					<td><input type="text" value=<%= requestorDTO.getSection()%> name="section"/></td>
				</tr>
				<tr>
					<td>Delievery Date :</td>
					<td><input type="text" id="datepicker" name="delieverydate"></td>
				</tr>
				<tr>
					<td>Item</td>
					<td>Item Description</td>
					<td>Quantity</td>
				</tr>
				<%
				int i =1;
				for(Map.Entry<String, List<ItemDescriptionDTO>> entrySet : itemsDescMap.entrySet()) { 
				
				for(ItemDescriptionDTO item : entrySet.getValue()) {
				%>

				<tr>
					<td><%= item.getItemName()%> <input type="hidden" value=<%=item.getItemName() %> name=<%=item.getItemName().toLowerCase()+String.valueOf(i) %> />
					</td>
					<td><%= item.getItemDesc()%> <input type="hidden" value="<%=item.getItemDesc() %>" name=<%="desc"+item.getItemName().toLowerCase()+String.valueOf(i) %> />
					</td>
					<td><input type="text" name= <%="qty"+i %> value="" /></td>
					<td><input type="checkbox" name="selecteditems"	value=<%=i %> /></td>
				</tr>
				<%
				i++;
				}
				}
				%>

				<tr>
					<td><input type="submit" value="Submit"/>
					<input type="hidden" value="requestSubmissionForm" name="requestform"/></td>
				</tr>
			</table>
		</form>

	</div>


</body>
</html>