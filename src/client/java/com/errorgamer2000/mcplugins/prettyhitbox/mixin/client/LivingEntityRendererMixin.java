package com.errorgamer2000.mcplugins.prettyhitbox.mixin.client;

import com.errorgamer2000.mcplugins.prettyhitbox.PrettyHitboxesConfig;
import com.errorgamer2000.mcplugins.prettyhitbox.interfaces.EntityHitboxExtension;
import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
    @ModifyArgs(method = "appendHitboxes(Lnet/minecraft/entity/LivingEntity;Lcom/google/common/collect/ImmutableList$Builder;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/state/EntityHitbox;<init>(DDDDDDFFF)V"))
    private void changeEyeHeightColor(Args args, @Share("alpha") LocalFloatRef alpha) {
        PrettyHitboxesConfig config = AutoConfig.getConfigHolder(PrettyHitboxesConfig.class).getConfig();
        PrettyHitboxesConfig.Color eyeHeightColor = config.eyeHeightColor;
        args.set(6, eyeHeightColor.red / 255f);
        args.set(7, eyeHeightColor.green / 255f);
        args.set(8, eyeHeightColor.blue / 255f);
        alpha.set(eyeHeightColor.alpha / 100f);
    }

    @WrapWithCondition(method = "appendHitboxes(Lnet/minecraft/entity/LivingEntity;Lcom/google/common/collect/ImmutableList$Builder;F)V", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList$Builder;add(Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList$Builder;"))
    private <E> boolean disableEyeHeight(ImmutableList.Builder<?> instance, E element) {
        PrettyHitboxesConfig config = AutoConfig.getConfigHolder(PrettyHitboxesConfig.class).getConfig();
        return config.showEyeHeight;
    }

    @ModifyArg(method = "appendHitboxes(Lnet/minecraft/entity/LivingEntity;Lcom/google/common/collect/ImmutableList$Builder;F)V", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList$Builder;add(Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList$Builder;", ordinal = 0))
    private <E> E storeAlpha(E element, @Share("alpha") LocalFloatRef alpha) {
        ((EntityHitboxExtension) element).pretty_hitboxes$setAlpha(alpha.get());
        return element;
    }
}
