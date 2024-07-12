package krelox.spartanaether;

import com.oblivioussp.spartanweaponry.api.WeaponTraits;
import com.rolfmao.aethergearexpansion.enums.ModItemTier;
import krelox.spartantoolkit.SpartanMaterial;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

class AetherGearExpModule {
    static TagKey<Item> INGOTS_AETHERITE = ItemTags.create(new ResourceLocation("forge", "ingots/aetherite"));

    static SpartanMaterial aetherite() {
        return SpartanAether.material(ModItemTier.AETHERITE, INGOTS_AETHERITE, WeaponTraits.FIREPROOF, SpartanAether.REACTIVE, SpartanAether.FLOATING);
    }
}
