package service;
/**
 * @author Jiawen Sun
 */
public interface IBestDateService {
	public boolean query(String para1,String para2);
	//可以添加新的功能
	public String getTime(String wp1,String wp2,String inputDate) throws Exception;
}
