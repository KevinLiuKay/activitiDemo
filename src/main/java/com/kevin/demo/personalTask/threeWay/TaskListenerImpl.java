package com.kevin.demo.personalTask.threeWay;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

@SuppressWarnings("serial")
public class TaskListenerImpl implements TaskListener {
	/**
	 * 用来指定个人和组任务的办理人
	 */
	@Override
	public void notify(DelegateTask delegateTask) {
		String assignee = "张无忌";
		//指定个人任务
		//通过类去查询数据库，将下一个任务的办理人查询获取，然后通过setAssignee()的方法来指定任务的办理人
		delegateTask.setAssignee(assignee);
	}

}
