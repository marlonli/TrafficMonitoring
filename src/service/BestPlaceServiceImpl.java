package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import net.sf.json.JSONObject;  
import net.sf.json.JSONArray;

import org.apache.commons.math3.distribution.BetaDistribution;

import dao.DbHelperImpl;
/**
 * @author Jiawen Sun
 */
//ÊµÏÖ½Ó¿Ú
public class BestPlaceServiceImpl implements IBestPlaceService{
	private DbHelperImpl dao=new DbHelperImpl();
	public boolean query(String para1,String para2){
		String sql="select count(*) n from mapinfo where para1=? and para=?";//ÊäÈëÄãµÄsqlÓï¾ä
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

	public String getExactWeather(String zipcode, String dateStr){
		try{
			String weather = "clear";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = sdf.parse(dateStr);
			Calendar cal_now = Calendar.getInstance();
			Calendar cal_quest = cal_now;
			cal_quest.setTime(date);
			int day_from_now = cal_quest.compareTo(cal_now)/(1000*60*60*24);
			if(day_from_now < 0){
				return null;				
			}else if(day_from_now > 5){
				//set default
				return "<default>";
			}
			
			/*String line="https://weather.cit.api.here.com/weather/1.0/"
					+ "report.json?product=forecast_7days_simple&"
					+ "zipcode="+zipcode
					+ "&oneobservation=true"
					+ "&app_id=C2H9Qlr71vkryLl8gVpC"
					+ "&app_code=Iv5I_hntvuvQcVa3HxdtZQ";*/
			String line = "http://api.openweathermap.org/data/2.5/forecast?"
					+"zip={"+zipcode+"}&appid=779922e75fb94438553500336bc03419";
			URL url = new URL(line);
			URLConnection conn = url.openConnection();
			conn.connect();
			String weatherString = readResponseBody(conn.getInputStream());
			
			 // "dailyForecasts": {
			 //   "forecastLocation": {
			 //     "forecast": [
			 //       {			
			JSONObject json = JSONObject.fromObject(weatherString);
			JSONObject jsonWeather = json.getJSONArray("list").getJSONObject(day_from_now*8).getJSONArray("weather").getJSONObject(0);
			/*double rain = 0.0;
			double snow = 0.0;
			if(jsonWeather.getString("rainFall") != "*"){
				rain = Double.parseDouble(jsonWeather.getString("rainFall"));
			}
			if(jsonWeather.getString("snowFall") != "*"){
				snow = Double.parseDouble(jsonWeather.getString("rainFall"));
			}*/
			
			weather = jsonWeather.getString("description");
			
			
			
			
			return weather;
			
		}catch(Exception e)
		{
			System.out.println(e.getMessage());
			return "1";
		}		
	}
	
	public String getWeather(String zipcode, String dateStr){
		try{
			String weather = "clear";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = sdf.parse(dateStr);
			Calendar cal_now = Calendar.getInstance();
			Calendar cal_quest = cal_now;
			cal_quest.setTime(date);
			int day_from_now = cal_quest.compareTo(cal_now)/(1000*60*60*24);
			if(day_from_now < 0){
				return null;				
			}else if(day_from_now > 5){
				//set default
				return weather;
			}
			
			/*String line="https://weather.cit.api.here.com/weather/1.0/"
					+ "report.json?product=forecast_7days_simple&"
					+ "zipcode="+zipcode
					+ "&oneobservation=true"
					+ "&app_id=C2H9Qlr71vkryLl8gVpC"
					+ "&app_code=Iv5I_hntvuvQcVa3HxdtZQ";*/
			String line = "http://api.openweathermap.org/data/2.5/forecast?"
					+"zip={"+zipcode+"}&appid=779922e75fb94438553500336bc03419";
			URL url = new URL(line);
			URLConnection conn = url.openConnection();
			conn.connect();
			String weatherString = readResponseBody(conn.getInputStream());
			
			 // "dailyForecasts": {
			 //   "forecastLocation": {
			 //     "forecast": [
			 //       {			
			JSONObject json = JSONObject.fromObject(weatherString);
			JSONObject jsonWeather = json.getJSONArray("list").getJSONObject(day_from_now*8).getJSONArray("weather").getJSONObject(0);
			/*double rain = 0.0;
			double snow = 0.0;
			if(jsonWeather.getString("rainFall") != "*"){
				rain = Double.parseDouble(jsonWeather.getString("rainFall"));
			}
			if(jsonWeather.getString("snowFall") != "*"){
				snow = Double.parseDouble(jsonWeather.getString("rainFall"));
			}*/
			
			if(jsonWeather.getString("main") != "Clear" && jsonWeather.getString("main") != "Clouds"){
				return "harsh";
			}
			
			
			
			
			return weather;
			
		}catch(Exception e)
		{
			System.out.println(e.getMessage());
			return "1";
		}		
	}

	public String getBestPlace(String zipcode1, String zipcode2, String dateStr){
		double[] r1_para = trafficEval(zipcode1, dateStr);
		if(r1_para == null){
			System.out.println("Cannot eval zipcode 1!");
			return null;
		}
		double[] r2_para = trafficEval(zipcode2, dateStr);
		if(r2_para == null){
			System.out.println("Cannot eval zipcode 2!");
			return null;
		}
		String output = new String();
		String weather1 = getExactWeather(zipcode1, dateStr);
		String weather2 = getExactWeather(zipcode2, dateStr);
		
		String textOut = "Zipcode1 : "+zipcode1+"\\nWeather : "+weather1+
						 "Evaluation : "+r1_para[0]+"\\nAvg. Jam Rate : "+
						 r1_para[1]+
						 "\\nZipcode2 : "+zipcode2+"\\nWeather : "+weather2+
						 "\\nEvaluation : "+r2_para[0]+"\\nAvg. Jam Rate : "+
						 r2_para[1]+"\\n";
		int[] color1 = new int[3];
		int[] color2 = new int[3];
		int[] initColor = {255, 0, 0};
		int[] endColor = {0, 255, 0};
		color1 = getColor(initColor, endColor, r1_para[0]/100);
		color2 = getColor(initColor, endColor, r2_para[0]/100);
		output += "{\n\t\"para\" :[\n\t\t{\n\t\t\t\"color1\" : ";
		output += "["+color1[0]+","+color1[1]+","+color1[2]+"],\n\t\t\t\"color2\" : ";
		output += "["+color2[0]+","+color2[1]+","+color2[2]+"],\n\t\t\t\"text\" : \"";
		output += textOut+"\"\n\t\t}\n\t]\n}\n";
		
		return output;
		
		
	}
	
	private int[] getColor(int[] initColor, int[] endColor, double scale){
		if(scale <0 || scale >1){
			return null;
		}
		int[] rColor = new int[3];
		for(int i=0;i<3;i++){
			rColor[i] = (int)((endColor[i]-initColor[i])*scale+initColor[i]);
		}
		
		return rColor;
	}
	
	private double[] trafficEval(String zipcode, String dateStr){
		//double eval = 0;
		double[] result = new double[2]; 
		//enum weatherCond = {"clear", "harsh"};
		int weatherMod = 0;
		String week = getWeek(dateStr);
		String weather = getWeather(zipcode, dateStr);
		if(weather != "clear"){
			weatherMod = 1;
		}
		double[] zipcodeLnglat = getZipcodeLnglat(zipcode);
		String regionInfo = collectRegionInfo(zipcodeLnglat);
		String sqlw = "select * from tm.weather where zipcode like '%"+zipcode+"%' and weather%2 ="+weatherMod;
		Map[] mapWeather = dao.runSelect(sqlw); 
		String sqlt = "select tm.traffic.* from tm.traffic,tm.zipcoderoute1 where tm.zipcoderoute1.zipcode="+zipcode+" and tm.traffic.route=tm.zipcoderoute1.route";
		//String sqlt = "select tm.traffic.*, tm.zipcoderoute1.zipcode, tm.weather.weather from tm.traffic join tm.zipcoderoute1 on tm.zipcoderoute1.zipcode = "+zipcode+ " and tm.zipcoderoute1.route = tm.traffic.route and tm.zipcoderoute1.lenth = tm.traffic.lenth join tm.weather on tm.weather.time = substring(tm.traffic.time,1,10) and tm.weather.zipcode = tm.zipcoderoute1.zipcode where tm.weather.weather%2="+weatherMod;
		Map[] mapTraffic = dao.runSelect(sqlt);
		//System.out.println(sqlw);
	//	System.out.println(sqlt);
		//String matchWeather="false";
		System.out.println("mt_size : "+mapTraffic.length);
		//System.out.println("mw_size : "+mapWeather.length);
		
		JSONObject jsonRegion = JSONObject.fromObject(regionInfo);
		JSONArray jsonArrayRegion = jsonRegion.getJSONArray("RWS").getJSONObject(0).getJSONArray("RW");
		
		int rCounter = 0;
		double totalLen = 0.0;
		double jamLen = 0.0;
		double evaluate = 0.0;
		double jamGate = 50;
		double jamRate = 0.0;
		BetaDistribution evalDist = new BetaDistribution(5,1);
		for(int i=0; i<jsonArrayRegion.size();i++){
			String de = jsonArrayRegion.getJSONObject(i).getJSONArray("FIS").getJSONObject(0).getJSONArray("FI").getJSONObject(0).getJSONObject("TMC").getString("DE");
			for(Map j : mapTraffic){
				//System.out.println((String)j.get("route")+",,,"+de);
				//if((String)j.get("route") == de){
				double len_sim = (double)j.get("lenth") - jsonArrayRegion.getJSONObject(i).getJSONArray("FIS").getJSONObject(0).getJSONArray("FI").getJSONObject(0).getJSONObject("TMC").getDouble("LE");
				if( (j.get("route").toString() == de ) || len_sim*len_sim ==0){
					//System.out.println(getWeek(((String)j.get("time")).substring(0, 10))+", "+week);
					if(getWeek(((String)j.get("time")).substring(0, 10)) == week){
						for(Map k : mapWeather){
							if( true||(String)k.get("time") == ((String)j.get("time")).substring(0, 10)){
								if(true ||((int)k.get("weather"))%2 == weatherMod ){
									//calc jam factor;
									rCounter++;
									totalLen += (double)j.get("lenth");
									double ff = jsonArrayRegion.getJSONObject(i).getJSONArray("FIS").getJSONObject(0).getJSONArray("FI").getJSONObject(0).getJSONArray("CF").getJSONObject(0).getDouble("FF");
									double cf = (double)j.get("flow");
									if(cf > ff){
										cf = ff;
									}
									double e = 100*evalDist.cumulativeProbability(cf/ff);
									if(e < jamGate){ 
										jamLen += (double)j.get("lenth");
									}
									evaluate += e;
								}
							}
						}
					}
				}
			}
			
		}
		if(rCounter == 0){
			System.out.println("Insufficient Data!\n");
			return null;
		}
		evaluate /= rCounter;
		jamRate = 100*jamLen/totalLen;
		result[0] = evaluate;
		result[1] = jamRate;
		return result;
	}
	
	
	private String collectRegionInfo(double[] zipcode_lnglat){
		try{
			//System.out.println("zip:"+zipcode_lnglat);
			String head = "https://traffic.cit.api.here.com/traffic/6.1/flow.json?";
			String bbox = "bbox="+zipcode_lnglat[0]+"%2C"+zipcode_lnglat[1]+"%3B"+zipcode_lnglat[2]+"%2C"+zipcode_lnglat[3];
			String tail = "&app_id=C2H9Qlr71vkryLl8gVpC&app_code=Iv5I_hntvuvQcVa3HxdtZQ";
			String result = new String();
			String uall = head+bbox+tail;
			System.out.println(uall);
			URL url = new URL(uall);
			URLConnection conn = url.openConnection();
			result = readResponseBody(conn.getInputStream());
			return result;
		}catch(Exception e){
			System.out.println(e.getMessage());
			//System.out.println("zip:"+zipcode_lnglat);
			return "-1";
		}
	}	
	
	private double[] getZipcodeLnglat(String zipcode){
		try{
		//Finding rectangular boundary of zipcode. return northeast and southwest point of rect.
		String head = "http://maps.googleapis.com/maps/api/geocode/json?address=";
		URL url = new URL(head+zipcode);
		System.out.println("URL:"+head+zipcode);
		URLConnection conn = url.openConnection();
		String geocoderString = readResponseBody(conn.getInputStream());
		
		JSONObject json = JSONObject.fromObject(geocoderString);
		JSONArray jsonArray = json.getJSONArray("results");		
		double[] lnglat = new double[4];
		
		lnglat[0] = jsonArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("bounds").getJSONObject("northeast").getDouble("lat");
		lnglat[1] = jsonArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("bounds").getJSONObject("northeast").getDouble("lng");
		lnglat[2] = jsonArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("bounds").getJSONObject("southwest").getDouble("lat");
		lnglat[3] = jsonArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("bounds").getJSONObject("southwest").getDouble("lng");
		
		return lnglat;
		
	    }catch(Exception e){
		System.out.println(e.getMessage());
		return null;
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
