package com.test.isd.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;

public class ISDConnection implements Cloneable {
	
	private static Connection conn = null;
	
	private ISDConnection() {}
	
	public static Connection getConnection() {
		try{  
			if(conn == null) {
				synchronized(Connection.class) {
					if(conn == null) {
						Class.forName("com.mysql.jdbc.Driver");  
						conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb","root","newpass");
						System.out.println("connecton object "+ conn);
					}
				}
			}
		}catch(Exception e) { 
			System.out.println(e);
		}  
		return conn;
	}
	
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
	
}
