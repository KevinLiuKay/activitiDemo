package com.kevin.demo.processVariables;

import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import com.kevin.demo.model.Person;

/**
 * @author Kevin
 */
public class ProcessVariablesTest {
	/** 
	 * 1.部署流程定义
	 */
	@Test
	public void deploymentProcessDefinition_zip() throws Exception{
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//获取上传文件输入流程
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("diagrams/processVariables.zip");
		ZipInputStream zipInputStream = new ZipInputStream(in);
		//获取仓库服务，从类路径下完成部署
		Deployment deployment = processEngine.getRepositoryService() //与流程定义和部署对象相关的Service
												.createDeployment()	//创建一个部署对象
												.name("审批流程（流程变量）") //添加部署名称
												.addZipInputStream(zipInputStream)//从zip的资源中加载
												.deploy();//完成部署
		System.out.println("部署ID:" + deployment.getId());	//2001
		System.out.println("部署名称:" + deployment.getName());//审批流程（流程变量）
	}
	
	/** 
	 * 2.启动流程实例
	 * 如果是单例流程（没有分支和聚合），那么流程实例ID和执行对象ID是相同的
	 * 一个流程只有一个流程实例，执行对象可以存在多个（存在分支和聚合）。
	 */
	@Test
	public void startProcess () throws Exception{
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//启动流程
		//使用流程定义的key启动流程实例，默认会按照最新版本启动流程实例
		String processDefinitionKey = "processVariables";
		ProcessInstance pi = processEngine.getRuntimeService() //与正在执行的流程实例和执行对象相关的Service
							.startProcessInstanceByKey(processDefinitionKey);//使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中的id的属性值
		System.out.println("流程实例ID:" + pi.getId());//流程实例ID:2101
		System.out.println("流程定义ID:"+ pi.getProcessDefinitionId());	//流程定义ID:   processVariables:2:2004
	}
	
	/**
	 * 设置流程变量
	 */
	@Test
	public void setVariables() {
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//获取执行的Service
		TaskService taskService = processEngine.getTaskService();
		//指定任务办理人
		String assignee = "张晓晓";
		//流程实例ID
		String processInstanceId = "2101";
		Task task = taskService.createTaskQuery()	//创建任务查询
						.taskAssignee(assignee)//指定任务办理人
						.processInstanceId(processInstanceId) //指定流程实例ID
						.singleResult();	//返回唯一结果集
		/**
		 * 一、变量中存放基本数据类型
		 */
		String taskId = task.getId();
		/**
		 * setVariable：设置流程变量的时候，流程变量名称相同的时候，后一次的值替换前一次的值，而且可以看到TASK_ID的字段不会存放任务ID的值
		 * setVariableLocal：
		 * 1：设置流程变量的时候，针对当前活动的节点设置流程变量，如果一个流程中存在2个活动节点，对每个活动节点都设置流程变量，即使流程变量的名称相同，后一次的版本的值也不会替换前一次版本的值，它会使用不同的任务ID作为标识，存放2个流程变量值，而且可以看到TASK_ID的字段会存放任务ID的值
	     * 例如act_hi_varinst 表的数据：不同的任务节点，即使流程变量名称相同，存放的值也是不同的。
	     * 2：还有，使用setVariableLocal说明流程变量绑定了当前的任务，当流程继续执行时，下个任务获取不到这个流程变量（因为正在执行的流程变量中没有这个数据），所有查询正在执行的任务时不能查询到我们需要的数据，此时需要查询历史的流程变量。
		 */
//		taskService.setVariableLocal(taskId, "请假天数", 5);//使用流程变量的名称和流程变量的值，设置流程变量，一次只能设置一个值,setVariableLocal与任务ID绑定
//		taskService.setVariable(taskId,"请假日期",new Date());
//		taskService.setVariable(taskId,"请假原因", "回家探亲，一起吃个饭!");
//		System.out.println("流程变量设置成功!");
		/**
		 * 二、变量中存放javabean对象，
		 * 前提:让javabean对象实现implements java.io.Serializable
		 * 
		 */
		Person person = new Person();
		person.setId(001);
		person.setName("翠花");
		taskService.setVariable(taskId, "人员信息", person);
		System.out.println("流程变量设置成功!");
	}
	
