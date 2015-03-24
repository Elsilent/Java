import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;

public class Search {
    private static IndexSearcher searcher = null;
    private QueryParser parser = null;

    public Search() throws IOException {
        searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(System.getProperty("user.dir")+"\\indexes\\"))));
        // поиск по имени
        parser = new QueryParser("name", new StandardAnalyzer());
    }

    public TopDocs performSearch(String queryString, int n)
            throws IOException, ParseException {
        Query query = parser.parse(queryString);
        return searcher.search(query, n);
    }

    public static Document getDocument(int docId)
            throws IOException {
        return searcher.doc(docId);
    }
}
