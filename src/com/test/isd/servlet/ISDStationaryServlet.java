package com.test.isd.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.test.isd.delegate.ISDDelegate;
import com.test.isd.dto.ItemDescriptionDTO;
import com.test.isd.dto.ItemRequestedDTO;
import com.test.isd.dto.RequestorDTO;
import com.test.isd.dto.SubmitRequestDTO;
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
		
		if(request.getParameter("loginform") != null && request.getParameter("loginform").equals("loginForm")) {
			validateUser(request, response);
		} else if(request.getParameter("requestform") != null && request.getParameter("requestform").equals("requestSubmissionForm")) {
			
			ISDDelegate delegate = new ISDDelegate();
			
			RequestorDTO requestorDTO = new RequestorDTO();
			requestorDTO.setRequestorName(request.getParameter("requestorName"));
			requestorDTO.setDepartment(request.getParameter("dept"));
			requestorDTO.setGroup(request.getParameter("group"));
			requestorDTO.setSection(request.getParameter("section"));
			requestorDTO.setTelephone(request.getParameter("telephone"));
			
			SubmitRequestDTO  submitRequestDTO = new SubmitRequestDTO();
			submitRequestDTO.setRequestBy(requestorDTO.getRequestorName());
			submitRequestDTO.setRequestorDTO(requestorDTO);
			
			Calendar today = Calendar.getInstance();
			Calendar deliveryDateCalendar = Calendar.getInstance();
			deliveryDateCalendar.setTime(new Date(request.getParameter("delieverydate"))); 
			deliveryDateCalendar.add(Calendar.DAY_OF_MONTH, 7);
			Date after7Days = deliveryDateCalendar.getTime();
			if(deliveryDateCalendar.after(after7Days)) {
				System.out.println("Delievery date is greater than 7 days from today");
			}
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			submitRequestDTO.setRequestLog("Raised by "+requestorDTO.getRequestorName() + " on "+ today.getTime());			
			submitRequestDTO.setDeliveryDate(new java.sql.Date(after7Days.getTime()));
			submitRequestDTO.setStatus("In Approval");
			String month = "";
			if(today.get(Calendar.MONTH) < 9) {
				month = 0+""+today.get(Calendar.MONTH);
			} else {
				month = today.get(Calendar.MONTH)+"";
			}
			int uniqueId  = 0;
			try {
				uniqueId = delegate.getUniqueId();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			String requestID = "CC"+month + today.get(Calendar.YEAR)%100+uniqueId;
			System.out.println(requestID);
			submitRequestDTO.setRequestId(requestID);
			
			
			System.out.println(request.getParameter("requestform") );
			String arr[] = request.getParameterValues("selecteditems");
			System.out.println("Array size : "+arr.length);
			System.out.println("Date"+ request.getParameterValues("delieverydate"));
			System.out.println();
			List<ItemRequestedDTO> itemList= new ArrayList<ItemRequestedDTO>();
			ItemRequestedDTO  itemRequestedDTO = null; 
			for(String i : arr)  {
				System.out.println("Value :  "+i);
				itemRequestedDTO= new ItemRequestedDTO();
				if(request.getParameter("stapler"+i) != null && request.getParameter("stapler"+i) != null) {
					itemRequestedDTO.setItem_desc(request.getParameter("descstapler"+i));
					itemRequestedDTO.setItem_name(request.getParameter("stapler"+i));
				} else if(request.getParameter("pencils"+i) != null && request.getParameter("pencils"+i) != null) {
					itemRequestedDTO.setItem_desc(request.getParameter("descpencils"+i));
					itemRequestedDTO.setItem_name(request.getParameter("pencils"+i));
				} else if(request.getParameter("pens"+i) != null && request.getParameter("pens"+i) != null) {
					itemRequestedDTO.setItem_desc(request.getParameter("descpens"+i));
					itemRequestedDTO.setItem_name(request.getParameter("pens"+i));
				}
				itemRequestedDTO.setQuantity(Integer.parseInt(request.getParameter("qty"+i)));
				itemList.add(itemRequestedDTO);
				
				System.out.println(request.getParameter("stapler"+i));
				System.out.println(request.getParameter("pencils"+i));
				System.out.println(request.getParameter("pens"+i));
				System.out.println(request.getParameter("qty"+i));

			}
			submitRequestDTO.setItemList(itemList);
			
			int rowCount = 0;
			try {
				rowCount = delegate.submitRequest(submitRequestDTO);
				
				PrintWriter out = response.getWriter();
				out.println("<html><body><script>alert('Request created !');</script></body></html>");
				
				RequestorDTO requestorDTO1 = delegate.fetchRequestorDetails(requestorDTO.getRequestorName());
				 Map<String, List<ItemDescriptionDTO>> itemsDescMap = delegate.fetchItemDetailsMap();
				System.out.println("name   "+requestorDTO1.getRequestorName());
				if(requestorDTO1.getRequestorName() != null) {
					request.setAttribute("username",requestorDTO.getRequestorName());
					request.setAttribute("requestor", requestorDTO1); 
					request.setAttribute("itemsMap", itemsDescMap);
					System.out.println("item details map "+itemsDescMap.size());
					request.setAttribute("requestservlet", "fromrequestformservlet");
					request.getRequestDispatcher("./pages/requestform.jsp").forward(request, response);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			if(rowCount > 0) {
				System.out.println("Rows inserted successfully");
			}
		} else if(request.getParameter("searchscreen") != null && request.getParameter("searchscreen").equals("searchscreen")) {
			ISDDelegate delegate = new ISDDelegate();
			String searchParameter = request.getParameter("search");
			List<SubmitRequestDTO> submitRequestDTOList = null;
			try {
				submitRequestDTOList = delegate.searchRequests(searchParameter);
				request.setAttribute("searchresults", submitRequestDTOList);
				request.setAttribute("searchby", searchParameter);
				request.getRequestDispatcher("./pages/adminsearch.jsp").forward(request, response);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		else if(request.getParameter("updaterequest") != null) {
			ISDDelegate delegate = new ISDDelegate();

			
			int listSize = Integer.parseInt(request.getParameter("requestsize"));
			List<SubmitRequestDTO> updateRequestDTOList = new ArrayList<SubmitRequestDTO>();
			SubmitRequestDTO submitRequestDTO = null;
			for(int i =1;i<=listSize;i++) {
				if(request.getParameter("action"+i) != null && !request.getParameter("action"+i).equals("--select--")) {
					submitRequestDTO = new SubmitRequestDTO();
					submitRequestDTO.setRequestId(request.getParameter("req"+String.valueOf(i)));
					submitRequestDTO.setStatus(request.getParameter("action"+i));
					if(request.getParameter("del"+i) != null &&  !request.getParameter("del"+i).equals("")) {
						SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
						try {
							submitRequestDTO.setDeliveryDate(new java.sql.Date(formatter.parse(request.getParameter("del"+i)).getTime()));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
					updateRequestDTOList.add(submitRequestDTO);
				}
				
			}
			try {
				delegate.updateRequest(updateRequestDTOList);
				
				List<SubmitRequestDTO> submitRequestDTOList = null;
				String searchParameter = request.getParameter("searchbyparam");
				submitRequestDTOList = delegate.searchRequests(searchParameter);
				request.setAttribute("searchresults", submitRequestDTOList);
				request.setAttribute("searchby", searchParameter);
				request.getRequestDispatcher("./pages/adminsearch.jsp").forward(request, response);
				
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			

		}
		
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
