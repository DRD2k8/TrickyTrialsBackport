package com.drd.trickytrialsbackport.client.model;

import com.drd.trickytrialsbackport.entity.monster.Bogged;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class BoggedModel<T extends Bogged> extends EntityModel<T> {
    private final ModelPart body;
    private final ModelPart waist;
    public final ModelPart head;
    private final ModelPart mushrooms;
    public final ModelPart hat;
    private final ModelPart rightArm;
    private final ModelPart rightItem;
    private final ModelPart leftArm;
    private final ModelPart leftItem;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public BoggedModel(ModelPart root) {
        this.body = root.getChild("body");
        this.waist = root.getChild("waist");
        this.head = root.getChild("head");
        this.mushrooms = this.head.getChild("mushrooms");
        this.hat = root.getChild("hat");
        this.rightArm = root.getChild("right_arm");
        this.rightItem = this.rightArm.getChild("right_item");
        this.leftArm = root.getChild("left_arm");
        this.leftItem = this.leftArm.getChild("left_item");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition waist = partdefinition.addOrReplaceChild("waist", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));
        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition mushrooms = head.addOrReplaceChild("mushrooms", CubeListBuilder.create(), PartPose.offset(3.0F, -7.5F, 3.0F));
        PartDefinition mushrooms_r1 = mushrooms.addOrReplaceChild("mushrooms_r1", CubeListBuilder.create().texOffs(50, 27).addBox(-3.0F, -5.0F, 0.0F, 6.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 6.5F, 0.0F, -1.5708F, 0.0F, 2.3562F));
        PartDefinition mushrooms_r2 = mushrooms.addOrReplaceChild("mushrooms_r2", CubeListBuilder.create().texOffs(50, 27).addBox(-3.0F, -5.0F, 0.0F, 6.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 6.5F, 0.0F, -1.5708F, 0.0F, 0.7854F));
        PartDefinition mushrooms_r3 = mushrooms.addOrReplaceChild("mushrooms_r3", CubeListBuilder.create().texOffs(50, 16).addBox(-3.0F, -3.5F, 0.0F, 6.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));
        PartDefinition mushrooms_r4 = mushrooms.addOrReplaceChild("mushrooms_r4", CubeListBuilder.create().texOffs(50, 16).addBox(-3.0F, -3.5F, 0.0F, 6.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));
        PartDefinition mushrooms_r5 = mushrooms.addOrReplaceChild("mushrooms_r5", CubeListBuilder.create().texOffs(50, 22).addBox(-3.0F, -2.5F, 0.0F, 6.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, -1.0F, -6.0F, 0.0F, 0.7854F, 0.0F));
        PartDefinition mushrooms_r6 = mushrooms.addOrReplaceChild("mushrooms_r6", CubeListBuilder.create().texOffs(50, 22).addBox(-3.0F, -2.5F, 0.0F, 6.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, -1.0F, -6.0F, 0.0F, -0.7854F, 0.0F));
        PartDefinition hat = partdefinition.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition rightArm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));
        PartDefinition rightItem = rightArm.addOrReplaceChild("right_item", CubeListBuilder.create(), PartPose.offset(-1.0F, 7.0F, 1.0F));
        PartDefinition leftArm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(5.0F, 2.0F, 0.0F));
        PartDefinition leftItem = leftArm.addOrReplaceChild("left_item", CubeListBuilder.create(), PartPose.offset(1.0F, 7.0F, 1.0F));
        PartDefinition rightLeg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 12.0F, 0.0F));
        PartDefinition leftLeg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(2.0F, 12.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);
        this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        this.leftArm.xRot  = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leftLeg.xRot  = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        this.mushrooms.visible = !entity.isSheared();
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        waist.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        hat.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        rightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        leftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        rightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        leftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
