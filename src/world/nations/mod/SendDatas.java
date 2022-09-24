package world.nations.mod;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.bukkit.entity.Player;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;

import world.nations.Core;
import world.nations.assault.AssaultManager;

public class SendDatas {


	public static void sendAssaultDatas(Faction assaultFac,Faction defendFac,int scoreAssault,int scoreDefend,float timer,List<String> allies,Player sender) {
		ByteArrayDataOutput buf = ByteStreams.newDataOutput();

		ByteArrayInputStream stream = new ByteArrayInputStream(buf.toByteArray());
		FactionColl coll = PacketListener.getFactionColl("factions_faction@default");

		AssaultManager manager = Core.plugin.getAssaultManager();

		int assaultAllies = 0;
		int defendAllies = 0;
		for(String facName : allies) {
			if(coll.getByName(facName).getRelationTo(assaultFac) == Rel.ALLY)
				assaultAllies++;
			if(coll.getByName(facName).getRelationTo(defendFac) == Rel.ALLY)
				defendAllies++;
		}

		buf.writeUTF(assaultFac.getName() + (assaultAllies > 0 ? " +" + assaultAllies : "" ));
		buf.writeUTF(defendFac.getName() + (defendAllies > 0 ? " +" + defendAllies : "" ));

		buf.writeInt(scoreAssault); // Score
		buf.writeInt(scoreDefend);
		buf.writeFloat(timer); // Time Restant

		buf.writeInt(assaultAllies);
		buf.writeInt(defendAllies);
		
		buf.writeBoolean(timer <= 0);


		buf.writeInt(assaultFac.getOnlinePlayers().size());
		for(Player player : assaultFac.getOnlinePlayers())
			buf.writeUTF(player.getUniqueId().toString());

		buf.writeInt(defendFac.getOnlinePlayers().size());
		for(Player player : defendFac.getOnlinePlayers())
			buf.writeUTF(player.getUniqueId().toString());

		for(Player p : assaultFac.getOnlinePlayers())
			p.sendPluginMessage(Core.plugin, "factionData", buf.toByteArray());
		for(Player p : defendFac.getOnlinePlayers())
			p.sendPluginMessage(Core.plugin, "factionData", buf.toByteArray());
	}
	
	
}
