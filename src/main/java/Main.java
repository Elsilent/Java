import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        //Parser.getTheatres();
        //Lucene.rebuildIndexes();
        // поиск
        // instantiate the search engine
        Search se = new Search();
        // retrieve top 100 matching document list for the query "theatreName"
        TopDocs topDocs = se.performSearch("Бо"+"*", 100);
        // obtain the ScoreDoc (= documentID, relevanceScore) array from topDocs
        ScoreDoc[] hits = topDocs.scoreDocs;
        // retrieve each matching document from the ScoreDoc array
        for (int i = 0; i < hits.length; i++) {
            Document doc = Search.getDocument(hits[i].doc);
            String theatreName = doc.get("name");
            System.out.println(theatreName);
        }
    }
}
