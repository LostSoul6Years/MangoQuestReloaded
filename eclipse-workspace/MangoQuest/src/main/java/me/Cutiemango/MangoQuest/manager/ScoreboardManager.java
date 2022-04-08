package me.Cutiemango.MangoQuest.manager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.data.QuestObjectProgress;
import me.Cutiemango.MangoQuest.data.QuestPlayerData;
import me.Cutiemango.MangoQuest.data.QuestProgress;
import me.Cutiemango.MangoQuest.questobject.CustomQuestObject;
import me.Cutiemango.MangoQuest.questobject.DecimalObject;
import me.Cutiemango.MangoQuest.questobject.NumerableObject;
import me.Cutiemango.MangoQuest.questobject.objects.QuestObjectDeliverItem;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class ScoreboardManager
{
	public static final int SCOREBOARD_TEXT_LIMIT = 40;

	public static Scoreboard update(QuestPlayerData pd) {
		Scoreboard s = pd.getScoreboard();
		Objective o = s.getObjective("mangoquest");
		if (o != null)
			o.unregister();
		o = s.registerNewObjective("mangoquest", "dummy", I18n.locMsg(pd.getPlayer(),"Scoreboard.Title"));
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		List<String> scoreList = new ArrayList<>();

		scoreList.add(I18n.locMsg(pd.getPlayer(),"Scoreboard.CurrentQuests"));

		for (QuestProgress qp : pd.getProgresses()) {
			if (!qp.getQuest().getSettings().displayOnProgress())
				continue;
			scoreList.add(pd.getQuestDisplayFormat(qp.getQuest()));
			for (QuestObjectProgress qop : qp.getCurrentObjects()) {
				String totalScore = formatObjectDisplayText(pd.getPlayer(),qop);
				if (formatObjectDisplayText(pd.getPlayer(),qop).length() > 40) {
					if (qop.getObject() instanceof QuestObjectDeliverItem) {
						scoreList.add(formatObjectDeliverItem(pd.getPlayer(),qop)[0]);
						scoreList.add(formatObjectDeliverItem(pd.getPlayer(),qop)[1]);
						continue;
					}else {
						//avoid some strings which is more than 40 length
						int segments = (int) Math.ceil(formatObjectDisplayText(pd.getPlayer(),qop).length()/40/100);
						if(segments >= 2) {
							List<String> scores = new ArrayList<>();
							for(int i = 39;i < (segments)*40;i+=40) {
							  scores.add(totalScore.substring(i-39,i+1));							  
							}
							scoreList.addAll(scores);
							continue;
						}
					}
				}
				scoreList.add(formatObjectDisplayText(pd.getPlayer(),qop));
			}
		}
		formatScoreboard(o, scoreList);
		return s;
	}

	private static void formatScoreboard(Objective o, List<String> list) {
		int scoreIndex = 0;
		for (int i = 0; i < list.size(); i++) {
			String text = list.get(list.size() - 1 - i);
			BaseComponent[] components = TextComponent.fromLegacyText(QuestChatManager.translateColor(text));
			LinkedList<String> texts = new LinkedList<>();

			StringBuilder saved = new StringBuilder();
			for (BaseComponent comp : components) {
				String legacy = comp.toLegacyText();
				if (saved.length() + legacy.length() >= SCOREBOARD_TEXT_LIMIT) {
					texts.add(saved.toString());
					saved = new StringBuilder("    ");
				}
				saved.append(legacy);
			}
			texts.add(saved.toString());

			while (texts.size() > 0)
				o.getScore(texts.pollLast()).setScore(scoreIndex++);
		}
	}

	private static String formatObjectDisplayText(Player p,QuestObjectProgress qop) {
		if (qop.isFinished())
			return QuestChatManager.trimColor(" &8&m&o - " + ChatColor.stripColor(qop.getObject().toDisplayText()));
		else {
			if (qop.getObject() instanceof NumerableObject)
				return QuestChatManager.trimColor("&f  - " + qop.getObject().toDisplayText() + " " + I18n
						.locMsg(p,"CommandInfo.Progress", Integer.toString(qop.getProgress()),
								Integer.toString(((NumerableObject) qop.getObject()).getAmount())));
			else if (qop.getObject() instanceof DecimalObject)
				return QuestChatManager.trimColor("&f  - " + qop.getObject().toDisplayText() + " " + I18n
						.locMsg(p,"CommandInfo.Progress", Double.toString(qop.getProgressD()),
								Double.toString(((DecimalObject) qop.getObject()).getAmount())));
			else if (qop.getObject() instanceof CustomQuestObject)
				return QuestChatManager.trimColor("&f  - " + ((CustomQuestObject) qop.getObject()).getProgressText(qop));
			else
				return QuestChatManager.trimColor("&f  - " + qop.getObject().toDisplayText());
		}
	}

	private static String[] formatObjectDeliverItem(Player p,QuestObjectProgress qop) {
		QuestObjectDeliverItem obj = (QuestObjectDeliverItem) qop.getObject();
		String[] array = new String[2];
		array[0] = QuestChatManager.trimColor("&f  - " + I18n
				.locMsg(p,"Scoreboard.QuestObject.ItemToDeliver", Integer.toString(obj.getAmount()), QuestUtil.translate(obj.getItem())));
		array[1] = QuestChatManager.trimColor("    " + I18n.locMsg(p,"Scoreboard.QuestObject.TargetNPC", obj.getTargetNPC().getName()) + " " + I18n
				.locMsg(p,"CommandInfo.Progress", Integer.toString(qop.getProgress()),
						Integer.toString(((NumerableObject) qop.getObject()).getAmount())));
		if (qop.isFinished()) {
			QuestChatManager.trimColor("&8&m&o  - " + ChatColor.stripColor(array[0]));
			QuestChatManager.trimColor("&8&m&o     " + ChatColor.stripColor(array[1]));
		}
		return array;
	}
}
