# Registrate [![Build Status](https://img.shields.io/jenkins/build?jobUrl=https%3A%2F%2Fci.tterrag.com%2Fjob%2FRegistrate%2Fjob%2F1.18%2F)](https://ci.tterrag.com/job/Registrate/job/1.18) [![License](https://img.shields.io/github/license/tterrag1098/Registrate?cacheSeconds=36000)](https://www.tldrlegal.com/l/mpl-2.0) [![Maven Version](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fmaven.tterrag.com%2Fcom%2Ftterrag%2Fregistrate%2FRegistrate%2Fmaven-metadata.xml)](https://maven.tterrag.com/com/tterrag/registrate/Registrate) ![Minecraft Version](https://img.shields.io/badge/minecraft-1.18.2-blue) [![Discord](https://img.shields.io/discord/175740881389879296?label=discord&logo=discord&color=7289da)](https://discord.gg/gZqYcEj)

A powerful wrapper for creating and registering objects in your mod. 

## Why Registrate?

- Allows you to organize your mod content however you like, rather than having pieces of each object defined in scattered places
- Simple fluent API
- Open to extension, build and register custom objects and data
- Automatic data generation with sane defaults
- Shadeable, contains no mod, only code

## How to Use

First, create a `Registrate` object which will be used across your entire project.

```java
public static final Registrate REGISTRATE = Registrate.create(MOD_ID);
```

Using a constant field is not necessary, it can be passed around and thrown away after registration is setup.

If declared static in your `@Mod` class, you must create the `Registrate` object lazily so it is not created too early during loading. This can be done easily like so:

```java
public static final NonNullSupplier<Registrate> REGISTRATE = NonNullSupplier.lazy(() -> Registrate.create(MOD_ID));
```

Next, begin adding objects.

If you have a block class such as

```java
public class MyBlock extends Block {

    public MyBlock(Block.Properties properties) {
        super(properties);
    }
    
    ...
}
```

then register it like so,

```java
public static final RegistryEntry<MyBlock> MY_BLOCK = REGISTRATE.block("my_block", MyBlock::new).register();
```

Registrate will create a block, with a default simple blockstate, model, loot table, and lang entry. However, all of these facets can be configured easily to use whatever custom data you may want. Example:

```java
public static final RegistryEntry<MyStairsBlock> MY_STAIRS = REGISTRATE.block("my_block", MyStairsBlock::new)
            .defaultItem()
            .tag(BlockTags.STAIRS)
            .blockstate(ctx -> ctx.getProvider()
                .stairsBlock(ctx.getEntry(), ctx.getProvider().modLoc(ctx.getName())))
            .lang("Special Stairs")
            .register();
```

This customized version will create a BlockItem (with its own default model and lang entry), add the block to a tag, configure the blockstate for stair properties, and add a custom localization.

To get an overview of the different APIs and methods, check out the [Javadocs](https://ci.tterrag.com/job/Registrate/job/1.16/javadoc/). For more advanced usage, read the [wiki](https://github.com/tterrag1098/Registrate/wiki) (WIP).

## Project Setup

Registrate can be installed in the mods folder as a typical dependency, but since it does not have a mod, it can also be shaded. Shading is the recommended way to include Registrate (at least until Forge jar-in-jar is working again).

This is easiest with the [Gradle Shadow plugin](https://imperceptiblethoughts.com/shadow/). Add the plugin to your buildscript like so:

```gradle
plugins {
    id 'com.github.johnrengelman.shadow' version '5.2.0'
}
```

**Note: Shadow 5.1+ requires Gradle 5.x. I recommend 5.6 as I know this version works with both Shadow and FG3.**

Once you have the plugin, it needs to be configured. First add a shade configuration,

```gradle
configurations {
    shade
}
```

configure the shadowJar task use it, and repackage Registrate classes.

```gradle
shadowJar {
    configurations = [project.configurations.shade]
    relocate 'com.tterrag.registrate', 'com.mymod.repack.registrate'
}
```

Then, make sure the shadow jar artifact is reobfuscated.

```gradle
reobf {
    shadowJar {}
}
```

Finally, the dependency itself must be added. First add my maven repository,

```gradle
repositories {
    maven { // Registrate
        url "http://maven.tterrag.com/"
    }
    mavenLocal()
}
```

and then the Registrate dependency to the implementation and shade configurations.

```gradle
dependencies {
    minecraft "net.minecraftforge:forge:${minecraft_version}-${forge_version}" // This should already be here
    
    def registrate = "com.tterrag.registrate:Registrate:MC${minecraft_version}-${registrate_version}"
    implementation fg.deobf(registrate)
    shade registrate
}
```

To build the jar containing shaded dependencies, use the `shadowJar` task, or configure the task to run automatically when using `build`.
 
```gradle
build.dependsOn shadowJar
build.dependsOn reobfShadowJar
```
