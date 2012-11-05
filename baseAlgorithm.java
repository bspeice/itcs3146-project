public class baseAlgorithm{
	
	int[] memoryBlock;
	
	void baseAlgorithm(int memorySize){
		/* Constructor needed for each algorithm */
		memoryBlock = new int[memorySize];
	}
	
	void allocate(int jobID, int jobSize, int jobTime){
		/* This method to be overloaded by each algorithm */
	}
	void deallocate(int jobSize, int beginningLocation){
		
	}
}
