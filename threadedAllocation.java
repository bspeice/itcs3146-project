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
		
		// Set up the array of job references
		this.jobArray = new Job[memoryManagement.JOBAMOUNT + 1];
		
		this.garbageThread = new threadedAllocationGarbage(this.memoryBlock, 20, this.jobArray);
		this.garbageThread.start();
		
	}

	int locateBlock(int blockSize){
		/* Locate a block of size blockSize, return -1 if can't find one */

		//Find an open location
		int memoryLoc = 0;
		while (memoryLoc < this.memoryBlock.length)
		{
			if (memoryBlock[memoryLoc] != 0){
				memoryLoc++;
				//Block isn't free
				continue;
			}

			//One location is free, find out total size free
			//This loop breaks either when we've found the size we need, or
			//we found the beginning of the next block.
			int beginningLoc = memoryLoc;
			int free = 0;
			while (free < blockSize && memoryLoc < this.memoryBlock.length && memoryBlock[memoryLoc] == 0)
			{
				memoryLoc += 1;
				free += 1;
			}

			if (free >= blockSize){
				//System.out.println("Found a block of size " + blockSize + " at " + beginningLoc);
				return beginningLoc;
			}
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
			synchronized(memoryBlock){
				for (int x = 0; x < jobSize; x++)
				{
					memoryBlock[beginningLocation + x] = jobID;
				}
			}
	
			Job newJob = new Job(jobLength, jobID, jobSize, beginningLocation, deallocateMethod, this);
			jobArray[jobID] = newJob;
			
			newJob.start();
		} catch (Exception e){
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public void deallocate(int jobSize, int beginningLocation){
		/* Over-rides deallocate() of baseAlgorithm */
		//System.err.println("Deallocation job with ID " + memoryBlock[beginningLocation] + " at time " + System.currentTimeMillis());
		//Simple algorithm, basically just mark the memory as cleared.
		synchronized(memoryBlock){
			for (int x = 0; x < jobSize; x++)
			{
				memoryBlock[beginningLocation + x] = 0;
			}
		}
	}

}

