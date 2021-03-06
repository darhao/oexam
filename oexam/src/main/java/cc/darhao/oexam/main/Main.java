package cc.darhao.oexam.main;

import cc.darhao.dautils.api.ResourcesUtil;
import cc.darhao.oexam.dao.DBManager;
import cc.darhao.oexam.util.ThreadBridge;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * <br>
 * <b>2018年1月15日</b>
 * @author 沫熊工作室 <a href="http://www.darhao.cc">www.darhao.cc</a>
 */
public class Main extends Application{

	private static final String VESION = "V0.1.4";
	
	private static final String NAME = "Oexam";
	
	//是否处于调试模式
	public static final boolean DEBUG = false;

	
	@Override
	public void start(Stage primaryStage) throws Exception {
        //显示主界面
		FXMLLoader loader = new FXMLLoader(ResourcesUtil.getResourceURL("fxml/app.fxml"));
		Parent root = loader.load();
        primaryStage.setTitle(NAME +" "+ VESION);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
	}
	
	
	/**
	 * 程序入口
	 * @param args
	 */
	public static void main(String[] args) {
		Thread.setDefaultUncaughtExceptionHandler(new ThreadBridge());
		DBManager.start();
		launch(args);
	}
	
}
