package world.nations.utils;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.DurationFormatUtils;

public class DurationFormatter {
	
	private static final long MINUTE = TimeUnit.MINUTES.toMillis(1L);
    private static final long HOUR = TimeUnit.HOURS.toMillis(1L);
    
    public static final ThreadLocal<DecimalFormat> REMAINING_SECONDS = new ThreadLocal<DecimalFormat>() {
        @Override
        protected DecimalFormat initialValue() {
            return new DecimalFormat("0.#");
        }
    };
    
    public static final ThreadLocal<DecimalFormat> REMAINING_SECONDS_TRAILING = new ThreadLocal<DecimalFormat>() {
        @Override
        protected DecimalFormat initialValue() {
            return new DecimalFormat("0.0");
        }
    };
    
    public static String getRemaining(final long millis, final boolean milliseconds) {
        return getRemaining(millis, milliseconds, true);
    }
    
    public static String getRemaining(final long duration, final boolean milliseconds, final boolean trail) {
        if (milliseconds && duration < DurationFormatter.MINUTE) {
            return String.valueOf((trail ? DurationFormatter.REMAINING_SECONDS_TRAILING : DurationFormatter.REMAINING_SECONDS).get().format(duration * 0.001)) + 's';
        }
        return DurationFormatUtils.formatDuration(duration, String.valueOf((duration >= DurationFormatter.HOUR) ? "HH:" : "") + "mm:ss");
    }
    
    public static String getFormatLongHours(long temps) {
		long totalSecs = temps / 1000L;
		return String.format("%02d heure(s) %02d minute(s) %02d seconde(s)", new Object[] { Long.valueOf(totalSecs / 3600L), Long.valueOf(totalSecs % 3600L / 60L), Long.valueOf(totalSecs % 60L) });
	}
	
	public static String getFormatLongHoursSimple(long temps) {
		long totalSecs = temps / 1000L;
		return String.format("%02d:%02d:%02d", new Object[] { Long.valueOf(totalSecs / 3600L), Long.valueOf(totalSecs % 3600L / 60L), Long.valueOf(totalSecs % 60L) });
	}
	
	public static String getFormatLongMinutes(long temps) {
		long totalSecs = temps / 1000L;
		return String.format("%02d minute(s) %02d seconde(s)", new Object[] { Long.valueOf(totalSecs % 3600L / 60L), Long.valueOf(totalSecs % 60L) });
	}
	
	public static String getFormatLongSecondes(long temps) {
		long totalSecs = temps / 1000L;
		return String.format("%02d seconde(s)", new Object[] { Long.valueOf(totalSecs % 60L) });
	}
}
