package flexzcraft.notrust.mixins;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RandomizableContainerBlockEntity.class)
public class RandomizableContainerBlockEntityMixin {
    private static final Logger LOGGER = LogUtils.getLogger();

    /*
    @Inject(method = "setItem", at = @At("HEAD"))
    public void setItem(int p_59616_, ItemStack p_59617_, CallbackInfo ci) {
        LOGGER.error("setItem {} {}", p_59616_, p_59617_, new Exception());
    }

    @Inject(method = "removeItem", at = @At("HEAD"))
    public void removeItem(int p_59613_, int p_59614_, CallbackInfoReturnable<ItemStack> cir) {
        LOGGER.error("removeItem {} {}", p_59613_, p_59614_, new Exception());
    }
     */
}
