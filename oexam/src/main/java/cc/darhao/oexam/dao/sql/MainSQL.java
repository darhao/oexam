package cc.darhao.oexam.dao.sql;

public class MainSQL {

	//参数1：被获取信息的用户的密钥
	public static final String GET_USER_INFO_BY_KEI = "SELECT id, name  FROM user WHERE kei = ?";
	
	//参数1：将被排除获取的用户的名字
	public static final String GET_USER_INFO_BY_NOT_ID_AND_NOT_ADMIN = "SELECT id, name  FROM user WHERE id != ? AND is_admin = 0";
	
	public static final String GET_ALL_QUESTIONS = "SELECT *  FROM question";
	
	public static final String GET_WHAT_DAY_IS_TODAY = "SELECT DATE_FORMAT(NOW(),'%w') AS day";
	
	//参数1：被获取上一次考核时间的用户的ID
	public static final String GET_LAST_EXAM_TIME_BY_USER_ID = "SELECT time FROM result JOIN user ON user.id = result.examer WHERE user.id = ? ORDER BY time limit 1";
}
