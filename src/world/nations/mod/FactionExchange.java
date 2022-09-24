package world.nations.mod;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FactionExchange {

	public int amount;
	public String sender;
	public LocalDateTime date;
}
