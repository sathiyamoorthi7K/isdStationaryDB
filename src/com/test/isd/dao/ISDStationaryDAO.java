package com.test.isd.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.test.isd.dto.ItemDescriptionDTO;
import com.test.isd.dto.ItemRequestedDTO;
import com.test.isd.dto.RequestorDTO;
import com.test.isd.dto.SubmitRequestDTO;
import com.test.isd.dto.UserDTO;
import com.test.isd.jdbc.ISDConnection;

public class ISDStationaryDAO {

	public String validateUser(UserDTO userDTO) throws SQLException {
		String result = "";
		boolean isSuccess = false;
		Connection conn = ISDConnection.getConnection();
		PreparedStatement stmt = conn.prepareStatement("SELECT USERNAME, PASSWORD, ROLE FROM USER WHERE USERNAME = ? AND PASSWORD = ?");
		stmt.setString(1, userDTO.getUserName());
		stmt.setString(2, userDTO.getPassWord());
		ResultSet rs=stmt.executeQuery();  
		if(rs.next())  {
			isSuccess = true;
			result += rs.getString("ROLE");
		}
		return result+"_"+isSuccess;
	}
	
	
	public RequestorDTO fetchRequestorDetails(String username) throws SQLException {
		System.out.println("Entering fetchRequestorDetails");
		RequestorDTO requestorDTO = new RequestorDTO();
		
		Connection conn = ISDConnection.getConnection();
		PreparedStatement stmt = conn.prepareStatement("SELECT NAME, TELEPHONE, GROUP_REL, DEPT, SECTION FROM PRE_POPULATED_STAFF INNER JOIN USER ON USER.STAFF_ID = PRE_POPULATED_STAFF.STAFF_ID AND USER.USERNAME = ?");
		stmt.setString(1, username);
		ResultSet rs=stmt.executeQuery();  
		if(rs.next())  {
			requestorDTO.setRequestorName(rs.getString("NAME"));
			requestorDTO.setDepartment(rs.getString("DEPT"));
			requestorDTO.setGroup(rs.getString("GROUP_REL"));
			requestorDTO.setTelephone(rs.getString("TELEPHONE"));
			requestorDTO.setSection(rs.getString("SECTION"));
		}
		System.out.println("Exiting fetchRequestorDetails");

		return requestorDTO;
	}
	
	public Map<String, List<ItemDescriptionDTO>> fetchItemDetailsMap() throws SQLException {
		System.out.println("Entering fetchItemDetailsMap");

		Map<String, List<ItemDescriptionDTO>> itemsDescMap = new HashMap<String , List<ItemDescriptionDTO>>();
		List<ItemDescriptionDTO> itemsList = null;
		ItemDescriptionDTO  itemDescriptionDTO = null;
		Connection conn = ISDConnection.getConnection();
		PreparedStatement stmt = conn.prepareStatement("SELECT ITEM_DESC.ITEM_NAME AS ITEM_NM, ITEM_DESC FROM ITEM_DESC INNER JOIN ITEMS ON ITEMS.ITEM_NAME = ITEM_DESC.ITEM_NAME ORDER BY ITEM_DESC.ITEM_NAME;");
		ResultSet rs=stmt.executeQuery(); 
		String prev = "";
		String curr = "";
		while(rs.next())  {
			curr = rs.getString("ITEM_NM");
			itemDescriptionDTO = new ItemDescriptionDTO();
			itemDescriptionDTO.setItemName(rs.getString("ITEM_NM"));
			itemDescriptionDTO.setItemDesc(rs.getString("ITEM_DESC"));
			
			if(!prev.equals(curr)) {
				itemsList = new ArrayList<ItemDescriptionDTO>();
			}
			
			itemsList.add(itemDescriptionDTO);
			itemsDescMap.put(rs.getString("ITEM_NM"), itemsList);
			prev = curr;
		}
		System.out.println("Exiting fetchItemDetailsMap");

		return itemsDescMap;
	}
	
