package com.kevin.demo.receiveTask;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

/**
 * ReceiveTask任务，机器自动完成的任务
 * 只会在act_ru_execution表中产生一条数据
 * @author Kevin
 */
public class ReceiveTaskTest {
	
	@Test
	public void testReceiveTaskExecution() throws Exception{
		/**1.发布流程*/
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//获取上传文件输入流程 
		InputStream inputStreamBpmn = this.getClass().getResourceAsStream("receiveTask.bpmn");
		InputStream inputStreamPng = this.getClass().getResourceAsStream("receiveTask.png");
		//获取仓库服务，从类路径下完成部署
		Deployment deployment = processEngine.getRepositoryService() //与流程定义和部署对象相关的Service
												.createDeployment()	//创建一个部署对象
												.name("接收活动任务") //添加部署名称
												.addInputStream("receiveTask.bpmn", inputStreamBpmn)
												.addInputStream("receiveTask.png", inputStreamPng)
												.deploy();//完成部署
		System.out.println("部署ID:" + deployment.getId());	//5001
		System.out.println("部署名称:" + deployment.getName());//接收活动任务
		/**2.启动流程*/
		//使用流程定义的key启动流程实例，默认会按照最新版本启动流程实例
		String processDefinitionKey = "receiveTask";
		ProcessInstance pi = processEngine.getRuntimeService() //与正在执行的流程实例和执行对象相关的Service
							.startProcessInstanceByKey(processDefinitionKey);//使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中的id的属性值
		System.out.println("流程实例ID:" + pi.getId());//流程实例ID:5005
		System.out.println("流程定义ID:"+ pi.getProcessDefinitionId());	//流程定义ID:receiveTask:2:5004
		/**3.查询是否有一个执行对象在描述"汇总当前日销售额"*/
		String processInstanceId = pi.getId();
		Execution execution1 = processEngine.getRuntimeService()
											.createExecutionQuery() //创建执行对象的查询
											.processInstanceId(processInstanceId)//使用流程实例ID查询
											.activityId("receivetask1")//当前活动的ID，对应receiveTask.bpmn文件中的活动节点ID的属性值
											.singleResult();
		/**4.使用流程变量设置当日销售额，用来传递业务参数*/
		Map<String, Object> processVariables = new HashMap<String, Object>();
		processVariables.put("当日销售额", 10000);
		/**5.流程向后执行一步:往后推移execution，使用signal给流程引擎信号，告诉他当前任务已经完成了，可以往后执行*/
		String executionId1 = execution1.getId();
		processEngine.getRuntimeService()
					 .signal(executionId1, processVariables);
		/**6.判断当前流程是否在"给总经理发短信"节点*/
		Execution execution2 = processEngine.getRuntimeService()
											.createExecutionQuery()
											.processInstanceId(processInstanceId)
											.activityId("receivetask2")
											.singleResult();
		/**7.获取流程变量*/
		Integer money = (Integer) processEngine.getRuntimeService()
											   .getVariable(executionId1, "当日销售额");
		System.out.println("总经理今天赚了:" + money);
		/**8.向后执行一步:任务完成，往后推移"给总经理发短信"任务*/
		String executionId2 = execution2.getId();
		processEngine.getRuntimeService()
						.signal(executionId2);
		/**9.查询流程状态*/
		pi = processEngine.getRuntimeService()
						  .createProcessInstanceQuery()
						  .processInstanceId(processInstanceId)
						  .singleResult();
		if(pi == null) {
			System.out.println("流程正常执行!!!,已经结束了");
		}
	}
	
}
