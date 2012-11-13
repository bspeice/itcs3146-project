import java.lang.reflect.Method;

public class threadedAllocation implements baseAlgorithm{
	int[] memoryBlock;
	threadedAllocationGarbage garbageThread;
	Job[] jobArray;
	
	threadedAllocation(int memorySize) {
		/* Constructor specific for this algorithm
		 *
		 * Start the threaded garbage collector, and then begin
		 * our actual operations */
		memoryBlock = new int[memorySize];
		
		this.garbageThread = new threadedAllocationGarbage(this.memoryBlock, 20, this.jobArray);
		this.garbageThread.run();
		
		// Set up the array of job references
		jobArray = new Job[memoryManagement.JOBAMOUNT];
	}

	int locateBlock(int blockSize){
		/* Locate a block of size blockSize, return -1 if can't find one */

		//Find an open location
		int memoryLoc = 0;
		while (memoryLoc < this.memoryBlock.length)
		{
			if (memoryBlock[memoryLoc] != 0)
				//Block isn't free
				continue;

			//One location is free, find out total size free
			//This loop breaks either when we've found the size we need, or
			//we found the beginning of the next block.
			int beginningLoc = memoryLoc;
			int free = 0;
			while (free < blockSize && memoryBlock[memoryLoc] == 0)
			{
				memoryLoc += 1;
				free += 1;
			}

			if (free >= blockSize)
				return beginningLoc;
		}
		//Once the above loop has exited, we've run out of locations to check
		return -1;
	}

	public void allocate(int jobID, int jobSize, int jobLength ){
		/* Over-rides allocate() of baseAlgorithm */
		try{
			Method deallocateMethod = this.getClass().getMethod("deallocate", new Class[]{int.class, int.class});
			
			//Loop until we get a block big enough for our job
			//	Note that this assumes we're not going to race against ourselves
			int beginningLocation = locateBlock( jobSize );
			while (beginningLocation == -1)
				beginningLocation = locateBlock( jobSize );
	
			//We've got a location, mark it as filled, and start the job.
			for (int x = 0; x < jobSize; x++)
			{
				memoryBlock[beginningLocation + x] = jobID;
			}
	
			//TODO: Code to start the job
			Job newJob = new Job(jobLength, jobID, jobSize, beginningLocation, deallocateMethod, this);
			jobArray[jobID] = newJob;
			newJob.start();
		} catch (Exception e){
			System.out.println("Could not allocate job with ID " + jobID);
		}
	}

	public void deallocate(int jobSize, int beginningLocation){
		/* Over-rides deallocate() of baseAlgorithm */

		//Simple algorithm, basically just mark the memory as cleared.
		for (int x = 0; x < jobSize; x++)
		{
			memoryBlock[beginningLocation + x] = 0;
		}
	}

}

