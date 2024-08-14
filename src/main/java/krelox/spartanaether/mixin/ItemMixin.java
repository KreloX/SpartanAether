package krelox.spartanaether.mixin;

import com.aetherteam.aether.data.resources.registries.AetherDimensions;
import com.oblivioussp.spartanweaponry.util.WeaponType;
import krelox.spartanaether.SpartanAether;
import krelox.spartantoolkit.IBetterWeaponTrait;
import krelox.spartantoolkit.WeaponItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.extensions.IForgeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Item.class)
public abstract class ItemMixin implements IForgeItem {
    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity itemEntity) {
        if (this instanceof WeaponItem weapon
                && weapon.getMaterial().getBonusTraits(WeaponType.MELEE).contains(SpartanAether.FLOATING.get())
                && ((IBetterWeaponTrait) SpartanAether.FLOATING.get()).isEnabled(weapon.getMaterial(), stack)) {
            spartanAether_floating(itemEntity);
        }
        return IForgeItem.super.onEntityItemUpdate(stack, itemEntity);
    }

    @Unique
    private void spartanAether_floating(ItemEntity itemEntity) {
        itemEntity.setNoGravity(false);
        boolean noGravity = false;
        if (itemEntity.level().dimension() == AetherDimensions.AETHER_LEVEL && !itemEntity.isInFluidType() && !itemEntity.onGround()) {
            for (int i = 1; i <= itemEntity.getOnPos().getY(); ++i) {
                BlockPos blockpos = itemEntity.blockPosition().below(i);
                if (itemEntity.level().getBlockState(blockpos).blocksMotion()) {
                    noGravity = false;
                    break;
                }

                if (i == itemEntity.getOnPos().getY()) {
                    noGravity = true;
                }
            }

            if (noGravity) {
                this.spartanAether$setFloatingMovement(itemEntity);
            } else {
                this.spartanAether$setLowerGravityMovement(itemEntity);
            }
        }

    }

    @Unique
    private void spartanAether$setLowerGravityMovement(ItemEntity itemEntity) {
        itemEntity.setDeltaMovement(itemEntity.getDeltaMovement().add(0.0, 0.035, 0.0));
    }

    @Unique
    private void spartanAether$setFloatingMovement(ItemEntity itemEntity) {
        Vec3 vec3 = itemEntity.getDeltaMovement();
        itemEntity.setDeltaMovement(vec3.x * 0.95, vec3.y * 0.95, vec3.z * 0.95);
        double sqrt = Math.sqrt(itemEntity.getDeltaMovement().x * itemEntity.getDeltaMovement().x + itemEntity.getDeltaMovement().y * itemEntity.getDeltaMovement().y + itemEntity.getDeltaMovement().z * itemEntity.getDeltaMovement().z);
        if (sqrt < 0.3) {
            itemEntity.setNoGravity(true);
        } else {
            double slow = 1.0 - Math.max(0.001, Math.min(0.999, sqrt));
            itemEntity.setDeltaMovement(vec3.x * slow, vec3.y * slow, vec3.z * slow);
        }
    }
}
