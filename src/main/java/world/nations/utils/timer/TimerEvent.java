package world.nations.utils.timer;


public abstract class TimerEvent extends Timer {
	private TimerRunnable runnable;

	public TimerEvent(String name, long defaultTime) {
		super(name, defaultTime);
	}

	public boolean clearCooldown() {
		if (this.runnable != null) {
			this.runnable.cancel();
			this.runnable = null;
			return true;
		}
		return false;
	}

	public boolean isPaused() {
		if (this.runnable != null && this.runnable.isPaused()) {
			return true;
		}
		return false;
	}

	public void setPaused(boolean paused) {
		if (this.runnable != null && this.runnable.isPaused() != paused) {
			this.runnable.setPaused(paused);
		}
	}

	public long getRemaining() {
		return this.runnable == null ? 0 : this.runnable.getRemaining();
	}

	public boolean setRemaining(boolean pause) {
		return this.setRemaining(this.getDefaultTime(), false, pause);
	}

	public boolean setRemaining(long duration, boolean overwrite, boolean pause) {
		boolean hadCooldown = false;
		if (this.runnable != null) {
			if (!overwrite) {
				return false;
			}
			hadCooldown = this.runnable.getRemaining() > 0;
			this.runnable.setRemaining(duration);
			if (pause) {
				this.runnable.setPaused(true);
			}
		} else {
			this.runnable = new TimerRunnable(this, duration);
			if (pause) {
				this.runnable.setPaused(true);
			}
		}
		return !hadCooldown;
	}
}

