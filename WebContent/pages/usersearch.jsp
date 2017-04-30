<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="com.test.isd.dto.ItemRequestedDTO"%>
<%@ page import="com.test.isd.dto.SubmitRequestDTO"%>
<%@ page import="java.util.Map"%>
<%@ page import="java.util.List"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
  <link rel="stylesheet" href="/resources/demos/style.css">
  <script src="https://code.jquery.com/jquery-1.12.4.js"></script>
  <script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
  <script>
  $( function() {
    $( "#datepicker" ).datepicker();
  } );
  </script>
</head>
<body>

<form>
<table>
<tr>
<td>My Requests : </td> 

<% List<String> itemIdList = (List<String>)request.getAttribute("requestIdList");

if(itemIdList!= null && !itemIdList.isEmpty()) {
%>
<td>
<select name="reqid">

<option>--select--</option>
<% for(String s : itemIdList) {
	%>
<option><%=s %></option>
<%} %>
</select>

</td>
<%} %>
</tr>
<tr><td><input type="submit" value="Search Request"/><input type="hidden" value="searchbyrequestid" name="searchbyrequestid"/>
<input type="hidden" value=<%=request.getParameter("userforrequestsearch") %> name="userforrequestsearch"/></td> </tr>
</table>
</form>

<% 
SubmitRequestDTO submitRequestDTO = (SubmitRequestDTO)request.getAttribute("searchbyreqidresult");
if(submitRequestDTO != null && submitRequestDTO.getRequestId() != null) { %>
<div align="center">
<form method="post" action="ISDStationaryServlet">
			<table>
			
				<tr>
					<td>Request ID </td>
					<td>Delivery Date</td>
					<td>Item Name</td>
					<td>Item Desc</td>
					<td>Quantity</td>
					<td>Status</td>
					<td>Change Delivery Date</td>
					<td></td>
				</tr>
				<%
				int i =1;
				for(ItemRequestedDTO itemDTO : submitRequestDTO.getItemList() ) {
					
				%>
				<tr>
					<td><%=submitRequestDTO.getRequestId() %> <input type="hidden" value=<%=submitRequestDTO.getRequestId()%> name=<%="requestid"+i%>></td>
					<td><%=submitRequestDTO.getDeliveryDate() %></td>
					<td><%=itemDTO.getItem_name() %></td>
					<td><%=itemDTO.getItem_desc() %><input type="hidden" value='<%=itemDTO.getItem_desc()%>' name=<%="itemdesc"+i%>></td>
					<td><input type="text" value=<%=itemDTO.getQuantity()%> 
					<%if(!submitRequestDTO.getStatus().equals("In Approval")){  %> disabled="disabled"<%} %>
					name=<%="newquantity"+i%>></td>
					<td><%=submitRequestDTO.getStatus() %></td>
					<td><input type="text" id="datepicker" 
					<%if(!submitRequestDTO.getStatus().equals("In Approval")){  %> disabled="disabled"<%} %>
					name=<%="delieverydate"+i%>></td>
				</tr>
				<% i++;} %>
				<tr>
				<td><input type="submit" value = "Update Request"/>
				<input type="hidden" value=<%=submitRequestDTO.getItemList().size() %> name="itemsize"/>
				<input type="hidden" value=<%=request.getAttribute("username")%> name="getusername">
				<input type="hidden" value="updaterequestbyuser" name="updaterequestbyuser"/>
				</td>
				</tr>
			</table>
		</form>
</div>
<%} %>

</body>
</html>