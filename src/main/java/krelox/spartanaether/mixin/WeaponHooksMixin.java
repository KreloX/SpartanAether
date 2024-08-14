package krelox.spartanaether.mixin;

import com.aetherteam.aether.event.hooks.AbilityHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbilityHooks.WeaponHooks.class)
public class WeaponHooksMixin {
    @Redirect(
            method = "reduceWeaponEffectiveness",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/String;startsWith(Ljava/lang/String;)Z",
                    ordinal = 1
            ),
            remap = false
    )
    private static boolean spartanaether_reduceWeaponEffectiveness(String string, String prefix) {
        return string.startsWith(prefix) || string.startsWith("item.spartanaether.");
    }
}
