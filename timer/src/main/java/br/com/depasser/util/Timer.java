package br.com.depasser.util;


public class Timer {
	
	public enum UNIT {
		NANO(1),
		MICRO(1000),
		MILLI(MICRO.divider * 1000),
		SECOND(MILLI.divider * 1000),
		MINUTE(SECOND.divider * 60),
		HOUR(MINUTE.divider * 60),
		DAY(HOUR.divider * 24)
		;
		
		private double divider = 0;
		private UNIT(double divider) {
			this.divider = divider;
		}
		
		public double getDivider() {
			return divider;
		}
	}
	
	protected long start, end;
	
	public Timer() {
		this(true);
	}
	
	public Timer (boolean start) {
		super();
		if (start) start();
	}
	
	public long getTotal() {
		if (!isStarted()) return 0;
		if (isRunning()) return System.nanoTime() - start;
		return end - start;
	}
	
	public double getTotal(UNIT unit) {
		return getTotal() / unit.divider;
	}
	
	public boolean isRunning () {
		return isStarted() && end == 0;
	}
	
	public boolean isStarted() {
		return start != 0;
	}
	
	public void reset() {
		start = 0;
		end = 0;
	}
	
	public void start() {
		start = System.nanoTime();
	}
	
	public void stop() {
		end = System.nanoTime();
	}
	
	public double timeInMicroseconds() {
		return getTotal(UNIT.MICRO);
	}
	
	public double timeInMilliseconds() {
		return getTotal(UNIT.MILLI);
	}
	
	public double timeInSeconds() {
		return getTotal(UNIT.SECOND);
	}

}