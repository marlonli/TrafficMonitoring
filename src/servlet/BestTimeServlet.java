package servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import service.IBestTimeService;
import service.BestTimeServiceImpl;
/**
 * @author Jiawen Sun
 */
public class BestTimeServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IBestTimeService service=new BestTimeServiceImpl();
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
			doPost(request,response);
	}

 
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");  
		String wp1=request.getParameter("f1origin");
		 String inputTime=request.getParameter("f1time");
		 String wp2=request.getParameter("f1destination");
		 System.out.println(wp1);
		 System.out.println(inputTime);
		 System.out.println(wp2);
		 IBestTimeService ts=new BestTimeServiceImpl();
		 //Map[] m=ts.query(para1);
		 //System.out.println(m.length);
		 //String wp1="52.5,13.4";
		// String wp2="52.5,13.45";
		 String responseText;
		 try {if (inputTime.length()!=7){
			 responseText = "Wrong Time Format!";
		 }
		 else{
			 String tag=inputTime.substring(5,7);
			 //System.out.println(tag);
				if(!tag.equals("AM")&&!tag.equals("PM")){
					responseText = "Wrong Time Format!";
				}
				else{
			responseText=ts.getTime(wp1, wp2,inputTime);
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
		 //从WEB端接收参数
		 //这里写处理过程

}
}
