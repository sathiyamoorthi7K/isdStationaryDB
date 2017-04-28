package com.test.isd.delegate;

import java.sql.SQLException;

import com.test.isd.dao.ISDStationaryDAO;
import com.test.isd.dto.RequestorDTO;
import com.test.isd.dto.UserDTO;

public class ISDDelegate {
	
	
	public boolean validateUser(UserDTO userDTO) throws SQLException {
		ISDStationaryDAO isdStationaryDAO = new ISDStationaryDAO();
		return isdStationaryDAO.validateUser(userDTO);
	}
	
	public RequestorDTO fetchRequestorDetails(String username) throws SQLException {
		ISDStationaryDAO ISDStationaryDAO = new ISDStationaryDAO();
		RequestorDTO requestorDTO = ISDStationaryDAO.fetchRequestorDetails(username);
		return requestorDTO;
	}

}
