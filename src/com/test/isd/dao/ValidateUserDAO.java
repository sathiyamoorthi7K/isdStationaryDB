package com.test.isd.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.test.isd.dto.UserDTO;
import com.test.isd.jdbc.ISDConnection;

public class ValidateUserDAO {
	
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

}
