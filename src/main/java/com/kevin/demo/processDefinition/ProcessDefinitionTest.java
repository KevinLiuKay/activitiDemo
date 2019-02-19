package com.kevin.demo.processDefinition;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class ProcessDefinitionTest {
	/** 部署流程定义 (从classpath资源加载)*/
	@Test
	public void deploymentProcessDefinition_classpath() {
		//获取流程引擎
		//调用ProcessEngines的getDefaultProceeEngine方法时会自动加载classpath下名为activiti.cfg.xml文件。
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//管理流程定义
		Deployment deployment = processEngine.getRepositoryService() //与流程定义和部署对象相关的Service
												.createDeployment()	//创建一个部署对象
												.name("流程定义") //添加部署名称
												.addClasspathResource("diagrams/helloworld.bpmn")//从classpath的资源中加载，一次只能加载一个文件
												.addClasspathResource("diagrams/helloworld.png")//从classpath的资源中加载，一次只能加载一个文件
												.deploy();//完成部署
		System.out.println("部署ID:" + deployment.getId());	//501
		System.out.println("部署名称:" + deployment.getName());//流程定义
	}
	
	/** 部署流程定义 (从zip资源加载) */
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
		System.out.println("部署ID:" + deployment.getId());	//601
		System.out.println("部署名称:" + deployment.getName());//流程定义
	}
	/**
	 * 查询流程定义
	 * id: {key}:{version}:(随机值)
	 * name: 对应流程文件process节点的name属性
	 * key: 对应流程文件process节点的id属性
	 * version: 发布时自动生成的。如果第一次发布的流程，version 默认从1开始；
	 * 			如果当前流程引擎中已存在相同的key的流程，则找到当前的key对应的最高版本号，在最高版本号上加1；
	 * @throws Exception
	 */
	@Test
	public void queryProcessDefinition() throws Exception{
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//获取仓库服务对象，使用版本的升序排列，查询列表
		List<ProcessDefinition> processDefinitionList = processEngine.getRepositoryService()
															.createProcessDefinitionQuery()	//创建流程定义查询
															//添加查询条件
															//.processDefinitionNameLike(processDefinitionNameLike)(processDefinitionName) //使用流程定义名称模糊查询
															//.processDefinitionId(processDefinitionId) //使用流程定义ID查询
															//.processDefinitionKey(processDefinitionKey)//使用流程定义key查询
															/**排序*/
															.orderByProcessDefinitionVersion().asc()//按照流程版本升序查询
															//.orderByProcessDefinitionName().desc()//按照流程定义名称降序查询
															/**查询的结果集*/
															//.singleResult();//返回唯一结果集
															//.listPage(firstResult, maxResults)//分页查询
															//.count()//返回结果集的数量
															.list();//返回一个集合列表，封装流程定义
															
		if(processDefinitionList != null && processDefinitionList.size() > 0) {
			for(ProcessDefinition pd : processDefinitionList) {
				System.out.println("流程定义ID:" + pd.getId());
				System.out.println("流程定义名称:" + pd.getName());
				System.out.println("流程定义key:" + pd.getKey());
				System.out.println("流程定义版本:" + pd.getVersion());
				System.out.println("流程定义资源名称bpmn文件:" + pd.getResourceName());
				System.out.println("流程定义资源名称png文件:" + pd.getDiagramResourceName());
				System.out.println("部署对象ID:" + pd.getDeploymentId());
				System.out.println("**************************************************");
			}
		}
	}
	
	/**
	 * 删除流程定义
	 * 如果该流程定义下没有正在运行的流程，则可以用普通删除。
	 * 如果是有关联的信息，用级联删除。项目开发中使用级联删除的情况比较多，删除操作一般只开放给超级管理员使用。
	 * @throws Exception
	 */
	@Test
	public void deleteProcessDefinition() throws Exception{
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//删除发布信息
		String deploymentId = "601";
		//获取仓库服务对象
		RepositoryService repositoryService = processEngine.getRepositoryService();
		//普通删除，如果当前规则下有正在执行的流程（只能删除没有启动的流程），则抛出异常
		//repositoryService.deleteDeployment(deploymentId);
		//级联删除，会删除和当前规则相关的所有信息，正在执行的信息，也包括历史信息
		//相当于：repositoryService.deleteDeploymentCascade(deploymentId);
		repositoryService.deleteDeployment(deploymentId, true);
		System.out.println("删除成功！");
	}
	
	/**
	 * 查看流程附件（查看流程图片）
	 * @throws Exception
	 */
	@Test
	public void viewProcessImage() throws Exception {
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		String deploymentId = "801 ";
		//获取图片资源名称（包括bpmn，png文件）
		List<String> list = processEngine.getRepositoryService()
								.getDeploymentResourceNames(deploymentId);
		//定义图片资源名称
		String resourceName = "";
		if(list != null && list.size() > 0) {
			for(String name : list) {
				System.out.println("name:" + name);
				if(name.indexOf(".png") >= 0) {
					resourceName = name;
				}
			}
			System.out.println("imageName:" + resourceName);
			if(resourceName != null) {
				//通过部署ID和文件名称得到文件的输入流
				InputStream in = processEngine.getRepositoryService()
									.getResourceAsStream(deploymentId, resourceName);
				//将图片生成到/Users/Kevin/Development/的目录下
				File file = new File("/Users/Kevin/Development/" + resourceName);
				//将输入流的图片写到/Users/Kevin/Development/下
				FileUtils.copyInputStreamToFile(in, file);
			}
		}
	}
	
	/**
	 * 查询最新版本的流程定义
	 * @throws Exception
	 */
	@Test
	public void queryAllLatestProcessDefinitionVersions() throws Exception {
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		//查询，把最大的版本都排到后面
		List<ProcessDefinition> list = processEngine.getRepositoryService()
											.createProcessDefinitionQuery()
											.orderByProcessDefinitionVersion().asc()
											.list();
		//过滤出最新版本
		Map<String, ProcessDefinition> map = new LinkedHashMap<>();
		if(list != null && list.size() > 0) {
			for(ProcessDefinition pd : list) {
				map.put(pd.getKey(), pd);
			}
		}
		//显示
		List<ProcessDefinition> pdList = new ArrayList<ProcessDefinition>(map.values());
		if(pdList != null && pdList.size() > 0) {
			for(ProcessDefinition pd : pdList) {
				System.out.println("流程定义ID:" + pd.getId());
				System.out.println("流程定义名称:" + pd.getName());
				System.out.println("流程定义key:" + pd.getKey());
				System.out.println("流程定义版本:" + pd.getVersion());
				System.out.println("流程定义资源名称bpmn文件:" + pd.getResourceName());
				System.out.println("流程定义资源名称png文件:" + pd.getDiagramResourceName());
				System.out.println("部署对象ID:" + pd.getDeploymentId());
				System.out.println("**************************************************");
			}
		}
	}
	
	/**
	 * 删除流程定义（删除key相同的所有不同版本的流程定义）
	 * @throws Exception
	 */
	@Test
	public void deleteProcessDefinitionByKey() throws Exception{
		//获取流程引擎
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
		String processDefinitionKey = "helloworld";
		//1.查询指定key的所有版本的流程定义
		List<ProcessDefinition> list = processEngine.getRepositoryService()
											.createProcessDefinitionQuery()
											.processDefinitionKey(processDefinitionKey) //指定流程定义key查询
											.list();
		//2.遍历，获取每个流程定义的部署ID
		if(list != null && list.size() > 0) {
			for(ProcessDefinition pd : list) {
				String deploymentId = pd.getDeploymentId();
				//3.for循环删除流程定义
				processEngine.getRepositoryService()
					.deleteDeployment(deploymentId, true);
			}
		}
		System.out.println("删除成功！");
	}
}
