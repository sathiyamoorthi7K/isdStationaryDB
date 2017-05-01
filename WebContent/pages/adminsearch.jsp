<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
	<%@ page import="com.test.isd.dto.SubmitRequestDTO"%>
		<%@ page import="com.test.isd.dto.ItemRequestedDTO"%>
	
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
    $( ".datepicker" ).datepicker();
  } );
  </script>
</head>
<body>
<form action="ISDStationaryServlet" method="post">
<input type="hidden" value="searchscreen" name="searchscreen"/>
<table>
<% List<SubmitRequestDTO> submitRequestDTOList = (List<SubmitRequestDTO>) request.getAttribute("searchresults"); 
String searchBy = request.getParameter("searchby");

%>
<tr>
<td>Search :</td>
<td><select name="search">
		<option>--select--</option>
		<option value="DateOfRequest" >By Date of Request</option>
		<option value="Status" >By Status</option>
		<option value="Name" >By Requesteor's name</option>
		<option value="DeliveryDate" >By Date of Delivery</option>
		<option value="DeliveryDate7days">By Date of Delivery</option>
	</select></td>
	<td><input type="submit" value="Submit"/>   <input type="hidden" value="1" name="firstpage"/></td>
</tr>
</table>
</form>
<%if(submitRequestDTOList != null) { %>
<form action="ISDStationaryServlet" method="post">
<table>
<tr>
	<td>Request ID</td>
	<td>Requestor Name</td>
	<td>Delivery Date</td>
	<td>Creation Time</td>
	<td>Item Name</td>
	<td>Item Description and Quantity</td>
	<td>Change Delivery Date</td>
	<td>Action</td>
</tr>

<%
int i =1;
for(SubmitRequestDTO dto : submitRequestDTOList) {
	
	%>

<tr>
<td>
<%=dto.getRequestId()%> <input type="hidden" value=<%=dto.getRequestId()%> name=<%="req"+String.valueOf(i)%> />
</td>
<td>
<%=dto.getRequestBy() %>
</td>
<td>
<%=dto.getDeliveryDate() %>
</td>
<td>
<%=dto.getCreationDate() %>
</td>
<td>
<% for(ItemRequestedDTO itemDTO : dto.getItemList()) { %>
<%=itemDTO.getItem_name() + "  *****  "+itemDTO.getQuantity()%>
<%
break;
} %>
</td>
<td>
<% for(ItemRequestedDTO itemDTO : dto.getItemList()) { %>
<%itemDTO.getItem_desc();
%>
<%
} %>
</td>
<td>
<input type="text" class="datepicker" name=<%="del"+String.valueOf(i)%> />
</td>
<td>
<select name=<%="action"+String.valueOf(i)%>>
<option>--select--</option>
<option>Approve</option>
<option>Reject</option>
<option>Deliver</option>
</select>

</td>
</tr>
<%
i++;
}
%>

<tr><td><input type="hidden" value=<%=i %> name="updaterequest" />
<input type="hidden" value=<%=submitRequestDTOList.size() %> name="requestsize"/>
<input type="hidden" value=<%=searchBy %> name="searchbyparam"/>
<input type="submit" value="Submit"/></td>
</tr>
<tr><td><%int totalRows = Integer.parseInt(request.getAttribute("totalrecords").toString());
int totalPages = (int) Math.ceil(totalRows / 5);
for(int k =1;k<=totalPages;k++) {
	
%>
<a href=<%="ISDStationaryServlet?page="+k+"&pageparam=pageparam&searchby="+searchBy%>><%=k %></a>

<%} %>
</td></tr>
</table>
</form>
<%

}

%>
	
</body>
</html>