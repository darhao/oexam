package cc.darhao.oexam.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import cc.darhao.oexam.dao.sql.MainSQL;
import cc.darhao.oexam.main.Main;
import cc.darhao.oexam.model.Question;
import cc.darhao.oexam.model.Result;
import cc.darhao.oexam.model.User;
import cc.darhao.oexam.model.bo.ExameeGradeDetail;
import cc.darhao.oexam.model.bo.LoginResult;
import cc.darhao.oexam.model.bo.LoginResult.LoginResultType;

/**
 * 主业务层
 * <br>
 * <b>2019年1月23日</b>
 * @author 几米物联自动化部-洪达浩
 */
public class MainService {
	
	/**
	 * @param key 登录key
	 * @return 成功返回考核人和被考核人列表，失败返回失败标志
	 */
	public static LoginResult login(String key) {
		//考核间隔周期
		final long EXAM_CYCLE = Main.DEBUG ? 0 : (5 * 24 * 60 * 60 * 1000);
		//要求的考核日
		final int REQURIED_EXAM_DAY_1 = Main.DEBUG ? new Date().getDay() : 5 , REQURIED_EXAM_DAY_2 = Main.DEBUG ? new Date().getDay() : 6;
		
		//校验key
		LoginResult result = new LoginResult();
		User examer = User.dao.findFirst(MainSQL.GET_USER_INFO_BY_KEI, key);
		if(examer == null) {
			result.setResult(LoginResultType.KEY_ERROR);
			return result;
		}
		
		//校验考核日
		int day = Integer.parseInt(Db.findFirst(MainSQL.GET_WHAT_DAY_IS_TODAY).getStr("day"));
		if(day != REQURIED_EXAM_DAY_1 && day != REQURIED_EXAM_DAY_2) {
			result.setResult(LoginResultType.DAY_ERROR);
			return result;
		}
		
		//校验考核周期
		Record lastExamDateRecord = Db.findFirst(MainSQL.GET_LAST_EXAM_TIME_BY_USER_ID, examer.getId());
		if(lastExamDateRecord != null && new Date().getTime() - lastExamDateRecord.getDate("time").getTime() <= EXAM_CYCLE) {
			result.setResult(LoginResultType.CYCLE_ERROR);
			return result;
		}
		
		//获取受评人列表
		List<User> examees = User.dao.find(MainSQL.GET_USER_INFO_BY_NOT_NAME_AND_NOT_ADMIN, examer);
		result.setExamer(examer);
		result.setExamee(examees);
		result.setResult(LoginResultType.SUCCEED);
		return result;
	}
	
	
	/**
	 * 返回所有考核问题（10个）
	 * @return
	 */
	public static List<Question> listQuestions(){
		return Question.dao.find(MainSQL.GET_ALL_QUESTIONS);
	}
	
	
	/**
	 * 提交考核
	 * @param exameeGradeDetails 考核细节
	 * @param examer 考核者
	 * @return 成功；失败（没填完）
	 */
	public static boolean submitExam(ArrayList<ExameeGradeDetail> exameeGradeDetails, User examer) {
		//检查有没有填完
		for (ExameeGradeDetail exameeGradeDetail : exameeGradeDetails) {
			short[] answers = exameeGradeDetail.getAnswers();
			for (short answer : answers) {
				if(answer == -1) {
					return false;
				}
			}
		}
		
		//计算平均值并插入数据库
		final int QUESTION_TYPE_SPLIT = 5;
		for (ExameeGradeDetail exameeGradeDetail : exameeGradeDetails) {
			int standardGradeSum = 0, teamGradeSum = 0;
			short[] answers = exameeGradeDetail.getAnswers();
			for (int i = 0 ; i < answers.length ; i++) {
				if(i < QUESTION_TYPE_SPLIT) {
					standardGradeSum += answers[i];
				}else {
					teamGradeSum += answers[i];
				}
			}
			Result result = new Result();
			result.setExamer(examer.getId());
			result.setExamee(exameeGradeDetail.getExamee().getId());
			result.setStandardGrade(standardGradeSum);
			result.setTeamGrade(teamGradeSum);
			result.save();
		}
		return true;
	}

	
}
