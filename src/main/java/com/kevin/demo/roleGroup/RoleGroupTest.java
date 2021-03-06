package com.kevin.demo.roleGroup;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.junit.Test;

/**
 * @author Kevin
 */
public class RoleGroupTest {
	/** 
	 * 部署流程定义
	 */
	@Test
	public void deploymentProcessDefinition_inputStream() throws Exception{
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//获取上传文件输入流程 
		InputStream inputStreamBpmn = this.getClass().getResourceAsStream("roleGroup.bpmn");
		InputStream inputStreamPng = this.getClass().getResourceAsStream("roleGroup.png");
		//获取仓库服务，从类路径下完成部署
		Deployment deployment = processEngine.getRepositoryService() //与流程定义和部署对象相关的Service
												.createDeployment()	//创建一个部署对象
												.name("角色组任务") //添加部署名称
												.addInputStream("roleGroup.bpmn", inputStreamBpmn)
												.addInputStream("roleGroup.png", inputStreamPng)
												.deploy();//完成部署
		System.out.println("部署ID:" + deployment.getId());	//部署ID:8301
		System.out.println("部署名称:" + deployment.getName());//部署名称:角色组任务
		/**添加用户角色组*/
		IdentityService identityService = processEngine.getIdentityService();
		//创建角色
		identityService.saveGroup(new GroupEntity("总经理"));
		identityService.saveGroup(new GroupEntity("部门经理"));
		//创建用户
		identityService.saveUser(new UserEntity("张三"));
		identityService.saveUser(new UserEntity("李四"));
		identityService.saveUser(new UserEntity("王五"));
		//建立用户角色关联关系
		identityService.createMembership("张三", "部门经理");
		identityService.createMembership("李四", "部门经理");
		identityService.createMembership("王五", "总经理");
		System.out.println("添加组织机构成功!");
	}
	
	/** 
	 * 启动流程实例
	 * 如果是单例流程（没有分支和聚合），那么流程实例ID和执行对象ID是相同的
	 * 一个流程只有一个流程实例，执行对象可以存在多个（存在分支和聚合）。
	 */
	@Test
	public void startProcess () throws Exception{
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//启动流程
		//使用流程定义的key启动流程实例，默认会按照最新版本启动流程实例
		String processDefinitionKey = "roleGroup";
		/**启动流程实例*/
		ProcessInstance pi = processEngine.getRuntimeService() //与正在执行的流程实例和执行对象相关的Service
							.startProcessInstanceByKey(processDefinitionKey);//使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中的id的属性值
		System.out.println("流程实例ID:" + pi.getId());//流程实例ID:8401
		System.out.println("流程定义ID:"+ pi.getProcessDefinitionId());	//流程定义ID:roleGroup:1:8304
	}
	
	/** 查看当前人的个人任务 */
	@Test
	public void queryPersonalTask() {
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//指定任务办理者 
		String assignee = "孙悟空";
		//查询任务列表
		List<Task> tasks = processEngine.getTaskService()
										.createTaskQuery() //创建任务查询对象
										/**查询条件*/
										.taskAssignee(assignee) //指定个人任务查询，指定办理人
										//.taskCandidateUser(candidateUser) //组任务的办理人查询
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
	/** 查看当前人的组任务 */
	@Test
	public void queryPersonalGroupTask() {
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//指定任务办理者 
		String candidateUser = "张三";
		//查询任务列表
		List<Task> tasks = processEngine.getTaskService()
										.createTaskQuery() //创建任务查询对象
										/**查询条件*/
										//.taskAssignee(assignee) //指定个人任务查询，指定办理人
										.taskCandidateUser(candidateUser) //组任务的办理人查询
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
	 * 查询组任务成员列表
	 */
	@Test
	public void queryGroupUser(){
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		String taskId = "6104";
		List<IdentityLink> list = processEngine.getTaskService()//
		                					   .getIdentityLinksForTask(taskId);
         //List<IdentityLink> list = processEngine.getRuntimeService()//
		//				.getIdentityLinksForProcessInstance(instanceId);
		if(list != null && list.size() > 0) {
			for(IdentityLink identityLink : list){
				System.out.println("用户ID:"+identityLink.getUserId());
				System.out.println("类型:"+identityLink.getType()); //candidate:表示候选者类型，participant:表示参与者类型
				System.out.println("任务ID:"+identityLink.getTaskId());
				System.out.println("流程实例ID:"+identityLink.getProcessInstanceId());
				System.out.println("**********************************************");
			}
		}
	}

	/**
	 * 查询组任务成员历史列表
	 */
	@Test
	public void queryGroupHisUser(){
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
//		String taskId = "6104";
//		List<HistoricIdentityLink> list = processEngine.getHistoryService()//
//													   .getHistoricIdentityLinksForTask(taskId);
		String processInstanceId = "8101";
        List<HistoricIdentityLink> list = processEngine.getHistoryService()//
          					 						   .getHistoricIdentityLinksForProcessInstance(processInstanceId);
		if(list != null && list.size() > 0) {
			for(HistoricIdentityLink identityLink : list){
				System.out.println("用户ID:"+identityLink.getUserId());
				System.out.println("类型:"+identityLink.getType()); //candidate:表示候选者类型，participant:表示参与者类型
				System.out.println("任务ID:"+identityLink.getTaskId());
				System.out.println("流程实例ID:"+identityLink.getProcessInstanceId());
				System.out.println("**********************************************");
			}
		}
	}
	
	 /**
	  * 拾取任务,将组任务分配给个人任务
	  */
	//由1个人去完成任务
	@Test
	public void claim(){
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//任务ID
		String taskId = "8404";
		//分配的个人任务（可以是组任务中的成员，也可以是非组任务中的成员）
		String userId = "张三";
		processEngine.getTaskService()
					 .claim(taskId, userId);
		System.out.println("拾取任务成功!!!");
	}
	
	/**
	 * 将个人任务回退到组任务,前提是之前该任务一定是组任务
	 */
	@Test
	public void backPersonalTaskToGroupTask() {
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//任务ID
		String taskId = "8404";
		//分配的个人任务（可以是组任务中的成员，也可以是非组任务中的成员）
		processEngine.getTaskService()
					 .setAssignee(taskId, null);
		System.out.println("将个人任务回退到组任务成功!!!");
	}
	
	/**
	 * 向组任务中添加成员
	 */
	@Test
	public void addAssigneeToGroup() {
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//任务ID
		String taskId = "8404";
		String userId = "大H";
		processEngine.getTaskService()
					 .addCandidateUser(taskId, userId);
		System.out.println("向组任务中添加成员成功!!!");
	}
	
	/**
	 * 从组任务中删除成员
	 */
	@Test
	public void deleteAssigneeToGroup() {
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//任务ID
		String taskId = "8404";
		String userId = "小B";
		processEngine.getTaskService()
					 .deleteCandidateUser(taskId, userId);
		System.out.println("从组任务中删除成员成功!!!");
	}
	
	/** 
	 * 完成我的个人任务 
	 */
	@Test
	public void completePersonalTask() throws Exception{
		String taskId = "8404";
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//完成任务
		processEngine.getTaskService()
						.complete(taskId);
		System.out.println("完成任务: 任务ID:" + taskId);
	}
}
