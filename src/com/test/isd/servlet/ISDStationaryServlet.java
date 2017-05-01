package com.test.isd.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
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
			Calendar tday = Calendar.getInstance();
			tday.add(Calendar.DAY_OF_MONTH, 7);
			if(request.getParameter("delieverydate").equals("")) {
				
				submitRequestDTO.setDeliveryDate(new java.sql.Date(tday.getTime().getTime()));
				
				
				submitRequestDTO.setRequestLog("Raised by "+requestorDTO.getRequestorName() + " on "+ new Date(submitRequestDTO.getDeliveryDate().getTime()));			
				
			} else {
				String inputDateString = request.getParameter("delieverydate");
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				try {
					Date inputDate = formatter.parse(inputDateString);
					if(inputDate.after(tday.getTime())) {
						submitRequestDTO.setDeliveryDate(new java.sql.Date(tday.getTime().getTime()));
					} else {
						submitRequestDTO.setDeliveryDate(new java.sql.Date(inputDate.getTime()));
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			
			
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
				if(request.getParameter("qty"+i).equals("")) {
					itemRequestedDTO.setQuantity(0);
				} else {
					itemRequestedDTO.setQuantity(Integer.parseInt(request.getParameter("qty"+i)));
				}
				
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
					request.setAttribute("usernamefromservlet",requestorDTO.getRequestorName());
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
			int pageNo = Integer.parseInt(request.getParameter("firstpage"));
			int total=5;
			
			List<SubmitRequestDTO> submitRequestDTOList = null;
			try {
				submitRequestDTOList = delegate.searchRequests(searchParameter, pageNo-1, total);
				int totalRecords = delegate.getRequestListSizeForPagination();
				request.setAttribute("searchresults", submitRequestDTOList);
				request.setAttribute("searchby", searchParameter);
				request.setAttribute("totalrecords", totalRecords);
				request.getRequestDispatcher("./pages/adminsearch.jsp").forward(request, response);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		} else if(request.getParameter("pageparam")!=null) {
			ISDDelegate delegate = new ISDDelegate();
			String page = request.getParameter("page");
			String searchParameter = request.getParameter("searchby");
			System.out.println("Page number : "+page);
			int pageNo = Integer.parseInt(page);
			int total=5;
			
			if(pageNo==1){}  
			else{  
				pageNo=pageNo-1;  
				pageNo=pageNo*total+1;  
			}
			
			List<SubmitRequestDTO> submitRequestDTOList = null;
			try {
				submitRequestDTOList = delegate.searchRequests(searchParameter, pageNo-1, total);
				int totalRecords = delegate.getRequestListSizeForPagination();
				request.setAttribute("searchresults", submitRequestDTOList);
				request.setAttribute("searchby", searchParameter);
				request.setAttribute("totalrecords", totalRecords);
				request.getRequestDispatcher("./pages/adminsearch.jsp").forward(request, response);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		} else if(request.getParameter("updaterequest") != null) {
			ISDDelegate delegate = new ISDDelegate();

			
			int listSize = Integer.parseInt(request.getParameter("requestsize"));
			List<SubmitRequestDTO> updateRequestDTOList = new ArrayList<SubmitRequestDTO>();
			SubmitRequestDTO submitRequestDTO = null;
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			for(int i =1;i<=listSize;i++) {
				if(request.getParameter("action"+i) != null && !request.getParameter("action"+i).equals("--select--")) {
					submitRequestDTO = new SubmitRequestDTO();
					submitRequestDTO.setRequestId(request.getParameter("req"+String.valueOf(i)));
					submitRequestDTO.setStatus(request.getParameter("action"+i));
					submitRequestDTO.setApprovalLog(submitRequestDTO.getStatus()+" by admin on "+new Timestamp(new Date().getTime()));
					if(request.getParameter("del"+i) != null &&  !request.getParameter("del"+i).equals("")) {
						
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
				submitRequestDTOList = delegate.searchRequests(searchParameter,0,5);
				int totalRecords = delegate.getRequestListSizeForPagination();
				request.setAttribute("totalrecords", totalRecords);
				request.setAttribute("searchresults", submitRequestDTOList);
				request.setAttribute("searchby", searchParameter);
				request.getRequestDispatcher("./pages/adminsearch.jsp").forward(request, response);
				
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			

		} else if(request.getParameter("searchrequest") != null && request.getParameter("searchrequest").equals("searchrequest")) {
			ISDDelegate delegate = new ISDDelegate();
			String username = request.getParameter("usernameforsearch");
			List<String> requestIdList = null;
			try {
				requestIdList = delegate.searchRequestsByUserName(username);
				request.setAttribute("requestIdList", requestIdList);
				request.setAttribute("userforrequestsearch", username);
				request.getRequestDispatcher("./pages/usersearch.jsp").forward(request, response);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		} else if(request.getParameter("searchbyrequestid") != null) {
			ISDDelegate delegate = new ISDDelegate();
			String requestID = request.getParameter("reqid");
			
			
			try {
				SubmitRequestDTO submitRequestDTO = delegate.searchByRequestId(requestID);
				
				String username = submitRequestDTO.getRequestBy();
				List<String> requestIdList = null;
				requestIdList = delegate.searchRequestsByUserName(username);
				request.setAttribute("username", username);
				request.setAttribute("requestIdList", requestIdList);
				request.setAttribute("searchbyreqidresult", submitRequestDTO);
				request.getRequestDispatcher("./pages/usersearch.jsp").forward(request, response);
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		}
		else if(request.getParameter("updaterequestbyuser")!=null) {
			ISDDelegate delegate = new ISDDelegate();
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			int itemsize = Integer.parseInt(request.getParameter("itemsize"));
			System.out.println("Item size : "+itemsize);
			List<ItemRequestedDTO> itemList = new ArrayList<ItemRequestedDTO>();
			ItemRequestedDTO itemRequestedDTO = null;
			SubmitRequestDTO submitRequestDTO = new SubmitRequestDTO();
			String reqId = "";
			String delDate = "";
			for(int i =1;i<=itemsize;i++) {
				itemRequestedDTO = new ItemRequestedDTO();
				String itemDesc = request.getParameter("itemdesc"+i);
				reqId = request.getParameter("requestid"+i);
				delDate = request.getParameter("delieverydate"+i);
				itemRequestedDTO.setItem_desc(itemDesc);
				int qty = 0;
				if(request.getParameter("newquantity"+i) != null && !request.getParameter("newquantity"+i).equals("")) {
					qty = Integer.parseInt(request.getParameter("newquantity"+i));
					itemRequestedDTO.setQuantity(qty);
				}
				
				itemList.add(itemRequestedDTO);
			}
			submitRequestDTO.setItemList(itemList);
			submitRequestDTO.setRequestId(reqId);
			try {
				if(!delDate.equals("")) {
					submitRequestDTO.setDeliveryDate(new java.sql.Date(formatter.parse(delDate).getTime()));
				}
				
				delegate.updateRequestById(submitRequestDTO);
				
				String username = request.getParameter("getusername");
				List<String> requestIdList = null;
				requestIdList = delegate.searchRequestsByUserName(username);
				request.setAttribute("requestIdList", requestIdList);
				request.getRequestDispatcher("./pages/usersearch.jsp").forward(request, response);
			} catch (ParseException e) {
				e.printStackTrace();
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
			String result = delegate.validateUser(userDTO);
			String arr[] = result.split("_");
			if(arr[1].equals("true")) {
				
				/*Cookie loginCookie = new Cookie("username",userDTO.getUserName());
				//setting cookie to expiry in 30 mins
				loginCookie.setMaxAge(30*60);
				response.addCookie(loginCookie);
				response.sendRedirect("./pages/requestform.jsp");*/
				if(arr[0].equals("admin")) {
					request.getRequestDispatcher("./pages/adminsearch.jsp").forward(request, response);
				} else {
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
