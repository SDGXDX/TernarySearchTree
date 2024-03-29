import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import static java.nio.file.Files.*;

class TNode
{
    char data;    // char for node
    public TNode center; // eqkid - data = root - go center
    public TNode left;   // lokid - data < node - go left
    public TNode right;  // hikid - data > node - go right
    public boolean wordComplete;    // finished word, used to signal the loop to stop recursion

    public TNode(char data, boolean wordComplete)
    {
        this.data = data;
        this.wordComplete = wordComplete;
    }

}

class InsideOutShuffle
{
    // Knuth/Fisher-Yates shuffle for strings
    // spec calls for for-loop, but object swapping worked faster
    // items is our dictionary file.
    public static String[] stringShuffle(String[] items)
    {

       Random r = new Random();
        // set our boundry
        int len = items.length;

        // do until 1 item
       while (len > 1)
       {
            // get a random int and decrement value.
            int j = r.nextInt(len--);
             // dont bother swapping the same item
            if (len != j)
            {
                // and swap the two strings in place.
                String swap = items[len];
                items[len] = items[j];
                items[j] = swap;
            }
       }
        return items;
    }

    /*
    To initialize an array a of n elements to a randomly shuffled copy of source, both 0-based:
    a[0] ← source[0]
        for i from 1 to n − 1 do
    j ← random integer with 0 ≤ j ≤ i
    if j ≠ i
    a[i] ← a[j]
    a[j] ← source[i]
    */

}

public class TSTSet
{
            /*
            A ternary search tree works like a binary search tree, but with a center branch.
            It holds a char per branch, and branches off for each word in the dictionary.
            All chars are unique and this allows for very quick + specific traversal of the tree.

            Insert : TNode
            Args:
                TNode node : the current node
                int c      : the index of the char in word
                char[] word: the complete word as a character array.
            Desc: recursive insert

            Exists : boolean
            Args:
                String s : the string to search for.
            Desc: iterative search for the string.

            insertString : void
            Args:
                String n : the string to insert
            Desc: Wrapper for insert(root,0, n)

            Research for this implementation:
            http://en.wikipedia.org/wiki/Ternary_search_tree
            http://www.drdobbs.com/database/ternary-search-trees/184410528
         */


    protected TNode root;

    public TSTSet()
    {
        root = null;
    }

    public boolean is_empty()
    {
        return root == null;
    }

    public void insertString(String n)
    {
        char[] word = n.toCharArray();
        insert(root, 0, word);
    }

    public TNode insert(TNode node, int c, char[] word)
    {
        // make sure node and root are not null.
        if (node == null) { node = new TNode(word[c], false); }
        if (is_empty()) { root = node; }

        // lokid, go left
        if (word[c] < node.data) {
            node.left = insert(node.left, c, word); }
        // eqkid, go center
        else if (word[c] == node.data)
        {
            // end of word?
            if ((c+1) == word.length) { node.wordComplete = true; }
            else {
                c++; // next letter!
                node.center = insert(node.center, c, word);
            }
        }
        // hikid - go right
        else if (word[c] > node.data) { node.right = insert(node.right, c, word);
        }
        return node;
    }

    public void count(String word)
    {

    }

    public String stripChars(String name)
    {
        String n = name.replaceAll("\\W","");
        return n;
    }

    public boolean exists(String name)
    {
        if (name.equals("")) { return false; }
        // Start at root
        int position = 0;
        TNode current = root;
        // String to find by character so we can traverse the tree
        char[] toFind = name.toCharArray();
        if (toFind.length == 0) { return false; }

        while (current != null)
        {
            // go left
            if (toFind[position] < current.data) { current = current.left; }
            // go right
            else if (toFind[position] > current.data) { current = current.right; }
            // center or end
            else
            {   // end of word
                if (++position == toFind.length) { return current.wordComplete; }
                // go center
                else { current = current.center; }
            }
        }
        return false;

    }

    public String checkLine(String line)
    {
        StringBuilder sb =new StringBuilder();
        String[] words = line.split(" ");
            for (String w : words)
            {
                if (w.equals("")||w.equals(" ")) { return ""; }
                if (!(exists(stripChars(w.trim().toLowerCase()))))
                 {
                    sb.append(stripChars(w) + '\n');
                  }
            }
        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        long before = System.currentTimeMillis();

        String s1 = "http://pglaf.org/fundraising.";


        TSTSet TreeSet = new TSTSet();
        try {

        // get our dictionary and make it into a
        List<String> itemsList = readAllLines(new File("c:\\temp\\words2.txt").toPath(), Charset.defaultCharset());
            String[] items = itemsList.toArray(new String[itemsList.size()]);

            // shuffle items to create a more balanced tree.
            String[] shuffledItems = InsideOutShuffle.stringShuffle(items);

            // fill up the tree
            for (int n1 = 0; n1 <= 9; n1++) { TreeSet.insertString(""+n1+""); }
            for (String item : shuffledItems) { TreeSet.insertString(item.trim().toLowerCase()); }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("File exception...");
        }

        List<String> bookList = Files.readAllLines(new File("c:\\temp\\huckfin.txt").toPath(),Charset.forName("ISO-8859-1"));
        String[] searchWords = bookList.toArray(new String[bookList.size()]);



        long after = System.currentTimeMillis();

        System.out.println("Time to build Tree and Store book: (ms) " + (after-before) );

        long beforeSearch = System.currentTimeMillis();

        StringBuilder badwords = new StringBuilder();
        for (String s : searchWords)
        {
            if (!(s.equals("")))
            {
                String checked = TreeSet.checkLine(s);
                if(checked.length() > 0)
                {
                    badwords.append(checked);
                }
            }
        }

        long afterSearch = System.currentTimeMillis();


        if (badwords.length() > 0)
        {
            System.out.println("Misspelled Words:");
            System.out.print(badwords.toString());
            System.out.println("End");
        }
        else
        {
            System.out.println("No spelling errors!");
        }

        System.out.println("Time to scan spelling for book: (ms) " + (afterSearch-beforeSearch));

    }





}
