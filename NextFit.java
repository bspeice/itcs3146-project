/*	David P. Turnbull
	ITCS3146
	group project
	this class sets up a First Fit memory scheme
*/

//this section sets up the Car class
class NextFit implements baseAlgorithm
{
	
	//this section sets up the private elements of the class
	private int	jobId,
					jobSize,
					jobTime,
					startLoc,
					endLoc,
					blkSize,
					memSize = memoryManagement.memory,
					active,
					noJobs=0,
					s1=0,
					currentPosition=0,
					positionToCompress=0,
					loopCount,
					compMemTest=0,
					tableEntries=1;
	private int[] tempVal = new int[6];
	private int[][] memTable = new int[memSize+2][6];
	private int[] memory = new int[memSize];
	
	//this is a no argument constructor
	public NextFit()
	{
		memTable[0][0]=0;				//job number
		memTable[0][1]=0;				//job size
		memTable[0][2]=0;				//start location in memory
		memTable[0][3]=memSize-1;	//end location in memory
		memTable[0][4]=memSize;		//mem blk size size
		memTable[0][5]=-1;			//status, 0=not active, 1=active, -1=special
	}
	
	
	//this method sets the job up 
	public void allocate(int ID, int size, int jTime)
	{
		jobId = ID;
		jobSize = size;
		jobTime = jTime;
		noJobs++;
		s1=0;
		loopCount=0;
		
		//checks to see if the job will fit in memory
		if(jobSize>memSize)
		{
			System.out.println("\n\n*********************************************************"+
										"         THIS JOB IS TO LARGE TO FIT INTO MEMORY"+
										"*********************************************************");
			System.exit(0);
		}

		//this section looks for a place to put the new job
		do
		{
			if(memTable[currentPosition][5]==-1 && memTable[currentPosition][4]>=jobSize && 
				memTable[currentPosition][3]==memSize-1)
			{
				//runs only for the first job
				if(noJobs==1)
				{
					memTable[currentPosition][0] = jobId;
					memTable[currentPosition][1] = jobSize;
					memTable[currentPosition][2] = 0;
					memTable[currentPosition][3] = jobSize-1;
					memTable[currentPosition][4] = memTable[0][3]-memTable[0][2]+1;
					memTable[currentPosition][5] = 1;
					fillMemory(jobId, jobSize, memTable[currentPosition][2]);
					memTable[currentPosition+1][0] = 0;
					memTable[currentPosition+1][1] = 0;
					memTable[currentPosition+1][2] = memTable[currentPosition][3]+1;
					memTable[currentPosition+1][3] = memSize-1;
					memTable[currentPosition+1][4] = memSize-memTable[currentPosition+1][2];
					memTable[currentPosition+1][5] = -1;
					currentPosition++;
					positionToCompress=currentPosition;
					tableEntries++;
					s1=memSize*2;
				}
				//runs after the first job and if the only available slot is at the end of memory
				else
				{
					memTable[currentPosition][0] = jobId;
					memTable[currentPosition][1] = jobSize;
					memTable[currentPosition][2] = memTable[currentPosition-1][3]+1;
					memTable[currentPosition][3] = jobSize+memTable[currentPosition][2]-1;
					memTable[currentPosition][4] = memTable[currentPosition][3]-memTable[currentPosition][2]+1;
					memTable[currentPosition][5] = 1;
					fillMemory(jobId, jobSize, memTable[currentPosition][2]);
					memTable[currentPosition+1][0] = 0;
					memTable[currentPosition+1][1] = 0;
					memTable[currentPosition+1][2] = memTable[currentPosition][3]+1;
					memTable[currentPosition+1][3] = memSize-1;
					memTable[currentPosition+1][4] = memSize-memTable[currentPosition+1][2];
					memTable[currentPosition+1][5] = -1;
					tableEntries++;
					currentPosition++;
					positionToCompress=currentPosition;
					s1=memSize*2;
				}
			}
			//checks for first available free block that has been deallocated
			else if(memTable[currentPosition][4]>=jobSize && memTable[currentPosition][5]==0)
			{
				memTable[currentPosition][0] = jobId;
				memTable[currentPosition][1] = jobSize;
				memTable[currentPosition][5] = 1;
				fillMemory(jobId, jobSize, memTable[currentPosition][2]);
				currentPosition++;
				positionToCompress=currentPosition;
				s1=memSize*2;
			}
			else if(currentPosition==tableEntries-1)
			{
				currentPosition=0;
				s1++;
			}
			else
			{
				s1++;
				currentPosition++;
			}
			
		}while(s1<tableEntries);

		
		//if job will not fit this section will compress memory and try placing the job again
		if(s1==tableEntries)
		{
			noJobs=noJobs-1;
			compMem();
			currentPosition=0;
			positionToCompress=0;
			allocate(ID, size, jobTime);
		}
	}
	
