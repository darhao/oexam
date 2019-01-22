package cc.darhao.oexam.dao;

public class SQL {

	public static final String GET_USER_INFO_BY_KEI = "SELECT id, name  FROM user WHERE kei = ?";
	
	public static final String GET_USER_INFO_BY_NOT_NAME_AND_NOT_ADMIN = "SELECT id, name  FROM user WHERE name != ? AND is_admin = 0";
	
	public static final String GET_ALL_QUESTIONS = "SELECT *  FROM question";
	
	public static final String GET_WHAT_DAY_IS_TODAY = "SELECT DATE_FORMAT(NOW(),'%w') AS day";
	
	public static final String GET_LAST_EXAM_TIME_BY_USER_ID = "SELECT time FROM result JOIN user ON user.id = result.examer WHERE user.id = ? ORDER BY time limit 1";
}
