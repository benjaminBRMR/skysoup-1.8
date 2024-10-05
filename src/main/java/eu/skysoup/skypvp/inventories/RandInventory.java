package eu.skysoup.skypvp.inventories;

import eu.skysoup.skypvp.data.Data;
import eu.skysoup.skypvp.utils.TempCooldown;
import eu.skysoup.skypvp.utils.builders.ItemBuilder;
import eu.skysoup.skypvp.utils.builders.ItemSkull;
import eu.skysoup.skypvp.utils.impl.PlotRandBlock;
import eu.skysoup.skypvp.utils.inventory.SingletonInventory;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;

import java.util.concurrent.atomic.AtomicInteger;

public class RandInventory extends SingletonInventory {
    public RandInventory(Player player) {
        super(InventoryTitles.RAND.getName(), Rows.CHEST_ROW_5, player);

        fillInventory(new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§r").setDataId(7));
        fillBorders(new ItemBuilder(Material.STAINED_GLASS_PANE).setName("§r").setDataId(15));
        fillCorners(new ItemBuilder(Material.STONE_BUTTON).setName("§r"));

        AtomicInteger atomicInteger = new AtomicInteger(10);

        for (PlotRandBlock plotRandBlock : Data.getPlotRandController().getPlotRandBlocks()) {

            if (atomicInteger.get() == 17 || atomicInteger.get() == 18) atomicInteger.set(19);
            if (atomicInteger.get() == 26 || atomicInteger.get() == 27) atomicInteger.set(28);
            if (atomicInteger.get() == 35 || atomicInteger.get() == 36) atomicInteger.set(37);
            if (atomicInteger.get() >= 44) return;

            final ItemBuilder item = new ItemBuilder(plotRandBlock.getRandBlock());
            item.setName("§8┃ §d§lRAND §8: §7" + plotRandBlock.getRandBlock().name().toUpperCase());
            item.lore(
                    "",
                    "§8┌ §7Status§8: " + (player.hasPermission(plotRandBlock.getPermission()) ? "§a§lVERFÜGBAR" : "§c§lNICHT VERFÜGBAR"),
                    "§8└ §7" + (!player.hasPermission(plotRandBlock.getPermission()) ? "§c§oErst mit der Berechtigung §8'§7§o" + plotRandBlock.getPermission() + "§8' §c§overfügbar!" : "§a§oDu kannst diesen Rand benutzen!"),
                    "");

            setItem(atomicInteger.get(), item, (click, type) -> {

                if (!player.hasPermission(plotRandBlock.getPermission())) {
                    player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 0.1F);
                    Data.getMessageUtil().sendMessage(player, "§cDazu hast du keine Rechte§8!");
                    return;
                }
                player.closeInventory();


                Data.getPlotSquaredUtil().getPlotPlayerIsStandingOn(player).getConnectedPlots().forEach(all -> {
                    all.setComponent("border", (item.getType() == Material.BARRIER ? Material.AIR.name() : plotRandBlock.getRandBlock().name()));
                });


                Data.getMessageUtil().sendMessage(player, "§aDu hast deinen Plot Rand erfolgreich geändert§8.");

            });

            atomicInteger.getAndIncrement();
        }


        setItem(36, ItemSkull.getSkull("§8┃ §d§lRAND §8: §7Zurück", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==",
                "§8§oKlicke, um zum",
                "§8§oHauptmenü zu gelangen."
        ), (click, type) -> {
            new MenuInventory(player).openGUI();
            player.playSound(player.getLocation(), Sound.WOOD_CLICK, 5, 0.1F);
        });
    }

    @Override
    public void onClose(@NonNull Player player, @NonNull InventoryView inventoryView) {

    }
}
