interface baseAlgorithm{
	
	public void allocate(int jobID, int jobSize, int jobTime);
	public void deallocate(int jobSize, int beginningLocation);
}
