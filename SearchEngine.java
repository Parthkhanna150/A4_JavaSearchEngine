
import java.util.*;
import java.io.*;

// This class implements a google-like search engine
public class SearchEngine {

    public HashMap<String, LinkedList<String>> wordIndex;                  // this will contain a set of pairs (String, LinkedList of Strings)	
    public DirectedGraph internet;             // this is our internet graph

    // Constructor initializes everything to empty data structures
    // It also sets the location of the internet files
    SearchEngine() {
        // Below is the directory that contains all the internet files
        HtmlParsing.internetFilesLocation = "internetFiles";
        wordIndex = new HashMap<String, LinkedList<String>>();
        internet = new DirectedGraph();
    } // end of constructor//2017

    // Returns a String description of a searchEngine
    public String toString() {
        return "wordIndex:\n" + wordIndex + "\ninternet:\n" + internet;
    }

    // This does a graph traversal of the internet, starting at the given url.
    // For each new vertex seen, it updates the wordIndex, the internet graph,
    // and the set of visited vertices.
    void traverseInternet(String url) throws Exception {
        /* WRITE SOME CODE HERE */
        Stack<String> stack = new Stack<String>();//stack declared
        stack.push(url);//puts url into stack
        internet.setVisited(url, true);//sets url 'visited'
        while (!stack.empty()) //Use of DFS
        {
            String s = stack.pop();//takes out and assigns url to s
            LinkedList<String> allLinks = HtmlParsing.getLinks(s);//linkedlist of links on webpage s
            LinkedList<String> words = HtmlParsing.getContent(s);//linkedlist of words in webpage s
            Iterator<String> w = words.iterator();//iterator
            while (w.hasNext())//until there are words
            {
                String s1 = w.next();//each word
                if (!wordIndex.containsKey(s1))//if a word doesn't already exist
                {
                    wordIndex.put(s1, new LinkedList<String>());//puts the word as key in the hashmap and creates a new linkedlist for it
                    wordIndex.get(s1).addLast(s);//adds the url to the linkedlist
                } else //if the word already exists in the hashmap
                {
                    wordIndex.get(s1).addLast(s);//simply adds the url to its already created linkedlist
                }
            }
            Iterator<String> n = allLinks.iterator();//runs an iterator on all links
            while (n.hasNext())//until there are links
            {
                String update = n.next();//each links
                internet.addEdge(s, update);//adds an edge from s --> update as update is on s's webpage
                if (!internet.getVisited(update))//if update is not set 'visited'
                {
                    internet.setVisited(update, true);//sets update 'visited'
                    stack.push(update);//pushes it into the stack and the whole process repeats as stack is not emply
                }
            }
        }
    }

    /* Hints
	   0) This should take about 50-70 lines of code (or less)
	   1) To parse the content of the url, call
	   htmlParsing.getContent(url), which returns a LinkedList of Strings 
	   containing all the words at the given url. Also call htmlParsing.getLinks(url).
	   and assign their results to a LinkedList of Strings.
	   2) To iterate over all elements of a LinkedList, use an Iterator,
	   as described in the text of the assignment
	   3) Refer to the description of the LinkedList methods at
	   http://docs.oracle.com/javase/6/docs/api/ .
	   You will most likely need to use the methods contains(String s), 
	   addLast(String s), iterator()
	   4) Refer to the description of the HashMap methods at
	   http://docs.oracle.com/javase/6/docs/api/ .
	   You will most likely need to use the methods containsKey(String s), 
	   get(String s), put(String s, LinkedList l).  
     */
    // end of traverseInternet
    /* This computes the pageRanks for every vertex in the internet graph.
       It will only be called after the internet graph has been constructed using 
       traverseInternet.
       Use the iterative procedure described in the text of the assignment to
       compute the pageRanks for every vertices in the graph. 
       This method will probably fit in about 30 lines.
     */
    void computePageRanks() {
        // WRITE YOUR CODE HERE 
        LinkedList<String> vertices = internet.getVertices();//linkedlist of all vertices
        for (int j = 0; j < vertices.size(); j++) {
            internet.setPageRank(vertices.get(j), 1);//setting all page ranks =1
        }
        for (int i = 0; i < 100; i++) {//100 iterations for convergence
            Iterator<String> v = vertices.iterator();//to take each vertix
            while (v.hasNext()) {
                double sum = 0;//this is the part inside the brackets in the formula
                String s2 = v.next();//each vertix
                LinkedList<String> edges = internet.getEdgesInto(s2);//linkedlist with edges going into (vertices pointing towards) the vertix
                Iterator<String> e = edges.iterator();//iterator
                while (e.hasNext()) {
                    String s = e.next();//each such vertex --> s2
                    sum = sum + internet.getPageRank(s) / internet.getOutDegree(s);//according to formula
                }
                internet.setPageRank(s2, (0.5) + (0.5 * sum));//updating page ranks
            }
        }
    }
    // end of computePageRanks

    /* Returns the URL of the page with the high page-rank containing the query word
       Returns the String "" if no web site contains the query.
       This method can only be called after the computePageRanks method has been executed.
       Start by obtaining the list of URLs containing the query word. Then return the URL 
       with the highest pageRank.
       This method should take about 25 lines of code.
     */    // Returns the URL of the page with the high page-rank containing the query word
    String getBestURL(String query) throws Exception {
        /* WRITE YOUR CODE HERE */
        String answer = "";
        LinkedList<String> withQuery = new LinkedList<>();//decalring a linkedlist
        if (wordIndex.containsKey(query)) {//checks if our hashmap has the query
            withQuery = wordIndex.get(query);//putting the linkedlist pointing to the key word (query) in our hashmap
        }
        for (int j = 1; j < withQuery.size(); j++) {
            if (internet.getPageRank(answer) < internet.getPageRank(withQuery.get(j))) {//finds the max page rank weboage
                answer = withQuery.get(j);//assigns the max page rank page weboage to answer
            }
        }
        return (answer+ ", " + internet.getPageRank(answer)); // FOR CHECKING
    } // end of getBestURL

    public static void main(String args[]) throws Exception {
        SearchEngine mySearchEngine = new SearchEngine();
        // to debug your program, start with.
        //mySearchEngine.traverseInternet("http://www.cs.mcgill.ca/~blanchem/250/a.html");
        // When your program is working on the small example, move on to
        mySearchEngine.traverseInternet("http://www.cs.mcgill.ca");

        // this is just for debugging purposes. REMOVE THIS BEFORE SUBMITTING
        System.out.println(mySearchEngine);

        mySearchEngine.computePageRanks();

        BufferedReader stndin = new BufferedReader(new InputStreamReader(System.in));
        String query;
        do {
            System.out.print("Enter query: ");
            query = stndin.readLine();
            if (query != null && query.length() > 0) {
                System.out.println("Best site = " + mySearchEngine.getBestURL(query));
            }
        } while (query != null && query.length() > 0);
    } // end of main
}
