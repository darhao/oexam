package cc.darhao.oexam.dao;

import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;

import cc.darhao.oexam.model.Question;
import cc.darhao.oexam.model.Result;
import cc.darhao.oexam.model.User;

public class DBManager {

	public static final String DB_USER = "oexam";
	
	public static final String DB_PASSWORD = "bobobo";
	
	
	public static void start() {
		PropKit.use("properties.ini");
		DruidPlugin dp = new DruidPlugin(PropKit.get("db_url"), DB_USER, DB_PASSWORD);
	    ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);
	    arp.addMapping("user", User.class);
	    arp.addMapping("result", Result.class);
	    arp.addMapping("question", Question.class);
	    dp.start();
	    arp.start();
	}
	
}
