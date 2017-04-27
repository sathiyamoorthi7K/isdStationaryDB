package com.test.isd.delegate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.test.isd.dto.UserDTO;
import com.test.isd.jdbc.ISDConnection;

public class ISDDelegate {
	
	
	public boolean validateUser(UserDTO userDTO) throws SQLException {
		
		Connection conn = ISDConnection.getConnection();
		
		Statement stmt=conn.createStatement();  
		ResultSet rs=stmt.executeQuery("SELECT USERNAME, PASSWORD FROM USER");  
		while(rs.next())  {
			
			if(userDTO.getUserName() != null && userDTO.getPassWord() != null) {
				System.out.println("user name "+rs.getString("USERNAME"));
				System.out.println("password "+ rs.getString("PASSWORD"));
				if(userDTO.getPassWord().equals(rs.getString("PASSWORD")) && userDTO.getUserName().equals(rs.getString("USERNAME"))) {
					System.out.println("Login success");
				} else {
					System.out.println("Login failed");
				}
			}
		}
		
		return false;
	}

}
