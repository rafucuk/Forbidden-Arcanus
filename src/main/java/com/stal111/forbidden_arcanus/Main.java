package com.stal111.forbidden_arcanus;

import com.stal111.forbidden_arcanus.block.CandelabraBlock;
import com.stal111.forbidden_arcanus.block.ModStandingSignBlock;
import com.stal111.forbidden_arcanus.block.ModWallSignBlock;
import com.stal111.forbidden_arcanus.block.tileentity.container.ModContainers;
import com.stal111.forbidden_arcanus.config.Config;
import com.stal111.forbidden_arcanus.init.*;
import com.stal111.forbidden_arcanus.item.ModItemGroup;
import com.stal111.forbidden_arcanus.item.block.WallFloorOrCeilingItem;
import com.stal111.forbidden_arcanus.particle.ModBreakingParticle;
import com.stal111.forbidden_arcanus.particle.SoulParticle;
import com.stal111.forbidden_arcanus.proxy.ClientProxy;
import com.stal111.forbidden_arcanus.proxy.IProxy;
import com.stal111.forbidden_arcanus.proxy.ServerProxy;
import com.stal111.forbidden_arcanus.sound.ModSounds;
import com.stal111.forbidden_arcanus.util.ModUtils;
import com.stal111.forbidden_arcanus.util.ModWoodType;
import com.stal111.forbidden_arcanus.world.gen.OreGenerator;
import com.stal111.forbidden_arcanus.world.gen.WorldGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Main.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Main {

	public static final String MOD_ID = "forbidden_arcanus";
	public static final Logger LOGGER = LogManager.getLogger(Main.MOD_ID);
	public static final ItemGroup FORBIDDEN_ARCANUS = new ModItemGroup(Main.MOD_ID);

	public static final Block EDELWOOD_SIGN = new ModStandingSignBlock(Block.Properties.from(Blocks.OAK_SIGN), ModWoodType.EDELWOOD).setRegistryName(ModUtils.location("edelwood_sign"));
	public static final Block EDELWOOD_WALL_SIGN = new ModWallSignBlock(Block.Properties.from(Blocks.OAK_WALL_SIGN).lootFrom(EDELWOOD_SIGN), ModWoodType.EDELWOOD).setRegistryName(ModUtils.location("edelwood_wall_sign"));
	public static final Block CHERRYWOOD_SIGN = new ModStandingSignBlock(Block.Properties.from(Blocks.OAK_SIGN), ModWoodType.CHERRYWOOD).setRegistryName(ModUtils.location("cherrywood_sign"));
	public static final Block CHERRYWOOD_WALL_SIGN = new ModWallSignBlock(Block.Properties.from(Blocks.OAK_WALL_SIGN).lootFrom(CHERRYWOOD_SIGN), ModWoodType.CHERRYWOOD).setRegistryName(ModUtils.location("cherrywood_wall_sign"));
	public static final Block MYSTERYWOOD_SIGN = new ModStandingSignBlock(Block.Properties.from(Blocks.OAK_SIGN), ModWoodType.MYSTERYWOOD).setRegistryName(ModUtils.location("mysterywood_sign"));
	public static final Block MYSTERYWOOD_WALL_SIGN = new ModWallSignBlock(Block.Properties.from(Blocks.OAK_WALL_SIGN).lootFrom(MYSTERYWOOD_SIGN), ModWoodType.MYSTERYWOOD).setRegistryName(ModUtils.location("mysterywood_wall_sign"));

	public static IProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

	public static Main instance;

	public Main() {
		instance = this;

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::stitchTextures);

		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);
        
        Config.loadConfig(Config.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve(Main.MOD_ID + "-client.toml").toString());
        Config.loadConfig(Config.SERVER_CONFIG, FMLPaths.CONFIGDIR.get().resolve(Main.MOD_ID + "-server.toml").toString());
	}

	private void setup(final FMLCommonSetupEvent event) {
		proxy.init();
		ModWoodType.register(ModWoodType.EDELWOOD);
		ModWoodType.register(ModWoodType.CHERRYWOOD);
		ModWoodType.register(ModWoodType.MYSTERYWOOD);

		OreGenerator.setupOreGen();
		WorldGenerator.setupWorldGen();
		ModUtils.addStrippable(ModBlocks.CHERRYWOOD_LOG.getBlock(), ModBlocks.STRIPPED_CHERRYWOOD_LOG.getBlock());
		ModUtils.addStrippable(ModBlocks.CHERRYWOOD.getBlock(), ModBlocks.STRIPPED_CHERRYWOOD.getBlock());
		ModUtils.addStrippable(ModBlocks.MYSTERYWOOD_LOG.getBlock(), ModBlocks.STRIPPED_MYSTERYWOOD_LOG.getBlock());
		ModUtils.addStrippable(ModBlocks.MYSTERYWOOD.getBlock(), ModBlocks.STRIPPED_MYSTERYWOOD.getBlock());
	}
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		for (ModBlocks block : ModBlocks.values()) {
			event.getRegistry().register(block.getBlock());
		}
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		for (ModBlocks block : ModBlocks.values()) {
			if (block.hasItem()) {
				if (block.hasSpecialItem()) {
					event.getRegistry().register(block.getItem());
				} else {
					BlockItem item = new BlockItem(block.getBlock(), ModItems.properties());
					item.setRegistryName(ModUtils.location(block.getName()));
					event.getRegistry().register(item);
				}
			} else if (block.getBlock() instanceof CandelabraBlock) {
				BlockItem item = new WallFloorOrCeilingItem(block.getBlock(), ForgeRegistries.BLOCKS.getValue(ModUtils.location("wall_" + block.getBlock().getRegistryName().getPath())), ForgeRegistries.BLOCKS.getValue(ModUtils.location("hanging_" + block.getBlock().getRegistryName().getPath())), ModItems.properties());
				item.setRegistryName(ModUtils.location(block.getName()));
				event.getRegistry().register(item);
			}
		}
		for (ModItems item : ModItems.values()) {
			event.getRegistry().register(item.getItem());
		}
	}
	
	@SubscribeEvent
	public static void registerContainers(RegistryEvent.Register<ContainerType<?>> event) {
		ModContainers.register(event);
	}

	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
		for (ModEntities entity : ModEntities.values()) {
			event.getRegistry().register(entity.getEntityType());
		}
	}
