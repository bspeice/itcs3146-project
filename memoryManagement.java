/*Memory Management Program
 * Used to test the algorithm and print out results.
 */

import java.io.File;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;

public class memoryManagement{

	static final int JOBAMOUNT = 200;
	static final int MEMORYSIZE = 100;
	
	public static void main(String args[])throws Exception{
				
		File file = new File("null");
		Scanner keyboard = new Scanner(System.in);
		Scanner fileScan;
		StringTokenizer token;
		Random rand = new Random();

		String read = null;
		int jobLength = 0;
		long timeStart, timeEnd;
		
		//*Job Info*
		int[] id = new int[JOBAMOUNT];
		int[] size = new int[JOBAMOUNT];
		int[] time = new int[JOBAMOUNT];
		
		//******Add your algorithm class here******//
		//baseAlgorithm alg = new dummyAlgorithm(MEMORYSIZE);
		threadedAllocation Bradlee_Speice = new threadedAllocation(MEMORYSIZE);
		//FirstFit David01 = new FirstFit();
		//NextFit David02 = new NextFit();
                BestFitAlgorithm David_Weber_BestFit = new BestFitAlgorithm(MEMORYSIZE);
                WorstFitAlgorithm David_Weber_WorstFit = new WorstFitAlgorithm(MEMORYSIZE);
		
		//Gets a file name, else creates five random jobs
		do{							
			System.out.println("Type filename to load jobs from a file or just press enter for random jobs");
			read = keyboard.nextLine();
			file = new File(read + ".txt");
			if(!read.equals("") && !file.exists())
				System.out.println("File not found, try again");
		}while(!read.equals("") && !file.exists());
	
		//Create random jobs or read from the file and create jobs
		if(read.equals("")){
			System.out.print("Creating " + JOBAMOUNT + " random jobs...");
			jobLength = JOBAMOUNT;
			for(int i = 0; i < jobLength; i++){
				id[i] = i+1;
				size[i] = rand.nextInt(5)+1;
				time[i] = rand.nextInt(1000)+2001;
			}
			System.out.println("complete");
		}
		else{
			System.out.print("File found, reading file...");
			fileScan = new Scanner(file);
			for(jobLength = 0; fileScan.hasNextLine() ; jobLength++){
				token = new StringTokenizer(fileScan.nextLine(),",");
				id[jobLength] = jobLength+1;
				size[jobLength] = Integer.parseInt(token.nextToken());
				time[jobLength] = Integer.parseInt(token.nextToken());
			}
			fileScan.close();
			System.out.println("complete");
			System.out.println(jobLength+" jobs found on file");
		}
                
                
                
		
		//Send jobs to algorithm, time is calculated and printed out after completion
		System.out.print("Sending jobs to threaded allocation algorithm...");
		timeStart = System.currentTimeMillis();
		//Note that we use `jobLength - 1` to compensate for the id above
		for(int i = 0; i < jobLength - 1; i++){
			Bradlee_Speice.allocate(id[i], size[i], time[i]);
			//David01.allocate(id[i], size[i], time[i]);
			//David02.allocate(id[i], size[i], time[i]);
		}
		timeEnd = System.currentTimeMillis() - timeStart;
		System.out.println("complete");
		System.out.println("Elapsed time for threaded allocation algorithm to complete " + jobLength +                                           
              " jobs is " + timeEnd + " milliseconds");
                
		//***Best Fit (David Weber)***
                timeStart = System.currentTimeMillis();  
                for(int i = 0; i < jobLength - 1; i++){
			David_Weber_BestFit.allocate(id[i], size[i], time[i]);
		}
                
                timeEnd = System.currentTimeMillis() - timeStart;  
		System.out.println("Elapsed time for best fit allocation algorithm to complete " + jobLength + " jobs is " + timeEnd + " milliseconds");
                
                //***Worst Fit (David Weber)***
                timeStart = System.currentTimeMillis();  
                for(int i = 0; i < jobLength - 1; i++){
			David_Weber_WorstFit.allocate(id[i], size[i], time[i]);
		}
                
                timeEnd = System.currentTimeMillis() - timeStart;  
		System.out.println("Elapsed time for worst fit allocation algorithm to complete " + jobLength + " jobs is " + timeEnd + " milliseconds");
               
                //Put other algorithms here.
         
		System.out.println("Completed Successfully");
		//Forcibly close down all threads
		System.exit(0);
	}
}
