/*
  This file is licensed under the MIT License!
  https://github.com/sylvxa/sswaystones/blob/main/LICENSE
*/
package lol.sylvie.sswaystones.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.List;
import lol.sylvie.sswaystones.Waystones;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

// reference: https://github.com/FabricExtras/StructurePoolAPI/blob/1.21.1/common/src/main/java/net/fabric_extras/structure_pool/mixin/StructurePoolBasedGenerator_StructurePoolGeneratorMixin.java
@Mixin(JigsawPlacement.Placer.class)
public class JigsawPlacerMixin {
    @Unique
    boolean hasPlacedWaystone = false;

    @Unique
    private boolean isWaystoneElement(StructurePoolElement element) {
        return element instanceof SinglePoolElement singlePoolElement
                && singlePoolElement.getTemplateLocation().getNamespace().equals(Waystones.MOD_ID)
                && singlePoolElement.getTemplateLocation().getPath().endsWith("waystone");
    }

    @WrapOperation(method = "tryPlacingChildren", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/structure/pools/StructureTemplatePool;getShuffledTemplates(Lnet/minecraft/util/RandomSource;)Ljava/util/List;"))
    private List<StructurePoolElement> sswaystones$limitWaystonesInVillage(StructureTemplatePool instance,
            RandomSource randomSource, Operation<List<StructurePoolElement>> original) {
        var result = original.call(instance, randomSource);
        result.removeIf(element -> hasPlacedWaystone && isWaystoneElement(element));
        return result;
    }

    @WrapOperation(method = "tryPlacingChildren", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z"))
    private boolean sswaystones$markWaystoneInVillagePlaced(List list, Object object, Operation<Boolean> original) {
        if (object instanceof PoolElementStructurePiece piece && isWaystoneElement(piece.getElement())) {
            hasPlacedWaystone = true;
        }
        return original.call(list, object);
    }
}
