import java.lang.reflect.Method;

/*
 * Author: Bradlee Speice
 * Job Class
 * 
 * The purpose of this class is to simulate a running "Job" in memory. To create a new Job, call the
 * "start" method, and inform the job of how long it should run, and give it an identifier.
 * The job will use this to report back that it is done. If you need to pause the job, you
 * can do so. Finally, the job will report back to whoever called it that it needs to be destroyed.
 * If there are any questions, email bspeice@uncc.edu
 */
public class Job {
	private jobThread myThread; //Reference to the thread we control
	private boolean myThreadPaused; //Used to keep track of the execution state of our thread
	
	public	Job(int jobTime, int jobID, Method parentAlgorithmDeallocate, int jobSize, int beginningLocation ){
		//Create a new job, and start it running
		myThread = new jobThread(jobTime, jobID, parentAlgorithmDeallocate, jobSize, beginningLocation);
		myThread.start();
		myThreadPaused = false;
	}
	
	public void pause(){
		if (!myThreadPaused){
			myThread.pause();
			myThreadPaused = true;
		}
	}
	
	public void resume(){
		if (myThreadPaused){
			myThread.jobResume();
			myThreadPaused = false;
		}
	}
	
	
}
