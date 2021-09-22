package it.hurts.sskirillss.relics.configs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraftforge.fml.loading.FMLPaths;

import javax.annotation.Nullable;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigHelper {
    @Getter
    private static final Gson serializer = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .disableHtmlEscaping()
            .create();

    @Getter
    private static final Path rootPath = FMLPaths.CONFIGDIR.get().resolve("relics");

    @SneakyThrows
    public static void createJSONConfig(Path path, Object source) {
        if (Files.exists(path))
            return;

        try (Writer writer = Files.newBufferedWriter(path)) {
            serializer.toJson(source, writer);

            writer.flush();
        }
    }

    @Nullable
    @SneakyThrows
    public static Object readJSONConfig(Path path, Type target) {
        if (!Files.exists(path))
            return null;

        Object result;

        try (Reader reader = Files.newBufferedReader(path)) {
            result = serializer.fromJson(reader, target);
        }

        return result;
    }
}