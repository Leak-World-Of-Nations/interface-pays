package world.nations.utils.timer;


import lombok.Getter;
import world.nations.utils.ConfigCreator;

public abstract class Timer {

	@Getter
	protected final String name;

	protected final long defaultCooldown;

	public Timer(String name, long defaultCooldown) {
		this.name = name;
		this.defaultCooldown = defaultCooldown;
	}

	public abstract String getScoreboardPrefix();

	public final String getDisplayName() {
		return getScoreboardPrefix() + name;
	}
	
	public long getDefaultTime() {
        return defaultCooldown;
    }

	public void load(ConfigCreator config) {
	}

	public void onDisable(ConfigCreator config) {
	}
}
