package com.kemalbeyaz.lucene.second;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.NIOFSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SearchWithLongRange {

    public static void main(String[] args) throws IOException, ParseException, org.apache.lucene.queryparser.surround.parser.ParseException {
        final var index = new NIOFSDirectory(Paths.get(Initializer.INDEX_PATH));
        final var reader = DirectoryReader.open(index);
        int i = reader.numDocs();
        System.out.println("Total document count: " + i);

        Query rangeQuery = LongPoint.newRangeQuery("longPoint", 1000, Long.MAX_VALUE);

        final var searcher = new IndexSearcher(reader);
        int count = searcher.count(rangeQuery);
        System.out.println("Found document count: " + count);

        if(count != 0) {
            final var search = searcher.search(rangeQuery, 10);
            final List<Document> documents = new ArrayList<>();
            for (ScoreDoc scoreDoc : search.scoreDocs) {
                documents.add(searcher.doc(scoreDoc.doc));
            }

            System.out.println("First 10 doc: ");
            documents.forEach(System.out::println);
        }

        reader.close();
        index.close();
    }
}
