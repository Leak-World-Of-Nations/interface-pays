package world.nations.bank.data;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class BankData {
	
	private String factionName;
	
	private double factionBank;
	
	private List<UUID> owners;
	
	public BankData(String factionName) {
		this.factionName = factionName;
		this.factionBank = 0.0D;
		this.owners = Lists.newArrayList();
	}
}
