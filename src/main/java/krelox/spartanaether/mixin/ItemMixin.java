package krelox.spartanaether.mixin;

import com.rolfmao.aethergearexpansion.content.FloatingItem;
import krelox.spartanaether.SpartanAether;
import krelox.spartantoolkit.WeaponItem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public abstract class ItemMixin implements FloatingItem, IForgeItem {
    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity itemEntity) {
        if (this instanceof WeaponItem weapon && weapon.getMaterial().equals(SpartanAether.AETHERITE)) {
            floating(itemEntity);
        }
        return IForgeItem.super.onEntityItemUpdate(stack, itemEntity);
    }
}
