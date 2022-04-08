package me.Cutiemango.MangoQuest.manager.database;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import me.Cutiemango.MangoQuest.data.QuestObjectProgress;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.questobject.DecimalObject;
import me.Cutiemango.MangoQuest.questobject.NumerableObject;
import me.Cutiemango.MangoQuest.questobject.SimpleQuestObject;

public class JSONSerializer {
	/**
	 * Serialize the object list into a json string. (so that it can be stored in
	 * the database)
	 * 
	 * @param lst list of object progress
	 * @return json string
	 */
	public static String jsonSerialize(List<QuestObjectProgress> lst) {
		JSONObject json = new JSONObject();
		for (int i = 0; i < lst.size(); i++) {
			if(lst.get(i).getObject() instanceof DecimalObject){			
				json.put(Integer.toString(i), Double.toString(lst.get(i).getProgressD()));				
			}else{
				json.put(Integer.toString(i), Integer.toString(lst.get(i).getProgress()));
			}
			json.put(i+"lastinvokedmilli",lst.get(i).getLastInvokedMilli());
		}

		return json.toJSONString();
	}

	/**
	 * Deserialize the object progress from a json string. Should be in this format:
	 * { "0": 1 "1": 2 "2": 0 }
	 *
	 * @param objs object list from the same quest
	 * @param obj  json string
	 * @return deserialized object list
	 */
	public static List<QuestObjectProgress> jsonDeserialize(List<SimpleQuestObject> objs, String obj) {
		List<QuestObjectProgress> prog = new ArrayList<>();
		JSONParser parser = new JSONParser();
		try {
			JSONObject json = (JSONObject) parser.parse(obj);
			for (int i = 0; i < json.keySet().size()/2; i++) {
				QuestObjectProgress progress = new QuestObjectProgress(objs.get(i),0);
				try {
					progress.setProgress(Integer.parseInt(json.get(Integer.toString(i)).toString()));
				}catch(NumberFormatException e) {
					progress.setProgressD(Double.parseDouble(json.get(Integer.toString(i)).toString()));
				}
				progress.setLastInvokedMilli(Long.parseLong(json.get(i+"lastinvokedmilli").toString()));				
				progress.checkIfFinished();
				prog.add(progress);				
			}
		} catch (ParseException e) {
			QuestChatManager.logCmd(Level.WARNING, "An error occured whilest decoding json.");
		}
		return prog;
	}
}
