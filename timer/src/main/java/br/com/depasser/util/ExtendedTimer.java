package br.com.depasser.util;

import java.util.ArrayList;
import java.util.List;

public class ExtendedTimer extends Timer {
	
	protected List<Long> laps = new ArrayList<Long>();
	protected long lapStart;
	
	public ExtendedTimer() {
		super();
	}
	
	public ExtendedTimer(boolean start) {
		super(start);
	}
	
	public Long[] getLaps() {
		return laps.toArray(new Long[laps.size()]);
	}
	
	public double getLastLap(UNIT unit) {
		return getLastLap() / unit.getDivider();
	}
	
	public double getLastLap() {
		if (laps.size() == 0) return 0;
		return laps.get(laps.size() - 1);
	}
	
	public long getTotal() {
		long total = 0;
		for (Long l : laps) {
			total += l;
		}
		return total;
	}
	
	public void reset() {
		super.reset();
		laps.clear();
		lapStart = 0;
	}
	
	public void start() {
		startLap();
	}
	
	public void startLap() {
		if (start == 0) {
			super.start();
			lapStart = start;
			return;
		}
		
		long now = System.nanoTime();
		if (lapStart != 0) {
			laps.add(now - lapStart);
		}
		lapStart = now;
	}
	
	public void stopLap() {
		if (lapStart != 0) {
			laps.add(System.nanoTime() - lapStart);
		}
		lapStart = 0;
	}
	
	public void stop() {
		super.stop();
		stopLap();
	}

}