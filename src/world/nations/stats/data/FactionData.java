package world.nations.stats.data;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class FactionData {

	private String factionName;

	private int points;
	private int scorezone;
	private int wins;
	private int loses;

	private int deaths;
	private int kills;

	private boolean urss;
	private boolean ally;
	private boolean axe;

	private List<String> bankAcces;

	public FactionData(String factionName) {
		this.factionName = factionName;

		this.points = 0;
		this.scorezone = 0;
		this.wins = 0;
		this.loses = 0;
		this.kills = 0;
		this.deaths = 0;

		this.urss = false;
		this.ally = false;
		this.axe = false;

		this.bankAcces = new ArrayList<String>();
	}

	public void addScoreZone(int amount) {
		this.scorezone += amount;
	}

	public void addPoints(int amount) {
		this.points += amount;
	}

	public void addKill() {
		this.kills += 1;
	}

	public void addKill(int amount) {
		this.kills += amount;
	}

	public void addDeath() {
		this.deaths += 1;
	}

	public void addDeath(int amount) {
		this.deaths += amount;
	}

	public void addWin() {
		this.wins += 1;
	}

	public void addWin(int amount) {
		this.wins += amount;
	}

	public void addLose() {
		this.loses += 1;
	}

	public void addLose(int amount) {
		this.loses += amount;
	}

	public int getRatio() {
		return (this.wins - this.loses);
	}

	public List<String> getBankAccess() {
		return this.bankAcces;
	}

	public String getKDR() {
		if (this.deaths != 0 && this.kills >= 0) {
			double result = kills / deaths;
			return "" + result;
		} else {
			return "N/A";
		}
	}
}
