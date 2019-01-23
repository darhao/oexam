package cc.darhao.oexam.model.bo;

import java.util.List;

import cc.darhao.oexam.model.User;

/**
 * 登录动作返回值
 * <br>
 * <b>2019年1月23日</b>
 * @author 几米物联自动化部-洪达浩
 */
public class LoginResult {
	
	public enum LoginResultType{
		SUCCEED,
		DAY_ERROR,
		CYCLE_ERROR,
		KEY_ERROR
	}
	
	private LoginResultType result;

	private User examer;
	
	private List<User> examee;

	
	public User getExamer() {
		return examer;
	}

	public void setExamer(User examer) {
		this.examer = examer;
	}

	public List<User> getExamee() {
		return examee;
	}

	public void setExamee(List<User> examee) {
		this.examee = examee;
	}

	public LoginResultType getResult() {
		return result;
	}

	public void setResult(LoginResultType result) {
		this.result = result;
	}
	
	
}
