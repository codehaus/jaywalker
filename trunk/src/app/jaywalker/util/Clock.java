package jaywalker.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Clock {

	private Map startMap = new HashMap();

	private Map stopMap = new HashMap();

	public void start(String value) {
		startMap.put(value, new Long(new Date().getTime()));
		stopMap.remove(value);
	}

	public void stop(String value) {
		stopMap.put(value, new Long(new Date().getTime()));
	}

	private long differenceFromNow(Long time) {
		return new Date().getTime() - time.longValue();
	}

	private long differenceFrom(Long stopTime, Long startTime) {
		return stopTime.longValue() - startTime.longValue();
	}

	public long check(String value) {
		if (stopMap.containsKey(value) && startMap.containsKey(value)) {
			Long stopTime = (Long) stopMap.get(value);
			Long startTime = (Long) startMap.get(value);
			return differenceFrom(stopTime, startTime);
		} else if (startMap.containsKey(value)) {
			Long time = (Long) startMap.get(value);
			return differenceFromNow(time);
		}
		return 0;
	}

	public String toString(String value) {
		return toString(value, check(value));
	}

	protected String toString(String value, long time) {
		final String[] units = { "ms", "s", "m", "h", "d" };
		final int[] offsets = { 1, 1000, 60, 60, 60, 24 };
		final String[] times = new String[units.length];
		long remainder = time;
		for (int i = 0; i < units.length; i++) {
			if (i + 1 < offsets.length && offsets[i + 1] < remainder) {
				long unitTime = remainder % offsets[i + 1];
				remainder /= offsets[i + 1];
				times[i] = createTimeAndUnitString(unitTime, units[i]);
			} else {
				long unitTime = remainder;
				times[i] = createTimeAndUnitString(unitTime, units[i]);
				break;
			}
		}
		return toString(value, times);
	}

	private String toString(final String value, final String[] times) {
		StringBuffer sb = new StringBuffer(value);
		sb.append(":");
		for (int i = times.length - 1; i >= 0; i--) {
			if (times[i] != null) {
				sb.append(times[i]);
			}
		}
		return sb.toString();
	}

	private String createTimeAndUnitString(long time, final String unit) {
		return " " + time + unit;
	}

	public Object watch(String value, ClockSubject subject) {
		try {
			start(value);
			return subject.watch();
		} finally {
			stop(value);
			System.out.println(toString(value));
		}
	}

}
