package net.quantrax.citybuild.utils;

import lombok.Getter;
import net.quantrax.citybuild.CityBuildPlugin;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class Chair {

    private final BukkitScheduler scheduler = Bukkit.getScheduler();

    private final CityBuildPlugin plugin;
    @Getter private final ArmorStand armorStand;

    public Chair(@NotNull CityBuildPlugin plugin, @NotNull Block block) {
        this.plugin = plugin;
        this.armorStand = block.getLocation().getWorld().spawn(block.getLocation().add(0.5D, -1.5D, 0.5D), ArmorStand.class, armorStand -> {
            armorStand.setInvulnerable(true);
            armorStand.setSilent(true);
            armorStand.setVisible(false);
            armorStand.setGravity(false);
            armorStand.setCanMove(false);
            armorStand.setCanTick(false);
            armorStand.setCollidable(false);
            armorStand.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.ADDING_OR_CHANGING);
            armorStand.addEquipmentLock(EquipmentSlot.CHEST, ArmorStand.LockType.ADDING_OR_CHANGING);
            armorStand.addEquipmentLock(EquipmentSlot.LEGS, ArmorStand.LockType.ADDING_OR_CHANGING);
            armorStand.addEquipmentLock(EquipmentSlot.FEET, ArmorStand.LockType.ADDING_OR_CHANGING);
            armorStand.addEquipmentLock(EquipmentSlot.HAND, ArmorStand.LockType.ADDING_OR_CHANGING);
            armorStand.addEquipmentLock(EquipmentSlot.OFF_HAND, ArmorStand.LockType.ADDING_OR_CHANGING);
        });

        applyGenericZeroHealth();
    }

    public void use(@NotNull Player player, @NotNull Consumer<Chair> onSitDown, @NotNull Consumer<Void> onStandUp) {
        armorStand.addPassenger(player);
        onSitDown.accept(this);

        AtomicInteger taskId = new AtomicInteger();
        taskId.set(scheduler.scheduleSyncRepeatingTask(plugin, () -> {
            if (!armorStand.getPassengers().isEmpty()) return;

            armorStand.remove();
            scheduler.cancelTask(taskId.get());
            onStandUp.accept(null);

        }, 0L, 20L));
    }

    private void applyGenericZeroHealth() {
        AttributeInstance maxHealthAttribute = armorStand.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealthAttribute == null) return;

        maxHealthAttribute.setBaseValue(0D);
        armorStand.setHealth(0D);
    }
}