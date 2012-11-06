/*	David P. Turnbull
	ITCS3146
	group project
	this class sets up a First Fit memory scheme
*/

//this section sets up the Car class
class FirstFit
{
	
	//this section sets up the private elements of the class
	private int	jobId,
					jobSize,
					startLoc,
					endLoc,
					blkSize,
					memSize=1024,
					active,
					noJobs=0,
					s1=0,
					chkCompress=0,
					compMemTest=0,
					tableEntries=1;
	private int[] tempVal = new int[6];
	private int[][] memTable = new int[memSize+2][6];
	
	
	//this is a no argument constructor
	public FirstFit()
	{
		memTable[0][0]=0;				//job number
		memTable[0][1]=0;				//job size
		memTable[0][2]=0;				//start location in memory
		memTable[0][3]=memSize-1;	//end location in memory
		memTable[0][4]=memSize;		//mem blk size size
		memTable[0][5]=-1;			//status, 0=not active, 1=active, -1=special
	}
	
	//this method sets the job up              need to modify the job class to return something
	public void addJob(int ID, int size)
	{
		jobId = ID;
		jobSize = size;
		noJobs++;
		s1=0;
		
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
			if(memTable[s1][5]==-1 && memTable[s1][4]>=jobSize && memTable[s1][3]==memSize-1)
			{
				//runs only for the first job
				if(noJobs==1)
				{
					memTable[s1][0] = jobId;
					memTable[s1][1] = jobSize;
					memTable[s1][2] = 0;
					memTable[s1][3] = jobSize-1;
					memTable[s1][4] = memTable[0][3]-memTable[0][2]+1;
					memTable[s1][5] = 1;
					memTable[s1+1][0] = 0;
					memTable[s1+1][1] = 0;
					memTable[s1+1][2] = memTable[s1][3]+1;
					memTable[s1+1][3] = memSize-1;
					memTable[s1+1][4] = memSize-memTable[s1+1][2];
					memTable[s1+1][5] = -1;
					chkCompress=0;
					tableEntries++;
					s1=memSize*2;
				}
				//runs after the first job and if the only available slot is at the end of memory
				else
				{
					memTable[s1][0] = jobId;
					memTable[s1][1] = jobSize;
					memTable[s1][2] = memTable[s1-1][3]+1;
					memTable[s1][3] = jobSize+memTable[s1][2]-1;
					memTable[s1][4] = memTable[s1][3]-memTable[s1][2]+1;
					memTable[s1][5] = 1;
					memTable[s1+1][0] = 0;
					memTable[s1+1][1] = 0;
					memTable[s1+1][2] = memTable[s1][3]+1;
					memTable[s1+1][3] = memSize-1;
					memTable[s1+1][4] = memSize-memTable[s1+1][2];
					memTable[s1+1][5] = -1;
					tableEntries++;
					chkCompress=0;
					s1=memSize*2;
				}
			}
			//checks for first available free block that has been deallocated
			else if(memTable[s1][4]>=jobSize && memTable[s1][5]==0)
			{
				memTable[s1][0] = jobId;
				memTable[s1][1] = jobSize;
				memTable[s1][5] = 1;
				chkCompress=0;
				s1=memSize*2;
			}
			else
			{
				s1++;
			}
		}while(s1<tableEntries);
		
		//this section runs if the job will not fit after memory compaction
		if(chkCompress==1)
		{
			chkCompress=0;
			System.out.println("put job back in queue");
			//need to return that this job needs to be returned to the queue
			System.exit(0);  
		}
		
		//if job will not fit this section will compress memory and try placing the job again
		if(s1==tableEntries && chkCompress==0)
		{
			noJobs=noJobs-1;
			compMem();
			chkCompress++;
			addJob(ID, size);
		}
	}
	
	//this method removes a job it does not check to see if the job exisits
	public void removeJob(int ID)
	{
		jobId = ID;
		s1=0;
		do
		{
			if(memTable[s1][0] == jobId)
			{
				memTable[s1][0] = 0;
				memTable[s1][1] = 0;
				memTable[s1][5] = 0;
				s1=0;
				jobId=-1;
				noJobs--;
			}
			else
			{
				s1++;
			}
		
		}while (jobId != -1 || s1<tableEntries);
		
	}
	
	//this method compacts the memory
	public void compMem()
	{
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