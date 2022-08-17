# Text-search-engine-based-on-Hadoop

## Introduction
This project implement a simple text search engine based on Hadoop. It retrieved text contents, which are stored at different nodes in a Hadoop cluster, related to the query and order them according to relevance. It covered three parts: text files collection, indexing & ranking and web server & interface.

## Methods
### Text files collection
First, split data into 250 files for the indexing and ranking later. For detailed code, see the ‘cut_file.java’ in the ‘Text_collection’ folder.
After splitting the data, import the dataset into the virtual machine. Use the regular expression to do the data cleaning in both Python and the MapReduce so that these datasets are available to be used in the next part.

### Indexing & ranking
Use python to implemente the indexing function. For detailed code, see the ‘Index_Python.ipynb’ file in the ‘Indexing’ folder. To calculate the time taken by Hadoop Map Reduce, add a timer to it to compare their performance.
* Python:
-	small file set:
 ![image](https://user-images.githubusercontent.com/57136383/185081395-64c0ea70-c36d-4919-9aea-207ed9a561bd.png)
-	large file set:
 ![image](https://user-images.githubusercontent.com/57136383/185081408-3185e336-59ff-4431-9f9c-cbc10823bce5.png)
 
* MapReduce:
-	small file set:
![image](https://user-images.githubusercontent.com/57136383/185081518-14007d0d-0ea5-4dbf-a468-5ad5826830cc.png)
- Large file set:
![image](https://user-images.githubusercontent.com/57136383/185081569-9d365805-69a6-4dfd-ba1d-9d1d2290d10e.png)

### Lucene

Lucene is an open source library for full-text search and search, supported and provided by the Apache Software Foundation. Lucene provides a simple but powerful application programming interface that can do full-text indexing and searching. 

Basically, there are two steps to full-text search: indexing and searching. So in order to test this process, I wrote two java classes, one for test indexing and the other for test retrieval.
First create a maven project, pom.xml is as follows:

![image](https://user-images.githubusercontent.com/57136383/185082071-b5f3ac1f-6a6c-4d1a-b90d-036bf9826392.png)
Before writing the program, put the text data in the directory of \lucene\data\directory. To quickly test the program, I simply use three text files of them as a small file set.

![image](https://user-images.githubusercontent.com/57136383/185082181-389f1cde-ccf3-46a0-a581-30666f8d8499.png)

Next, create the java program for indexing:
![image](https://user-images.githubusercontent.com/57136383/185082218-3710b863-05be-4393-bb20-01d624c49664.png)
![image](https://user-images.githubusercontent.com/57136383/185082243-d60126b3-16bb-44dc-ba9f-ede6c896b967.png)
A total of 3 files were indexed, which took 1265 milliseconds, which is quite fast, and the path of the index file is also correct. Then see the file under the path \ lucene \ will generate some files, these are the generated indexes.

![image](https://user-images.githubusercontent.com/57136383/185082405-082cd3e6-f56f-4704-bdef-e102ef673fe6.png)
![image](https://user-images.githubusercontent.com/57136383/185082426-fc6e7188-a76b-47c3-86e8-1b9ee9b59cd8.png)

Now that using the index, we can retrieve the characters we want to query. I chose the string "first into Media" as the search object. Take a look at the retrieved java code before retrieving:

![image](https://user-images.githubusercontent.com/57136383/185082717-89b335b8-5053-4d46-99e1-f41c80372d12.png)

Result:

![image](https://user-images.githubusercontent.com/57136383/185082756-ed52e58f-dfd5-45f8-9d57-5fb4a7b6b922.png)

### Ranking
Sorting determines where a particular document appears in search engine query results. First, use Python to read the data line by line and then use the input file to create a DataFrame. It looks like this:

![image](https://user-images.githubusercontent.com/57136383/185083745-c10c2069-8b52-4e22-b887-565cea931f54.png)

Then call the “split function” twice. 
a.	The first time is used to split the word+file and TF-IDF value. After getting the TF-IDF value, call the sort.values function to sort the left part (word+file) by TF-IDF value in ascending order.
b.	The second time is used to split the word and file. For the same key with different values, I apply the following one to the first one by a “+”. Then call the groupby function to combine those lines with the same key.
After that, we can get the file sorted by the TF-IDF value in ascending order.

### Web server and interface
I designed the basic interface with HTML and then connected with Django. Through the Django framework, connected html with background text data. In Django, web pages and other content are derived from views. Each view appears as a simple Python function. Django will choose which view to use based on the URL requested by the user. Use one to display. 

![image](https://user-images.githubusercontent.com/57136383/185084402-3bbb9be4-8635-46b5-8eb6-c34a33abd4fd.png)

