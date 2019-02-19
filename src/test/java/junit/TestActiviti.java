package junit;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.junit.Test;

public class TestActiviti {
	/**使用代码创建工作流需要的23张表 */
	@Test
	public void createTable() {
		//1.创建Activiti配置对象实例
		ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
		//2.设置数据库连接信息
		//设置数据库驱动
		processEngineConfiguration.setJdbcDriver("com.mysql.jdbc.Driver");
		//设置数据库连接地址
		processEngineConfiguration.setJdbcUrl("jdbc:mysql://localhost:3306/activitiDemo?useUnicode=true&characterEncoding=utf-8&useSSL=false");
		//设置数据库连接用户名
		processEngineConfiguration.setJdbcUsername("root");
		//设置数据库连接密码
		processEngineConfiguration.setJdbcPassword("123456");
		//设置数据库建表策略
	    /** 
		 * public static final String DB_SCHEMA_UPDATE_FALSE = "false"; //不能自动创建表，需要表存在
		 * public static final String DB_SCHEMA_UPDATE_CREATE_DROP = "create-drop"; //先删除表再创建表
		 * public static final String DB_SCHEMA_UPDATE_TRUE = "true";// 如果表不存在，自动创建表
		 */
		processEngineConfiguration.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
		//3.使用配置对象创建流程引擎实例（检查数据库连接等环境是否正确）
		ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();
		System.out.println(processEngine);
	}
	/**使用配置文件创建工作流需要的23张表 */
	@Test
	public void createTable2() {
		ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
		System.out.println(processEngine);
	}
}
