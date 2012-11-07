interface baseAlgorithm{
	
	void allocate(int jobID, int jobSize, int jobTime);
	void deallocate(int jobSize, int beginningLocation);
}
