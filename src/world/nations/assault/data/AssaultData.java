package world.nations.assault.data;

import java.util.ArrayList;
import java.util.List;

import com.massivecraft.factions.entity.Faction;

import lombok.Data;
import world.nations.utils.timings.CountdownTimer;

@Data
public class AssaultData {
	
	private String factionName, factionTarget;
	
	private CountdownTimer timer;
	
	private Faction faction;
	private Faction target;
	
	private List<String> factionAllied;
	private int points, pointsTarget;
	
	public AssaultData(Faction faction, Faction target, CountdownTimer timer) {
		this.faction = faction;
		this.target = target;
		
		this.timer = timer;
		
		this.factionName = faction.getName();
		this.factionTarget = target.getName();
		
		this.factionAllied = new ArrayList<String>();
		
		this.points = 0;
		this.pointsTarget = 0;
	}
	
	public void addPoints(int amount) {		
		this.points += amount;
	}
	
	public void addTargetPoints(int amount) {
		this.pointsTarget += amount;
	}
}