	public int submitRequest(SubmitRequestDTO submitRequestDTO) throws SQLException {
		System.out.println("Entering submitRequest");

		Connection conn = ISDConnection.getConnection();
		String inserQuery = "INSERT INTO REQUESTS ( REQUEST_ID, REQUESTED_BY, DELIVERY_DATE, REQUEST_LOG, STATUS, CREATION_TIME, LAST_UPD_TIME, APPROVAL_LOG)"
				+ " VALUES (?, ? , ? , ?, ? , CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, ?)";
		PreparedStatement preparedStatement = conn.prepareStatement(inserQuery);
		preparedStatement.setString(1, submitRequestDTO.getRequestId());
		preparedStatement.setString(2, submitRequestDTO.getRequestorDTO().getRequestorName());
		preparedStatement.setDate(3, submitRequestDTO.getDeliveryDate());
		preparedStatement.setString(4, submitRequestDTO.getRequestLog());
		preparedStatement.setString(5, submitRequestDTO.getStatus());
		preparedStatement.setString(6, submitRequestDTO.getApprovalLog());

		int rowCount = preparedStatement.executeUpdate();
		
		inserQuery = "INSERT INTO ITEM_REQUESTED ( REQUEST_ID, ITEM_NAME, ITEM_DESC, QUANTITY)"
				+ " VALUES (?, ? , ? , ?)";
		
		List<ItemRequestedDTO> itemList = submitRequestDTO.getItemList();
		
		preparedStatement = conn.prepareStatement(inserQuery);
		for(ItemRequestedDTO itemDTO : itemList) {
			preparedStatement.setString(1, submitRequestDTO.getRequestId());
			preparedStatement.setString(2, itemDTO.getItem_name());
			preparedStatement.setString(3, itemDTO.getItem_desc());
			preparedStatement.setInt(4, itemDTO.getQuantity());
			
			preparedStatement.executeUpdate();
		}
		
		String updateQuery = "UPDATE PARAMS SET PARAMVALUE_INT = ? WHERE PARAM_NAME = 'unique_id'";
		preparedStatement = conn.prepareStatement(updateQuery);
		System.out.println(submitRequestDTO.getRequestId().substring(6,submitRequestDTO.getRequestId().length()));
		preparedStatement.setString(1, submitRequestDTO.getRequestId().substring(6,submitRequestDTO.getRequestId().length()));
		preparedStatement.executeUpdate();

		System.out.println("Exiting submitRequest");

		return rowCount;
		
	}
	
	public int getUniqueId() throws SQLException {
		int uniqueID= 0;
		Connection conn = ISDConnection.getConnection();
		PreparedStatement stmt = conn.prepareStatement("SELECT MAX(PARAMVALUE_INT)+1 AS ID FROM PARAMS WHERE PARAM_NAME = 'unique_id'");
		ResultSet rs=stmt.executeQuery(); 
		while(rs.next())  {
			uniqueID = rs.getInt("ID");
		}
		return uniqueID;
	}
	
	public List<SubmitRequestDTO> searchRequests(String searchParam) throws SQLException {
		SubmitRequestDTO submitRequestDTO = null;
		List<SubmitRequestDTO> submitRequestDTOList = new ArrayList<SubmitRequestDTO>();
		List<ItemRequestedDTO> itemsDTOList = null;
		ItemRequestedDTO itemRequestedDTO = null;
		String orderBy = "";
		Connection conn = ISDConnection.getConnection();
		String searchQuery = "SELECT A.REQUEST_ID, A.REQUESTED_BY, A.DELIVERY_DATE, A.CREATION_TIME, B.ITEM_NAME, B.ITEM_DESC FROM REQUESTS A INNER JOIN ITEM_REQUESTED B ON"
				+ "	A.REQUEST_ID = B.REQUEST_ID ";
		if(searchParam.equals("DateOfRequest")) {
			orderBy = " WHERE A.STATUS = 'In Approval' ORDER BY A.CREATION_TIME DESC";
		} else if(searchParam.equals("Status")) {
			orderBy = "  WHERE A.STATUS = 'In Approval' ORDER BY A.STATUS ASC";
		} else if(searchParam.equals("Name")) {
			orderBy = "  WHERE A.STATUS = 'In Approval' ORDER BY A.REQUESTED_BY";
		} else if(searchParam.equals("DeliveryDate")) {
			orderBy = "  WHERE A.STATUS = 'In Approval' ORDER BY A.DELIVERY_DATE DESC";
		} else if (searchParam.equals("DeliveryDate7days")) {
			orderBy = " WHERE A.DELIVERY_DATE >= DATE(NOW()) - INTERVAL 7 DAY AND A.STATUS = 'In Approval'";
		} else {
			orderBy = "  WHERE A.STATUS = 'In Approval' ORDER BY A.STATUS DESC";
		}
		System.out.println(searchQuery);
		String prev = "";
		String curr = "";
		PreparedStatement stmt = conn.prepareStatement(searchQuery.concat(orderBy));
		ResultSet rs=stmt.executeQuery(); 
		rs=stmt.executeQuery(); 		
	
		while(rs.next())  {
			curr = rs.getString("REQUEST_ID");
			itemRequestedDTO = new ItemRequestedDTO();
			itemRequestedDTO.setItem_desc(rs.getString("ITEM_DESC"));
			itemRequestedDTO.setItem_name(rs.getString("ITEM_NAME"));
			itemRequestedDTO.setRequestId(rs.getString("REQUEST_ID"));
			if(!prev.equals(curr)) {
				itemsDTOList = new ArrayList<ItemRequestedDTO>();
				submitRequestDTO = new SubmitRequestDTO();
				submitRequestDTO.setRequestId(rs.getString("REQUEST_ID"));
				submitRequestDTO.setRequestBy(rs.getString("REQUESTED_BY"));
				submitRequestDTO.setDeliveryDate(rs.getDate("DELIVERY_DATE"));
				submitRequestDTO.setCreationDate(rs.getTimestamp("CREATION_TIME"));
				submitRequestDTO.setItemList(itemsDTOList);
				submitRequestDTOList.add(submitRequestDTO);
			}
			itemsDTOList.add(itemRequestedDTO);
			prev = curr;
			
		}
		return submitRequestDTOList;
	}
	
