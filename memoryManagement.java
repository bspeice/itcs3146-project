/*Memory Management Program
 * Used to test the algorithm and print out results.
 */

import java.io.File;
//import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;

public class memoryManagement{
	public static void main(String args[])throws Exception{
		final int JOBAMOUNT = 1000;
		
		File file = new File("null");
		Scanner keyboard = new Scanner(System.in);
		Scanner fileScan;
		StringTokenizer token;
		//PrintWriter output = new PrintWriter(new File("out.txt"));
		Random rand = new Random();

		String read = null;
		int jobLength = 0, tempSize, tempTime;
		long timeStart, timeEnd;
		
		//******Add your algorithm class here******//
		//Algorithm alg = new Algorithm();
		Job[] jobs = new Job[JOBAMOUNT];
		
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
			System.out.print("Creating "+JOBAMOUNT+" random jobs...");
			jobLength = JOBAMOUNT;
			for(int i = 0; i < jobLength; i++)
				jobs[i] = new Job(rand.nextInt(1000)+1,rand.nextInt(100)+1);	//Job(size, time)
			System.out.println("complete");
		}
		else{
			System.out.print("File found, reading file...");
			fileScan = new Scanner(file);
			for(jobLength = 0; fileScan.hasNextLine() ; jobLength++){
				token = new StringTokenizer(fileScan.nextLine(),",");
				tempSize = Integer.parseInt(token.nextToken());
				tempTime = Integer.parseInt(token.nextToken());
				jobs[jobLength] = new Job(tempSize, tempTime);
			}
			fileScan.close();
			System.out.println("complete");
			System.out.println(jobLength+" jobs found on file");
		}
		
		//Send jobs to algorithm, time is calculated and printed out after completion
		System.out.print("Sending jobs to algorithm...");
		timeStart = System.currentTimeMillis();
		for(int i = 0; i < jobLength; i++){
			//alg.allocate(jobs[i]);		//Uncomment this line to get your algorithm to work
		}
		timeEnd = System.currentTimeMillis() - timeStart;
		System.out.println("complete");
		System.out.println("Elapsed time for algorithm to complete "+ jobLength+" jobs is "+timeEnd+" milliseconds");
		
		/*
		//This will be updated to print out timing information and other useful info
		System.out.print("Writing gathered data to file...");
		for(int i = 0; i < jobLength; i++)
			output.println(jobs[i]);
		System.out.println("complete");
		*/
		
		System.out.println("Completed Successfully");
		//output.close();
	}
}
