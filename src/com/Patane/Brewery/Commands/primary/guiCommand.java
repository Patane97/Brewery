package com.Patane.Brewery.commands.primary;

import org.bukkit.command.CommandSender;

import com.Patane.Commands.CommandInfo;
import com.Patane.Commands.PatCommand;

@CommandInfo(
	name = "gui",
	description = "GUI stuff.",
	usage = "/brewery gui",
	permission = "brewery.gui"
)
public class guiCommand extends PatCommand {
	
	@Override
	public boolean execute(CommandSender sender, String[] args, Object... objects) {
//		Player player = (Player) sender;
//		BrItem item = Brewery.getItemCollection().getItem(args[0]);
//		GUIInstance gui = new GUIInstance(item.guiPage(), player);
//		GUIHandler gui = new GUIHandler();
//		GUIPage page1 = new GUIPage("Page1", 3);
//		GUIPage page2 = new GUIPage("Page2", 4);	
//		GUIIcon icon = new GUIIcon(new ItemStack(Material.ACACIA_FENCE));
//		icon.addAction(GUIClick.LEFT, new GUIAction() {
//			@Override
//			public boolean execute() {
//				gui.instance().open(page2);
//				return true;
//			}
//		});
//		page1.addIcon(0, icon);
//		gui.start(page1, player);
		return true;
	}
}
