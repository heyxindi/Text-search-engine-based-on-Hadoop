package lucence;

import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Searcher {

	 public static void search(String indexDir, String q) throws Exception {

	        Directory dir = FSDirectory.open(Paths.get(indexDir)); //Gets the path to query, which is where the index is
	        IndexReader reader = DirectoryReader.open(dir);
	        IndexSearcher searcher = new IndexSearcher(reader);
	        Analyzer analyzer = new StandardAnalyzer(); //The standard word divider will automatically remove the Spaces
	        QueryParser parser = new QueryParser("contents", analyzer);//Query parser
	        Query query = parser.parse(q); //Gets the query object by parsing the String to be queried

	        long startTime = System.currentTimeMillis(); //Record the index start time
	        TopDocs docs = searcher.search(query, 10);//Start the query, query the first 10 pieces of data, and save the record in your docs
	        long endTime = System.currentTimeMillis(); //Record the index end time
	        System.out.println("matching" + q + "time elapsed" + (endTime-startTime) + "ms");
	        System.out.println(docs.totalHits + "record was found");

	        for(ScoreDoc scoreDoc : docs.scoreDocs) { //Fetch each query result
	            Document doc = searcher.doc(scoreDoc.doc); //Scoredoc.doc is equivalent to docID, from which documents are obtained
	            System.out.println(doc.get("fullPath")); //FullPath is a field that we defined when we first started indexing
	        }
	        reader.close();
	    }

	    public static void main(String[] args) {
	        String indexDir = "C:\\Users\\19214\\Desktop\\P_Workspace\\lucence";
	        String q = "first into Media"; // Querying this string
	        try {
	            search(indexDir, q);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
}
