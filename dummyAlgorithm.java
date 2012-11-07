public class dummyAlgorithm implements baseAlgorithm{
	
	int[] memoryBlock;
	
	dummyAlgorithm(int memorySize){
		/* Constructor needed for each algorithm */
		memoryBlock = new int[memorySize];
	}
	
	public void allocate(int jobID, int jobSize, int jobTime){
		/* This method to be overloaded by each algorithm */
		System.out.println("Allocating job " + jobID + " with size: " + jobSize + " for: " + jobTime + " milliseconds.");
	}
	public void deallocate(int jobSize, int beginningLocation){
		System.out.println("Removing job with size: " + jobSize + " beginning at: " + beginningLocation);
	}
}
