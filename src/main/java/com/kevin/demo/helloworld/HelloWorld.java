package com.kevin.demo.helloworld;

import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

public class HelloWorld {
	
	/** 1.发布流程实例 */
	@Test
	public void deploymentProcessDefinition() {
		//获取流程引擎
		//调用ProcessEngines的getDefaultProceeEngine方法时会自动加载classpath下名为activiti.cfg.xml文件。
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//管理流程定义
		Deployment deployment = processEngine.getRepositoryService() //与流程定义和部署对象相关的Service
												.createDeployment()	//创建一个部署对象
												.name("helloworld 入门程序") //添加部署名称
												.addClasspathResource("diagrams/helloworld.bpmn")//从classpath的资源中加载，一次只能加载一个文件
												.addClasspathResource("diagrams/helloworld.png")//从classpath的资源中加载，一次只能加载一个文件
												.deploy();//完成部署
		System.out.println("部署ID:" + deployment.getId());	//1
		System.out.println("部署名称:" + deployment.getName());//helloworld 入门程序
	}
	
	/** 2.启动流程实例 */
	@Test
	public void startProcess () throws Exception{
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//启动流程
		//使用流程定义的key启动流程实例，默认会按照最新版本启动流程实例
		String processDefinitionKey = "helloworld";
		ProcessInstance pi = processEngine.getRuntimeService() //与正在执行的流程实例和执行对象相关的Service
							.startProcessInstanceByKey(processDefinitionKey);//使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中的id的属性值
		System.out.println("流程实例ID:" + pi.getId());//流程实例ID 101
		System.out.println("流程定义ID:"+ pi.getProcessDefinitionId());	//流程定义ID  helloworld:1:4
	}
	
	/** 3.查看我的个人任务 */
	@Test
	public void findMyPersonalTask() {
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//指定任务办理者 
		String assignee = "王五";
		//查询任务列表
		List<Task> tasks = processEngine.getTaskService()
								.createTaskQuery() //创建任务查询对象
								.taskAssignee(assignee) //指定个人任务办理人
								.list();
		if(tasks != null && tasks.size() > 0) {
			//遍历结合查看内容
			for(Task task : tasks) {
				System.out.println("任务ID:" + task.getId());
				System.out.println("任务名称:" + task.getName());
				System.out.println("任务创建时间:" + task.getCreateTime());
				System.out.println("任务办理人:" + task.getAssignee());
				System.out.println("流程实例ID:" + task.getProcessInstanceId());
				System.out.println("执行对象ID:" + task.getExecutionId());
				System.out.println("流程定义ID:" + task.getProcessDefinitionId());
				System.out.println("******************************************************");
			}	
		}
	}
	/** 4.完成我的个人任务 */
	@Test
	public void completeMyPersonalTask() throws Exception{
		String taskId = "302";
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//完成任务
		processEngine.getTaskService()
						.complete(taskId);
		System.out.println("完成任务: 任务ID:" + taskId);
	}
}
