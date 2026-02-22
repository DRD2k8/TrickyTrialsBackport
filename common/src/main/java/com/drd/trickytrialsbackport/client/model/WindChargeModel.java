package com.drd.trickytrialsbackport.client.model;

import com.drd.trickytrialsbackport.entity.WindCharge;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public class WindChargeModel extends EntityModel<WindCharge> {
    private final ModelPart bone;
    private final ModelPart wind;
    private final ModelPart windCharge;

    public WindChargeModel(ModelPart root) {
        this.bone = root.getChild("bone");
        this.wind = this.bone.getChild("wind");
        this.windCharge = this.bone.getChild("wind_charge");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition bone = root.addOrReplaceChild("bone",
                CubeListBuilder.create(),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        bone.addOrReplaceChild("wind",
                CubeListBuilder.create()
                        .texOffs(15, 20)
                        .addBox(-4.0F, -1.0F, -4.0F, 8.0F, 2.0F, 8.0F)
                        .texOffs(0, 9)
                        .addBox(-3.0F, -2.0F, -3.0F, 6.0F, 4.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        bone.addOrReplaceChild("wind_charge",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(WindCharge entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {

        float rot = ageInTicks * 16.0F * ((float)Math.PI / 180F);

        this.windCharge.yRot = -rot;
        this.wind.yRot = rot;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        bone.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
