package servlet;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import service.IBestPlaceService;

import service.BestPlaceServiceImpl;

/**
 * @author Ke Xu
 */
public class BestPlaceServlet extends HttpServlet {
	/**
	 * 
	 */
	private IBestPlaceService service=new BestPlaceServiceImpl();
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
			doPost(request,response);
	}

 
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");  
		String zipcode1 = request.getParameter("zipcode1");
		String zipcode2 = request.getParameter("zipcode2");
		 String date = request.getParameter("date");
		 System.out.println(zipcode1);
		 System.out.println(zipcode2);
		 System.out.println(date);
		 IBestPlaceService ps=new BestPlaceServiceImpl();

		 String responseText="";
		 
		 
		 
		 
		 try {
			 if (date.length()!=10){
			 responseText = "{\"error\" : \"Wrong Date Format!\"}";
		 }
		 else{
			 if(date.charAt(4) != date.charAt(7) || date.charAt(7) != '-'){
					responseText = "{\"error\" : \"Wrong Date Format!\"}";
				}
				else{
			responseText=ps.getBestPlace(zipcode1, zipcode2,date);
				if(responseText == null){
					responseText = "{\"error\" : \"Cannot find best place!\"}";
				}
				}
		 }
				
//			int hour= Integer.parseInt(inputTime.substring(0, 2));
//			int minute=Integer.parseInt(inputTime.substring(3,5));
//			String tag=inputTime.substring(5,7);
//			System.out.println(tag);
//			System.out.println(timeSec);
//			System.out.println(minute);
//			System.out.println(hour);
//			int minuteTotal=hour * 60+minute;
//			int predict=minuteTotal-(int)(timeSec/60);
//			String newHour=String.valueOf(((int)predict/60));
//			String newMinute=String.valueOf(predict % 60);
//			if (newMinute.length()==1)
//				newMinute="0"+newMinute;
		
			
			
//			if(tag.equals("AM")){				
//				responseText = "the proper time you can leave is  "+newHour+":"+newMinute+"AM";		 		  		         
//			}
//			
//			else if(tag.equals("PM")){
//				responseText = "the proper time you can leave is  "+newHour+":"+newMinute+"PM";				
//			}
//			else{

//			}
			System.out.println(responseText);
			PrintWriter out = response.getWriter(); 
			out.println(responseText);  
	        out.close();  
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
  
		}

}
}
