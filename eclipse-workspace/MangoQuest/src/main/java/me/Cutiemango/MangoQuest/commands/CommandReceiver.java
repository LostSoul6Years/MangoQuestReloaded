package me.Cutiemango.MangoQuest.commands;

import me.Cutiemango.MangoQuest.ConfigSettings;
import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.I18n.SupportedLanguage;
import me.Cutiemango.MangoQuest.Main;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import me.Cutiemango.MangoQuest.manager.config.QuestConfigManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandReceiver implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(Main.lockDown.get()) {
			return false;
		}
		if (!(sender instanceof Player))
			return false;
		Player p = (Player) sender;
		if (args.length == 0) {
			sendHelp(p);
			return false;
		}
		switch (args[0]) {
			case "conv":
			case "c":
				ConversationCommand.execute(p, args);
				break;
			case "quest":
			case "q":
				QuestCommand.execute(p, args);
				break;
			case "editor":
			case "e":
				QuestEditorCommand.execute(p, args);
				break;
			case "conveditor":
			case "ce":
				ConversationEditorCommand.execute(p, args);
				break;
			case "lang":
			case "language":
				if(args.length == 1) {
					QuestChatManager.info(p, I18n.locMsg(p, "Language.CurrentLanguage",I18n.getPlayerLang(p).equals(I18n.SupportedLanguage.DEFAULT)?ConfigSettings.DEFAULT_LOCALE.toString():I18n.getPlayerLang(p).toString()));
					return false;
				}
				if(args.length!=2) {
					sendHelp(p);
					return false;
				}
				if(Main.getInstance().configManager.getConfig().getConfig().get("playerLanguageEnabled") == null) {
					Main.getInstance().configManager.getConfig().set("playerLanguageEnabled", true);
					Main.getInstance().configManager.getConfig().save();
				}else if(!Main.getInstance().configManager.getConfig().getBoolean("playerLanguageEnabled")) {
					QuestChatManager.info(p, I18n.locMsg(p, "CommandInfo.LanguageDisabled"));
					return false;
				}
				
				String language = args[1];
				I18n.appendLangData(p.getUniqueId(), SupportedLanguage.valueOfSave(language));
				QuestConfigManager.getSaver().savePlayerLang(p, SupportedLanguage.valueOfSave(language));
				
				QuestChatManager.info(p,I18n.locMsg(p, "Language.Changed",SupportedLanguage.valueOfSave(language).name()));
				return false;
			default:
				sendHelp(p);
				break;
		}
		return false;
	}

	public void sendHelp(Player p) {
		QuestChatManager.info(p, I18n.locMsg(p,"CommandHelp.Title"));
		QuestChatManager.info(p, I18n.locMsg(p,"CommandHelp.Language"));
		QuestChatManager.info(p, I18n.locMsg(p,"CommandHelp.Quest"));
		QuestChatManager.info(p, I18n.locMsg(p,"CommandHelp.Editor"));
	}

}
