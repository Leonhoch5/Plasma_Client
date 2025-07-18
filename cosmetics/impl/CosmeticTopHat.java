package plasma.cosmetics.impl;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import plasma.cosmetics.CosmeticBase;
import plasma.cosmetics.CosmeticModelBase;
import plasma.cosmetics.CosmeticsController;

public class CosmeticTopHat extends CosmeticBase {
		
	private final ModelTopHat modelTopHat;
	private static final ResourceLocation TEXTURE = new ResourceLocation("client/cosmetics/hat.png");

	
	public CosmeticTopHat(RenderPlayer renderPlayer) {
		super(renderPlayer);
		this.modelTopHat = new ModelTopHat(renderPlayer);
	}
	@Override
	public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float HeadYaw, float headPitch, float scale) {
	    

	    if (CosmeticsController.shouldRenderTopHat(player)) {
	        GlStateManager.pushMatrix();
	        playerRenderer.bindTexture(TEXTURE);

	        if (player.isSneaking()) {
	            GL11.glTranslated(0, 0.225D, 0);
	        }
	        float[] color = CosmeticsController.getTopHatColor(player);
	        GL11.glColor3f(color[0], color[1], color[2]);
	        modelTopHat.render(player, limbSwing, limbSwingAmount, ageInTicks, HeadYaw, headPitch, scale);
	        GL11.glColor3f(1, 1, 1);
	        GL11.glPopMatrix();
	    }
	}
	
	private class ModelTopHat extends CosmeticModelBase {
		
		private ModelRenderer rim;
		private ModelRenderer pointy;
		
		public ModelTopHat(RenderPlayer player) {
			super(player);
			rim = new ModelRenderer(playerModel, 0, 0);
			rim.addBox(-5.5F, -9F, -5.5F, 11, 2, 11);
			pointy = new ModelRenderer(playerModel, 0, 13);
			pointy.addBox(-3.5F, -17F, -3.5F, 7, 8, 7);
		}
		@Override
		public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageIntTicks, float netHeadYaw, float headPitch, float scale) {
			rim.rotateAngleX = playerModel.bipedHead.rotateAngleX;
			rim.rotateAngleY = playerModel.bipedHead.rotateAngleY;
			rim.rotationPointX = 0.0F;
			rim.rotationPointY = 0.0F;
			rim.render(scale);
			
			pointy.rotateAngleX = playerModel.bipedHead.rotateAngleX;
			pointy.rotateAngleY = playerModel.bipedHead.rotateAngleY;
			pointy.rotationPointX = 0.0F;
			pointy.rotationPointY = 0.0F;
			pointy.render(scale);
		}
	}
}
