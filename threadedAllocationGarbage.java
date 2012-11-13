class threadedAllocationGarbage extends Thread
{
	/* This class implements the garbage collecting functionality for the
	 * threaded allocation algorithm.
	 * It had to be put in a separate class since it implements a threading
	 * interface */

	int[] memoryBlock;
	int sleepTime;
	Job[] jobArray;

	threadedAllocationGarbage( int[] memoryBlock, int sleepTime, Job[] jobArray ){
		/* Set up a reference to the algorithm's memory location */
		this.memoryBlock = memoryBlock;

		/* Set up the time quantum */
		this.sleepTime = sleepTime;
		
		/* Set up the array of jobs so that we can pause them as need be */
		this.jobArray = jobArray;
	}
	
	public int[] largestBlock(){
		//Find an open location
		int memoryLoc = 0;
		int maxFreeSize = 0, maxFreeIndex = 0;
		while (memoryLoc < this.memoryBlock.length)
		{
			if (this.memoryBlock[memoryLoc] != 0)
				//Block isn't free
				continue;

			//One location is free, find out total size free
			//This loop breaks either when we've found the size we need, or
			//we found the beginning of the next block.
			int beginningLoc = memoryLoc;
			int free = 0;
			while (this.memoryBlock[memoryLoc] == 0)
			{
				memoryLoc += 1;
				free += 1;
			}
			//We've found the end of that chunk, see if it's bigger than what we have on file
			if (free > maxFreeSize){
				maxFreeSize = free;
				maxFreeIndex = beginningLoc;
			}
		}
		
		//We've reached the end of memory, return what the largest block was (if we found a block)
		if (maxFreeSize > 0)
			return new int[]{maxFreeIndex, maxFreeSize};
		else
			return new int[]{-1, -1};
	}

	public void run() {
		/* Code to run in the background */

		while (true)
		{
			/* The way this algorithm works is to:
			 * 		Start at the beginning of the memory block
			 * 		Find the largest available block
			 * 		Shift the closest job down to fill up this space
			 * 		Repeat until deconstructed
			 */
			
			int[] largestBlockInfo = largestBlock();
			int maxFreeBeginning = largestBlockInfo[0];
			int maxFreeSize = largestBlockInfo[1];
			
			if (maxFreeSize == -1)
				//No open space found
				continue;
			
			//Find out what ID the job is, and how big it is
			int jobID = this.memoryBlock[maxFreeBeginning + maxFreeSize + 1];
			
			int jobSize = 0;
			int counter = maxFreeBeginning + maxFreeSize;
			while (this.memoryBlock[counter] == jobID){
				counter++;
				jobSize++;
			}
			
			//Pause the job, and then relocate it
			//Note that we need to lock out the allocation to prevent a race
			synchronized (this.memoryBlock) {
				//Pause the job operation
				jobArray[jobID].pause();
				
				//Write the job into the free space
				int memoryLoc = maxFreeBeginning;
				counter = 0;
				while (counter < jobSize){
					memoryBlock[memoryLoc] = jobID;
					counter++;
				}
				
				//Inform the job of its new beginning location
				jobArray[jobID].setBeginningLocation(maxFreeBeginning);
				
				//Restart the job
				jobArray[jobID].resume();
				
				//Write the remaining memory as free
				counter = 0;
				while (counter < maxFreeSize){
					memoryBlock[memoryLoc] = 0;
				}
				
			}
			//Sleep for sleepTime, then go back to the top to continue compaction
			try {
				sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.println("Error in compaction thread! Algorithm is aborting.");
				
				//Kill ourselves
				this.interrupt();
			}
		}
	}
}
