package service;

import java.util.Map;

/**
 * @author Jiawen Sun
 */
public interface IBestTimeService {
	public Map[] query(String para1);
	public String getTime(String wp1,String wp2,String inputTime) throws Exception;
	//��������µĹ���
}
