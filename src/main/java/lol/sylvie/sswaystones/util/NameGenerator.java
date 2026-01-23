/*
  This file is licensed under the MIT License!
  https://github.com/sylvxa/sswaystones/blob/main/LICENSE
*/
package lol.sylvie.sswaystones.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import lol.sylvie.sswaystones.Waystones;
import lol.sylvie.sswaystones.config.Configuration;
import org.apache.commons.lang3.StringUtils;

public class NameGenerator {
    private static String[] loadLangFile(String lang, String file) {
        return readResourceAsString("assets/sswaystones/lang/" + lang + "/" + file).replace("\r", "").split("\n");
    }

    private static final String MISSING_FILE = "MISSINGNO";
    private static String[] nameFirst = {MISSING_FILE};
    private static String[] nameLast = {MISSING_FILE};

    private static String readResourceAsString(String path) {
        try (InputStream stream = NameGenerator.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null)
                return MISSING_FILE;
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return MISSING_FILE;
        }
    }

    public static String generateName() {
        Random random = new Random();
        int firstIndex = random.nextInt(0, nameFirst.length);
        int lastIndex = random.nextInt(0, nameLast.length);

        String combined = nameFirst[firstIndex] + nameLast[lastIndex];

        boolean upsideDown = Waystones.configuration.getInstance().randomGeneratedNameLanguage.equals("en_ud");
        if (upsideDown)
            return combined;

        return StringUtils.capitalize(combined);
    }

    public static void reloadFiles() {
        Configuration.Instance config = Waystones.configuration.getInstance();

        nameFirst = loadLangFile(config.randomGeneratedNameLanguage, "name_first.txt");
        nameLast = loadLangFile(config.randomGeneratedNameLanguage, "name_last.txt");
    }
}
