package eu.skysoup.skypvp.commands.player;


import eu.skysoup.skypvp.controller.PrefixController;
import eu.skysoup.skypvp.controller.ToggleController;
import eu.skysoup.skypvp.controller.UserController;
import eu.skysoup.skypvp.data.Data;
import eu.skysoup.skypvp.data.implementorings.Permissions;
import eu.skysoup.skypvp.data.implementorings.SettingTypes;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * JavaDoc this file!
 * Created: 07.01.2023
 *
 * @author thvf
 */
public class CommandReply implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (sender instanceof Player) {
            final Player player = (Player) sender;


            if (args.length > 0) {


                if (!Data.getReply().containsValue(player)) {
                    Data.getMessageUtil().sendMessage(player, "§cDu kannst keinem Spieler antworten.");
                    return true;
                }

                final Player target = Data.getReply().get(player);
                StringBuilder msg = new StringBuilder();

                for (int i = 0; i < args.length; i++) {
                    msg.append(args[i]).append(" ");
                }

                final UserController userController = new UserController().getUserByUUID(target.getUniqueId());


                if (!Data.getToggleController().isToggled(ToggleController.Types.MSG)) {
                    Data.getMessageUtil().sendMessage(player, "§cDu darfst aktuell keine Msg's schreiben.");
                    return true;
                }

                if (!userController.getSettingFromUser(SettingTypes.MSG)) {
                    Data.getMessageUtil().sendMessage(player, "§cDieser Spieler hat seine Msg's deaktiviert.");
                    return true;
                }


                for (String blw : Data.getChatController().getBlacklistedWords()) {
                    if (msg.toString().contains(blw.toLowerCase()) && !player.hasPermission(Permissions.ADMIN.getPermission())) {
                        Data.getMessageUtil().sendMessage(player, "§cDeine Nachricht wurde blockiert.");
                        player.playSound(player.getLocation(), Sound.ITEM_BREAK, 15, 0.1F);

                        Data.getMessageUtil().sendMessageWithCstmPrefixToTeam(PrefixController.getChatFilter(), "§c" + player.getName() + " §8(§f" + getSentWort(msg.toString()) + "§8)");
                        Data.getMessageUtil().sendMessageWithCstmPrefixToTeam(PrefixController.getChatFilter(), "§c" + player.getName() + " §8(§f" + msg + "§8)");
                        return true;
                    }


                }


                if (target != null) {
                    Data.getMessageUtil().sendMessage(player, "§7Du §8-> §f" + target.getName() + " §8┃ §7" + msg);
                    Data.getMessageUtil().sendMessage(target, "§f" + player.getName() + " §8-> §7dir §8┃ §7" + msg);


                    Data.getMessageUtil().sendMessageToAllPlayersWithSetting(SettingTypes.MSGSPY, PrefixController.getMsgSpy(), "§7" + player.getName() + " §8-> §f" + target.getName() + " §8┃ §7" + msg);

                } else {
                    Data.getMessageUtil().sendMessage(player, PrefixController.getOffline());
                }

                target.playSound(target.getLocation(), Sound.SUCCESSFUL_HIT, 10, 18.4F);

                return true;
            }
            Data.getMessageUtil().sendSyntax(player, "§8/§eReply §7<nachricht>");
        }
        return false;
    }

    private String getSentWort(String str) {

        for (String s : Data.getChatController().getBlacklistedWords()) {
            if (str.contains(s)) {
                return s;
            }
        }
        return "unknown";
    }
}
