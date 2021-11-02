package com.kemalbeyaz.lucene.second;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class SampleDataProvider {

    private static final String SAMPLE_NAMES_PATH = "/sample/names.txt";
    private static final String SAMPLE_SURNAMES_PATH = "/sample/surnames.txt";
    private static final String SAMPLE_TEXTS_PATH = "/sample/texts.txt";
    private static final Random RANDOM = new Random();

    private final List<String> names = new ArrayList<>();
    private final List<String> surnames = new ArrayList<>();
    private final List<String> texts = new ArrayList<>();

    public SampleDataProvider() throws FileNotFoundException, URISyntaxException {
        readFile(SAMPLE_NAMES_PATH, names);
        readFile(SAMPLE_SURNAMES_PATH, surnames);
        readFile(SAMPLE_TEXTS_PATH, texts);
    }

    public String getRandomName() {
        int i = RANDOM.nextInt(2000);
        return names.get(i);
    }

    public String getRandomSurName() {
        int i = RANDOM.nextInt(1000);
        return surnames.get(i);
    }

    public String getRandomText() {
        int i = RANDOM.nextInt(10000);
        return texts.get(i);
    }

    public Date getRandomDate() {
        int minDay = (int) LocalDate.of(1900, 1, 1).toEpochDay();
        int maxDay = (int) LocalDate.of(2015, 1, 1).toEpochDay();
        long randomDay = minDay + RANDOM.nextInt(maxDay - minDay);

        LocalDate randomBirthDate = LocalDate.ofEpochDay(randomDay);
        return java.util.Date.from(randomBirthDate.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    private void readFile(final String path, final List<String> target) throws FileNotFoundException, URISyntaxException {
        final URL resource = this.getClass().getResource(path);

        final File file = new File(resource.toURI());
        final Scanner myReader = new Scanner(file);
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();
            target.add(data);
        }
    }
}
