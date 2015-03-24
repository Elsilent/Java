import com.google.gson.*;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by - on 23.03.2015.
 */
public class Lucene {
    public Lucene() {
    }

    private static IndexWriter indexWriter = null;

    public static IndexWriter getIndexWriter(boolean create) throws IOException {
        if (indexWriter == null) {
            Directory indexDir = FSDirectory.open(new File(System.getProperty("user.dir")+"\\indexes\\"));
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_2, new StandardAnalyzer());
            indexWriter = new IndexWriter(indexDir, config);
        }
        return indexWriter;
    }

    public static void closeIndexWriter() throws IOException {
        if (indexWriter != null) {
            indexWriter.close();
        }
    }

    public static void indexTheatre(JsonObject theatre) throws IOException {
        System.out.println("Indexing theatre: " + theatre);
        IndexWriter writer = getIndexWriter(false);
        Document doc = new Document();
        /*doc.add(new StringField("id", hotel.getId(), Field.Store.YES));
        doc.add(new StringField("name", hotel.getName(), Field.Store.YES));
        doc.add(new StringField("city", hotel.getCity(), Field.Store.YES));
        String fullSearchableText = hotel.getName() + " " + hotel.getCity() + " " + hotel.getDescription();
        doc.add(new TextField("content", fullSearchableText, Field.Store.NO));*/


        // координаты
        String coord = theatre.getAsJsonPrimitive("coord").toString();
        doc.add(new StringField("coord", coord, Field.Store.YES));
        // адрес
        String address = theatre.getAsJsonPrimitive("address").toString();
        doc.add(new StringField("address", address, Field.Store.YES));
        // метро
        for (JsonElement metro : theatre.getAsJsonArray("metro")) {
            doc.add(new StringField("metro", metro.toString(), Field.Store.YES));
        }
        // name
        String name = theatre.getAsJsonPrimitive("name").toString();
        doc.add(new StringField("name", name, Field.Store.YES));
        // data
        for (JsonElement data : theatre.getAsJsonArray("data")) {

        }
        // rep
        for (JsonElement rep : theatre.getAsJsonArray("rep")) {
            doc.add(new StringField("rep", rep.toString(), Field.Store.YES));
        }
        System.out.println("test");
        writer.addDocument(doc);
    }

    public static void rebuildIndexes() throws IOException {
        //
        // Erase existing index
        //
        getIndexWriter(true);
        //
        // Index all Accommodation entries
        //
        JsonParser parser = new JsonParser();
        String workingDir = System.getProperty("user.dir");

        File dir = new File(workingDir+"\\json\\");
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File child : directoryListing) {
                // Do something with child
                Object obj = parser.parse(new FileReader(child));
                JsonObject current = (JsonObject) obj;
                indexTheatre(current);
            }
        } else {
            System.out.println("Directory is empty");
        }
        // Don't forget to close the index writer when done
        //
        closeIndexWriter();
    }
}
