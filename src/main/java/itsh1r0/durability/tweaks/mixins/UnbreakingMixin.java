package itsh1r0.durability.tweaks.mixins;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DigDurabilityEnchantment.class)
public class UnbreakingMixin {

    @Inject(method = "getMinCost", at = @At("HEAD"), cancellable = true)
    public void minCost(int level, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue( 7 * level - 3);
        cir.cancel();
    }

    @Inject(method = "getMaxCost", at = @At("HEAD"), cancellable = true)
    public void maxCost(int level, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue( 9 * level + 37);
        cir.cancel();
    }

    @Inject(method = "getMaxLevel", at = @At("HEAD"), cancellable = true)
    public void maxLevel(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(5);
        cir.cancel();
    }

    @Inject(method = "shouldIgnoreDurabilityDrop", at = @At("HEAD"), cancellable = true)
    private static void ignoreBreak(ItemStack item, int level, RandomSource RNG, CallbackInfoReturnable<Boolean> cir) {
        if (level == 0)
            cir.setReturnValue(false);
        else {
            cir.setReturnValue(RNG.nextFloat() >= (1F / (level + 0.35F)));
        }
        cir.cancel();
    }
}
