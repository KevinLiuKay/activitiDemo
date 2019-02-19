package com.kevin.demo.groupTask.threeWay;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

@SuppressWarnings("serial")
public class GroupTaskListenerImpl implements TaskListener {
	/**
	 * 用来指定个人和组任务的办理人
	 */
	@Override
	public void notify(DelegateTask delegateTask) {
		String userId1 = "孙悟空";
		String userId2 = "猪八戒";
		//指定组任务
		delegateTask.addCandidateUser(userId1);
		delegateTask.addCandidateUser(userId2);
	}

}
