/**
 * Copyright 2012 Alessandro Bahgat Shehata
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rsvp.answering.index.gst;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A Generalized Suffix Tree, based on the Ukkonen's paper "On-line construction of suffix trees"
 * http://www.cs.helsinki.fi/u/ukkonen/SuffixT1withFigs.pdf
 * 
 * Allows for fast storage and fast(er) retrieval by creating a tree-based index out of a set of strings. Unlike common
 * suffix trees, which are generally used to build an index out of one (very) long string, a Generalized Suffix Tree can
 * be used to build an index over many strings.
 * 
 * Its main operations are put and search: Put adds the given key to the index, allowing for later retrieval of the
 * given value. Search can be used to retrieve the set of all the values that were put in the index with keys that
 * contain a given input.
 * 
 * In particular, after put(K, V), search(H) will return a set containing V for any string H that is substring of K.
 * 
 * The overall complexity of the retrieval operation (search) is O(m) where m is the length of the string to search
 * within the index.
 * 
 * Although the implementation is based on the original design by Ukkonen, there are a few aspects where it differs
 * significantly.
 * 
 * The tree is composed of a set of nodes and labeled edges. The labels on the edges can have any length as long as it's
 * greater than 0. The only constraint is that no two edges going out from the same node will start with the same
 * character.
 * 
 * Because of this, a given (startNode, stringSuffix) pair can denote a unique path within the tree, and it is the path
 * (if any) that can be composed by sequentially traversing all the edges (e1, e2, ...) starting from startNode such
 * that (e1.label + e2.label + ...) is equal to the stringSuffix. See the search method for details.
 * 
 * The union of all the edge labels from the root to a given leaf node denotes the set of the strings explicitly
 * contained within the GST. In addition to those Strings, there are a set of different strings that are implicitly
 * contained within the GST, and it is composed of the strings built by concatenating e1.label + e2.label + ... + $end,
 * where e1, e2, ... is a proper path and $end is prefix of any of the labels of the edges starting from the last node
 * of the path.
 * 
 * This kind of "implicit path" is important in the testAndSplit method.
 * 
 */
public class GSuffixTree
{

    public static ArrayList<GSTNode> nodes = new ArrayList<GSTNode>();

    public static ArrayList<GSTEdge> edges = new ArrayList<GSTEdge>();

    /**
     * The index of the last item that was added to the GST
     */
    private int last = 0;

    /**
     * The root of the suffix tree
     */
    private int root = 0;

    /**
     * The last leaf that was added during the update operation
     */
    private int activeLeaf = 0;

    public GSuffixTree()
    {
        root = createNode();
        activeLeaf = root;
    }

