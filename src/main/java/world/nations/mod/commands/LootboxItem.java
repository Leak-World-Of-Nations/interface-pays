package world.nations.mod.commands;

import org.bukkit.inventory.ItemStack;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor 
public class LootboxItem {

	private int lootboxID;
	private int percentage;
	private ItemStack itemToDrop;
	
	
}
