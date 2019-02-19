package com.kevin.demo.start;

import java.io.InputStream;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.junit.Test;

/**
 * @author Kevin
 */
public class StartTest {
	/** 
	 * 1.部署流程定义
	 */
	@Test
	public void deploymentProcessDefinition_inputStream() throws Exception{
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//获取上传文件输入流程 
		InputStream inputStreamBpmn = this.getClass().getResourceAsStream("start.bpmn");
		InputStream inputStreamPng = this.getClass().getResourceAsStream("start.png");
		//获取仓库服务，从类路径下完成部署
		Deployment deployment = processEngine.getRepositoryService() //与流程定义和部署对象相关的Service
												.createDeployment()	//创建一个部署对象
												.name("开始流程") //添加部署名称
												.addInputStream("start.bpmn", inputStreamBpmn)
												.addInputStream("start.png", inputStreamPng)
												.deploy();//完成部署
		System.out.println("部署ID:" + deployment.getId());	//4701
		System.out.println("部署名称:" + deployment.getName());//开始流程
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
		String processDefinitionKey = "start";
		ProcessInstance pi = processEngine.getRuntimeService() //与正在执行的流程实例和执行对象相关的Service
							.startProcessInstanceByKey(processDefinitionKey);//使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中的id的属性值
		System.out.println("流程实例ID:" + pi.getId());//流程实例ID:4801
		System.out.println("流程定义ID:"+ pi.getProcessDefinitionId());	//流程定义ID:start:1:4704
		String processInstanceId = pi.getId();
		//当流程已经结束后，流程实例被删除，运行时服务对象不能查询
		ProcessInstance rpi = processEngine.getRuntimeService()
				.createProcessInstanceQuery()
				.processInstanceId(processInstanceId)
				.singleResult();
		//说明流程实例结束了
		if(rpi == null) {
			//可以使用历史的记录查询
			HistoricProcessInstance historicProcessInstance = processEngine.getHistoryService()
																.createHistoricProcessInstanceQuery()
																.processInstanceId(processInstanceId)
																.singleResult();
			System.out.println("流程实例ID:" + historicProcessInstance.getId()); //流程实例ID:4801
			//使用断言检测结果，判断流程的执行结果是否和自己想象的一样
			System.out.println("流程正常执行!");
		}
	}
	
}
