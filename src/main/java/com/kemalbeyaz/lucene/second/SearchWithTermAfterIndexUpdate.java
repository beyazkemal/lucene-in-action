package com.kemalbeyaz.lucene.second;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.NIOFSDirectory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SearchWithTermAfterIndexUpdate {

    public static void main(String[] args) throws IOException, URISyntaxException {
        final var sampleDataProvider = new SampleDataProvider();
        final var index = new NIOFSDirectory(Paths.get(Initializer.INDEX_PATH));

        final var analyzer = new StandardAnalyzer();
        final var indexWriterConfig = new IndexWriterConfig(analyzer);
        final var writer = new IndexWriter(index, indexWriterConfig);

        // final var reader = DirectoryReader.open(writer);
        final var reader = DirectoryReader.open(index);
        int i = reader.numDocs();
        System.out.println("Total document count: " + i);

        final var newDocument = Initializer.createSampleDocument(sampleDataProvider, 2345676);
        Term term = new Term("firstname", newDocument.get("firstname"));
        TermQuery query = new TermQuery(term);

        Term term2 = new Term("surname", newDocument.get("surname"));
        TermQuery query2 = new TermQuery(term2);

        searchAnLog(reader, query, query2);

        writer.addDocument(newDocument);
        writer.commit(); // important

        // important
        final var newReader = DirectoryReader.openIfChanged(reader);
        searchAnLog(Objects.requireNonNullElse(newReader, reader), query, query2);

        // searchAnLog(reader, query, query2); // --> not working!

        writer.close();
        reader.close();
        index.close();
    }

    private static void searchAnLog(final DirectoryReader reader, final Query ...query) throws IOException {
        BooleanQuery.Builder builder = new BooleanQuery.Builder();

        Arrays.stream(query)
                .forEach(q -> builder.add(q, BooleanClause.Occur.MUST));

        BooleanQuery booleanQuery = builder.build();

        final var searcher = new IndexSearcher(reader);
        int count = searcher.count(booleanQuery);
        System.out.println("Found document count: " + count);

        if(count != 0) {
            final var search = searcher.search(booleanQuery, 10);
            final List<Document> documents = new ArrayList<>();
            for (ScoreDoc scoreDoc : search.scoreDocs) {
                documents.add(searcher.doc(scoreDoc.doc));
            }

            System.out.println("First 10 doc: ");
            documents.forEach(System.out::println);
        }
    }
}
