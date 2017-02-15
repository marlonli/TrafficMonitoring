package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import dao.DbHelperImpl;
import net.sf.json.JSONObject;
/**
 * @author Jiawen Sun
 */
//实现接口
public class BestDateServiceImpl implements IBestDateService{
	private DbHelperImpl dao=new DbHelperImpl();
	public boolean query(String para1,String para2){
		String sql="select count(*) n from mapinfo where para1=? and para=?";//输入你的sql语句
		Object[] params={para1,para2};
		Map row=dao.runSelect(sql, params)[0];
		int n=Integer.parseInt(row.get("n").toString());
		return n==1;
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
	public String getNowDate(){
		 Date now = new Date(); 
		 SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		 String nowDate = dateFormat.format( now );
		 return nowDate;
	}
    public int nDaysBetweenTwoDate(String firstString, String secondString) {  
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");  
        Date firstDate = null;  
        Date secondDate = null;  
        try {  
            firstDate = df.parse(firstString);  
            secondDate = df.parse(secondString);  
        } catch (Exception e) {  
         
            System.out.println("wrong date format");  
        }  
        int nDay = (int) ((secondDate.getTime() - firstDate.getTime()) / (24 * 60 * 60 * 1000));  
        return nDay;  
    } 
    
	public String getTime(String wp1,String wp2,String inputDate) throws Exception{
		String base="https://route.cit.api.here.com/routing/7.2/calculateroute.json?app_id=C2H9Qlr71vkryLl8gVpC&app_code=Iv5I_hntvuvQcVa3HxdtZQ&";
		String para="waypoint0=geo!"+wp1+"&waypoint1=geo!"+wp2+"&mode=fastest;car;traffic:disabled";
		String outText="";
		String nowDate=getNowDate();
		 try {
			 if (inputDate.length()!=10){
				 
				 outText="date Format is wrong!";
				 return outText;
			 }
			 String thisWeek=getWeek(inputDate);
			 int ndays=nDaysBetweenTwoDate(nowDate,inputDate);
			 if (ndays<0){
				 outText="date Format is wrong!";
				 return outText; 
			 }
			
			ArrayList<String> dateList=new ArrayList<String>();
			Date Nowdate = (new SimpleDateFormat("yyyy-MM-dd")).parse(nowDate);
			Calendar cal = Calendar.getInstance();
			cal.setTime(Nowdate);
			for(int i=0;i<ndays;i++){
			cal.add(Calendar.DATE, 1);
			dateList.add((new SimpleDateFormat("yyyy-MM-dd")).format(cal.getTime()).toString());
			
			}
			
//			for (int i=0;i<dateList.size();i++){
//				System.out.println(dateList.get(i));
//				
//			}
		
			
			 
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
				//for (int i=0;i<datas.length;i++)
				//System.out.println(datas[i]);
				
				
				Map[] datae=dao.runSelect(sqle);
				HashMap<String , ArrayList<Map>> date2prediction = new HashMap<String ,  ArrayList<Map>>(); 
				for (int i=0;i<dateList.size();i++){
				date2prediction.put(dateList.get(i),new ArrayList<Map>());
				
			}
//				for (String e:date2prediction.keySet()){
//					System.out.println(e+" : "+date2prediction.get(e));
//				}
				
				if(datas.length!=0){
					for (Map d: datas){
						for(Map.Entry<String , ArrayList<Map>> entry : date2prediction.entrySet()){
							String day2week=getWeek(entry.getKey());
							String data2week=getWeek(d.get("time").toString().substring(0,10));
							if(day2week.equals(data2week))
								entry.getValue().add(d);
						
							
						}
					}
				}
				if(datae.length!=0){
					for (Map d: datae){
						for(Map.Entry<String , ArrayList<Map>> entry : date2prediction.entrySet()){
							String day2week=getWeek(entry.getKey());
							String data2week=getWeek(d.get("time").toString().substring(0,10));
							if(day2week.equals(data2week))
								entry.getValue().add(d);
						
							
						}
					}
				}
				
//			for (String e:date2prediction.keySet()){
//				System.out.println(e);
//			}
			
				TreeMap<String , Double> flow2prediction = new TreeMap<String , Double>();	
				for (int i=0;i<dateList.size();i++){
					flow2prediction.put(dateList.get(i),0.0);
					
				}
				
			for(String flowdate: flow2prediction.keySet()){
				
				for(String date:date2prediction.keySet()){
					if(flowdate.equals(date)){
						ArrayList<Map> datelist=date2prediction.get(date);
						double sum=0.0;
						int len=datelist.size();
						if(len>0){
							
							for (Map s : datelist){
								sum+=Double.parseDouble(s.get("flow").toString());
								
							}
							flow2prediction.put(flowdate, sum/len);	
						}
						else
					flow2prediction.put(flowdate, sum);	
					}
				}
				
			}
				
		for(Map.Entry<String, Double> entry : flow2prediction.entrySet()){
			if(!entry.getValue().equals(0.0))
			outText+=entry.getKey().toString()+": the flow evaluation on this day is  "+entry.getValue().toString()+"\n";
		}
		for(Map.Entry<String, Double> entry : flow2prediction.entrySet()){
			if(entry.getValue()!=0.0){
		outText+="the best day you can leave from today is "+entry.getKey()+", "+getWeek(entry.getKey());
		break;
			}
		}
		
		return outText;
	} catch (Exception e) {
        System.out.println("发送GET请求出现异常！" + e);
        e.printStackTrace();

       return "0";

    }
		 
		 	 
		 
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