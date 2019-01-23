package cc.darhao.oexam.util;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Method;

import cc.darhao.oexam.main.Main;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * 线程桥——用于JavaFx的UI线程与业务线程之间的异步通信（线程桥<u><b>不保证</b></u>业务层线程安全）
 * <br>
 * <b>2019年1月23日</b>
 * @author 几米物联自动化部-洪达浩
 */
public class ThreadBridge implements UncaughtExceptionHandler{
	

	public interface Response<T> {
		public void onReturn(T result);
	}
	
	
	/**
	 * 异步调用子线程处理业务逻辑，如果抛出RuntimeException会弹出错误窗口并显示错误栈
	 * @param requestServiceClass 被请求的业务层类
	 * @param requestMethodName 被请求的方法名（业务层方法必须为静态，控制器层传入的参数的类型必须和业务层方法签名的完全一致，即使存在继承关系也不行）
	 * @param response 回调对象
	 * @param requestParameters 请求参数列表（如果没有参数，则为null）
	 */
	public static <T> void call(Class<?> requestServiceClass, String requestMethodName, Response<T> response, Object... requestParameters) {
		new Thread(()-> {
			//匹配业务层方法签名
			Class[] requestParametersClasses = null;
			if(requestParameters != null) {
				requestParametersClasses = new Class[requestParameters.length];
				for (int i = 0 ; i < requestParametersClasses.length; i++) {
					requestParametersClasses[i] = requestParameters[i].getClass();
				}
			}
			try {
				Method method = requestServiceClass.getMethod(requestMethodName, requestParametersClasses);
				Object result = method.invoke(null, requestParameters);
				//调用UI线程
				Platform.runLater(()->{
					response.onReturn((T) result);
				});
			} catch (ReflectiveOperationException | SecurityException | IllegalArgumentException e) {
				e.printStackTrace();
			}
		}).start();
	}
	
	
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		if(Main.DEBUG) {
			e.printStackTrace();
		}else {
			Platform.runLater(()->{
				new Alert(AlertType.ERROR, StackExceptionGetter.getStackTrace(e)).showAndWait();
				System.exit(0);
			});
		}
	}
	

}
