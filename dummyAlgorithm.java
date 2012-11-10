import java.lang.reflect.*;

public class dummyAlgorithm implements baseAlgorithm{
	
	int[] memoryBlock;
	
	dummyAlgorithm(int memorySize){
		/* Constructor needed for each algorithm */
		memoryBlock = new int[memorySize];
	}
	
	public void allocate(int jobID, int jobSize, int jobTime){
		/* This method to be overloaded by each algorithm */
		
		//Generic code to get this classes deallocate() method
		Method deallocateMethod;
		try {
			deallocateMethod = this.getClass().getMethod("deallocate", new Class[]{int.class, int.class});
			Job newJob = new Job(jobTime, jobID, jobSize, 999, deallocateMethod, this);
			newJob.start();
			System.out.println("Allocating job " + jobID + " with size: " + jobSize + " for: " + jobTime + " milliseconds.");
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void deallocate(int jobSize, int beginningLocation){
		System.err.println("Removing job with size: " + jobSize + " beginning at: " + beginningLocation);
	}
}
