package io.github.thebusybiscuit.dough.updater;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nonnull;

import org.bukkit.plugin.Plugin;

import io.github.thebusybiscuit.dough.versions.Version;

public interface PluginUpdater {

    @Nonnull
    Plugin getPlugin();

    @Nonnull
    File getFile();

    @Nonnull
    Version getCurrentVersion();

    @Nonnull
    CompletableFuture<Version> getLatestVersion();

    int getConnectionTimeout();

    void start();

}
