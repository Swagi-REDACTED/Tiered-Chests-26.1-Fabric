package me.pajic.tiered_chests.client.model;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Util;

import java.util.Set;

public class TieredChestModel extends ChestModel {
    public TieredChestModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createDoubleBodyRightLayer128() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        Set<Direction> visibleFaces = Util.allOfEnumExcept(Direction.EAST);
        root.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(64, 19).addBox(1.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F, visibleFaces), PartPose.ZERO);
        root.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(64, 0).addBox(1.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F, visibleFaces), PartPose.offset(0.0F, 9.0F, 1.0F));
        root.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(64, 0).addBox(15.0F, -2.0F, 14.0F, 1.0F, 4.0F, 1.0F, visibleFaces), PartPose.offset(0.0F, 9.0F, 1.0F));
        return LayerDefinition.create(mesh, 128, 64);
    }

    public static LayerDefinition createDoubleBodyLeftLayer128() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        Set<Direction> visibleFaces = Util.allOfEnumExcept(Direction.WEST);
        root.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 19).addBox(0.0F, 0.0F, 1.0F, 15.0F, 10.0F, 14.0F, visibleFaces), PartPose.ZERO);
        root.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 15.0F, 5.0F, 14.0F, visibleFaces), PartPose.offset(0.0F, 9.0F, 1.0F));
        root.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -2.0F, 14.0F, 1.0F, 4.0F, 1.0F, visibleFaces), PartPose.offset(0.0F, 9.0F, 1.0F));
        return LayerDefinition.create(mesh, 128, 64);
    }

    public static LayerDefinition createSingleBodyLayerFancy() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        // Swapped lid and bottom UVs as requested
        root.addOrReplaceChild("bottom", CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F), PartPose.ZERO);
        root.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 19).addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F), PartPose.offset(0.0F, 9.0F, 1.0F));
        root.addOrReplaceChild("lock", CubeListBuilder.create().texOffs(0, 0).addBox(7.0F, -2.0F, 14.0F, 2.0F, 4.0F, 1.0F), PartPose.offset(0.0F, 9.0F, 1.0F));
        return LayerDefinition.create(mesh, 64, 64);
    }
}
