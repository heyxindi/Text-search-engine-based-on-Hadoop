package lucence;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer {

	private IndexWriter writer; // create an index instance

	// Construction method, Instantiate IndexWriter
	public Indexer(String indexDir) throws Exception {
		Directory dir = FSDirectory.open(Paths.get(indexDir));
		Analyzer analyzer = new StandardAnalyzer(); // Standard Tokenizer
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		writer = new IndexWriter(dir, config);
	}

	// Close write index
	public void close() throws Exception {
		writer.close();
	}

	// Index specifies all files in the directory
	public int indexAll(String dataDir) throws Exception {
		File[] files = new File(dataDir).listFiles(); // Gets all the files under this path
		for (File file : files) {
			indexFile(file); // Call the indexFile method below to index each file
		}
		return writer.numRamDocs(); // Returns the number of files indexed
	}

	// Index the specified file
	private void indexFile(File file) throws Exception {
		System.out.println("索引文件的路径：" + file.getCanonicalPath());
		Document doc = getDocument(file); // Gets the document of the file
		writer.addDocument(doc); // Call the getDocument method below to add doc to the index
	}

	// Get the document and set each field in the document like a row in the database
	private Document getDocument(File file) throws Exception {
		Document doc = new Document();
		// Add fields
		doc.add(new TextField("contents", new FileReader(file))); // Add content
		doc.add(new TextField("fileName", file.getName(), Field.Store.YES)); // Add the filename and save the field to the index file
		doc.add(new TextField("fullPath", file.getCanonicalPath(), Field.Store.YES)); // Add file path
		return doc;
	}

	public static void main(String[] args) {
		String indexDir = "C:\\Users\\19214\\Desktop\\P_Workspace\\lucence"; // The path to which the index is saved
		String dataDir = "C:\\Users\\19214\\Desktop\\P_Workspace\\lucence\\data"; // The directory where file data needs to be indexed
		Indexer indexer = null;
		int indexedNum = 0;
		long startTime = System.currentTimeMillis(); // Record the index start time
		try {
			indexer = new Indexer(indexDir);
			indexedNum = indexer.indexAll(dataDir);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				indexer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		long endTime = System.currentTimeMillis(); // Record the index end time
		System.out.println("The index takes" + (endTime - startTime) + "ms");
		System.out.println("The total indexed files number is: " + indexedNum );
	}

}