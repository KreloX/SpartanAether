package krelox.spartanaether.mixin;

import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.loot.modifiers.DoubleDropsModifier;
import com.oblivioussp.spartanweaponry.util.WeaponType;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import krelox.spartanaether.SpartanAether;
import krelox.spartantoolkit.BetterWeaponTrait;
import krelox.spartantoolkit.WeaponItem;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DoubleDropsModifier.class)
public class DoubleDropsModifierMixin {
    @Redirect(
            method = "doApply",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/storage/loot/LootContext;getParamOrNull(Lnet/minecraft/world/level/storage/loot/parameters/LootContextParam;)Ljava/lang/Object;",
                    ordinal = 0
            )
    )
    @SuppressWarnings("unchecked")
    private <T> T spartanaether_doApply(LootContext context, LootContextParam<T> param) {
        var direct = context.getParamOrNull(param);
        if (direct instanceof AbstractArrow arrow) {
            return (T) arrow.getOwner();
        }
        return direct;
    }

    @Redirect(
            method = "doApply",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"
            )
    )
    private Item spartanaether_doApply(ItemStack stack, ObjectArrayList<ItemStack> lootStacks, LootContext context) {
        if (stack.getItem() instanceof WeaponItem weapon
                && weapon.getMaterial().getBonusTraits(WeaponType.MELEE).contains(SpartanAether.DOUBLE_DROPS.get())
                && ((BetterWeaponTrait) SpartanAether.DOUBLE_DROPS.get()).isEnabled(weapon.getMaterial(), stack)) {
            return AetherItems.SKYROOT_SWORD.get();
        }
        return stack.getItem();
    }
}
