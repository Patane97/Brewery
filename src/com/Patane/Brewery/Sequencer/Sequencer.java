package com.Patane.Brewery.Sequencer;

import java.util.HashMap;
import java.util.Iterator;

public class Sequencer {
	/**
	 * ******************* STATIC YML SECTION *******************
	 */
	private static SequencesYML yml;

	public static void setYML(SequencesYML yml) {
		Sequencer.yml = yml;
	}
	public static SequencesYML YML() {
		return yml;
	}
	/**
	 * **********************************************************
	 */
	HashMap<Runnable, Integer> sequence = new HashMap<Runnable, Integer>();
	
	public boolean run() {
//		Iterator<Runnable> runnableIterator = sequence.keySet().iterator();
//		
//		Runnable current = runnableIterator.next();
//		Brewery.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Brewery.getInstance(), arg1, arg2);
		return true;
	}
	public boolean load() {
		
		return true;
	}
	public class Sequencable implements Runnable{
		Sequencable(Iterator<Runnable> runnableIterator) {
			
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
	}
}
