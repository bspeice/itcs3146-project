/*
 *  Best Fit Algorithm by David Weber
 *  ITCS 3146
 *  11/9/2012
 */

import java.lang.reflect.Method;
import java.util.ArrayList;

public class BestFitAlgorithm implements baseAlgorithm{
    
    int memoryBlock[];
    private Job[] jobArray = new Job[memoryManagement.JOBAMOUNT+10];
    ArrayList<Integer> candidates;
    
    public BestFitAlgorithm(int memorySize)
    {
        //Initialize memory block to whatever the size is
        memoryBlock = new int[memorySize];
        System.out.println("The size of the memory is: " + memoryBlock.length);
    }
    
    public int getBestSizeIndex(int jobSize)
    {
        int bestSize; //The most suitable block size for the job
        int bestSizeIndex; //The most suitable block size starting index for the job
        
        System.out.println("The size of the job is: " + jobSize);
        
        candidates = new ArrayList<Integer>(); //Dynamically resizable array list for allocation candidates (interleaved with index and memory size)

            int counter = 0; //Counter for measuring unallocated memory
            //Scan through memory block and get free blocks. Add candidates for allocation to array list
            for(int i = 0; i < memoryBlock.length; i++)
            {
                //If position in memory block here is 0, iterate from that index and count up sequential 0's (free space)
                if(memoryBlock[i] == 0)
                {
                    for(int j = i; j < memoryBlock.length - i; j++)
                    {
                        if(memoryBlock[j] == 0)
                        {
                            counter++;
                        }
                    }
                    if(counter == jobSize)
                    {
                        candidates.add(i); //Store index
                        candidates.add(counter); //Store size of free memory chunk
                    }
                    else if(counter >= jobSize)
                    {
                        candidates.add(i); //Store index
                        candidates.add(counter); //Store size of free memory chunk
                    }
                    //System.out.println("The size of the counter is: " + counter);
                    counter = 0;
                }
            }
            for(int i = 0; i < candidates.size(); i++)
            {
                System.out.println("Candidate index: " + candidates.get(i));
                System.out.println("Candidate size: " + candidates.get(i+1));
            }
            
            //Iterate through candidate sizes
            bestSizeIndex = candidates.get(0).intValue(); //Initialize best index to first spot in array list
            
            bestSize = candidates.get(1).intValue(); //Initialize bestSize to first space size in candidate 
            
            //Iterate through sizes and find the best fit
            for(int i = 1; i < candidates.size(); i=i+2)
            {
                //Best possible case: job size = free block size (you're done)
                if(candidates.get(i).intValue() == jobSize)
                {
                    bestSizeIndex = i - 1;
                    System.out.println("The best size index is: " +  bestSizeIndex);
                    return bestSizeIndex; //You're done. Return the value.
                }
                //If the current size is less than the previous best size, make this the new best size
                else if(candidates.get(i).intValue() < bestSize)
                {
                    bestSize = candidates.get(i+1).intValue();
                    bestSizeIndex = i - 1;
                    System.out.println("The best size index is: " +  bestSizeIndex);
                }
            }
            System.out.println("The best size is: " +  bestSize);
            
        //No candidates
        if(candidates.isEmpty())
        {
            System.out.println("No best size index");
            return -1;
        }
        
        return bestSizeIndex;  
    }

    @Override
    public void allocate(int jobID, int jobSize, int jobTime)
    {
        try
        {
            Method deallocateMethod = this.getClass().getMethod("deallocate", new Class[]{int.class, int.class});

            //checks to see if the job will fit in memory
            if(jobSize>memoryBlock.length)
            {
                    System.out.println("This job is too large");
                    System.exit(0);
            }

            int bestSizeIndex = getBestSizeIndex(jobSize);

            //No candidates found
            if(bestSizeIndex == -1) 
            {
                System.out.println("No candidates found...attempting to compact");
                //Try compacting, then attempt to get an index again
                compact();

                bestSizeIndex = getBestSizeIndex(jobSize);

                //Compacting still didn't produce an appropriate block
                if(bestSizeIndex == -1)
                {
                    //TODO .....
                }
            }
            else
            {
                //Allocate the memory
                for(int i = bestSizeIndex; i < jobSize; i++)
                {
                    memoryBlock[i] = jobID;
                }
                
                System.out.println("Successfully allocated!");
                
                for(int i = 0; i < memoryBlock.length; i++)
                {
                    System.out.println("Job at position " + i + "in memoryblock: " + memoryBlock[i]);
                }
            }

        }   
        
        catch (Exception e)
        {
            System.out.println("Could not allocate job with ID " + jobID);
        }
    }
    
    public void compact()
    {
        //TODO: Compact memory if no suitable allocation candidates are found on the first pass
    }
    
    @Override
    public void deallocate(int jobSize, int beginningLocation)
    {
        for(int i = beginningLocation; i < jobSize; i++)
        {
            memoryBlock[i] = 0;
        }
    }
    
}
