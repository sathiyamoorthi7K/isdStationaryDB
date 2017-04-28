package com.test.isd.delegate;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.test.isd.dao.ISDStationaryDAO;
import com.test.isd.dto.ItemDescriptionDTO;
import com.test.isd.dto.RequestorDTO;
import com.test.isd.dto.UserDTO;

public class ISDDelegate {
	
	ISDStationaryDAO isdStationaryDAO = new ISDStationaryDAO();

	public boolean validateUser(UserDTO userDTO) throws SQLException {
		return isdStationaryDAO.validateUser(userDTO);
	}
	
	public RequestorDTO fetchRequestorDetails(String username) throws SQLException {
		RequestorDTO requestorDTO = isdStationaryDAO.fetchRequestorDetails(username);
		return requestorDTO;
	}
	
	public Map<String, List<ItemDescriptionDTO>> fetchItemDetailsMap() throws SQLException {
		Map<String, List<ItemDescriptionDTO>> itemsDescMap = isdStationaryDAO.fetchItemDetailsMap();
		return itemsDescMap;
	}

}
