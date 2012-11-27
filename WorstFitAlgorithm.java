/*
 *  Best Fit Algorithm by David Weber
 *  ITCS 3146
 *  11/9/2012
 */

import java.lang.reflect.Method;
import java.util.*;

public class WorstFitAlgorithm implements baseAlgorithm{
    
    int memoryBlock[];
    private Job[] jobArray = new Job[memoryManagement.JOBAMOUNT+10];
    ArrayList<Integer> indices;
    ArrayList<Integer> blocks;
    int memoryLocation;
    int worstSize; //The most suitable block size for the job
    int worstSizeIndex; //The most suitable block size starting index for the job

    public WorstFitAlgorithm(int memorySize)
    {
        //Initialize memory block to whatever the size is
        memoryBlock = new int[memorySize];
        blocks = new ArrayList(); //Dynamically resizable array list for allocation candidates (interleaved with index and memory size);
        indices = new ArrayList(); //Dynamically resizable array list for allocation candidates (interleaved with index and memory size);
    }
    
    public int getWorstIndex(int jobSize)
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
        
        int worstIndex = -1;
        int wSize = -1;
        
        if(!blocks.isEmpty())
        {
            wSize = blocks.get(0).intValue();
        }
        
       
        //GET WORST INDEX
        for(int i = 0; i < blocks.size(); i++)
        {
            //Get largest possible free block to allocate to
            if(blocks.get(i).intValue() >= wSize || blocks.get(i).intValue() > -1)
            {
                worstIndex = indices.get(i).intValue();
            }

            //"Worst fit"....same size as job size
            else if(blocks.get(i).intValue() == jobSize)
            {
                //"Worst" possible fit. You're done.
                //System.out.println("Worst Case");
                worstIndex = indices.get(i).intValue();
            }
            else
            {
                if(!blocks.isEmpty())
                {
                    worstIndex = indices.get(i).intValue();
                }
            }
        }
        
        //System.out.println("bestIndex: " + bestIndex);
        //System.out.println("bSize: " + bSize);
        
        return worstIndex;
    }

    @Override
    public void allocate(int jobID, int jobSize, int jobTime)
    {  
        try
        {
            Method deallocateMethod = this.getClass().getMethod("deallocate", new Class[]{int.class, int.class});
            
            worstSizeIndex = this.getWorstIndex(jobSize);
            
            if(jobSize > memoryBlock.length)
            {
                //System.out.println("Job is too large for current memory size");
            }
            
            if(worstSizeIndex == -1)
            {
                while(worstSizeIndex == -1)
                {
                    //Compact and try again
                    System.out.println("Compacting memory...");
                    this.compact();
                    worstSizeIndex = this.getWorstIndex(jobSize);
                }
            }
            if(worstSizeIndex != -1)
            {
                 System.out.println("Phew");
                //System.out.println("The size of the job is: " + jobSize);
                //System.out.println("The worst size index is: " + worstSizeIndex);
                synchronized(memoryBlock)
                {
                    for(int i = worstSizeIndex; i < jobSize + worstSizeIndex; i++)
                    {
                        //System.out.println("Writing jobID: " + jobID + " to position " + i + " in memory block!");
                        this.memoryBlock[i] = jobID;
                    }
                }
                //System.out.println("Successfully allocated! Starting job...");
                
                Job newJob = new Job(jobTime, jobID, jobSize, worstSizeIndex, deallocateMethod, this);
        System.out.println("Job started!");
                jobArray[jobID] = newJob;
        System.out.println("The size of the job array is: " + jobArray.length);
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
        synchronized(memoryBlock)
        {
            for(int i = 0; i < takenBlocks.size(); i++)
            {
                this.memoryBlock[i] = takenBlocks.get(i).intValue();
            }
        }
        synchronized(memoryBlock)
        {
            for(int i = takenBlocks.size(); i < this.memoryBlock.length; i++)
            {
                this.memoryBlock[i] = 0;
            }
        }
        
        /*
        System.out.println("Successfully compacted!");
        
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
