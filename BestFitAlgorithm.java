/*
 *  Best Fit Algorithm by David Weber
 *  ITCS 3146
 *  11/9/2012
 */

import java.lang.reflect.Method;
import java.util.*;

public class BestFitAlgorithm implements baseAlgorithm{
    
    private int memoryBlock[];
    private Job[] jobArray = new Job[memoryManagement.JOBAMOUNT+10];
    ArrayList<Integer> indices;
    ArrayList<Integer> blocks;
    int memoryLocation;
    int bestSize; //The most suitable block size for the job
    int bestSizeIndex; //The most suitable block size starting index for the job

    public BestFitAlgorithm(int memorySize)
    {
        //Initialize memory block to whatever the size is
        memoryBlock = new int[memorySize];
        blocks = new ArrayList(); //Dynamically resizable array list for allocation candidates (interleaved with index and memory size);
        indices = new ArrayList(); //Dynamically resizable array list for allocation candidates (interleaved with index and memory size);
    }
    
    public int getBestIndex(int jobSize)
    {
        memoryLocation = 0;
        
        indices.clear();
        blocks.clear();
        synchronized(memoryBlock)
        {
            while (memoryLocation < this.memoryBlock.length)
            {
                if (memoryBlock[memoryLocation] != 0){
                        memoryLocation++;
                        continue;
                }

                int beginningLoc = memoryLocation;
                int free = 0;

                while (memoryLocation < this.memoryBlock.length && memoryBlock[memoryLocation] == 0)
                {
                    memoryLocation++;
                    free++;
                }

                if (free >= jobSize){
                    //System.out.println("Found a block of size " + free + " at " + beginningLoc);
                    blocks.add(free);
                    indices.add(beginningLoc);
                }
            }
        }
        //System.out.println("Size of indices array: " + indices.size());
        //System.out.println("Size of sizes array: " + blocks.size());
        
        for(int i = 0; i < blocks.size(); i++)
        {
            //System.out.println("Index: " + indices.get(i));
            //System.out.println("Size: " + blocks.get(i));
        }
        int bSize = 1;
        int bestIndex = -1;
        if(!blocks.isEmpty())
        {
            bSize = blocks.get(0).intValue();
        }

        //GET BEST INDEX
        for(int i = 0; i < blocks.size(); i++)
        {
            //BEST CASE
            if(blocks.get(i).intValue() == jobSize)
            {
                //Best possible fit. You're done.
                //System.out.println("Best Case");
                bestIndex = indices.get(i).intValue();
            }
            else if((blocks.get(i).intValue() <= bSize && blocks.get(i).intValue() >= jobSize) || blocks.get(i).intValue() > -1)
            {
                bestIndex = indices.get(i).intValue();
            }
        }
        
        //System.out.println("bestIndex: " + bestIndex);
        //System.out.println("bSize: " + bSize);
        
        return bestIndex;
    }

    @Override
    public void allocate(int jobID, int jobSize, int jobTime)
    {  
        try
        {
            Method deallocateMethod = this.getClass().getMethod("deallocate", new Class[]{int.class, int.class});
            
            bestSizeIndex = this.getBestIndex(jobSize);
            
            if(bestSizeIndex == -1)
            {
                while(bestSizeIndex == -1)
                {
                    //Compact and try again
                    //System.out.println("Compacting memory...");
                    this.compact();
                    bestSizeIndex = this.getBestIndex(jobSize);
                }
            }
            
            if(jobSize > memoryBlock.length)
            {
                //System.out.println("Job is too large for current memory size");
            }
            
           
            if(bestSizeIndex != -1)
            {
                System.out.println("The size of the job is: " + jobSize);
                System.out.println("The best size index is: " + bestSizeIndex);
                
                synchronized(memoryBlock)
                {
                    for(int i = bestSizeIndex; i < jobSize + bestSizeIndex; i++)
                    {
                        //System.out.println("Writing jobID: " + jobID + " to position " + i + " in memory block!");
                        this.memoryBlock[i] = jobID;
                    }
                }

                //System.out.println("Successfully allocated! Starting job...");
                
                System.out.println("Job time: " + jobTime);
                
                Job newJob = new Job(jobTime, jobID, jobSize, bestSizeIndex, deallocateMethod, this);
        
                jobArray[jobID] = newJob;

                newJob.start();
                
                //System.out.println("Job started!");
            }
        }   
        catch (Exception e)
        {
            e.printStackTrace();
	    System.exit(-1);
        }
    }
    /*
     * This method gathers all occupied memory and stores it contiguously in an array list of blocks.
     * After that, it rewrites the memoryBlock array by writing the memory in contiguous order, and then
     * filling in the rest of the memory with 0's
     */
    public void compact()
    {
        ArrayList<Integer> takenBlocks = new ArrayList();
        
        memoryLocation = 0;
        
        //Gather allocated memory
        while(memoryLocation < this.memoryBlock.length && memoryBlock[memoryLocation] != 0)
        {
            takenBlocks.add(memoryBlock[memoryLocation]);
            memoryLocation++;
        }
       
        for(int i = 0; i < takenBlocks.size(); i++)
        {
            
            synchronized(memoryBlock)
            {
                this.memoryBlock[i] = takenBlocks.get(i).intValue();
            }
        }
        
        for(int i = takenBlocks.size(); i < this.memoryBlock.length; i++)
        {
            synchronized(memoryBlock)
            {
                this.memoryBlock[i] = 0;
            }
        }
        
        /*System.out.println("Successfully compacted!");
        
        if(takenBlocks.isEmpty())
        {
            System.out.println("Cannot compact!");
        }
        */
    }
    
    @Override
    public void deallocate(int jobSize, int beginningLocation)
    {
        synchronized(memoryBlock)
        {
            for(int i = beginningLocation; i < jobSize + beginningLocation; i++)
            {
                memoryBlock[i] = 0;
            }
        }
    }
    
}
