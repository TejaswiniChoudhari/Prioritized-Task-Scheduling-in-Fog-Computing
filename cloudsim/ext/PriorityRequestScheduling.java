package cloudsim.ext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import gridsim.GridSim;

/*
 * Get the list of all the requests.
 * Assign priority to the requests randomly 
 * also assign deadline time for the tasks. It will be greater than the current time
 * calculate the total delay allowed by the request
 * when we change the size of the data, we can schedule the data which is of higher data size
 * calculate the priority by checking the allowed deadline-the total delay for the task. 
 * then assign the tasks to one of three queues and send them for processing 
 * 
 * check the code for userbase internetcloudlet. it stores the id, and other info. userbase also has a provision to check the processing time. 
 * I definitely need to get the algo working for a cloudlet. as that is getting processed. it also is giving processing time. hence, priority can be set. 
 * 
 * I can make a static list here and get the data from the userbase class. I will need the id and processing time. getdata("procTime") can be used to get processing time.  
 */


public class PriorityRequestScheduling {

	public static List<requestInfo> reqinfo = new ArrayList<requestInfo>();
	List<InternetCloudlet> highQ = new ArrayList<InternetCloudlet>(),mediumQ= new ArrayList<InternetCloudlet>(),lowQ= new ArrayList<InternetCloudlet>();
	List<InternetCloudlet> Queue= new ArrayList<InternetCloudlet>(), requests; //high-1,medium-2,low-3
	
	public PriorityRequestScheduling() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	void setReqs(){
		requests = UserBase.getReqs(); //we will call addData for each of the cloudlets in this array
		for(int i=0;i<requests.size();i++){
			addData(requests.get(i));
		}
		
	}

	void addData(InternetCloudlet cl){
		
		requestInfo data = new requestInfo();
		Random rand = new Random();
		data.cl = cl;
		data.priority = rand.nextInt(3)+1;
		data.processingTime =GridSim.clock();//(double) cl.getData("procTime");	//null pointer exception
		data.deadline = GridSim.clock() - rand.nextDouble();
		reqinfo.add(data);
	}

	
	double totalProcessingTime(){
		double totalTime=0.0,tmp;
		for(int i=0;i<reqinfo.size();i++){
			
			requestInfo data = new requestInfo();
			data = reqinfo.get(i);
			tmp = getProcTime(data);
			totalTime = totalTime + tmp;
		}
		return totalTime;	
	}
	
	
	double getProcTime(requestInfo ri){
		
		return ri.processingTime;
	}
	
	List<InternetCloudlet> setPriorityQ(){
		
		//setReqs();
		double totalProcTime = totalProcessingTime(); //
		int newPriority;
		for(int i=0;i<reqinfo.size();i++){
			requestInfo data = new requestInfo();
			data = reqinfo.get(i);
			double estimatedServiceTime = (totalProcTime/GridSim.clock()) + data.processingTime;
			newPriority = assignPriority(data.deadline, estimatedServiceTime, data.priority);
			
			if(newPriority == 1){
				highQ.add(data.cl);
			}
			if(newPriority == 2){
				mediumQ.add(data.cl);
			}
			if(newPriority == 3){
				lowQ.add(data.cl);
			}
		}
		
		for(int i=0;i<highQ.size();i++){
			Queue.add(highQ.get(i));
		}
		for(int i=0;i<mediumQ.size();i++){
			Queue.add(mediumQ.get(i));
		}
		for(int i=0;i<lowQ.size();i++){
			Queue.add(lowQ.get(i));
		}
		return Queue;
	} 
	
	int assignPriority(double deadline, double estimatedServiceTime, int priority){
		
		if(deadline == estimatedServiceTime){
			return 1;
		}
		if(deadline < estimatedServiceTime && priority == 1){
			return 1;
		}
		if(deadline < estimatedServiceTime && priority == 2){
			return 2;
		}
		if(deadline < estimatedServiceTime && priority == 3){
			return 3;
		}
		return 3;
	}
}

class requestInfo{
	InternetCloudlet cl;
	int priority;
	double processingTime;
	double deadline;
	
	public requestInfo() {
		// TODO Auto-generated constructor stub
		priority = 1;
		deadline = 0.0;
	}
}
