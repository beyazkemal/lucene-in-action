package com.kemalbeyaz.lucene.first;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        Directory index = new NIOFSDirectory(Paths.get("./index"));
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        IndexWriter writter = new IndexWriter(index, indexWriterConfig);

        for (int i = 0; i < 500; i++) {
            Document document = new Document();
            document.add(new TextField("title", "title" + i, Field.Store.YES));
            document.add(new TextField("body", "body" + i, Field.Store.YES));
            writter.addDocument(document);
        }

        IndexReader reader = DirectoryReader.open(writter);
        int i = reader.numDocs();
        System.out.println(i);

        Query query = new QueryParser("title", analyzer).parse("ti*");
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs search = searcher.search(query, 10);

        List<Document> documents = new ArrayList<>();
        for (ScoreDoc scoreDoc : search.scoreDocs) {
            documents.add(searcher.doc(scoreDoc.doc));
        }

        documents.forEach(System.out::println);

        writter.close();
    }
}
