package com.tterrag.registrate.providers;

import java.util.function.Function;

import com.tterrag.registrate.AbstractRegistrate;

import net.minecraft.core.Registry;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class RegistrateItemTagsProvider extends RegistrateTagsProvider<Item> {

    private final Function<TagKey<Block>, Tag.Builder> builderLookup;

    @SuppressWarnings({ "deprecation", "null" })
    public RegistrateItemTagsProvider(AbstractRegistrate<?> owner, ProviderType<RegistrateItemTagsProvider> type, String name, FabricDataGenerator generatorIn, RegistrateTagsProvider<Block> blockTags) {
        super(owner, type, name, generatorIn, Registry.ITEM);
        this.builderLookup = blockTags::getOrCreateRawBuilder;
    }

    public void copy(TagKey<Block> p_240521_1_, TagKey<Item> p_240521_2_) {
        Tag.Builder itag$builder = this.getOrCreateRawBuilder(p_240521_2_);
        Tag.Builder itag$builder1 = this.builderLookup.apply(p_240521_1_);
        itag$builder1.getEntries().forEach(itag$builder::add);
    }
}
