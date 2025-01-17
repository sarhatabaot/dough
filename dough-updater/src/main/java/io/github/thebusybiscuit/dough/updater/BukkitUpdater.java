package io.github.thebusybiscuit.dough.updater;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.logging.Level;

import javax.annotation.Nonnull;

import org.bukkit.plugin.Plugin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.github.thebusybiscuit.dough.versions.SemanticVersion;
import io.github.thebusybiscuit.dough.versions.Version;

public class BukkitUpdater extends AbstractPluginUpdater {

    private static final String API_URL = "https://api.curseforge.com/servermods/files?projectIds=";

    private final int projectId;

    public BukkitUpdater(@Nonnull Plugin plugin, @Nonnull File file, int id) {
        super(plugin, file, getVersion(plugin));

        this.projectId = id;
    }

    private static @Nonnull Version getVersion(@Nonnull Plugin plugin) {
        String pluginVersion = plugin.getDescription().getVersion().toLowerCase(Locale.ROOT);
        return SemanticVersion.parse(pluginVersion);
    }

    @Override
    public void start() {
        try {
            URL url = new URL(API_URL + projectId);

            scheduleAsyncUpdateTask(new UpdaterTask(this, url) {

                @Override
                public UpdateInfo parse(String result) throws MalformedURLException {
                    JsonArray array = (JsonArray) new JsonParser().parse(result);

                    if (array.size() == 0) {
                        getLogger().log(Level.WARNING, "The Auto-Updater could not connect to dev.bukkit.org, is it down?");
                        return null;
                    }

                    JsonObject latest = array.get(array.size() - 1).getAsJsonObject();

                    URL download = new URL(latest.get("downloadUrl").getAsString());
                    String remoteVersion = latest.getAsJsonObject().get("name").getAsString();
                    remoteVersion = remoteVersion.toLowerCase(Locale.ROOT);

                    return new UpdateInfo(download, SemanticVersion.parse(remoteVersion));
                }

            });
        } catch (MalformedURLException e) {
            getLogger().log(Level.SEVERE, "Auto-Updater URL is malformed", e);
        }
    }

}
