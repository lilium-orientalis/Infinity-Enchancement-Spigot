package com.llsoares;

import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.Objects;

public class BowShotListener implements Listener {
    @EventHandler
    public void onBowShotEvent(EntityShootBowEvent event) {
        Entity entity = event.getEntity();
        //Check if Player shot and bow has Infinity
        if (entity instanceof Player && Objects.requireNonNull(event.getBow()).containsEnchantment(Enchantment.ARROW_INFINITE)) {
            AbstractArrow arrowAbs = ((AbstractArrow) event.getProjectile());

            //Instantiate objects for a potential arrow and its potion data, and a potential spectral arrow bc it's a special snowflake
            Arrow arrow = null;
            PotionData potionData = null;
            SpectralArrow spectralArrow = null;

            //Assign the event projectile to its respective object that was just instantiated
            if ((arrowAbs instanceof Arrow)) {
                arrow = (Arrow) arrowAbs;
                //Assign potionData if it has any/is legal - normal arrows have the UNCRAFTABLE potion type
                //Why would anyone shoot actual uncraftable tipped arrows though
                if (!arrow.getBasePotionData().getType().equals(PotionType.UNCRAFTABLE)) {
                    potionData = (arrow).getBasePotionData();
                }
            } else { //Can be just else since SpectralArrow is the only other class to inherit from AbstractArrow
                spectralArrow = (SpectralArrow) arrowAbs;
            }

            //If the fired arrow was not a normal arrow, cancel the shot and spawn in a replacement of the proper class, critical status, velocity, and potion data, if applicable
            //updateInventory is necessary for the client to realize that the arrow was not removed from the Player's inventory
            if (arrow == null) { //check for spectral arrow - arrow will be null since spectralArrow was the only variable
                event.setCancelled(true);
                SpectralArrow launched = ((Player) entity).launchProjectile(spectralArrow.getClass(), spectralArrow.getVelocity());
                launched.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                launched.setCritical(spectralArrow.isCritical());
                ((Player) entity).updateInventory();
                entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 1);
            } else if (potionData != null) { //check for tipped arrow
                event.setCancelled(true);
                Arrow launched = ((Player) entity).launchProjectile(arrow.getClass(), arrow.getVelocity());
                launched.setBasePotionData(potionData);
                launched.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                launched.setCritical(arrow.isCritical());
                ((Player) entity).updateInventory();
                entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 1);
            }
        }
    }
}
