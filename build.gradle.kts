import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    java
    `java-library`
    idea
    id("io.freefair.lombok") version "8.6"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0" // Generates the plugin.yml when building the project
    id("xyz.jpenilla.run-paper") version "2.2.3"
}

group = "net.quantrax"
version = "1.0.0.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.quantrax.net/nexus/content/repositories/releases")
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://repo.minebench.de/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    // Dependencies that are available at runtime
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("me.lucko:spark-api:0.1-SNAPSHOT")

    // Dependencies that have to be shadowed
    implementation("org.jetbrains:annotations:24.1.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("io.github.cdimascio:dotenv-java:3.0.0")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.3.2")
    implementation("de.chojo.sadu:sadu:1.4.1")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("com.moandjiezana.toml:toml4j:0.7.2")
    implementation("org.codehaus.plexus:plexus-utils:4.0.0")
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    implementation("de.themoep:inventorygui:1.6.1-SNAPSHOT")
    implementation("net.wesjd:anvilgui:1.9.2-SNAPSHOT")
    implementation("club.minnced:discord-webhooks:0.8.4")

    // Test dependencies
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.20:3.71.0")
    testImplementation("org.mockito:mockito-core:5.10.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks {
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
        withSourcesJar()
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    compileTestJava {
        options.encoding = "UTF-8"
    }

    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    shadowJar {
        archiveBaseName.set("shadow")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "net.quantrax.citybuild.CityBuildPlugin"))
        }

        val base = "net.quantrax.citybuild.libs."
        val mapping = mapOf(
                "org.jetbrains" to "jetbrains",
                "com.google.code.gson" to "gson",
                "io.github.cdimascio" to "dotenv",
                "org.mariadb.jdbc" to "jdbc",
                "de.chojo.sadu" to "sadu",
                "com.github.ben-manes.caffeine" to "caffeine",
                "com.moandjiezana" to "toml",
                "org.codehaus.plexus" to "plexus",
                "co.aikar" to "acf",
                "de.themoep" to "inventorygui",
                "net.wesjd" to "anvilgui",
                "club.minnced" to "discord.webhook"
        )
        for ((pattern, name) in mapping) relocate(pattern, "${base}${name}")
    }

    build {
        dependsOn(":bumpVersion", shadowJar)
    }

    runServer {
        minecraftVersion("1.20.4")
        downloadPlugins {
            url("https://ci.lucko.me/job/spark/400/artifact/spark-bukkit/build/libs/spark-1.10.59-bukkit.jar")
        }
        dependsOn(":bumpVersion")
    }

    create("bumpVersion") {
        val currentVersion: String? by project
        doFirst {
            println("Current version: $version")
            val newVersion = takeIf { currentVersion.isNullOrBlank() }?.let {
                val versions = version.toString().split(".")
                "${versions[0]}.${versions[1]}.${versions[2]}.${versions.last().toInt().plus(1)}"
            } ?: currentVersion

            buildFile.readText().apply {
                println("Bump version to $newVersion")
                val content = replaceFirst("version = \"$version\"", "version = \"$newVersion\"")
                buildFile.writeText(content)
            }
        }
    }
}

bukkit {
    main = "net.quantrax.citybuild.CityBuildPlugin"
    apiVersion = "1.20"
    foliaSupported = false
    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD
    author = "ByTRYO"
    contributors = listOf("Merry", "GhostException", "DeRio_")
    depend = listOf("spark")
}