    public GSuffixTree(String path)
    {
        try
        {
            System.out.print("Loading...");
            long t1 = System.currentTimeMillis();

            BufferedReader eReader = new BufferedReader(new InputStreamReader(new FileInputStream(path + ".edges.gst"), "utf-8"));
            String line = null;
            while ((line = eReader.readLine()) != null)
            {
                // edges.add(Utils.toEdge(line));
                String[] iss = line.split("[\t]");
                edges.add(new GSTEdge(iss[0], Integer.parseInt(iss[1])));
            }
            eReader.close();

            BufferedReader nReader = new BufferedReader(new InputStreamReader(new FileInputStream(path + ".nodes.gst"), "utf-8"));
            line = null;
            while ((line = nReader.readLine()) != null)
            {
                // nodes.add(Utils.toNode(line));
                GSTNode node = new GSTNode();
                String[] iss = line.split("[\t]");
                for (String is : iss)
                {
                    node.addIdx(Integer.parseInt(is));
                }
                line = nReader.readLine();
                iss = line.split("[\t]");
                for (int i = 0; i < iss.length - 1; i = i + 2)
                {
                    node.addEdge(iss[0].charAt(i), Integer.parseInt(iss[i + 1]));
                }
                nodes.add(node);
            }
            nReader.close();
            long t2 = System.currentTimeMillis();
            System.out.println("Done : " + (t2 - t1) + "ms");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public GSuffixTree(String path, boolean fromBinary)
    {
        try
        {
            System.out.print("Loading...");
            long t1 = System.currentTimeMillis();

            BufferedReader eReader = new BufferedReader(new InputStreamReader(new FileInputStream(path + ".edges.gst.bin"), "utf-8"));
            int edgeNum = eReader.read();
            for (int i = 0; i < edgeNum; i++)
            {
                int lableCharNum = eReader.read();
                char[] lableChar = new char[lableCharNum];
                eReader.read(lableChar);
                int dest = eReader.read();
                edges.add(new GSTEdge(new String(lableChar), dest));
            }
            eReader.close();

            BufferedReader nReader = new BufferedReader(new InputStreamReader(new FileInputStream(path + ".nodes.gst.bin"), "utf-8"));

            int nodeNum = nReader.read();
            for (int i = 0; i < nodeNum; i++)
            {
                GSTNode node = new GSTNode();
                int dataNum = nReader.read();
                for (int j = 0; j < dataNum; j++)
                {
                    node.addIdx(nReader.read());
                }
                int edgeMapNum = nReader.read();
                for (int j = 0; j < edgeMapNum; j++)
                {
                    char ch = (char) nReader.read();
                    node.addEdge(ch, nReader.read());
                }
                nodes.add(node);
            }

            nReader.close();
            long t2 = System.currentTimeMillis();
            System.out.println("Done : " + (t2 - t1) + "ms");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void toStringFile(String path)
    {
        try
        {
            long t1 = System.currentTimeMillis();
            System.out.print("Writing...");
            BufferedWriter eWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path + ".edges.gst"), "utf-8"));
            for (GSTEdge edge : edges)
            {
                eWriter.append(edge.getLabel()).append("\t" + edge.getDest() + "\n");
            }
            eWriter.close();

            BufferedWriter nWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path + ".nodes.gst"), "utf-8"));
            for (GSTNode node : nodes)
            {
                for (int idx : node.getData())
                {
                    nWriter.append(idx + "\t");
                }
                nWriter.append("\n");

                for (char ch : node.getEdges().keySet())
                {
                    nWriter.append(ch + "\t").append(node.getEdges().get(ch) + "\t");
                }
                nWriter.append("\n");
            }
            nWriter.close();
            long t2 = System.currentTimeMillis();
            System.out.println("Done : " + (t2 - t1) + "ms");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void toBinaryFile(String path)
    {
        try
        {
            long t1 = System.currentTimeMillis();
            System.out.print("Writing...");
            BufferedWriter eWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path + ".edges.gst.bin"), "utf-8"));
            eWriter.write(edges.size());
            for (GSTEdge edge : edges)
            {
                eWriter.write(edge.getLabel().length());
                eWriter.write(edge.getLabel().toCharArray());
                eWriter.write(edge.getDest());
            }
            eWriter.close();

            BufferedWriter nWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path + ".nodes.gst.bin"), "utf-8"));
            nWriter.write(nodes.size());
            for (GSTNode node : nodes)
            {
                nWriter.write(node.getData().size());
                for (int idx : node.getData())
                {
                    nWriter.write(idx);
                }
                nWriter.write(node.getEdges().size());
                for (char ch : node.getEdges().keySet())
                {
                    nWriter.write(ch);
                    nWriter.write(node.getEdges().get(ch));
                }
            }
            nWriter.close();
            long t2 = System.currentTimeMillis();
            System.out.println("Done : " + (t2 - t1) + "ms");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Searches for the given word within the GST.
     * 
     * Returns all the indexes for which the key contains the <tt>word</tt> that was supplied as input.
     * 
     * @param word the key to search for
     * @return the collection of indexes associated with the input <tt>word</tt>
     */
    public Collection<Integer> search(String word)
    {
        return search(word, -1);
    }

    /**
     * Searches for the given word within the GST and returns at most the given number of matches.
     * 
     * @param word the key to search for
     * @param results the max number of results to return
     * @return at most <tt>results</tt> values for the given word
     */
    public Collection<Integer> search(String word, int results)
    {
        GSTNode tmpNode = searchNode(word);
        if (tmpNode == null)
        {
            return null;
        }
        return tmpNode.getData(results);
    }

    /**
     * Searches for the given word within the GST and returns at most the given number of matches.
     * 
     * @param word the key to search for
     * @param to the max number of results to return
     * @return at most <tt>results</tt> values for the given word
     * @see GSuffixTree#ResultInfo
     */
    public ResultInfo searchWithCount(String word, int to)
    {
        GSTNode tmpNode = searchNode(word);
        if (tmpNode == null)
        {
            return new ResultInfo(Collections.<Integer> emptyList(), 0);
        }

        return new ResultInfo(tmpNode.getData(to), tmpNode.getResultCount());
    }

    /**
     * Returns the tree node (if present) that corresponds to the given string.
     */
    private GSTNode searchNode(String word)
    {
        /*
         * Verifies if exists a path from the root to a node such that the concatenation of all the labels on the path
         * is a superstring of the given word. If such a path is found, the last node on it is returned.
         */
        int currentNode = root;
        int currentEdge = -1;

        for (int i = 0; i < word.length(); ++i)
        {
            char ch = word.charAt(i);
            // follow the edge corresponding to this char
            currentEdge = node(currentNode).getEdge(ch);
            if (-1 == currentEdge)
            {
                // there is no edge starting with this char
                return null;
            }
            else
            {
                String label = edge(currentEdge).getLabel();
                int lenToMatch = Math.min(word.length() - i, label.length());
                if (!word.regionMatches(i, label, 0, lenToMatch))
                {
                    // the label on the edge does not correspond to the one in
                    // the string to search
                    return null;
                }

                if (label.length() >= word.length() - i)
                {
                    return node(edge(currentEdge).getDest());
                }
                else
                {
                    // advance to next node
                    currentNode = edge(currentEdge).getDest();
                    i += lenToMatch - 1;
                }
            }
        }

        return null;
    }

    /**
     * Take word as prefix, searches for the longest match.
     * 
     * @param word the key to search for
     * @return matched word for the given word
     */
    public String match(String word)
    {
        /*
         * Verifies if exists a path from the root to a node such that the concatenation of all the labels on the path
         * is a superstring of the given word. If such a path is found, the last node on it is returned.
         */
        StringBuffer result = new StringBuffer();
        int currentNode = root;
        int currentEdge = -1;

        for (int i = 0; i < word.length(); ++i)
        {
            char ch = word.charAt(i);
            // follow the edge corresponding to this char
            currentEdge = node(currentNode).getEdge(ch);
            if (-1 == currentEdge)
            {
                // there is no edge starting with this char
                return null;
            }
            else
            {
                String label = edge(currentEdge).getLabel();
                result.append(label);
                int lenToMatch = Math.min(word.length() - i, label.length());
                if (!word.regionMatches(i, label, 0, lenToMatch))
                {
                    // the label on the edge does not correspond to the one in
                    // the string to search
                    return null;
                }

                if (label.length() >= word.length() - i)
                {
                    return result.toString();
                }
                else
                {
                    // advance to next node
                    currentNode = edge(currentEdge).getDest();
                    i += lenToMatch - 1;
                }
            }
        }

        return null;
    }

    /**
     * Adds the specified <tt>index</tt> to the GST under the given <tt>key</tt> .
     * 
     * Entries must be inserted so that their indexes are in non-decreasing order, otherwise an IllegalStateException
     * will be raised.
     * 
     * @param key the string key that will be added to the index
     * @param index the value that will be added to the index
     * @throws IllegalStateException if an invalid index is passed as input
     */
    protected void put(String key, int index) throws IllegalStateException
    {
        if (index < last)
        {
            throw new IllegalStateException("The input index must not be less than any of the previously inserted ones. Got " + index
                    + ", expected at least " + last);
        }
        else
        {
            last = index;
        }

        // reset activeLeaf
        activeLeaf = root;

        String remainder = key;
        int s = root;

        // proceed with tree construction (closely related to procedure in
        // Ukkonen's paper)
        String text = "";
        // iterate over the string, one char at a time
        for (int i = 0; i < remainder.length(); i++)
        {
            // line 6
            text += remainder.charAt(i);
            // use intern to make sure the resulting string is in the pool.
            text = text.intern();

            // line 7: update the tree with the new transitions due to this new
            // char
            Pair<Integer, String> active = update(s, text, remainder.substring(i), index);
            // line 8: make sure the active pair is canonical
            active = canonize(active.getFirst(), active.getSecond());

            s = active.getFirst();
            text = active.getSecond();
        }

        // add leaf suffix link, is necessary
        if (-1 == node(activeLeaf).getSuffix() && activeLeaf != root && activeLeaf != s)
        {
            node(activeLeaf).setSuffix(s);
        }

    }

    /**
     * Tests whether the string stringPart + t is contained in the subtree that has inputs as root. If that's not the
     * case, and there exists a path of edges e1, e2, ... such that e1.label + e2.label + ... + $end = stringPart and
     * there is an edge g such that g.label = stringPart + rest
     * 
     * Then g will be split in two different edges, one having $end as label, and the other one having rest as label.
     * 
     * @param s2 the starting node
     * @param stringPart the string to search
     * @param t the following character
     * @param remainder the remainder of the string to add to the index
     * @param value the value to add to the index
     * @return a pair containing true/false depending on whether (stringPart + t) is contained in the subtree starting
     *         in inputs the last node that can be reached by following the path denoted by stringPart starting from
     *         inputs
     * 
     */
    private Pair<Boolean, Integer> testAndSplit(final int s2, final String stringPart, final char t, final String remainder, final int value)
    {
        // descend the tree as far as possible
        Pair<Integer, String> ret = canonize(s2, stringPart);
        int s = ret.getFirst();
        String str = ret.getSecond();

        if (!"".equals(str))
        {
            int g = node(s).getEdge(str.charAt(0));

            String label = edge(g).getLabel();
            // must see whether "str" is substring of the label of an edge
            if (label.length() > str.length() && label.charAt(str.length()) == t)
            {
                return new Pair<Boolean, Integer>(true, s);
            }
            else
            {
                // need to split the edge
                String newlabel = label.substring(str.length());
                assert (label.startsWith(str));

                // build a new node
                int r = createNode();
                // build a new edge
                int newedge = createEdge(str, r);

                edge(g).setLabel(newlabel);

                // link s -> r
                node(r).addEdge(newlabel.charAt(0), g);
                node(s).addEdge(str.charAt(0), newedge);

                return new Pair<Boolean, Integer>(false, r);
            }

        }
        else
        {
            int e = node(s).getEdge(t);
            if (-1 == e)
            {
                // if there is no t-transtion from s
                return new Pair<Boolean, Integer>(false, s);
            }
            else
            {
                if (remainder.equals(edge(e).getLabel()))
                {
                    // update payload of destination node
                    node(edge(e).getDest()).addRef(value);
                    return new Pair<Boolean, Integer>(true, s);
                }
                else if (remainder.startsWith(edge(e).getLabel()))
                {
                    return new Pair<Boolean, Integer>(true, s);
                }
                else if (edge(e).getLabel().startsWith(remainder))
                {
                    // need to split as above
                    int newNode = createNode(value);

                    int newEdge = createEdge(remainder, newNode);

                    edge(e).setLabel(edge(e).getLabel().substring(remainder.length()));

                    node(newNode).addEdge(edge(e).getLabel().charAt(0), e);

                    node(s).addEdge(t, newEdge);

                    return new Pair<Boolean, Integer>(false, s);
                }
                else
                {
                    // they are different words. No prefix. but they may still
                    // share some common substr
                    return new Pair<Boolean, Integer>(true, s);
                }
            }
        }

    }

    /**
     * Return a (Node, String) (n, remainder) pair such that n is a farthest descendant of s (the input node) that can
     * be reached by following a path of edges denoting a prefix of inputstr and remainder will be string that must be
     * appended to the concatenation of labels from s to n to get inpustr.
     */
    private Pair<Integer, String> canonize(final int s, final String inputstr)
    {

        if ("".equals(inputstr))
        {
            return new Pair<Integer, String>(s, inputstr);
        }
        else
        {
            int currentNode = s;
            String str = inputstr;
            int g = node(s).getEdge(str.charAt(0));
            // descend the tree as long as a proper label is found
            while (g != -1 && str.startsWith(edge(g).getLabel()))
            {
                str = str.substring(edge(g).getLabel().length());
                currentNode = edge(g).getDest();
                if (str.length() > 0)
                {
                    g = node(currentNode).getEdge(str.charAt(0));
                }
            }

            return new Pair<Integer, String>(currentNode, str);
        }
    }

    /**
     * Updates the tree starting from inputNode and by adding stringPart.
     * 
     * Returns a reference (Node, String) pair for the string that has been added so far. This means: - the Node will be
     * the Node that can be reached by the longest path string (S1) that can be obtained by concatenating consecutive
     * edges in the tree and that is a substring of the string added so far to the tree. - the String will be the
     * remainder that must be added to S1 to get the string added so far.
     * 
     * @param inputNode the node to start from
     * @param stringPart the string to add to the tree
     * @param rest the rest of the string
     * @param value the value to add to the index
     */
    private Pair<Integer, String> update(int inputNode, final String stringPart, final String rest, final int value)
    {
        int s = inputNode;
        String tempstr = stringPart;
        char newChar = stringPart.charAt(stringPart.length() - 1);

        // line 1
        int oldroot = root;

        // line 1b
        Pair<Boolean, Integer> ret = testAndSplit(s, tempstr.substring(0, tempstr.length() - 1), newChar, rest, value);

        int r = ret.getSecond();
        boolean endpoint = ret.getFirst();

        int leaf = -1;
        // line 2
        while (!endpoint)
        {
            // line 3
            int tempEdge = node(r).getEdge(newChar);
            if (-1 != tempEdge)
            {
                // such a node is already present. This is one of the main
                // differences from Ukkonen's case:
                // the tree can contain deeper nodes at this stage because
                // different strings were added by previous iterations.
                leaf = edge(tempEdge).getDest();
            }
            else
            {
                // must build a new leaf
                leaf = createNode(value);
                int newedge = createEdge(rest, leaf);
                node(r).addEdge(newChar, newedge);
            }

            // update suffix link for newly created leaf
            if (activeLeaf != root)
            {
                node(activeLeaf).setSuffix(leaf);
            }
            activeLeaf = leaf;

            // line 4
            if (oldroot != root)
            {
                node(oldroot).setSuffix(r);
            }

            // line 5
            oldroot = r;

            // line 6
            if (-1 == node(s).getSuffix())
            { // root node
                assert (root == s);
                // this is a special case to handle what is referred to as node
                // _|_ on the paper
                tempstr = tempstr.substring(1);
            }
            else
            {
                Pair<Integer, String> canret = canonize(node(s).getSuffix(), safeCutLastChar(tempstr));
                s = canret.getFirst();
                // use intern to ensure that tempstr is a reference from the
                // string pool
                tempstr = (canret.getSecond() + tempstr.charAt(tempstr.length() - 1)).intern();
            }

            // line 7
            ret = testAndSplit(s, safeCutLastChar(tempstr), newChar, rest, value);
            r = ret.getSecond();
            endpoint = ret.getFirst();

        }

        // line 8
        if (oldroot != root)
        {
            node(oldroot).setSuffix(r);
        }
        oldroot = root;

        return new Pair<Integer, String>(s, tempstr);
    }

    private static int createNode()
    {
        nodes.add(new GSTNode());
        return nodes.size() - 1;
    }

    private static int createNode(int ref)
    {
        GSTNode n = new GSTNode();
        n.addRef(ref);
        nodes.add(n);
        return nodes.size() - 1;
    }

    protected static int createEdge(String rest, int leaf)
    {
        edges.add(new GSTEdge(rest, leaf));
        return edges.size() - 1;
    }

    protected static GSTNode node(int index)
    {
        return nodes.get(index);
    }

    protected static GSTEdge edge(int index)
    {
        return edges.get(index);
    }

    protected GSTNode getRoot()
    {
        return node(root);
    }

    private String safeCutLastChar(String seq)
    {
        if (seq.length() == 0)
        {
            return "";
        }
        return seq.substring(0, seq.length() - 1);
    }

    public int computeCount()
    {
        return node(root).computeAndCacheCount();
    }

    /**
     * An utility object, used to store the data returned by the GeneralizedSuffixTree
     * GeneralizedSuffixTree.searchWithCount method. It contains a collection of results and the total number of results
     * present in the GST.
     * 
     * @see GSuffixTree#searchWithCount(java.lang.String, int)
     */
    public static class ResultInfo
    {

        /**
         * The total number of results present in the database
         */
        public int totalResults;

        /**
         * The collection of (some) results present in the GST
         */
        public Collection<Integer> results;

        public ResultInfo(Collection<Integer> results, int totalResults)
        {
            this.totalResults = totalResults;
            this.results = results;
        }
    }

    /**
     * A private class used to return a tuples of two elements
     */
    private class Pair<A, B>
    {

        private A first;

        private B second;

        public Pair(A first, B second)
        {
            this.first = first;
            this.second = second;
        }

        public A getFirst()
        {
            return first;
        }

        public B getSecond()
        {
            return second;
        }
    }

    public static GSuffixTree construct(String path)
    {
        long t1 = System.currentTimeMillis();
        GSuffixTree tree = new GSuffixTree();
        System.out.print("Loading words from " + path + " ...");
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "utf-8"));
            int index = 0;
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                tree.put(line, index++);
                if (index % 1000 == 0)
                {
                    System.out.println(index);
                }
            }
            long t2 = System.currentTimeMillis();
            System.out.println("Done : " + index + " : " + (t2 - t1) + "ms");
            return tree;
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args)
    {
        // GSuffixTree in = new GSuffixTree();
        // String[] words = new String[] { "白日依山尽", "飞流直下三千尺", "两只黄鹂鸣翠柳" };
        // for (int i = 0; i < words.length; ++i) {
        // in.put(words[i], i);
        // }
        //
        // System.out.println(in.search("飞流"));
        // System.out.println(in.search("两"));

        GSuffixTree tree = construct("data/poi2.txt");

        tree.toBinaryFile("data/poi2");

        GSuffixTree tree2 = new GSuffixTree("data/poi2", true);

        System.out.println("Nodes : " + GSuffixTree.nodes.size());

        System.out.println("Edges : " + GSuffixTree.edges.size());

        System.out.println(tree.search("门"));
        System.out.println(tree2.search("门"));
//        System.out.println(tree2.computeCount());
    }
}
