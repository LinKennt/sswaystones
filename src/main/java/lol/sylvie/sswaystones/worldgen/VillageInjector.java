/*
  This file is licensed under the MIT License!
  https://github.com/sylvxa/sswaystones/blob/main/LICENSE
*/
package lol.sylvie.sswaystones.worldgen;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import lol.sylvie.sswaystones.Waystones;
import lol.sylvie.sswaystones.mixin.StructureTemplatePoolAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

// reference https://github.com/finallion/VillagersPlus/blob/1.20_multiloader/common/src/main/java/com/lion/villagersplus/util/StructurePoolAddition.java
public class VillageInjector {
    private static final ResourceKey<StructureProcessorList> EMPTY_PROCESSOR_LIST_KEY = ResourceKey
            .create(Registries.PROCESSOR_LIST, Identifier.withDefaultNamespace("empty"));

    private static final Identifier plainsPool = Identifier.withDefaultNamespace("village/plains/terminators");
    private static final Identifier desertPool = Identifier.withDefaultNamespace("village/desert/terminators");
    private static final Identifier savannaPool = Identifier.withDefaultNamespace("village/savanna/terminators");
    private static final Identifier snowyPool = Identifier.withDefaultNamespace("village/snowy/terminators");
    private static final Identifier taigaPool = Identifier.withDefaultNamespace("village/taiga/terminators");

    public static void addBuildingToPool(Registry<StructureTemplatePool> templatePoolRegistry,
            Registry<StructureProcessorList> processorListRegistry, Identifier poolId, String structureId, int weight) {
        Holder<StructureProcessorList> processorList = processorListRegistry.getOrThrow(EMPTY_PROCESSOR_LIST_KEY);
        StructureTemplatePool pool = templatePoolRegistry.getValue(poolId);
        if (pool == null)
            return;

        SinglePoolElement piece = SinglePoolElement.single(structureId, processorList)
                .apply(StructureTemplatePool.Projection.RIGID);

        for (int i = 0; i < weight; i++) {
            ((StructureTemplatePoolAccessor) pool).getTemplates().add(piece);
        }

        List<Pair<StructurePoolElement, Integer>> listOfPieceEntries = new ArrayList<>(
                ((StructureTemplatePoolAccessor) pool).getRawTemplates());
        listOfPieceEntries.add(new Pair<>(piece, weight));
        ((StructureTemplatePoolAccessor) pool).setRawTemplates(listOfPieceEntries);
    }

    private static void addTownCenter(Registry<StructureTemplatePool> templatePoolRegistry,
            Registry<StructureProcessorList> processorListRegistry, Identifier pool, String biome) {
        addBuildingToPool(templatePoolRegistry, processorListRegistry, pool,
                Waystones.id("village/" + biome + "/waystone").toString(), 4);
    }

    public static void inject(MinecraftServer server) {
        if (!Waystones.configuration.getInstance().injectVillageStructures)
            return;

        RegistryAccess.Frozen registryAccess = server.registryAccess();
        Registry<StructureTemplatePool> templatePoolRegistry = registryAccess.lookupOrThrow(Registries.TEMPLATE_POOL);
        Registry<StructureProcessorList> processorListRegistry = registryAccess
                .lookupOrThrow(Registries.PROCESSOR_LIST);

        Waystones.LOGGER.info("Injecting waystone village structures");
        addTownCenter(templatePoolRegistry, processorListRegistry, desertPool, "desert");
        addTownCenter(templatePoolRegistry, processorListRegistry, plainsPool, "plains");
        addTownCenter(templatePoolRegistry, processorListRegistry, savannaPool, "savanna");
        addTownCenter(templatePoolRegistry, processorListRegistry, snowyPool, "snowy");
        addTownCenter(templatePoolRegistry, processorListRegistry, taigaPool, "taiga");
    }
}
