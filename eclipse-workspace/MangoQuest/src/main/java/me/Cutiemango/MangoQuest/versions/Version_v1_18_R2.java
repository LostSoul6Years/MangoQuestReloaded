package me.Cutiemango.MangoQuest.versions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_18_R2.inventory.CraftMetaBook;
import org.bukkit.craftbukkit.v1_18_R2.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.Cutiemango.MangoQuest.I18n;
import me.Cutiemango.MangoQuest.QuestUtil;
import me.Cutiemango.MangoQuest.manager.QuestChatManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.network.protocol.game.PacketPlayOutOpenBook;
import net.minecraft.network.protocol.game.PacketPlayOutWorldParticles;
import net.minecraft.world.EnumHand;
public class Version_v1_18_R2 implements VersionHandler {
	//itemstack getNBTTag from 1.18:s becomes 1.18.2 t
	@Override
	public void sendTitle(Player p, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
		CraftPlayer cp = (CraftPlayer) p;
		ClientboundSetTitlesAnimationPacket times = new ClientboundSetTitlesAnimationPacket(fadeIn, stay, fadeOut);
		
		cp.getHandle().b.a(times);
		if (title != null) {
			ClientboundSetTitleTextPacket packetTitle = new ClientboundSetTitleTextPacket(
					CraftChatMessage.fromStringOrNull(QuestChatManager.translateColor(title)));
			(cp.getHandle()).b.a(packetTitle);
		}
		if (subtitle != null) {
			ClientboundSetSubtitleTextPacket packetSubtitle = new ClientboundSetSubtitleTextPacket(
					CraftChatMessage.fromStringOrNull(QuestChatManager.translateColor(subtitle)));
			(cp.getHandle()).b.a(packetSubtitle);
		}
	}

	@Override
	public void openBook(Player p, TextComponent... texts) {
		
		ArrayList<BaseComponent[]> list = new ArrayList<>();
		for (TextComponent t : texts)
			list.add(new BaseComponent[] { t });

		ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
		CraftMetaBook meta = (CraftMetaBook) book.getItemMeta();

		meta.spigot().setPages(list.toArray(new BaseComponent[][] {}));
		meta.setAuthor("MangoQuest");
		meta.setTitle("MangoQuest");
		book.setItemMeta(meta);

		int slot = p.getInventory().getHeldItemSlot();
		ItemStack old = p.getInventory().getItem(slot);
		p.getInventory().setItem(slot, book);
		((CraftPlayer) p).getHandle().b.a(new PacketPlayOutOpenBook(EnumHand.a));
		p.getInventory().setItem(slot, old);
		
	}

	@Override
	public net.md_5.bungee.api.chat.TextComponent textFactoryConvertLocation(Player p,String name, Location loc, boolean isFinished) {
		if (loc == null)
			return new TextComponent("");

		ItemStack is = new ItemStack(Material.PAINTING);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);

		String displayMsg = I18n.locMsg(p,"QuestJourney.NPCLocDisplay", loc.getWorld().getName(),
				Integer.toString(loc.getBlockX()), Integer.toString(loc.getBlockY()),
				Integer.toString(loc.getBlockZ()));

		im.setLore(QuestUtil.createList(displayMsg));

		is.setItemMeta(im);
		TextComponent text = new TextComponent(isFinished ? QuestChatManager.finishedObjectFormat(name) : name);
		NBTTagCompound tag = new NBTTagCompound();
		//b should be the same
		CraftItemStack.asNMSCopy(is).b(tag);
		//ItemTag itemTag = ItemTag.ofNbt(CraftItemStack.asNMSCopy(is).getTag().asString());
		BaseComponent[] hoverEventComponents = new BaseComponent[] {
				new TextComponent(tag.toString()) // The only element of the hover
																					// events basecomponents is the item
																					// json
		};
		
	    //net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(is);
	    
		//text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new Item(name,1,ItemTag.ofNbt(nmsItem.s().toString()))));
	    //text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,new Item(is.getData()., 1, ItemTag.ofNbt(nmsItem.s().toString()))));
	    text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents));
		return text;
	}

	/**
	 * displayText = the real text displayed hoverItem = the hover item
	 */
	@Override
	public TextComponent textFactoryConvertItem(final ItemStack item, boolean finished) {
		
		String displayText = QuestUtil.translate(item);

		if (finished)
			displayText = QuestChatManager.finishedObjectFormat(displayText);
		else
			displayText = ChatColor.BLACK + displayText;

		TextComponent text = new TextComponent(displayText);
		if (item != null) {
			NBTTagCompound tag = new NBTTagCompound();
			//b same
			CraftItemStack.asNMSCopy(item).b(tag);
			
			
			//from s to u in 1.18.2
			if (CraftItemStack.asNMSCopy(item).u() == null) {
				//displayText = ChatColor.WHITE + ChatColor.stripColor(displayText);
				//text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new Text(displayText)));
				return text;
			}
			ItemTag itemTag = ItemTag.ofNbt(tag.toString());
			
			BaseComponent[] hoverEventComponents = new BaseComponent[] {
					new TextComponent(tag.toString()) // The only element of the hover
																						// events basecomponents is the item
																						// json
			};


			text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents));
			//text.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,hoverEventComponents));
		}
		return text;
	}

	//useless function
	@Override
	public boolean hasTag(Player p, String s) {
		return false;
		//return ((CraftPlayer) p).getHandle().af().contains(s);
	}

	@Override
	public ItemStack addGUITag(ItemStack item) {
		net.minecraft.world.item.ItemStack nmscopy = CraftItemStack.asNMSCopy(item);
		//this.u is the tag
		//r becomes s in 1.18.2                       //s to u in 1.18.2
		NBTTagCompound stag = (nmscopy.s()) ? nmscopy.u() : new NBTTagCompound();
		//setboolean = a(string x,boolean y)
		//same
		stag.a("GUIitem", true);
		//c becomes a
		//net.minecraft.world.item.ItemStack nmscopy1 = net.minecraft.world.item.ItemStack.a(stag);
		nmscopy.c(stag);
		return CraftItemStack.asBukkitCopy(nmscopy);
	}

	@Override
	public boolean hasGUITag(ItemStack item) {
		net.minecraft.world.item.ItemStack  nmscopy = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tag = (nmscopy.s()) ? nmscopy.u() : new NBTTagCompound();
		//same
		return tag.e("GUIitem");
	}

	@Override
	public void playNPCEffect(Player p, Location location) {
		location.setY(location.getY() + 2);
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(Particles.Q, false,
				(float) location.getX(), (float) location.getY(), (float) location.getZ(), 0, 0, 0, 1, 1);
		//same
		((CraftPlayer) p).getHandle().b.a(packet);
	}
}
