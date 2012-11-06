class threadedAllocationGarbage extends Thread
{
	/* This class implements the garbage collecting functionality for the
	 * threaded allocation algorithm.
	 * It had to be put in a separate class since it implements a threading
	 * interface */

	int[] memoryBlock;
	int sleepTime;

	threadedAllocationGarbage( int[] memoryBlock, int sleepTime ){
		/* Set up a reference to the algorithm's memory location */
		this.memoryBlock = memoryBlock;

		/* Set up the time quantum */
		this.sleepTime = sleepTime;
	}

	public void run() {
		/* Code to run in the background */

		//Sleep for sleepTime, then scan for memory to compact
	}
}