	public int updateRequest(List<SubmitRequestDTO> requestList) throws SQLException {
		Connection conn = ISDConnection.getConnection();
		int rowCount  = 0;
		for(SubmitRequestDTO submitRequestDTO: requestList) {
			String inserQuery = "UPDATE REQUESTS SET LAST_UPD_TIME = CURRENT_TIMESTAMP STATUS = ? ";
			if(submitRequestDTO.getDeliveryDate() != null) {
				inserQuery += ", DELIVERY_DATE = ? ";
			}
			inserQuery += "WHERE REQUEST_ID = ?";
			
			PreparedStatement preparedStatement = conn.prepareStatement(inserQuery);
			preparedStatement.setString(1, submitRequestDTO.getStatus());
			if(submitRequestDTO.getDeliveryDate() != null) {
				preparedStatement.setDate(2, submitRequestDTO.getDeliveryDate());
				preparedStatement.setString(3, submitRequestDTO.getRequestId());
			} else {
				preparedStatement.setString(2, submitRequestDTO.getRequestId());
			}
			rowCount = preparedStatement.executeUpdate();
		}
		return rowCount;
	}
	
	public List<String> searchRequestsByUserName(String userName) throws SQLException {
		List<String> requestIdList = new ArrayList<String>();
		Connection conn = ISDConnection.getConnection();
		PreparedStatement stmt = conn.prepareStatement("SELECT REQUEST_ID FROM REQUESTS WHERE REQUESTED_BY = ?");
		stmt.setString(1, userName);
		ResultSet rs=stmt.executeQuery(); 
		while(rs.next())  {
			requestIdList.add(rs.getString("REQUEST_ID"));
		}
		
		return requestIdList;
	}
	
	public SubmitRequestDTO searchByRequestId(String requestId) throws SQLException {
		SubmitRequestDTO submitRequestDTO = new SubmitRequestDTO();
		Connection conn = ISDConnection.getConnection();
		PreparedStatement stmt = conn.prepareStatement("SELECT A.REQUEST_ID,A.REQUESTED_BY,  A.STATUS, A.DELIVERY_DATE, B.ITEM_NAME, B.ITEM_DESC,B.QUANTITY FROM REQUESTS A INNER JOIN ITEM_REQUESTED B ON A.REQUEST_ID = B.REQUEST_ID WHERE A.REQUEST_ID = ?");
		stmt.setString(1, requestId);
		ResultSet rs=stmt.executeQuery(); 
		ItemRequestedDTO itemRequestedDTO = null;
		List<ItemRequestedDTO> itemRequested = new ArrayList<ItemRequestedDTO>();
		while(rs.next())  {
			itemRequestedDTO= new ItemRequestedDTO();
			submitRequestDTO.setRequestId(rs.getString("REQUEST_ID"));
			submitRequestDTO.setDeliveryDate(rs.getDate("DELIVERY_DATE"));
			submitRequestDTO.setRequestBy(rs.getString("REQUESTED_BY"));
			submitRequestDTO.setStatus(rs.getString("STATUS"));
			itemRequestedDTO.setItem_desc(rs.getString("ITEM_DESC"));
			itemRequestedDTO.setItem_name(rs.getString("ITEM_NAME"));
			itemRequestedDTO.setQuantity(rs.getInt("QUANTITY"));
			itemRequested.add(itemRequestedDTO);
		}
		submitRequestDTO.setItemList(itemRequested);
		return submitRequestDTO;
		
	}
	
	public int updateRequestById(SubmitRequestDTO submitRequestDTO) throws SQLException {
		Connection conn = ISDConnection.getConnection();
		int rowCount  = 0;
			
			if(submitRequestDTO.getDeliveryDate() != null) {
				String inserQuery = "UPDATE REQUESTS ";
				inserQuery += " SET DELIVERY_DATE = ?, LAST_UPD_TIME = CURRENT_TIMESTAMP WHERE REQUEST_ID = ? ";
				PreparedStatement preparedStatement = conn.prepareStatement(inserQuery);
				preparedStatement.setDate(1, submitRequestDTO.getDeliveryDate());
				preparedStatement.setString(2, submitRequestDTO.getRequestId());
				rowCount = preparedStatement.executeUpdate();
			} 
			for(ItemRequestedDTO item : submitRequestDTO.getItemList()) {
				if(item.getQuantity() > 0) {
					String inserQuery = "UPDATE ITEM_REQUESTED ";
					inserQuery += " SET QUANTITY = ? WHERE REQUEST_ID = ? AND ITEM_DESC = ?";
					PreparedStatement preparedStatement = conn.prepareStatement(inserQuery);
					preparedStatement.setInt(1, item.getQuantity());
					preparedStatement.setString(2, submitRequestDTO.getRequestId());
					preparedStatement.setString(3,item.getItem_desc());
					rowCount = preparedStatement.executeUpdate();
					
				}
			}
		return rowCount;
	}
	
	
	
}
