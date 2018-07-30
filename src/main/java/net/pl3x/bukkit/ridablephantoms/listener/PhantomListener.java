package net.pl3x.bukkit.ridablephantoms.listener;

import net.pl3x.bukkit.ridablephantoms.configuration.Lang;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PhantomListener implements Listener {
    @EventHandler
    public void onClickPhantom(PlayerInteractAtEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return; // dont fire twice
        }

        Entity clickedEntity = event.getRightClicked();
        if (clickedEntity.getType() != EntityType.PHANTOM) {
            return; // not a phantom
        }

        Phantom phantom = (Phantom) clickedEntity;
        if (!phantom.getPassengers().isEmpty()) {
            return; // phantom already has rider
        }

        Player player = event.getPlayer();
        if (player.getVehicle() != null) {
            return; // player already riding something
        }

        if (!player.hasPermission("allow.phantom.ride")) {
            Lang.send(player, Lang.RIDE_NO_PERMISSION);
            return;
        }

        phantom.addPassenger(player);
    }
}
