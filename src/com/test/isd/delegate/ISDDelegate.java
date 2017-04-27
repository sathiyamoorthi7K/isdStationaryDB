package com.test.isd.delegate;

import java.sql.SQLException;

import com.test.isd.dao.ValidateUserDAO;
import com.test.isd.dto.UserDTO;

public class ISDDelegate {
	
	
	public boolean validateUser(UserDTO userDTO) throws SQLException {
		ValidateUserDAO validateUserDAO = new ValidateUserDAO();
		return validateUserDAO.validateUser(userDTO);
	}

}
