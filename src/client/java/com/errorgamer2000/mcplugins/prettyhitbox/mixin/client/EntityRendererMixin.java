package com.errorgamer2000.mcplugins.prettyhitbox.mixin.client;

import com.errorgamer2000.mcplugins.prettyhitbox.HitboxUtils;
import com.errorgamer2000.mcplugins.prettyhitbox.PrettyHitboxesConfig;
import com.errorgamer2000.mcplugins.prettyhitbox.interfaces.EntityHitboxExtension;
import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
    // change branch logic here instead of adding this logic to disableDragonBox in order to not have position vectors
    @ModifyExpressionValue(method = "updateRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;shouldRenderHitboxes()Z"))
    private boolean disableHitboxesForCertainEntities(boolean original, Entity entity, EntityRenderState state, float tickProgress) {
        PrettyHitboxesConfig config = AutoConfig.getConfigHolder(PrettyHitboxesConfig.class).getConfig();
        if (entity instanceof ItemEntity && !config.showItemHitboxes) return false;
        if (entity instanceof ThrownItemEntity && !config.showThrowableItemHitboxes) return false;
        if (entity instanceof BoatEntity && !config.showBoatHitboxes) return false;
        if (entity instanceof PaintingEntity && !config.showPaintingHitboxes) return false;
        if (entity instanceof ItemFrameEntity && !config.showItemFrameHitboxes) return false;
        return true;
    }

    @ModifyArgs(method = "createHitbox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/state/EntityHitbox;<init>(DDDDDDFFF)V"), slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getVehicle()Lnet/minecraft/entity/Entity;")))
    private void changeBoundingBoxColor(Args args, Entity entity, float tickProgress, boolean green, @Share("alpha") LocalFloatRef alpha) {
        PrettyHitboxesConfig config = AutoConfig.getConfigHolder(PrettyHitboxesConfig.class).getConfig();
        PrettyHitboxesConfig.Color color = config.boundingBoxColor;
        if (HitboxUtils.isTargeted(entity) && config.differentColorWhenTargeted) {
            color = config.entityTargetedColor;
        } else if (entity instanceof ItemEntity) {
            color = config.itemHitboxColor;
        }
        args.set(6, color.red / 255f);
        args.set(7, color.green / 255f);
        args.set(8, color.blue / 255f);
        alpha.set(color.alpha / 100f);
    }

    @WrapWithCondition(method = "createHitbox", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList$Builder;add(Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList$Builder;", ordinal = 0))
    private <E> boolean disableDragonBox(ImmutableList.Builder<?> instance, E element, Entity entity, float tickProgress, boolean green, @Share("alpha") LocalFloatRef alpha) {
        ((EntityHitboxExtension) element).pretty_hitboxes$setAlpha(alpha.get());
        PrettyHitboxesConfig config = AutoConfig.getConfigHolder(PrettyHitboxesConfig.class).getConfig();
        return !(entity instanceof EnderDragonEntity) || !config.hideBigDragonBox;
    }

    @ModifyArgs(method = "createHitbox", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/state/EntityHitbox;<init>(DDDDDDFFF)V", ordinal = 2))
    private void changePassengerPosColor(Args args, @Share("alpha") LocalFloatRef alpha) {
        PrettyHitboxesConfig config = AutoConfig.getConfigHolder(PrettyHitboxesConfig.class).getConfig();
        PrettyHitboxesConfig.Color passengerPosColor = config.passengerPosColor;
        args.set(6, passengerPosColor.red / 255f);
        args.set(7, passengerPosColor.green / 255f);
        args.set(8, passengerPosColor.blue / 255f);
        alpha.set(passengerPosColor.alpha / 100f);
    }

    @WrapWithCondition(method = "createHitbox", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableList$Builder;add(Ljava/lang/Object;)Lcom/google/common/collect/ImmutableList$Builder;", ordinal = 1))
    private <E> boolean disablePassengerPosBox(ImmutableList.Builder<?> instance, E element, @Share("alpha") LocalFloatRef alpha) {
        ((EntityHitboxExtension) element).pretty_hitboxes$setAlpha(alpha.get());
        PrettyHitboxesConfig config = AutoConfig.getConfigHolder(PrettyHitboxesConfig.class).getConfig();
        return config.showPassengerPos;
    }
}
