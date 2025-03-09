package itsh1r0.durability.tweaks.mixins;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ExperienceOrb.class)
public class MendingExpMixin {

    @Redirect(method = "playerTouch", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/player/Player;takeXpDelay:I", opcode = Opcodes.PUTFIELD))
    private void redirectTakeXpDelay(Player player, int value) {
        player.takeXpDelay = 0; // Force takeXpDelay to 0
    }

    @Inject(method = "repairPlayerItems", at = @At("HEAD"), cancellable = true)
    public void repairItem(Player player, int amount, CallbackInfoReturnable<Integer> cir) {
        Map.Entry<EquipmentSlot, ItemStack> checkFirst = EnchantmentHelper.getRandomItemWith(Enchantments.MENDING, player);
        if (checkFirst == null) {
            cir.setReturnValue(amount);
            cir.cancel();
            return;
        }
        Map.Entry<EquipmentSlot, ItemStack> entry = EnchantmentHelper.getRandomItemWith(Enchantments.MENDING, player, ItemStack::isDamaged);
        if (entry != null) {
            ItemStack item = entry.getValue();
            RandomSource RNG = RandomSource.create();
            int expRemain = 0, unbreakingLevel = item.getEnchantmentLevel(Enchantments.UNBREAKING);
            for (int i = amount; i > 0; i--)
                if (RNG.nextInt(unbreakingLevel + 3) == 0)
                    expRemain++;
            int minDMG = 0;
            float dmgRatio = 9F / (6 + 4*unbreakingLevel);
            if (unbreakingLevel != 0)
                minDMG = item.getMaxDamage() - (int)(item.getMaxDamage() * dmgRatio);
            if (item.getDamageValue() > minDMG)
                item.setDamageValue(Mth.clamp(item.getDamageValue() - expRemain, minDMG, item.getMaxDamage()));
        }
        cir.setReturnValue(0);
        cir.cancel();
    }
}
