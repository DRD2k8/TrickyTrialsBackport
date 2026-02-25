package com.drd.trickytrialsbackport.client.model;

import com.drd.trickytrialsbackport.client.animation.BreezeAnimation;
import com.drd.trickytrialsbackport.entity.monster.breeze.Breeze;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;

public class BreezeModel<T extends Breeze> extends HierarchicalModel<T> {
    private static final float WIND_TOP_SPEED = 0.6F;
    private static final float WIND_MIDDLE_SPEED = 0.8F;
    private static final float WIND_BOTTOM_SPEED = 1.0F;
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart windBody;
    private final ModelPart windTop;
    private final ModelPart windMid;
    private final ModelPart windBottom;
    private final ModelPart rods;

    public BreezeModel(ModelPart p_309507_) {
        super(RenderType::entityTranslucent);
        this.root = p_309507_;
        this.windBody = p_309507_.getChild("wind_body");
        this.windBottom = this.windBody.getChild("wind_bottom");
        this.windMid = this.windBottom.getChild("wind_mid");
        this.windTop = this.windMid.getChild("wind_top");
        this.head = p_309507_.getChild("body").getChild("head");
        this.rods = p_309507_.getChild("body").getChild("rods");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        PartDefinition $$2 = $$1.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition $$3 = $$2.addOrReplaceChild("rods", CubeListBuilder.create(), PartPose.offset(0.0F, 8.0F, 0.0F));
        $$3.addOrReplaceChild("rod_1", CubeListBuilder.create().texOffs(0, 17).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.5981F, -3.0F, 1.5F, -2.7489F, -1.0472F, 3.1416F));
        $$3.addOrReplaceChild("rod_2", CubeListBuilder.create().texOffs(0, 17).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.5981F, -3.0F, 1.5F, -2.7489F, 1.0472F, 3.1416F));
        $$3.addOrReplaceChild("rod_3", CubeListBuilder.create().texOffs(0, 17).addBox(-1.0F, 0.0F, -3.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, -3.0F, 0.3927F, 0.0F, 0.0F));
        $$2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));
        PartDefinition $$5 = $$1.addOrReplaceChild("wind_body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition $$6 = $$5.addOrReplaceChild("wind_bottom", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition $$7 = $$6.addOrReplaceChild("wind_mid", CubeListBuilder.create(), PartPose.offset(0.0F, -7.0F, 0.0F));
        $$7.addOrReplaceChild("wind_top", CubeListBuilder.create(), PartPose.offset(0.0F, -6.0F, 0.0F));
        return LayerDefinition.create($$0, 32, 32);
    }

    public static LayerDefinition createEyesLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        PartDefinition $$2 = $$1.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition $$3 = $$2.addOrReplaceChild("rods", CubeListBuilder.create(), PartPose.offset(0.0F, 8.0F, 0.0F));
        PartDefinition $$4 = $$2.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 4.0F, 0.0F));
        $$4.addOrReplaceChild("eyes", CubeListBuilder.create().texOffs(4, 24).addBox(-5.0F, -5.0F, -4.2F, 10.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(7, 16).addBox(-4.0F, -2.0F, -4.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition $$5 = $$1.addOrReplaceChild("wind_body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition $$6 = $$5.addOrReplaceChild("wind_bottom", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition $$7 = $$6.addOrReplaceChild("wind_mid", CubeListBuilder.create(), PartPose.offset(0.0F, -7.0F, 0.0F));
        $$7.addOrReplaceChild("wind_top", CubeListBuilder.create(), PartPose.offset(0.0F, -6.0F, 0.0F));
        return LayerDefinition.create($$0, 32, 32);
    }

    public static LayerDefinition createWindBodyLayer() {
        MeshDefinition $$0 = new MeshDefinition();
        PartDefinition $$1 = $$0.getRoot();
        PartDefinition $$2 = $$1.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition $$3 = $$2.addOrReplaceChild("rods", CubeListBuilder.create(), PartPose.offset(0.0F, 8.0F, 0.0F));
        $$2.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 4.0F, 0.0F));
        PartDefinition $$4 = $$1.addOrReplaceChild("wind_body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition $$5 = $$4.addOrReplaceChild("wind_bottom", CubeListBuilder.create().texOffs(1, 83).addBox(-2.5F, -7.0F, -2.5F, 5.0F, 7.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition $$6 = $$5.addOrReplaceChild("wind_mid", CubeListBuilder.create().texOffs(74, 28).addBox(-6.0F, -6.0F, -6.0F, 12.0F, 6.0F, 12.0F, new CubeDeformation(0.0F)).texOffs(78, 32).addBox(-4.0F, -6.0F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(49, 71).addBox(-2.5F, -6.0F, -2.5F, 5.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, 0.0F));
        $$6.addOrReplaceChild("wind_top", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -8.0F, -9.0F, 18.0F, 8.0F, 18.0F, new CubeDeformation(0.0F)).texOffs(6, 6).addBox(-6.0F, -8.0F, -6.0F, 12.0F, 8.0F, 12.0F, new CubeDeformation(0.0F)).texOffs(105, 57).addBox(-2.5F, -8.0F, -2.5F, 5.0F, 8.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 0.0F));
        return LayerDefinition.create($$0, 128, 128);
    }

    public void setupAnim(T p_310040_, float p_311440_, float p_313252_, float p_309514_, float p_311824_, float p_311398_) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        float $$6 = p_309514_ * 3.1415927F * -0.1F;
        this.windTop.x = Mth.cos($$6) * 1.0F * 0.6F;
        this.windTop.z = Mth.sin($$6) * 1.0F * 0.6F;
        this.windMid.x = Mth.sin($$6) * 0.5F * 0.8F;
        this.windMid.z = Mth.cos($$6) * 0.8F;
        this.windBottom.x = Mth.cos($$6) * -0.25F * 1.0F;
        this.windBottom.z = Mth.sin($$6) * -0.25F * 1.0F;
        this.head.y = 4.0F + Mth.cos($$6) / 4.0F;
        this.rods.yRot = p_309514_ * 3.1415927F * 0.1F;
        this.animate(p_310040_.shoot, BreezeAnimation.SHOOT, p_309514_);
        this.animate(p_310040_.slide, BreezeAnimation.SLIDE, p_309514_);
        this.animate(p_310040_.longJump, BreezeAnimation.JUMP, p_309514_);
    }

    public ModelPart root() {
        return this.root;
    }

    public ModelPart windTop() {
        return this.windTop;
    }

    public ModelPart windMiddle() {
        return this.windMid;
    }

    public ModelPart windBottom() {
        return this.windBottom;
    }
}
