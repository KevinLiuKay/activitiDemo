package com.kevin.demo.parallelGateWay;

import java.io.InputStream;
import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

/**
 * @author Kevin
 */
public class ParallelGateWayTest {
	/** 
	 * 1.部署流程定义
	 */
	@Test
	public void deploymentProcessDefinition_inputStream() throws Exception{
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//获取上传文件输入流程 
		InputStream inputStreamBpmn = this.getClass().getResourceAsStream("parallelGateWay.bpmn");
		InputStream inputStreamPng = this.getClass().getResourceAsStream("parallelGateWay.png");
		//获取仓库服务，从类路径下完成部署
		Deployment deployment = processEngine.getRepositoryService() //与流程定义和部署对象相关的Service
												.createDeployment()	//创建一个部署对象
												.name("并行网关") //添加部署名称
												.addInputStream("parallelGateWay.bpmn", inputStreamBpmn)
												.addInputStream("parallelGateWay.png", inputStreamPng)
												.deploy();//完成部署
		System.out.println("部署ID:" + deployment.getId());	//4101
		System.out.println("部署名称:" + deployment.getName());//并行网关
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
		String processDefinitionKey = "parallelGateWay";
		ProcessInstance pi = processEngine.getRuntimeService() //与正在执行的流程实例和执行对象相关的Service
							.startProcessInstanceByKey(processDefinitionKey);//使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中的id的属性值
		System.out.println("流程实例ID:" + pi.getId());//流程实例ID:4201
		System.out.println("流程定义ID:"+ pi.getProcessDefinitionId());	//流程定义ID:parallelGateWay:1:4104
	}
	
	/** 3.查看当前人的个人任务 */
	@Test
	public void queryPersonalTask() {
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//指定任务办理者 
		String assignee = "商家";
		//查询任务列表
		List<Task> tasks = processEngine.getTaskService()
								.createTaskQuery() //创建任务查询对象
								/**查询条件*/
								.taskAssignee(assignee) //指定个人任务查询，指定办理人
								//.taskCandidateUser(candidateUser) //组任务的办理人查询
								//.processDefinitionId(processDefinitionId)//使用流程定义ID查询
								//.processInstanceId(processInstanceId) //使用了流程实例ID查询
								//.executionId(executionId)//使用执行对象ID查询
								/**排序*/
								.orderByTaskCreateTime().asc()//使用任务创建时间的升序排列
								/**返回结果集*/
								//.singleResult() //返回唯一结果集
								//.count() //查询结果数量
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
	
	/** 
	 * 4.完成我的个人任务 
	 */
	@Test
	public void completePersonalTask() throws Exception{
		String taskId = "4303";
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//完成任务
		processEngine.getTaskService()
						.complete(taskId);
		System.out.println("完成任务: 任务ID:" + taskId);
	}
}
