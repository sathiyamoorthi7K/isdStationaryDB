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
import com.test.isd.dto.RequestorDTO;
import com.test.isd.dto.UserDTO;
import com.test.isd.jdbc.ISDConnection;

public class ISDStationaryDAO {

	public boolean validateUser(UserDTO userDTO) throws SQLException {
		boolean isSuccess = false;
		Connection conn = ISDConnection.getConnection();
		PreparedStatement stmt = conn.prepareStatement("SELECT USERNAME, PASSWORD FROM USER WHERE USERNAME = ? AND PASSWORD = ?");
		stmt.setString(1, userDTO.getUserName());
		stmt.setString(2, userDTO.getPassWord());
		ResultSet rs=stmt.executeQuery();  
		if(rs.next())  {
			isSuccess = true;
		}
		return isSuccess;
	}
	
	
	public RequestorDTO fetchRequestorDetails(String username) throws SQLException {
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
		return requestorDTO;
	}
	
	public Map<String, List<ItemDescriptionDTO>> fetchItemDetailsMap() throws SQLException {
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
		return itemsDescMap;
	}
	
}
