package world.nations.assault.data;

import java.util.ArrayList;
import java.util.List;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.Faction;

import com.massivecraft.factions.entity.FactionColl;
import lombok.Data;
import world.nations.mod.PacketListener;
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
		
		this.factionAllied = new ArrayList<>();
		
		this.points = 0;
		this.pointsTarget = 0;
	}

	public List<Faction> getFactionAllies() {
		List<Faction> factions = new ArrayList<>();

		FactionColl coll = PacketListener.getFactionColl("factions_faction@default");
		if(coll == null) return factions;
		for (Faction faction : coll.getAll()) {
			if (faction == null) return factions;
			if( faction.getRelationTo(this.faction) == null) return factions;

			if (faction.getRelationTo(this.faction) == Rel.ALLY)
				factions.add(faction);
		}

		return factions;
	}

	public List<Faction> getTargetAllies() {
		List<Faction> factions = new ArrayList<>();

		FactionColl coll = PacketListener.getFactionColl("factions_faction@default");
		if(coll == null) return factions;
		for (Faction faction : coll.getAll()) {
			if (faction == null) return factions;
			if( faction.getRelationTo(this.faction) == null) return factions;

			if (faction.getRelationTo(this.target) == Rel.ALLY)
				factions.add(faction);
		}

		return factions;
	}

	public void addPoints(int amount) {		
		this.points += amount;
	}
	
	public void addTargetPoints(int amount) {
		this.pointsTarget += amount;
	}
}
