package org.gcn.plinguacore.simulator.cellLike.probabilistic;

public class ThreadBarrier {
	
	private int count;
	private int maxCount;
	
		
	public ThreadBarrier(int maxCount)
	{
		if (maxCount<0)
			throw new IllegalArgumentException();
		count=maxCount;
		this.maxCount=maxCount;
	}
	
	
	
	public synchronized void sync()
	{
		count--;
		if (count<=0)
		{
			count=maxCount;
			notifyAll();
		}
		else
		{
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
