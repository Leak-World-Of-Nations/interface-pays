package world.nations.mod;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.FactionColls;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.entity.UPlayerColls;
import com.massivecraft.factions.event.EventFactionsCreate;
import com.massivecraft.factions.event.EventFactionsMembershipChange;
import com.massivecraft.factions.event.EventFactionsMembershipChange.MembershipChangeReason;
import com.massivecraft.massivecore.store.MStore;

public class PacketListener implements PluginMessageListener {

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
    	ByteArrayDataOutput buf = ByteStreams.newDataOutput();
		ByteArrayInputStream stream = new ByteArrayInputStream(buf.toByteArray());
		FactionColl coll = getFactionColl("factions_faction@default");

	    if(channel.equals("getFList"))
	    	PacketFunctions.getFactionList(in, coll, buf, player);
	    if(channel.equals("getOPlayers"))
	    	PacketFunctions.getOnlinePlayersDatas(buf, player);
	    if(channel.equals("getFInfo"))
	    	PacketFunctions.getFactionInfo(buf, coll, in.readUTF(), player);
	    if(channel.equals("getAllies"))
	    	PacketFunctions.getFactionAllies(coll, buf, in.readUTF(),player);
	    if(channel.equals("getStats"))
	    	PacketFunctions.getFactionStats(coll, buf,in.readUTF(),player);
	    if(channel.equals("getFExchanges"))  {
	    	in.readUTF();
	    	PacketFunctions.getFactionExchanges(coll, in.readUTF(), buf, player);
	    }
	    if(channel.equals("getWars"))
	    	PacketFunctions.getFactionWars(coll, in.readUTF(), buf, player);
	    if(channel.equals("createFac")) {
	    	createFaction(in.readUTF(),UPlayer.get(player.getUniqueId()));
	    }
	}

	public static FactionColl getFactionColl(String name) {
		for(FactionColl coll : FactionColls.get().getColls()) {
			if(coll.getName().equalsIgnoreCase(name))
				return coll;
		}

		return null;
	}

	private void createFaction(String name,UPlayer usender) {
		String newName = name;

		if(usender.hasFaction()) {
			usender.sendMessage("§aVous ne pouvez pas créer de faction lorsque vous en avez déjà une");
			return;
		}
		// Verify
		FactionColl coll = FactionColls.get().get(usender);

		if (coll.isNameTaken(newName))
		{
			usender.sendMessage("that name is already in use.");
			return;
		}

		ArrayList<String> nameValidationErrors = coll.validateName(newName);
		if (nameValidationErrors.size() > 0)
		{
			usender.sendMessage(nameValidationErrors);
			return;
		}

		// Pre-Generate Id
		String factionId = MStore.createId();

		// Event

		EventFactionsCreate createEvent = new EventFactionsCreate(usender.getPlayer(), coll.getUniverse(), factionId, newName);
		createEvent.run();
		if (createEvent.isCancelled()) return;


		// Apply
		Faction lastFac = null;
		Faction faction = coll.create(factionId);
		faction.setName(newName);

		if (usender.hasFaction())
			lastFac = usender.getFaction();

		usender.setRole(Rel.LEADER);
		usender.setFaction(faction);

		if (lastFac != null) {
			if (!coll.containsEntity(lastFac))
				coll.attach(lastFac);
			lastFac.getUPlayers().add(usender);
			//lastFac.leader = usender;
		}

		EventFactionsMembershipChange joinEvent = new EventFactionsMembershipChange(usender.getPlayer(), usender, faction, MembershipChangeReason.CREATE);
		joinEvent.run();
		// NOTE: join event cannot be cancelled or you'll have an empty faction

		// Inform
		for (UPlayer follower : UPlayerColls.get().get(usender).getAllOnline())
		{
			follower.msg("%s<i> created a new faction %s", usender.describeTo(follower, true), faction.getName(follower));
		}

		usender.sendMessage("You should now: " + Factions.get().getOuterCmdFactions().cmdFactionsDescription.getUseageTemplate());

		if (MConf.get().logFactionCreate)
		{
			Factions.get().log(usender.getName()+" created a new faction: "+newName);
		}
	}
}
