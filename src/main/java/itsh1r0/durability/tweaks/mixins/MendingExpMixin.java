package itsh1r0.durability.tweaks.mixins;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(ExperienceOrb.class)
public class MendingExpMixin {

    @Redirect(method = "playerTouch", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Player;takeXpDelay:I", opcode = Opcodes.PUTFIELD))
    private void redirectTakeXpDelay(Player player, int value) {
        player.takeXpDelay = 0;
    }

    @Inject(method = "repairPlayerItems", at = @At("HEAD"), cancellable = true)
    public void repairItem(Player player, int amount, CallbackInfoReturnable<Integer> cir) {
        if (!durabilityTweak$hasMending(player)) {
            cir.setReturnValue(amount);
            cir.cancel();
            return;
        }
        HashMap<ItemStack, Integer> selectItems = durabilityTweak$getItemList(player);
        if (!selectItems.isEmpty()) {

            List<Map.Entry<ItemStack,Integer>> entries = new ArrayList<>(selectItems.entrySet());
            int index = RandomSource.create().nextInt(selectItems.size());
            ItemStack item = entries.get(index).getKey();
            int minDMG = entries.get(index).getValue();

            int expRemain = 0, unbreakingLevel = item.getEnchantmentLevel(Enchantments.UNBREAKING);;
            RandomSource RNG = RandomSource.create();
            for (int i = amount; i > 0; i--)
                if (RNG.nextInt(unbreakingLevel+2) == 0)
                    expRemain++;
            item.setDamageValue(Mth.clamp(item.getDamageValue() - expRemain, minDMG, item.getMaxDamage()));
        }

        cir.setReturnValue(0);
        cir.cancel();
    }

    @Unique
    private boolean durabilityTweak$hasMending(Player player) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack item = player.getItemBySlot(slot);
            if (!item.isEmpty() && item.getEnchantmentLevel(Enchantments.MENDING) > 0) {
                return true;
            }
        }
        return false;
    }

    @Unique
    private HashMap<ItemStack, Integer> durabilityTweak$getItemList(Player player) {
        HashMap<ItemStack, Integer> items = new HashMap<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack item = player.getItemBySlot(slot);
            if (!item.isEmpty() && item.getEnchantmentLevel(Enchantments.MENDING) > 0) {
                int minDMG = 0, unbreakingLevel = item.getEnchantmentLevel(Enchantments.UNBREAKING);
                float dmgRatio = 9F / (6 + 4*unbreakingLevel);
                if (unbreakingLevel != 0)
                    minDMG = item.getMaxDamage() - (int) (item.getMaxDamage() * dmgRatio);
                if (item.getDamageValue() > minDMG)
                    items.put(item, minDMG);
            }
        }
        return items;
    }
}
