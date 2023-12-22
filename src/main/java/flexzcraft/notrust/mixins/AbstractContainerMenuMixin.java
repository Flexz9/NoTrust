package flexzcraft.notrust.mixins;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;


//2x safeInsert
// this.setCarried(slot7.safeInsert(itemstack10, i3));
// this.setCarried(slot7.safeInsert(itemstack10, k3));
//2x tryRemove
// Optional<ItemStack> optional1 = slot7.tryRemove(j3, Integer.MAX_VALUE, p_150434_);
// Optional<ItemStack> optional = slot7.tryRemove(itemstack9.getCount(), itemstack10.getMaxStackSize() - itemstack10.getCount(), p_150434_);


@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {
    private static final Logger LOGGER = LogUtils.getLogger();
/*
    @Redirect(method = "doClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;tryRemove(IILnet/minecraft/world/entity/player/Player;)Ljava/util/Optional;") )
    public Optional<ItemStack> tryRemove(Slot slot, int p_150642_, int p_150643_, Player p_150644_) {
        LOGGER.error("tryRemove {} {} {}", p_150642_, p_150643_, p_150644_);
        return slot.tryRemove(p_150642_, p_150643_, p_150644_);
    }

    @Redirect(method = "doClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;safeInsert(Lnet/minecraft/world/item/ItemStack;I)Lnet/minecraft/world/item/ItemStack;") )
    public ItemStack safeInsert(Slot slot, ItemStack p_150657_, int p_150658_) {
        LOGGER.error("safeInsert {} {}", p_150657_, p_150658_);
        return slot.safeInsert(p_150657_, p_150658_);
    }
 */
}
