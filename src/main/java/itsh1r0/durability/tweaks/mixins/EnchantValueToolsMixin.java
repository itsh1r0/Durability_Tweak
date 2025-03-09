package itsh1r0.durability.tweaks.mixins;

import net.minecraft.world.item.Tiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Tiers.class)
public class EnchantValueToolsMixin {

    @Inject(method = "getEnchantmentValue", at = @At("HEAD"), cancellable = true)
    private void modifyEnchantability(CallbackInfoReturnable<Integer> cir) {
        Tiers tier = (Tiers) (Object) this;
        if (tier == Tiers.DIAMOND) {
            cir.setReturnValue(15);
        } else if (tier == Tiers.NETHERITE) {
            cir.setReturnValue(10);
        }
    }
}