	//this method is used if you want to deallocate a job by jobId
	public void removeJob(int ID)
	{
		jobId = ID;
		s1=0;
		do
		{
			if(memTable[s1][0] == jobId)
			{
				jobSize = memTable[s1][1];
				startLoc = memTable[s1][2];
				s1=memSize*2;
			}
			else
			{
				s1++;
			}
			
		}while (s1<tableEntries);
		deallocate(jobSize, startLoc);
	}

	//this method removes a job it does not check to see if the job exisits
	public void deallocate(int jobSize, int beginningLocation)
	//public void removeJob(int ID)
	{
		jobId = 0;
		jobSize = jobSize;
		startLoc = beginningLocation;
		s1=0;
		do
		{
			if(memTable[s1][2] == startLoc)
			{
				memTable[s1][0] = 0;
				memTable[s1][1] = 0;
				memTable[s1][5] = 0;
				s1=memSize*2;
				jobId=-1;
				noJobs--;
			}
			else
			{
				s1++;
			}
		
		}while (s1<tableEntries);
		
	}
	
	//this method compacts the memory
	public void compMem()
	{
		System.out.println("***********************enter compress************************");
		compMemTest=tableEntries;
		for(int c=0; c<=compMemTest; c++)
		{
			//this section checks to see if two unused blks are next to each other and then
			//comdines them
			if(memTable[c][5]==0 && memTable[c+1][5]==0)
			{
				tempVal[0] = memTable[c+1][0];
				tempVal[1] = memTable[c+1][1];
				tempVal[2] = memTable[c+1][2];
				tempVal[3] = memTable[c+1][3];
				tempVal[4] = memTable[c+1][4];
				tempVal[5] = memTable[c+1][5];
				memTable[c+1][0]=-1;
				memTable[c+1][1]=-1;
				memTable[c+1][2]=-1;
				memTable[c+1][3]=-1;
				memTable[c+1][4]=-1;
				memTable[c+1][5]=-1;
				memTable[c][0]=0;
				memTable[c][1]=0;
				memTable[c][3]=tempVal[3];
				memTable[c][4]=memTable[c][4]+tempVal[4];
				memTable[c][5]=0;
				//this loop shifts the remaining jobs up
				for(int srt=c+1; srt<tableEntries; srt++)
				{
					memTable[srt][0]=memTable[srt+1][0];
					memTable[srt][1]=memTable[srt+1][1];
					memTable[srt][2]=memTable[srt+1][2];
					memTable[srt][3]=memTable[srt+1][3];
					memTable[srt][4]=memTable[srt+1][4];
					memTable[srt][5]=memTable[srt+1][5];
				}
				memTable[tableEntries-1][0]=-1;
				memTable[tableEntries-1][1]=-1;
				memTable[tableEntries-1][2]=-1;
				memTable[tableEntries-1][3]=-1;
				memTable[tableEntries-1][4]=-1;
				memTable[tableEntries-1][5]=-1;
				c--;
			}
		}
		
		
		
		s1=0;
		for(int c1=0; c1<tableEntries; c1++)
		{
			if(memTable[c1][0]==-1)
			{
				s1++;
			}
		}
		tableEntries=tableEntries-s1;
		
		if(memTable[tableEntries-2][5]==0 && memTable[tableEntries-1][5]==-1)
		{
			memTable[tableEntries-2][3]=memTable[tableEntries-1][3];
			memTable[tableEntries-2][4]=memTable[tableEntries-1][4]+memTable[tableEntries-2][4];
			memTable[tableEntries-2][5]=-1;
			tableEntries--;
		}
		currentPosition = 0;
		positionToCompress = 0;
	}
	
	//this method fills the memory location with the data
	private void fillMemory(int job, int size, int start)
	{
		jobId=job;
		jobSize=size;
		startLoc=start;
		
		for(int fillCount=startLoc; fillCount<jobSize+startLoc; fillCount++)
		{
			memory[fillCount]=jobId;
		}
	}

	//this method returns a String of all the elements stored in the object
	public String toString()
	{
		String str;
		str = ("\n\nJob ID\tJob Size\tStart Loc\tEnd Loc\tMem Blk Size\tStatus");
		
		for(int cnt=0; cnt<tableEntries; cnt++)
		{
		str = (str+"\n"+memTable[cnt][0]+"\t"+memTable[cnt][1]+"\t\t"+memTable[cnt][2]+"\t\t"+
						memTable[cnt][3]+"\t\t"+memTable[cnt][4]+"\t"+memTable[cnt][5]);
		}
		
		return str;
	}
	
}