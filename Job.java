/*Job Class
 * Contains Int Size and Int Time
 */

public class Job {
	private int size,time;
	
	public Job(int s, int t){
		size = s;
		time = t;
	}
	
	public void setSize(int s){size = s;}
	public void setTime(int t){time = t;}
	
	public int getSize(){return size;}
	public int getTime(){return time;}
	
	public String toString(){
		return "("+size+","+time+")";
	}
}
