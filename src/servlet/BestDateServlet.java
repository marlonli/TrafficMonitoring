package servlet;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import service.BestDateServiceImpl;

import service.IBestDateService;

/**
 * @author Jiawen Sun
 */
public class BestDateServlet extends HttpServlet {
	private IBestDateService service=new BestDateServiceImpl();
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
			doPost(request,response);
	}

 
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");  
		String wp1=request.getParameter("f3origin");
		 String inputDate=request.getParameter("f3date");
		 String wp2=request.getParameter("f3destination");
		 
		 IBestDateService ts=new BestDateServiceImpl();
		 System.out.println(wp1);
		 System.out.println(inputDate);
		 System.out.println(wp2);
		 try {
			ts.getTime(wp1, wp2, inputDate);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 String responseText="";
		try {
			responseText = ts.getTime(wp1, wp2, inputDate);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 PrintWriter out = response.getWriter(); 
			out.println(responseText);  
	        out.close();  
}
}