package br.com.depasser.util;

import junit.framework.Assert;

import org.junit.Test;

import br.com.depasser.util.Timer.UNIT;

public class TimerTest {
	
	@Test
	public void testTime() throws InterruptedException {
		int toWait = 100;
		int count = 10;
		double total = 0;
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		for (int i = 0; i < count; i++) {
			Timer timer = new Timer();
			Thread.sleep(toWait);
			timer.stop();
			total += timer.getTotal(UNIT.MILLI);
			System.out.println("Timer: " + timer.getTotal(UNIT.MILLI) + ", Average: " + (total / (i + 1)));
		}
		
		System.out.println("Final Average: " + (total/count));
		int average = (int) Math.floor(total/count);
		Assert.assertTrue("Average should be less than 5ms from {toWait}", Math.abs(toWait - average) < 5);
	}

}