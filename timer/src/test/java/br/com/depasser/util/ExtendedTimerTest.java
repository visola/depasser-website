package br.com.depasser.util;

import junit.framework.Assert;

import org.junit.Test;

import br.com.depasser.util.Timer.UNIT;

public class ExtendedTimerTest {
	
	@Test
	public void testLaps() throws InterruptedException {
		ExtendedTimer timer = new ExtendedTimer(false);
		
		int toWait = 100;
		int count = 10;
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		for (int i = 0; i < count; i++) {
			timer.startLap();
			Thread.sleep(toWait);
			timer.stopLap();
			System.out.println("Last lap: " + timer.getLastLap(UNIT.MILLI));
		}
		
		double total = 0;
		for (Long lap : timer.getLaps()) {
			total += lap;
		}
		
		total /= UNIT.MILLI.getDivider();
		
		System.out.println("Average lap: " + (total/count));
		int average = (int) Math.floor(total/count);
		Assert.assertTrue("Average should be less than 5ms from {toWait}", Math.abs(toWait - average) < 5);
		
		System.out.println("Total time: " + timer.getTotal(UNIT.MILLI));
	}

}
