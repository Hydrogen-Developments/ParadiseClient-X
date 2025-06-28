package net.paradise_client.inject.mixin.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.paradise_client.WallPaper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RotatingCubeMapRenderer.class)
public class RotatingCubeMapRendererMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(DrawContext context, int width, int height, boolean rotate, CallbackInfo ci) {
        // Calls dynamic rendering based on the defined theme
        WallPaper.render(context, width, height);
        ci.cancel();
    }
}
