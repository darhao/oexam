package cc.darhao.oexam.model.bo;

import cc.darhao.oexam.main.Main;
import cc.darhao.oexam.model.User;

/**
 * 受评人所得成绩详情 <br>
 * <b>2019年1月21日</b>
 * 
 * @author 几米物联自动化部-洪达浩
 */
public class ExameeGradeDetail {

	private User examee;

	private short[] answers;
	
	
	public ExameeGradeDetail() {
		this.answers = new short[10];
		for (int i = 0; i < answers.length; i++) {
			if(Main.DEBUG) {
				answers[i] = 1;
			}else {
				answers[i] = -1;
			}
		}
	}

	public short[] getAnswers() {
		return answers;
	}

	public void setAnswers(short[] answers) {
		this.answers = answers;
	}

	public User getExamee() {
		return examee;
	}

	public void setExamee(User exameeName) {
		this.examee = exameeName;
	}

}
