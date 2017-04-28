package com.test.isd.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.test.isd.delegate.ISDDelegate;
import com.test.isd.dto.ItemDescriptionDTO;
import com.test.isd.dto.RequestorDTO;
import com.test.isd.dto.UserDTO;

/**
 * Servlet implementation class ISDStationaryServlet
 */
@WebServlet("/ISDStationaryServlet")
public class ISDStationaryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ISDStationaryServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		validateUser(request, response);
		
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private void validateUser(HttpServletRequest request, HttpServletResponse response) {
		String userName = request.getParameter("username");
		String password = request.getParameter("password");
		
		System.out.println(userName);
		System.out.println(password);
		
		UserDTO userDTO = new UserDTO();
		userDTO.setUserName(userName.trim());
		userDTO.setPassWord(password.trim());
		
		ISDDelegate delegate = new ISDDelegate();
		try {
			if(delegate.validateUser(userDTO)) {
				
				/*Cookie loginCookie = new Cookie("username",userDTO.getUserName());
				//setting cookie to expiry in 30 mins
				loginCookie.setMaxAge(30*60);
				response.addCookie(loginCookie);
				response.sendRedirect("./pages/requestform.jsp");*/
				
				RequestorDTO requestorDTO = delegate.fetchRequestorDetails(userDTO.getUserName());
				 Map<String, List<ItemDescriptionDTO>> itemsDescMap = delegate.fetchItemDetailsMap();
				
				if(requestorDTO.getRequestorName() != null) {
					request.setAttribute("username",userDTO.getUserName());
					request.setAttribute("requestor", requestorDTO);
					request.setAttribute("itemsMap", itemsDescMap);
					request.getRequestDispatcher("./pages/requestform.jsp").forward(request, response);
				} else {
					System.out.println("UnAuthorized");
					response.sendRedirect("./pages/login.jsp");
				}
			} else {
				System.out.println("UnAuthorized");
				response.sendRedirect("./pages/login.jsp");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
