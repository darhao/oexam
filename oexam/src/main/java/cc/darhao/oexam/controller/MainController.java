package cc.darhao.oexam.controller;

import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import cc.darhao.oexam.dao.SQL;
import cc.darhao.oexam.model.Question;
import cc.darhao.oexam.model.Result;
import cc.darhao.oexam.model.User;
import cc.darhao.oexam.model.bo.ExameeGradeDetail;
import cc.darhao.oexam.util.StackExceptionGetter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

/**
 * <br>
 * <b>2018年1月15日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class MainController implements Initializable, UncaughtExceptionHandler {

	//是否处于调试模式
	public static final boolean DEBUG = false;
	
	@FXML
	private Group examGp;
	@FXML
	private TextField keyTf;
	@FXML
	private Button loginBt;
	@FXML
	private Button lastPageBt;
	@FXML
	private Button nextPageBt;
	@FXML
	private Button submitBt;
	@FXML
	private Label examerLb;
	@FXML
	private Label exameeLb;
	@FXML
	private Label pageLb;
	@FXML
	private Label q1Lb, q2Lb, q3Lb, q4Lb, q5Lb, q6Lb, q7Lb, q8Lb, q9Lb, q10Lb;
	@FXML
	private RadioButton q1aRb, q2aRb, q3aRb, q4aRb, q5aRb, q6aRb, q7aRb, q8aRb, q9aRb ,q10aRb;
	@FXML
	private RadioButton q1bRb, q2bRb, q3bRb, q4bRb, q5bRb, q6bRb, q7bRb, q8bRb, q9bRb ,q10bRb;
	@FXML
	private RadioButton q1cRb, q2cRb, q3cRb, q4cRb, q5cRb, q6cRb, q7cRb, q8cRb, q9cRb ,q10cRb;
	
	
	private List<ExameeGradeDetail> exameeGradeDetails = new ArrayList<>();
	
	private User examer;
	
	private User currentExamee;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Thread.setDefaultUncaughtExceptionHandler(this);
		initQuestionList();
		
		if(DEBUG) {
			keyTf.setText("6224F86DD7B07E8A23D246BCDDBBC41F");
			for (Node node : examGp.getChildren()) {//清空单选框
				if(node.getClass().equals(RadioButton.class)) {
					RadioButton rb = (RadioButton) node;
					rb.setSelected(true);
				}
			}
		}
	}
	
	
	/**
	 * 初始化问题集
	 */
	private void initQuestionList() {
		new Thread(()-> {
			List<Question> questions = Question.dao.find(SQL.GET_ALL_QUESTIONS);
			Platform.runLater(()->{
				q1Lb.setText(questions.get(0).getDesc());
				q2Lb.setText(questions.get(1).getDesc());
				q3Lb.setText(questions.get(2).getDesc());
				q4Lb.setText(questions.get(3).getDesc());
				q5Lb.setText(questions.get(4).getDesc());
				q6Lb.setText(questions.get(5).getDesc());
				q7Lb.setText(questions.get(6).getDesc());
				q8Lb.setText(questions.get(7).getDesc());
				q9Lb.setText(questions.get(8).getDesc());
				q10Lb.setText(questions.get(9).getDesc());
			});
		}).start();
	}


	/**
	 * 验证key，封锁登录相关控件
	 * 如果正确，验证上一次考核时间
	 * 		如果小于6日，则提示还没到考核时间
	 * 		如果大于等于6日，则显示登录者，初始化受评人分数详情列表，解锁、加载评价界面，显示第一个受评者，显示分页
	 * 如果失败，解锁登录相关控件，并弹出提示登录错误
	 */
	public void onLoginBtClick() {
		keyTf.setDisable(true);
		loginBt.setDisable(true);
		new Thread(()->{
			String key = keyTf.getText();
			examer = User.dao.findFirst(SQL.GET_USER_INFO_BY_KEI, key);
			if(examer != null) {
				Record lastExamDateRecord = Db.findFirst(SQL.GET_LAST_EXAM_TIME_BY_USER_ID, examer.getId());
				if(lastExamDateRecord == null || new Date().getTime() - lastExamDateRecord.getDate("time").getTime() > 6 * 24 * 60 * 60 * 1000) {
					initExameeGradeDetail(examer.getName());
					Platform.runLater(()->{
						examerLb.setText(examer.getName());
						examGp.setDisable(false);
						exameeLb.setText(exameeGradeDetails.get(0).getExamee().getName());
						pageLb.setText("1 / " + exameeGradeDetails.size());
					});
				}else {
					Platform.runLater(()->{
						new Alert(AlertType.ERROR, "每次考核时间必须间隔6日").showAndWait();
						keyTf.setDisable(false);
						loginBt.setDisable(false);
					});
				}
			}else {
				Platform.runLater(()->{
					new Alert(AlertType.ERROR, "不存在该key").showAndWait();
					keyTf.setDisable(false);
					loginBt.setDisable(false);
				});
			}
		}).start();
		
		
	}
	

	/**
	 * 初始化受评人分数详情列表
	 */
	private void initExameeGradeDetail(String examer) {
		List<User> examees = User.dao.find(SQL.GET_USER_INFO_BY_NOT_NAME_AND_NOT_ADMIN, examer);
		currentExamee = examees.get(0);
		for (User examee : examees) {
			ExameeGradeDetail exameeGradeDetail = new ExameeGradeDetail();
			exameeGradeDetail.setExamee(examee);
			exameeGradeDetails.add(exameeGradeDetail);
		}
	}
	
	
	/**
	 * 如果不是第一页，则更新ExameeGradeDetail，翻上一页，更新评价界面
	 */
	public void onLastPageBtClick() {
		int currentPage = getCurrentPage();
		if(currentPage != 1) {
			exameeGradeDetails.set((currentPage - 1), dumpExameeGradeDetail());
			refreshExamGp(exameeGradeDetails.get((currentPage - 1) - 1));
			setCurrentPage(currentPage - 1);
		}
	}
	
	
	/**
	 * 如果不是最后一页，则更新ExameeGradeDetail，翻下一页，更新评价界面
	 */
	public void onNextPageBtClick() {
		int currentPage = getCurrentPage();
		if(currentPage != exameeGradeDetails.size()) {
			exameeGradeDetails.set((currentPage - 1), dumpExameeGradeDetail());
			refreshExamGp(exameeGradeDetails.get((currentPage - 1) + 1));
			setCurrentPage(currentPage + 1);
		}
	}


	private void setCurrentPage(int pageNo) {
		pageLb.setText(pageNo + " / " + exameeGradeDetails.size());
	}


	private int getCurrentPage() {
		String pageLbString = pageLb.getText();
		String[] pageInfo = pageLbString.split("/");
		int currentPage = Integer.parseInt(pageInfo[0].trim());
		return currentPage;
	}
	
	
	private ExameeGradeDetail dumpExameeGradeDetail() {
		ExameeGradeDetail exameeGradeDetail = new ExameeGradeDetail();//清空缓存
		exameeGradeDetail.setExamee(currentExamee);
		short[] a = exameeGradeDetail.getAnswers();
		if(q1aRb.isSelected()){a[0] = 2;}else if(q1bRb.isSelected()){a[0] = 1;}else if(q1cRb.isSelected()){a[0] = 0;}
		if(q2aRb.isSelected()){a[1] = 2;}else if(q2bRb.isSelected()){a[1] = 1;}else if(q2cRb.isSelected()){a[1] = 0;}
		if(q3aRb.isSelected()){a[2] = 2;}else if(q3bRb.isSelected()){a[2] = 1;}else if(q3cRb.isSelected()){a[2] = 0;}
		if(q4aRb.isSelected()){a[3] = 2;}else if(q4bRb.isSelected()){a[3] = 1;}else if(q4cRb.isSelected()){a[3] = 0;}
		if(q5aRb.isSelected()){a[4] = 2;}else if(q5bRb.isSelected()){a[4] = 1;}else if(q5cRb.isSelected()){a[4] = 0;}
		if(q6aRb.isSelected()){a[5] = 2;}else if(q6bRb.isSelected()){a[5] = 1;}else if(q6cRb.isSelected()){a[5] = 0;}
		if(q7aRb.isSelected()){a[6] = 2;}else if(q7bRb.isSelected()){a[6] = 1;}else if(q7cRb.isSelected()){a[6] = 0;}
		if(q8aRb.isSelected()){a[7] = 2;}else if(q8bRb.isSelected()){a[7] = 1;}else if(q8cRb.isSelected()){a[7] = 0;}
		if(q9aRb.isSelected()){a[8] = 2;}else if(q9bRb.isSelected()){a[8] = 1;}else if(q9cRb.isSelected()){a[8] = 0;}
		if(q10aRb.isSelected()){a[9] = 2;}else if(q10bRb.isSelected()){a[9] = 1;}else if(q10cRb.isSelected()){a[9] = 0;}
		return exameeGradeDetail;
	}
	
	
	private void refreshExamGp(ExameeGradeDetail exameeGradeDetail) {
		currentExamee = exameeGradeDetail.getExamee();//刷新当前受评人
		exameeLb.setText(currentExamee.getName());//刷新界面受评人提示
		for (Node node : examGp.getChildren()) {//清空单选框
			if(node.getClass().equals(RadioButton.class)) {
				RadioButton rb = (RadioButton) node;
				rb.setSelected(false);
			}
		}
		short[] a = exameeGradeDetail.getAnswers();//刷新单选框
		if(a[0] == 2){q1aRb.setSelected(true);}else if(a[0] == 1){q1bRb.setSelected(true);}else if(a[0] == 0){q1cRb.setSelected(true);}
		if(a[1] == 2){q2aRb.setSelected(true);}else if(a[1] == 1){q2bRb.setSelected(true);}else if(a[1] == 0){q2cRb.setSelected(true);};
		if(a[2] == 2){q3aRb.setSelected(true);}else if(a[2] == 1){q3bRb.setSelected(true);}else if(a[2] == 0){q3cRb.setSelected(true);};
		if(a[3] == 2){q4aRb.setSelected(true);}else if(a[3] == 1){q4bRb.setSelected(true);}else if(a[3] == 0){q4cRb.setSelected(true);};
		if(a[4] == 2){q5aRb.setSelected(true);}else if(a[4] == 1){q5bRb.setSelected(true);}else if(a[4] == 0){q5cRb.setSelected(true);};
		if(a[5] == 2){q6aRb.setSelected(true);}else if(a[5] == 1){q6bRb.setSelected(true);}else if(a[5] == 0){q6cRb.setSelected(true);};
		if(a[6] == 2){q7aRb.setSelected(true);}else if(a[6] == 1){q7bRb.setSelected(true);}else if(a[6] == 0){q7cRb.setSelected(true);};
		if(a[7] == 2){q8aRb.setSelected(true);}else if(a[7] == 1){q8bRb.setSelected(true);}else if(a[7] == 0){q8cRb.setSelected(true);};
		if(a[8] == 2){q9aRb.setSelected(true);}else if(a[8] == 1){q9bRb.setSelected(true);}else if(a[8] == 0){q9cRb.setSelected(true);};
		if(a[9] == 2){q10aRb.setSelected(true);}else if(a[9] == 1){q10bRb.setSelected(true);}else if(a[9] == 0){q10cRb.setSelected(true);};
	}
	
	
	/**
	 * 如果不是周五和周六，则无法提交
	 * 如果没有填写完整，则无法提交
	 * 计算平均值并插入到数据库
	 */
	public void onSubmitBtClick() {
		submitBt.setDisable(true);
		new Thread(()->{
			String requriedExamDay1, requriedExamDay2;
			if(DEBUG) {
				requriedExamDay1 = requriedExamDay2 = new Date().getDay() +"";
			}else {
				requriedExamDay1 = "5";
				requriedExamDay2 = "6";
			}
			String day = Db.findFirst(SQL.GET_WHAT_DAY_IS_TODAY).getStr("day");
			if(!day.equals(requriedExamDay1) && !day.equals(requriedExamDay2)) {
				Platform.runLater(()->{
					new Alert(AlertType.ERROR, "今天不能进行考核，请在每周最后一个工作日进行考核").showAndWait();
					submitBt.setDisable(false);
				});
			}else {
				exameeGradeDetails.set((getCurrentPage() - 1), dumpExameeGradeDetail());
				for (ExameeGradeDetail exameeGradeDetail : exameeGradeDetails) {
					short[] answers = exameeGradeDetail.getAnswers();
					for (short answer : answers) {
						if(answer == -1) {
							Platform.runLater(()->{
								new Alert(AlertType.ERROR, "还有项目没填完").showAndWait();
								submitBt.setDisable(false);
							});
							return;
						}
					}
				}
				//计算平均值并插入数据库
				for (ExameeGradeDetail exameeGradeDetail : exameeGradeDetails) {
					int standardGradeSum = 0, teamGradeSum = 0;
					short[] answers = exameeGradeDetail.getAnswers();
					for (int i = 0 ; i < answers.length ; i++) {
						if(i < 5) {
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
				Platform.runLater(()->{
					new Alert(AlertType.INFORMATION, "考核完成，下一次考核在6天后解锁").showAndWait();
					System.exit(0);
				});
			}
			
		}).start();;
	}


	@Override
	public void uncaughtException(Thread t, Throwable e) {
		if(DEBUG) {
			e.printStackTrace();
		}else {
			Platform.runLater(()->{
				new Alert(AlertType.ERROR, StackExceptionGetter.getStackTrace(e)).showAndWait();
				System.exit(0);
			});
		}
	}
	
	
}
