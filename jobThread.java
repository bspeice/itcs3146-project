public class jobThread extends Thread {
	private final int sleepResolution = 200; //Milliseconds
	private long jobTime; //Milliseconds
	private long elapsedTime;
	private boolean isPaused, pauseStateChanged;
	private long startTime;
	private int jobID;
	private boolean jobDone;
	/* private BaseAlgorithm parentAlgorithm *///Our parent to notify when we're done.
	
	public jobThread(long jobTime, int jobID /*, BaseAlgorithm parentAlgorithm*/){
		this.jobTime = jobTime;
		elapsedTime = 0;
		isPaused = false;
		startTime = 0;
		this.jobID = jobID;
		//this.parentAlgorithm = parentAlgorithm;
	}
	
	public void pause(){
		synchronized(this){
			isPaused = true;
			pauseStateChanged = true;
		}
	}
	
	public void jobResume(){
		synchronized(this){
			isPaused = false;
			pauseStateChanged = true;
		}
	}
	
	public void run(){
		//The event loop.
		//Basically, check that elapsedTime plus current time delta
		//are not greater than the time we're supposed to run.
		//If paused, don't do anything until we resume
		try{
			while (!jobDone){
				//Begin event logic
				sleep(sleepResolution);
				
				synchronized(this){
					if (pauseStateChanged){
						if (isPaused){
							//We have just been paused, save the time that we've currently
							//been running, and then go back to the event loop
							elapsedTime += System.currentTimeMillis() - startTime;
							continue;
						} else {
							//We have just been resumed, restart the timer
							startTime = System.currentTimeMillis();
							continue;
						}
					} else {
						if (isPaused){
							//Nothing much happening, we're still paused.
							continue;
						} else {
							//Not paused, Check if we need to keep running
							long currentDelta = System.currentTimeMillis() - startTime;
							if (currentDelta + elapsedTime >= jobTime){
								jobDone = true;
							}
						}
					}
				}
			}
			
			//We're done, go ahead and notify our algorithm to clean us up
			/* parentAlgorithm.deallocate(jobID); */
		} catch (Exception e) {
			return;
		}
		
		
				
	}
}
