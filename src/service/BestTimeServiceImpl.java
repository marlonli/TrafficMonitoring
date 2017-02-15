package service;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import java.util.Map;
import dao.DbHelperImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import net.sf.json.JSONObject;

//实现接口
/**
 * @author Jiawen Sun
 */
public class BestTimeServiceImpl implements IBestTimeService {
	private DbHelperImpl dao=new DbHelperImpl();
	public Map[] query(String para1){
		//输入你的sql语句
		//这里是一个简单例子
//		String sql="select count(*) n from mapinfo where para1=? and para=?";
//		Object[] params={para1,para2};
//		Map row=dao.runSelect(sql, params)[0];
//		int n=Integer.parseInt(row.get("n").toString());
//		return n==1;
		String sql="select * from traffic where route like '%"+para1+"%'";
		System.out.println(sql);
		//Object[] params={para1};
		Map[] row=dao.runSelect(sql);
		return row;
	}
	public String getWeek(String dateStr){
		try  
		{  
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
		    Date date = sdf.parse(dateStr);  
		    String[] weekDays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
	        Calendar cal = Calendar.getInstance();
	        cal.setTime(date);
	        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
	        if (w < 0)
	            w = 0;
	        return weekDays[w];
		}  
		catch (ParseException e)  
		{  
		    System.out.println(e.getMessage());  
		}
		return null;  
	}
	public String getTime(String wp1,String wp2,String inputTime) throws Exception{
		String base="https://route.cit.api.here.com/routing/7.2/calculateroute.json?app_id=C2H9Qlr71vkryLl8gVpC&app_code=Iv5I_hntvuvQcVa3HxdtZQ&";
		String para="waypoint0=geo!"+wp1+"&waypoint1=geo!"+wp2+"&mode=fastest;car;traffic:disabled";
		String outText="the proper time you can leave on each day:            \r\n";
		
		 try {
			 if (inputTime.length()!=7){
				 
				 outText="date Format is wrong!";
				 return outText;
			 }
			 String inputHour=inputTime.substring(0, 2);
			 String inputMinute=inputTime.substring(3, 5);
			 String tag=inputTime.substring(5, 7);
			 if(tag.equals("PM"))
			 {
				 int temp=Integer.parseInt(inputHour);
				 inputHour=String.valueOf(temp+12);
			 }
				 
		URL url=new URL(base+para);
		URLConnection connection = url.openConnection();
		connection.connect();
		String responseBody = readResponseBody(connection.getInputStream());
		
		JSONObject json = JSONObject.fromObject(responseBody);
		JSONObject start=(JSONObject)json.getJSONObject("response").getJSONArray("route").get(0);
		String startname=(String)((JSONObject)start.getJSONArray("leg").get(0)).getJSONObject("start").getString("mappedRoadName");
		JSONObject end=(JSONObject)json.getJSONObject("response").getJSONArray("route").get(0);
		String endname=(String)((JSONObject)start.getJSONArray("leg").get(0)).getJSONObject("end").getString("mappedRoadName");
		String time=((JSONObject)((JSONObject)json.getJSONObject("response").getJSONArray("route").get(0)).getJSONArray("leg").get(0)).getString("travelTime");
		
		String sqls="select * from traffic where route like '%"+startname+"%'";
		
		String sqle="select * from traffic where route like '%"+endname+"%'";
		
		Map[] datas=dao.runSelect(sqls);
		for (int i=0;i<datas.length;i++)
		System.out.println(datas[i]);
		
		
		Map[] datae=dao.runSelect(sqle);
//		System.out.println("input hour"+inputHour);
		for (int i=0;i<datae.length;i++)
		System.out.println(datae[i]);
//		int startTimeCompute=0;
//		int endTimeCompute=0;
		Map<String, ArrayList<Double>> weekTimeS=initialWeekTime(new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"});
		Map<String, ArrayList<Double>> weekTimeE=initialWeekTime(new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"});

		if(datas.length > 0){
			
			for(Map i : datas){
				
				String datatime=(String)i.get("time");
				
				
				if (datatime.substring(11, 13).equals(inputHour)){
					System.out.println("databasehour"+datatime.substring(11, 13));
					String date=datatime.substring(0, 10);
					String weekTag=getWeek(date);
					//System.out.println(weekTag);
					double flow=(double) i.get("flow");
					double lenth=(double) i.get("lenth");
					
					ArrayList<Double> thislist=weekTimeS.get(weekTag);
					thislist.add((lenth/flow)*60*600);
					weekTimeS.put(weekTag, thislist);
					}
				

				
			}
		}
		if(datae.length > 0){

			for(Map i : datae){
				String datatime=(String)i.get("time");
//				System.out.println(i);
				
				if (datatime.substring(11, 13).equals(inputHour)){
					String date=datatime.substring(0, 10);
					String weekTag=getWeek(date);
					double flow=(double) i.get("flow");
					double lenth=(double) i.get("lenth");
					//System.out.println(weekTag);
					ArrayList<Double> thislist=weekTimeE.get(weekTag);
					thislist.add((lenth/flow)*60*600);
					weekTimeE.put(weekTag, thislist);
					}
				
			}

			
		}

		String temp=outText;
		Map<String, Double> merge=MergeMap(weekTimeS,weekTimeE);
		for(String obj : merge.keySet()){
			System.out.println(obj+" : " +merge.get(obj));
			if(!merge.get(obj).equals(0.0)){
				int minuteTotal=Integer.parseInt(inputHour) * 60+Integer.parseInt(inputMinute);
				int predict=minuteTotal-(int)(merge.get(obj)/60);
				String newHour=String.valueOf(((int)predict/60));
				String newMinute=String.valueOf(predict % 60);
				if (newMinute.length()==1)
					
					newMinute="0"+newMinute;
				outText+=(obj+" : "+newHour+":"+newMinute+"        \r\n");
			}
			
		}
			
		if (!outText.equals(temp)){
		return outText;
		}
		else {
			return "no records at this time yet";
		}
		

//		if(startTimeCompute+endTimeCompute==0)
//			
//			return time;
//		else
//		return String.valueOf(startTimeCompute+endTimeCompute);
		
		//System.out.println("this is the route"+startname);
		//System.out.println("this is the route"+endname);

		 }
		 catch (Exception e) {
	            System.out.println("发送GET请求出现异常！" + e);
	            e.printStackTrace();

	           return "0";

	        }
	
		
	}
	//return spend time of each day
	public Map<String, Double> MergeMap(Map<String, ArrayList<Double>> weekTimeS,Map<String, ArrayList<Double>> weekTimeE){
		Map<String,Double> merge=new HashMap<String,Double>();

		 for(String obj : weekTimeS.keySet()){
			 double totle=0;
			 ArrayList<Double> start=weekTimeS.get(obj);
//			 for(String obje:weekTimeE.keySet()){
			 if (weekTimeE.containsKey(obj))/*weekTimeE.containsKey(obj)*/
			 {
				 ArrayList<Double> end=weekTimeE.get(obj);
				 if(!start.isEmpty()){
					 double ts=0;
					 for(int i = 0; i<start.size(); i++){
						   ts+= start.get(i);

						}
					 totle+=ts/start.size();
				 }
				 if(!end.isEmpty()){
					 double te=0;
					 for(int i = 0; i<end.size(); i++){
						   te+= end.get(i);

						}
					 totle+=te/end.size();
				 }
				 merge.put(obj, totle);
				 weekTimeE.remove(obj);
			 }
//			 else{
//				 ArrayList<Double> end=weekTimeE.get(obje);
////				 if(!start.isEmpty()){
////					 double ts=0;
////					 for(int i = 0; i<start.size(); i++){
////						   ts+= start.get(i);
////
////						}
////					 totle+=ts/start.size();
////				 }
//				if(!end.isEmpty()){
//					double te=0;
//					 for(int i = 0; i<end.size(); i++){
//						   te+= end.get(i);
//
//						}
//					 totle+=te/end.size();
//				}
//				merge.put(obje, totle); 
//			 }
//				 merge.put(obje, totle);
			 
				
		
		 }
		
		if(!weekTimeE.isEmpty()){
			for(String obj : weekTimeE.keySet()){
				double totle=0;
				ArrayList<Double> end=weekTimeE.get(obj);
				 if(!end.isEmpty()){
					 double te=0;
					 for(int i = 0; i<end.size(); i++){
						   te+= end.get(i);

						}
					 totle+=te/end.size();
				 }
				 merge.put(obj, totle);
			}
		}
		 
		return merge;
	}
	public Map<String, ArrayList<Double>> initialWeekTime(String[] args){
		Map<String, ArrayList<Double>> timeByWeek=new HashMap<String, ArrayList<Double>>();
		for(String i : args){
			timeByWeek.put(i, new ArrayList<Double>());
		}
		return timeByWeek;
	}
	private String readResponseBody(InputStream inputStream) throws IOException {
		 
	    BufferedReader in = new BufferedReader(
	            new InputStreamReader(inputStream));
	    String inputLine;
	    StringBuffer response = new StringBuffer();
	 
	    while ((inputLine = in.readLine()) != null) {
	        response.append(inputLine);
	    }
	    in.close();
	     
	    return response.toString();
	}
}
