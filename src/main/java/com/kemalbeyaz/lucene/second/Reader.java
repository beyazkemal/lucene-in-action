package com.kemalbeyaz.lucene.second;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.NIOFSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Reader {

    // Kassandra.Coffey
    private static final String DEFAULT_FIELD = "text1";
    private static final String QUERY = "te*";

    public static void main(String[] args) throws IOException, ParseException, org.apache.lucene.queryparser.surround.parser.ParseException {
        final var index = new NIOFSDirectory(Paths.get(Initializer.INDEX_PATH));
        final var analyzer = new StandardAnalyzer();
        final var reader = DirectoryReader.open(index);
        int i = reader.numDocs();
        System.out.println("Total document count: " + i);


        Term term = new Term(DEFAULT_FIELD, QUERY);
        Query query = new WildcardQuery(term);

        // QueryParser queryParser = new QueryParser(DEFAULT_FIELD, analyzer);
        // final var query = queryParser.parse(QUERY);
        final var searcher = new IndexSearcher(reader);
        int count = searcher.count(query);
        System.out.println("Found document count: " + count);

        if(count != 0) {
            final var search = searcher.search(query, 10);
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
