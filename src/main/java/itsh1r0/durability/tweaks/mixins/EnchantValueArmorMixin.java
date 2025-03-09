package itsh1r0.durability.tweaks.mixins;

import net.minecraft.world.item.ArmorMaterials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorMaterials.class)
public class EnchantValueArmorMixin {
    
    @Inject(method = "getEnchantmentValue", at = @At("HEAD"), cancellable = true)
    private void modifyEnchantability(CallbackInfoReturnable<Integer> cir) {
        ArmorMaterials material = (ArmorMaterials) (Object) this;
        if (material == ArmorMaterials.DIAMOND) {
            cir.setReturnValue(15);
        } else if (material == ArmorMaterials.NETHERITE) {
            cir.setReturnValue(10);
        }
    }
}
