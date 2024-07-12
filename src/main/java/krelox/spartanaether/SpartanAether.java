package krelox.spartanaether;

import com.aetherteam.aether.AetherTags;
import com.aetherteam.aether.item.AetherItems;
import com.aetherteam.aether.item.EquipmentUtil;
import com.aetherteam.aether.item.combat.AetherItemTiers;
import com.aetherteam.aether.item.combat.abilities.weapon.ZaniteWeapon;
import com.google.common.collect.ImmutableMultimap;
import com.oblivioussp.spartanweaponry.api.WeaponMaterial;
import com.oblivioussp.spartanweaponry.api.data.model.ModelGenerator;
import com.oblivioussp.spartanweaponry.api.trait.WeaponTrait;
import krelox.spartantoolkit.*;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@Mod(SpartanAether.MODID)
public class SpartanAether extends SpartanAddon {
    public static final String MODID = "spartanaether";

    public static final WeaponMap WEAPONS = new WeaponMap();
    public static final DeferredRegister<Item> ITEMS = itemRegister(MODID);
    public static final DeferredRegister<WeaponTrait> TRAITS = traitRegister(MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = tabRegister(MODID);

    public static final RegistryObject<Item> SKYROOT_HANDLE = ITEMS.register("skyroot_handle", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SKYROOT_POLE = ITEMS.register("skyroot_pole", () -> new Item(new Item.Properties()));

    // Traits
    public static final RegistryObject<WeaponTrait> DOUBLE_DROPS = registerTrait(TRAITS, new BetterWeaponTrait("double_drops", MODID, WeaponTrait.TraitQuality.POSITIVE)
            .setUniversal(false));
    public static final RegistryObject<WeaponTrait> PROSPECT = registerTrait(TRAITS, new BetterWeaponTrait("prospect", MODID, WeaponTrait.TraitQuality.POSITIVE) {
        @Override
        public float modifyDamageDealt(WeaponMaterial material, float baseDamage, DamageSource source, LivingEntity attacker, LivingEntity victim) {
            if (EquipmentUtil.isFullStrength(attacker)) {
                if (!victim.getType().is(AetherTags.Entities.NO_AMBROSIUM_DROPS) && victim.level().getRandom().nextInt(25) == 0) {
                    victim.spawnAtLocation(AetherItems.AMBROSIUM_SHARD.get());
                }
            }
            return super.modifyDamageDealt(material, baseDamage, source, attacker, victim);
        }
    }.setUniversal(false));
    public static final RegistryObject<WeaponTrait> ADAPTIVE = registerTrait(TRAITS, new BetterWeaponTrait("adaptive", MODID, WeaponTrait.TraitQuality.POSITIVE) {
        ItemStack stack = ItemStack.EMPTY;

        @Override
        public void onModifyAttributesMelee(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder) {
            var map = builder.build();
            double baseDamage = 0.0;
            for (var it = map.get(Attributes.ATTACK_DAMAGE).stream().iterator(); it.hasNext(); ) {
                AttributeModifier modifier = it.next();
                baseDamage += modifier.getAmount();
            }
            double boostedDamage = Math.max(EquipmentUtil.calculateZaniteBuff(stack, baseDamage) - baseDamage, 0);

            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ZaniteWeapon.DAMAGE_MODIFIER_UUID,
                    "Damage modifier", Math.round(boostedDamage), AttributeModifier.Operation.ADDITION));
        }

        @Override
        public boolean isEnabled(WeaponMaterial material, ItemStack stack) {
            this.stack = stack;
            return super.isEnabled(material, stack);
        }
    }.setMelee().setThrowing());
    public static final RegistryObject<WeaponTrait> ETHEREAL = registerTrait(TRAITS, new BetterWeaponTrait("ethereal", MODID, WeaponTrait.TraitQuality.NEUTRAL) {
        ItemStack stack = ItemStack.EMPTY;

        @Override
        public void onModifyAttributesMelee(ImmutableMultimap.Builder<Attribute, AttributeModifier> builder) {
            var map = builder.build();
            double baseDamage = 0.0;
            for (var it = map.get(Attributes.ATTACK_DAMAGE).stream().iterator(); it.hasNext(); ) {
                AttributeModifier modifier = it.next();
                baseDamage += modifier.getAmount();
            }
            double boostedDamage = baseDamage * ((double) stack.getDamageValue() / stack.getMaxDamage() + 0.5);
            boostedDamage -= baseDamage;
            builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(ZaniteWeapon.DAMAGE_MODIFIER_UUID,
                    "Damage modifier", Math.round(-boostedDamage), AttributeModifier.Operation.ADDITION));
        }

        @Override
        public boolean isEnabled(WeaponMaterial material, ItemStack stack) {
            this.stack = stack;
            return super.isEnabled(material, stack);
        }
    }.setMelee().setThrowing());
    public static final RegistryObject<WeaponTrait> UPDRAFT = registerTrait(TRAITS, new BetterWeaponTrait("updraft", MODID, WeaponTrait.TraitQuality.POSITIVE) {
        @Override
        public void onHitEntity(WeaponMaterial material, ItemStack stack, LivingEntity target, LivingEntity attacker, Entity projectile) {
            if (EquipmentUtil.isFullStrength(attacker)) {
                if (!target.getType().is(AetherTags.Entities.UNLAUNCHABLE) && (target.onGround() || target.isInFluidType())) {
                    target.push(0.0, 1.0, 0.0);
                    if (target instanceof ServerPlayer serverPlayer) {
                        serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
                    }
                }
            }
        }
    }.setMelee());

    // Materials
    public static final ArrayList<SpartanMaterial> MATERIALS = new ArrayList<>();

    public static final SpartanMaterial SKYROOT = material(AetherItemTiers.SKYROOT, AetherTags.Items.SKYROOT_REPAIRING, DOUBLE_DROPS);
    public static final SpartanMaterial HOLYSTONE = material(AetherItemTiers.HOLYSTONE, AetherTags.Items.HOLYSTONE_REPAIRING, PROSPECT);
    public static final SpartanMaterial ZANITE = material(AetherItemTiers.ZANITE, AetherTags.Items.ZANITE_REPAIRING, ADAPTIVE);
    public static final SpartanMaterial SKYJADE = ModList.get().isLoaded("deep_aether") ? DeepAetherModule.skyjade() : null;
    public static final SpartanMaterial GRAVITITE = material(AetherItemTiers.GRAVITITE, AetherTags.Items.GRAVITITE_REPAIRING, UPDRAFT);
    public static final SpartanMaterial STRATUS = ModList.get().isLoaded("deep_aether") ? DeepAetherModule.stratus() : null;

    @SuppressWarnings("unused")
    public static final RegistryObject<CreativeModeTab> SPARTAN_AETHER_TAB = registerTab(TABS, MODID,
            () -> WEAPONS.get(GRAVITITE, WeaponType.GREATSWORD).get(),
            (parameters, output) -> ITEMS.getEntries().forEach(item -> output.accept(item.get())));

    public SpartanAether() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();

        registerSpartanWeapons(ITEMS);
        ITEMS.register(bus);
        TRAITS.register(bus);
        TABS.register(bus);
    }

    static SpartanMaterial material(Enum<?> tier, TagKey<Item> repairTag, RegistryObject<WeaponTrait> trait) {
        SpartanMaterial material = new SpartanMaterial(tier.name().toLowerCase(), MODID, (Tier) tier, repairTag, trait)
                .setPlanks(AetherTags.Items.SKYROOT_STICK_CRAFTING).setStick(AetherTags.Items.SKYROOT_STICKS).setHandle(SKYROOT_HANDLE).setPole(SKYROOT_POLE);
        MATERIALS.add(material);
        return material;
    }

    @Override
    protected void addTranslations(LanguageProvider provider, Function<RegistryObject<?>, String> formatName) {
        super.addTranslations(provider, formatName);

        provider.add(SKYROOT_HANDLE.get(), "Skyroot Handle");
        provider.add(SKYROOT_POLE.get(), "Skyroot Pole");
    }

    @Override
    protected void registerModels(ItemModelProvider provider, ModelGenerator generator) {
        super.registerModels(provider, generator);

        provider.basicItem(SKYROOT_HANDLE.get());
        provider.basicItem(SKYROOT_POLE.get());
    }

    @Override
    @SuppressWarnings("DataFlowIssue")
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        super.buildCraftingRecipes(consumer);

        TriConsumer<ShapelessRecipeBuilder, Integer, TagKey<Item>> witherboneRecipe = (builder, witherboneCount, ingredient) -> builder
                .requires(Ingredient.of(AetherTags.Items.SKYROOT_STICKS), witherboneCount)
                .requires(ingredient)
                .group(ForgeRegistries.ITEMS.getKey(builder.getResult()).toString())
                .unlockedBy("has_skyroot_sticks", has(AetherTags.Items.SKYROOT_STICKS))
                .save(consumer, ForgeRegistries.ITEMS.getKey(builder.getResult()).withSuffix("_from_" + ingredient.location().getPath()));

        witherboneRecipe.accept(ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, SKYROOT_HANDLE.get()), 1, Tags.Items.STRING);
        witherboneRecipe.accept(ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, SKYROOT_HANDLE.get(), 4), 4, ItemTags.WOOL);
        witherboneRecipe.accept(ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, SKYROOT_HANDLE.get(), 4), 4, Tags.Items.LEATHER);

        witherboneRecipe.accept(ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, SKYROOT_POLE.get(), 4), 8, ItemTags.WOOL);
        witherboneRecipe.accept(ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, SKYROOT_POLE.get(), 4), 8, Tags.Items.LEATHER);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, SKYROOT_POLE.get())
                .define('|', AetherTags.Items.SKYROOT_STICKS)
                .define('#', Tags.Items.STRING)
                .pattern("| ")
                .pattern("|#")
                .pattern("| ")
                .group(SKYROOT_POLE.getId().toString())
                .unlockedBy("has_skyroot_sticks", has(AetherTags.Items.SKYROOT_STICKS))
                .save(consumer, SKYROOT_POLE.getId() + "_from_string");
    }

    @Override
    protected Map<RegistryObject<WeaponTrait>, String> getTraitDescriptions() {
        return Map.of(
                DOUBLE_DROPS, "Foes drop 2x as many items",
                PROSPECT, "4% chance of dropping an Ambrosium Shard while attacking",
                ADAPTIVE, "Gains buffs the more it is used",
                ETHEREAL, "Starts very powerful. Wears down with use",
                UPDRAFT, "Flings foes into the air"
        );
    }

    @Override
    public String modid() {
        return MODID;
    }

    @Override
    public List<SpartanMaterial> getMaterials() {
        return MATERIALS;
    }

    @Override
    public WeaponMap getWeaponMap() {
        return WEAPONS;
    }
}
