package com.errorgamer2000.mcplugins.prettyhitbox.mixin.client;

import com.errorgamer2000.mcplugins.prettyhitbox.HitboxUtils;
import com.errorgamer2000.mcplugins.prettyhitbox.PrettyHitboxesConfig;
import com.errorgamer2000.mcplugins.prettyhitbox.interfaces.EntityHitboxExtension;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.render.entity.EnderDragonEntityRenderer;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EnderDragonEntityRenderer.class)
public abstract class EnderDragonEntityRendererMixin {
    @ModifyArgs(method = "appendHitboxes(Lnet/minecraft/entity/boss/dragon/EnderDragonEntity;Lcom/google/common/collect/ImmutableList$Builder;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/state/EntityHitbox;<init>(DDDDDDFFFFFF)V"))
    private void changeDragonPartColor(Args args, @Local EnderDragonPart part, @Share("alpha") LocalFloatRef alpha) {
        PrettyHitboxesConfig config = AutoConfig.getConfigHolder(PrettyHitboxesConfig.class).getConfig();
        PrettyHitboxesConfig.Color color = config.dragonPartColor;
        if (HitboxUtils.isTargeted(part) && config.differentColorWhenTargeted) {
            color = config.entityTargetedColor;
        }
        args.set(9, color.red / 255f);
        args.set(10, color.green / 255f);
        args.set(11, color.blue / 255f);
        alpha.set(color.alpha / 100f);

    }

    @ModifyArg(method = "appendHitboxes(Lnet/minecraft/entity/boss/dragon/EnderDragonEntity;Lcom/google/common/collect/ImmutableList$Builder;F)V", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList$Builder;add(Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList$Builder;", ordinal = 0))
    private <E> E storeAlpha(E element, @Share("alpha") LocalFloatRef alpha) {
        ((EntityHitboxExtension) element).pretty_hitboxes$setAlpha(alpha.get());
        return element;
    }
}
