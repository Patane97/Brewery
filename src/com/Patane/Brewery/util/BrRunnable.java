package com.Patane.Brewery.util;

import com.Patane.Brewery.Brewery;

public abstract class BrRunnable implements Runnable{
	private final int scheduleID;
	
	public BrRunnable(long delay, long period){
		scheduleID = Brewery.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(Brewery.getInstance(), this, delay, period);
	}
	
	@Override
	public abstract void run();
	
	protected void cancel(){
		Brewery.getInstance().getServer().getScheduler().cancelTask(scheduleID);
	}

}
