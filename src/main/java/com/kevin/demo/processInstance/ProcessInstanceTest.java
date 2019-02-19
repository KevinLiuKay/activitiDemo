package com.kevin.demo.processInstance;

import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

/**
 * @author Kevin
 */
public class ProcessInstanceTest {
	/** 
	 * 1.部署流程定义
	 */
	@Test
	public void deploymentProcessDefinition_zip() throws Exception{
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//获取上传文件输入流程
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("diagrams/helloworld.zip");
		ZipInputStream zipInputStream = new ZipInputStream(in);
		//获取仓库服务，从类路径下完成部署
		Deployment deployment = processEngine.getRepositoryService() //与流程定义和部署对象相关的Service
												.createDeployment()	//创建一个部署对象
												.name("流程定义") //添加部署名称
												.addZipInputStream(zipInputStream)//从zip的资源中加载
												.deploy();//完成部署
		System.out.println("部署ID:" + deployment.getId());	//901
		System.out.println("部署名称:" + deployment.getName());//流程定义
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
		String processDefinitionKey = "helloworld";
		ProcessInstance pi = processEngine.getRuntimeService() //与正在执行的流程实例和执行对象相关的Service
							.startProcessInstanceByKey(processDefinitionKey);//使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中的id的属性值
		System.out.println("流程实例ID:" + pi.getId());//流程实例ID 1001
		System.out.println("流程定义ID:"+ pi.getProcessDefinitionId());	//流程定义ID  helloworld:1:904
	}
	
	/** 3.查看当前人的个人任务 */
	@Test
	public void findMyPersonalTask() {
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//指定任务办理者 
		String assignee = "王五";
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
	public void completeMyPersonalTask() throws Exception{
		String taskId = "1202";
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//完成任务
		processEngine.getTaskService()
						.complete(taskId);
		System.out.println("完成任务: 任务ID:" + taskId);
	}
	
	/**
	 * 5.查看流程状态（判断流程是正在执行，还是已经结束）
	 */
	@Test
	public void queryProcessState() throws Exception{
		String processInstanceId = "101";
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//通过流程实例ID查询流程实例
		ProcessInstance pi = processEngine.getRuntimeService()
								.createProcessInstanceQuery() //创建流程实例查询，查询正在执行的流程实例
								.processInstanceId(processInstanceId) //按照流程实例ID查询
								.singleResult(); //返回唯一结果集
		if(pi != null) {
			System.out.println("当前流程在" + pi.getActivityId());
		}else {
			System.out.println("流程已结束！");
		}
	}
	
	/**
	 * 查询历史任务
	 */
	@Test
	public void queryHistoryTask() throws Exception {
		//历史任务办理人
		String taskAssignee = "张三";
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//通过流程实例ID查询流程实例
		List<HistoricTaskInstance> list = processEngine.getHistoryService() //与历史数据相关的Service
											.createHistoricTaskInstanceQuery() //创建历史任务查询
											.taskAssignee(taskAssignee) //指定办理人查询历史任务
											.list();
		if(list != null && list.size() > 0) {
			for(HistoricTaskInstance historicTaskInstance : list) {
				System.out.println("任务ID:" + historicTaskInstance.getId());
				System.out.println("流程名称:" + historicTaskInstance.getName());
				System.out.println("流程实例ID:" + historicTaskInstance.getProcessInstanceId());
				System.out.println("任务办理人:" + historicTaskInstance.getAssignee());
				System.out.println("执行对象ID:" + historicTaskInstance.getExecutionId());
				System.out.println("流程开始时间:"+ historicTaskInstance.getStartTime() + " 流程结束时间:" + historicTaskInstance.getEndTime()+ " 流程持续时间:" +historicTaskInstance.getDurationInMillis());
			}
		}
	}
	
	/**
	 * 查询历史流程实例
	 */
	@Test
	public void queryHistoryProcessInstance() throws Exception{
		String processInstanceId = "1001";
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		HistoricProcessInstance historicProcessInstance = processEngine.getHistoryService()//与历史数据相关的Service
															.createHistoricProcessInstanceQuery() //创建历史流程实例查询
															.processInstanceId(processInstanceId)//使用流程实例ID查询
															.singleResult();
		System.out.println("流程定义ID:" + historicProcessInstance.getProcessDefinitionId());
		System.out.println("流程实例ID:" + historicProcessInstance.getId());
		System.out.println("流程开始时间:"+ historicProcessInstance.getStartTime() + " 流程结束时间:" + historicProcessInstance.getEndTime()+ " 流程持续时间:" +historicProcessInstance.getDurationInMillis());
	}
}
