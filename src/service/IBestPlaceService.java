package service;
/**
 * @author Jiawen Sun
 */
public interface IBestPlaceService {
	public boolean query(String para1,String para2);
	public String getBestPlace(String zipcode1, String zipcode2, String dateStr);
	
}
