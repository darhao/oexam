package cc.darhao.oexam.main;

import cc.darhao.dautils.api.ResourcesUtil;
import cc.darhao.oexam.dao.DBManager;
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

	private static final String VESION = "V0.1";
	
	private static final String NAME = "Oexam";

	
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
		DBManager.start();
		launch(args);
	}
	
}