	/**
	 * 获取流程变量
	 */
	@Test
	public void getVariables() {
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//获取执行的Service
		TaskService taskService = processEngine.getTaskService();
		//指定任务办理人
		String assignee = "张晓晓";
		//流程实例ID
		String processInstanceId = "2101";
		Task task = taskService.createTaskQuery() //创建任务查询
						.taskAssignee(assignee) //指定办理人
						.processInstanceId(processInstanceId) //制定流程实例ID
						.singleResult();
		/**一:变量中存放基本数据类型 */
//		String taskId = task.getId();
//		Integer integerValue = (Integer)taskService.getVariableLocal(taskId, "请假天数");
//		Date dateValue = (Date)taskService.getVariable(taskId, "请假日期");
//		String stringValue = (String) taskService.getVariable(taskId, "请假原因");
//		System.out.println("请假天数:" + integerValue +" 请假日期:" + dateValue + " 请假原因:"+ stringValue);
		/**二:变量中存放JavaBean对象:前提:让javabean对象实现implements java.io.Serializable */
		/**
		 * 获取流程实例变量时注意:流程变量如果是JavaBean对象，除了要求实现Serializable之外，
		 * 同时要求流程变量对象的属性不能发生变化，否则抛出异常
		 * 解决法案:在设置流程变量的时候，在JavaBean的对象中使用
		 * private static final long serialVersionUID = -2485554908660237381L;
		 * 固定序列化ID
		 */
		String taskId = task.getId();
		Person p = (Person) taskService.getVariable(taskId, "人员信息");
		System.out.println("人员ID:" + p.getId() + "   人员名称:" + p.getName());
	}
	
	/**
	 * 模拟设置和获取流程变量的场景
	 */
	public void setAndGetVariables() {
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		/**
		 * 方式一:设置流程变量
		 * 与流程实例相关，执行对象（正在执行）
		 */
		RuntimeService runtimeService = processEngine.getRuntimeService();
		/**设置流程变量*/
		//runtimeService.setVariable(executionId, variableName, value);//表示使用执行对象ID，流程变量名称，设置流程变量的值（一次只能设置一个值）
		/**
		 * 表示使用执行对象ID，和Map集合设置流程变量；
		 * Map集合的key就是流程变量的名称
		 * Map集合的value就是流程变量的值
		 * （一次可以设置多个值）
		 */
		//runtimeService.setVariables(executionId, variables);
		/**
		 * 方式二:设置流程变量
		 * 与任务相关（正在执行）
		 */
		TaskService taskService = processEngine.getTaskService();
		//taskService.setVariable(taskId, variableName, value);//表示使用任务ID，流程变量名称，设置流程变量的值（一次只能设置一个值）
		/**
		 * 表示使用任务ID，和Map集合设置流程变量；
		 * Map集合的key就是流程变量的名称
		 * Map集合的value就是流程变量的值
		 * （一次可以设置多个值）
		 */
		//taskService.setVariables(taskId, variables);
		//runtimeService.startProcessInstanceByKey(processDefinitionKey, variables) //启动流程实例的同时，可以设置流程变量，用Map集合。
		//taskService.complete(taskId, variables); //完成任务的时候，可以设置流程变量，通过Map集合。
		/**获取流程变量*/
		//runtimeService.getVariable(executionId, variableName) //使用执行对象ID和流程变量名称，获取流程变量的值
		//runtimeService.getVariables(executionId) //使用执行对象ID，获取所有的流程变量，将流程变量放置到Map集合中，Map集合的key就是流程变量的名称，Map集合的value就是流程变量的值
		//runtimeService.getVariables(executionId, variableNames) //使用执行对象ID,获取流程变量的值，通过设置流程变量的名称存放到集合中，获取指定流程变量名称的流程变量的值得集合
		
		//runtimeService.getVariable(taskId, variableName) //使用任务ID和流程变量名称，获取流程变量的值
		//runtimeService.getVariables(taskId) //使用任务ID，获取所有的流程变量，将流程变量放置到Map集合中，Map集合的key就是流程变量的名称，Map集合的value就是流程变量的值
		//runtimeService.getVariables(taskId, variableNames) //使用任务ID,获取流程变量的值，通过设置流程变量的名称存放到集合中，获取指定流程变量名称的流程变量的值得集合
	}
	
	/** 
	 * 完成我的个人任务 
	 */
	@Test
	public void completeMyPersonalTask() throws Exception{
		String taskId = "2302";
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//完成任务
		processEngine.getTaskService()
						.complete(taskId);
		System.out.println("完成任务: 任务ID:" + taskId);
	}
	
	/**
	 * 查询历史的流程变量（使用流程变量的名称）
	 */
	@Test
	public void getHistoricVariables() {
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		List<HistoricVariableInstance> list = processEngine.getHistoryService()
												.createHistoricVariableInstanceQuery()
												.variableName("请假天数")
												.list();
		if(list != null && list.size() > 0) {
			for(HistoricVariableInstance historicVariableInstance : list) {
				System.out.println(historicVariableInstance.getVariableName() +"------"+ historicVariableInstance.getValue());
			}
		}
	}
}
