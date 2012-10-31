public class baseAlgorithm{
	
	int[] memoryBlock;
	
	void baseAlgorithm(int memorySize){
		/* Constructor needed for each algorithm */
		memoryBlock = new int[memorySize];
	}
	
	void allocate(){
		/* This method to be overloaded by each algorithm */
	}
	void deallocate(){
		
	}
}
