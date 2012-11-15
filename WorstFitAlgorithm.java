/*
 *  Worst Fit Algorithm by David Weber
 *  ITCS 3146
 *  11/9/2012
 */

import java.util.ArrayList;

public class WorstFitAlgorithm implements baseAlgorithm{
    
    int memoryBlock[];
   
    
    public WorstFitAlgorithm(int memorySize)
    {
        //Initialize memory block to whatever the size is
        memoryBlock = new int[memorySize];
    }
    
    public int getWorstSizeIndex(int jobSize)
    {
        int worstSize = 0;
        int worstSizeIndex = 0;
        
        ArrayList<Integer> candidates = new ArrayList<Integer>(); //Dynamically resizable array list for allocation candidates (interleaved with index and memory size)

            int counter = 0; //Counter for measuring unallocated memory
            //Scan through memory block and get free blocks
            for(int i = 0; i < memoryBlock.length; i++)
            {
                //If position in memory block here is 0, iterate from that index and count up sequential 0's
                if(memoryBlock[i] == 0)
                {
                    for(int j = i; j < memoryBlock.length - i; j++)
                    {
                        if(memoryBlock[j] == 0)
                        {
                            counter++;
                        }
                    }
                    if(counter >= jobSize)
                    {
                        candidates.add(i); //Store index
                        candidates.add(counter); //Store size of free memory chunk
                    }
                    counter = 0;
                }
            }
            
            //Iterate through candidate sizes
            worstSizeIndex = 0; //Initialize best index to first spot in array list
            
            worstSize = candidates.get(1).intValue(); //Initialize bestSize to first space size in candidate 
            
            //Iterate through sizes and find the best fit
            for(int i = 1; i < candidates.size(); i=i+2)
            {
                //If the current size is greater than the previous best size, make this the new best size
                if(candidates.get(i).intValue() > worstSize)
                {
                    worstSize = candidates.get(i+1).intValue();
                    worstSizeIndex = i - 1;
                }
            }
            
        //If the best size is less than the job size, run this again
        if(candidates.isEmpty())
        {
            return -1;
        }
        
        return worstSizeIndex;  
    }

    @Override
    public void allocate(int jobID, int jobSize, int jobTime)
    {
        int worstSizeIndex = getWorstSizeIndex(jobSize);
        
        if(worstSizeIndex == -1) //No candidates found
        {
            //Try compacting, then attempt to get an index again
            compact(); 
            worstSizeIndex = getWorstSizeIndex(jobSize);
            
            //Compacting still didn't produce an appropriate block
            if(worstSizeIndex == -1)
            {
                //TODO .....
            }
        }
        
        //Allocate the memory
        for(int i = worstSizeIndex; i < jobSize; i++)
        {
            memoryBlock[i] = jobID;
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
