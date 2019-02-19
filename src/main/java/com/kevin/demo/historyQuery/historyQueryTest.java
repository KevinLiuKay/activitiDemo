package com.kevin.demo.historyQuery;

import java.util.List;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.junit.Test;
/**
 * @author Kevin
 */
public class historyQueryTest {
	/**
	 * 1.查询历史流程实例
	 */
	@Test
	public void queryHistoricProcessInstance() throws Exception{
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		String processDefinitionKey = "processVariables";
		//获取历史流程实例，返回历史流程实例的集合
		List<HistoricProcessInstance> list = processEngine.getHistoryService()
												.createHistoricProcessInstanceQuery() //创建历史流程实例查询
												.processDefinitionKey(processDefinitionKey) //按照流程定义的key查询
												.orderByProcessInstanceStartTime().desc()//按照流程开始时间降序排列
												.list();//返回结果集
		if(list != null && list.size() > 0) {
			for(HistoricProcessInstance historicProcessInstance : list) {
				System.out.println("流程实例ID:"+historicProcessInstance.getId());
				System.out.println("流程定义ID:"+historicProcessInstance.getProcessDefinitionId());
				System.out.println("流程开始时间:"+historicProcessInstance.getStartTime());
				System.out.println("流程结束时间:"+historicProcessInstance.getEndTime());
				System.out.println("流程持续时间:"+historicProcessInstance.getDurationInMillis());
				System.out.println("---------------------------------------------------------");
			}
		}
	}
	/**
	 * 2.查询历史活动
	 */
	@Test
	public void queryHistoricActivityInstance() throws Exception{
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		String processInstanceId = "2101";
		List<HistoricActivityInstance> list = processEngine.getHistoryService()
												.createHistoricActivityInstanceQuery() //创建历史活动实例查询
												.processInstanceId(processInstanceId) //使用流程实例ID查询
												//.listPage(firstResult, maxResults) //分页条件
												.orderByHistoricActivityInstanceEndTime().asc() //排序条件
												.list();//查询结果集
		if(list != null && list.size() > 0) {
			for(HistoricActivityInstance historicActivityInstance : list) {
				System.out.println("活动ID:" + historicActivityInstance.getActivityId());
				System.out.println("活动名称:" + historicActivityInstance.getActivityName());
				System.out.println("活动类别:" + historicActivityInstance.getActivityType());
				System.out.println("活动办理人:" + historicActivityInstance.getAssignee());
				System.out.println("活动开始时间:" + historicActivityInstance.getStartTime());
				System.out.println("活动结束时间:" + historicActivityInstance.getEndTime());
				System.out.println("活动持续时间:" + historicActivityInstance.getDurationInMillis());
				System.out.println("------------------------------------------------------------");
			}
		}
	}
	
	/**
	 * 3.查询历史任务
	 */
	@Test
	public void queryHistoricTask() throws Exception{
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		String processInstanceId = "2101";
		List<HistoricTaskInstance> list = processEngine.getHistoryService()
											.createHistoricTaskInstanceQuery() //创建历史任务的查询
											.processInstanceId(processInstanceId) //使用流程实例ID查询
											//.listPage(firstResult, maxResults) //分页条件
											.orderByHistoricTaskInstanceStartTime().asc() //排序条件
											.list(); //查询结果集
		if(list != null && list.size() > 0) {
			for(HistoricTaskInstance historicTaskInstance : list) {
				System.out.println("历史任务ID:" + historicTaskInstance.getId());
				System.out.println("历史任务名称:" + historicTaskInstance.getName());
				System.out.println("历史流程定义ID:" + historicTaskInstance.getProcessDefinitionId());
				System.out.println("历史流程实例ID:" + historicTaskInstance.getProcessInstanceId());
				System.out.println("历史流程办理人:" + historicTaskInstance.getAssignee());
				System.out.println("历史流程开始时间:" + historicTaskInstance.getStartTime());
				System.out.println("历史流程结束时间:" + historicTaskInstance.getEndTime());
				System.out.println("历史流程持续时间:" + historicTaskInstance.getDurationInMillis());
				System.out.println("------------------------------------------------------------");
			}
		}
	}
	
	/**
	 * 4.查询历史流程变量
	 */
	@Test
	public void queryHistoricVariables() throws Exception{
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		String processInstanceId = "2101"; 
		List<HistoricVariableInstance> list = processEngine.getHistoryService()
												.createHistoricVariableInstanceQuery()
												.processInstanceId(processInstanceId)
												.orderByVariableName().asc()
												.list();
		if(list != null && list.size() > 0) {
			for(HistoricVariableInstance historicVariableInstance : list) {
				System.out.println("流程实例ID:" + historicVariableInstance.getProcessInstanceId());
				System.out.println("流程变量名称:" + historicVariableInstance.getVariableName());
				System.out.println("流程变量类别:" + historicVariableInstance.getVariableTypeName());
				System.out.println("流程变量值:" + historicVariableInstance.getValue());
				System.out.println("------------------------------------------------");
			}
		}
	}
}
