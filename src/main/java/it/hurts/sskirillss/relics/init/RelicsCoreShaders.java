package it.hurts.sskirillss.relics.init;


import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import it.hurts.sskirillss.relics.Relics;
import it.hurts.sskirillss.relics.utils.Reference;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;

@EventBusSubscriber(modid = Reference.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RelicsCoreShaders {

    public static ShaderInstance REVEAL_SHADER = null;
    public static RenderStateShard.ShaderStateShard REVEAL_SHADER_SHARD = new RenderStateShard.ShaderStateShard(()->REVEAL_SHADER);

    @SubscribeEvent
    public static void register(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(),ResourceLocation.tryBuild(Reference.MODID,"reveal_panel"), DefaultVertexFormat.POSITION_TEX),
                (inst)->{
                    REVEAL_SHADER = inst;
                });
    }

}
