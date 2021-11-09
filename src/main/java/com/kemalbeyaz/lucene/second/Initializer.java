package com.kemalbeyaz.lucene.second;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.NIOFSDirectory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class Initializer {

    static final String INDEX_PATH = "./second-index";

    public static void main(String[] args) throws IOException, URISyntaxException {
        final var sampleDataProvider = new SampleDataProvider();

        final var index = new NIOFSDirectory(Paths.get(INDEX_PATH));

        final var analyzer = new StandardAnalyzer();
        final var indexWriterConfig = new IndexWriterConfig(analyzer);
        final var writer = new IndexWriter(index, indexWriterConfig);

        final var start = LocalDateTime.now();
        for (int i = 0; i < 100_000; i++) {
            final var document = createSampleDocument(sampleDataProvider, i);
            writer.addDocument(document);
        }

        long duration = ChronoUnit.SECONDS.between(start, LocalDateTime.now());
        System.out.println("Duration: " + duration + " s");

        writer.close();
        index.close();
    }

    public static Document createSampleDocument(final SampleDataProvider sampleDataProvider, long point) {
        final var document = new Document();
        var name = sampleDataProvider.getRandomName();
        var surname = sampleDataProvider.getRandomSurName();

        document.add(new StoredField("id", UUID.randomUUID().toString()));
        document.add(new StringField("firstname", name, Field.Store.YES));
        document.add(new StringField("surname", surname, Field.Store.YES));
        document.add(new StringField("name", name + " " + surname, Field.Store.YES));
        document.add(new StringField("email", name + "." + surname + "@example.com", Field.Store.YES));
        document.add(new StoredField("birtDate", sampleDataProvider.getRandomDate().getTime()));
        document.add(new TextField("text1", sampleDataProvider.getRandomText(), Field.Store.YES));
        document.add(new TextField("text2", getLongText(sampleDataProvider), Field.Store.YES));
        document.add(new LongPoint("longPoint",point));

        return document;
    }

    private static String getLongText(final SampleDataProvider sampleDataProvider) {
        final var builder = new StringBuilder();
        builder.append(sampleDataProvider.getRandomText());
        builder.append(" ");
        builder.append(sampleDataProvider.getRandomText());
        builder.append(" ");
        builder.append(sampleDataProvider.getRandomText());

        return builder.toString();
    }
}
