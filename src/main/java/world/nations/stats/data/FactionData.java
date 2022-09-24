package world.nations.stats.data;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class FactionData implements Comparable<FactionData> {

    private String factionName;

    private int points;
    private double scorezone;
    private int wins;
    private int loses;

    private int deaths;
    private int kills;

    private List<String> bankAcces;

    public FactionData(String factionName) {
        this.factionName = factionName;

        this.points = 0;
        this.scorezone = 0;
        this.wins = 0;
        this.loses = 0;
        this.kills = 0;
        this.deaths = 0;

        this.bankAcces = new ArrayList<>();
    }

    public void addScoreZone(double amount) {
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
            long result = this.kills / this.deaths;
            return "" + Math.round(result * 100.0) / 100.0;
        } else {
            return "N/A";
        }
    }

    @Override
    public int compareTo(FactionData factionData) {
        return Double.compare(scorezone, factionData.getScorezone());
    }
}