//	
//	@SubscribeEvent
//	public static void registerPotions(RegistryEvent.Register<Potion> event) {
//		ModPotions.register(event);
//	}

	@SubscribeEvent
	public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
		ModSounds.register(event);
	}

	@SubscribeEvent
	public static void registerParticleTypes(RegistryEvent.Register<ParticleType<?>> event) {
		ModParticles.register(event);
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void registerFactories(ParticleFactoryRegisterEvent event) {
		Minecraft.getInstance().particles.registerFactory(ModParticles.soul, SoulParticle.Factory::new);
		Minecraft.getInstance().particles.registerFactory(ModParticles.item_seed_bullet, new ModBreakingParticle.Factory());
	}

	@SubscribeEvent
	public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
		for (ModFeatures feature : ModFeatures.values()) {
			event.getRegistry().register(feature.getFeature());
		}
	}

	@SubscribeEvent
	public static void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
		for (ModRecipeSerializers recipe : ModRecipeSerializers.values()) {
			event.getRegistry().register(recipe.getRecipe());
		}
	}

	public void stitchTextures(TextureStitchEvent.Pre event) {
		if (event.getMap().getBasePath().equals(Atlases.SIGN_ATLAS)) {
			event.addSprite(new ResourceLocation(Main.MOD_ID, "entity/signs/edelwood"));
			event.addSprite(new ResourceLocation(Main.MOD_ID, "entity/signs/cherrywood"));
			event.addSprite(new ResourceLocation(Main.MOD_ID, "entity/signs/mysterywood"));
		}
	}
}
