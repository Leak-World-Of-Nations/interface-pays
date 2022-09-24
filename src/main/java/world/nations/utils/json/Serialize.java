package world.nations.utils.json;

import java.lang.reflect.Type;

import net.minecraft.util.com.google.gson.Gson;
import net.minecraft.util.com.google.gson.GsonBuilder;
import world.nations.bank.data.BankData;
import world.nations.stats.data.FactionData;

public class Serialize {
	private static Gson gson = (new GsonBuilder()).setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();

	public static String serialize(Object object) {
		return gson.toJson(object);
	}

	public static <T> Object deserialize(String json, Type type) {
		return gson.fromJson(json, type);
	}
	
	public static <T> BankData[] deserializeBankData(String json){
		return gson.fromJson(json, BankData[].class);
	}
	
	public static <T> FactionData[] deserializeFactionData(String json){
		return gson.fromJson(json, FactionData[].class);
	}
}
