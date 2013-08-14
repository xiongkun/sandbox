/**
 *
 * Copyright 2013 University of Waterloo. All rights reserved.
 * Trie.java
 *
 */
package rsvp.answering.index.trie;

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

/**
 * @author Kun Xiong (xiongkun04@gmail.com)
 * @date 2013-08-12
 */
public class TrieTree
{
    public static ArrayList<TrieNode> nodes = new ArrayList<TrieNode>();

    public static ArrayList<TrieEdge> edges = new ArrayList<TrieEdge>();

    // private Map<Character, Integer> roots = new HashMap<Character, Integer>();

    private int root = 0;

    public TrieTree()
    {
        root = createNode();
    }

    public TrieTree(String path)
    {
        try
        {
            System.out.print("Loading...");
            long t1 = System.currentTimeMillis();

            BufferedReader eReader = new BufferedReader(new InputStreamReader(new FileInputStream(path + ".edges.trie"), "utf-8"));
            String line = null;
            while ((line = eReader.readLine()) != null)
            {
                // edges.add(Utils.toEdge(line));
                String[] iss = line.split("[\t]");
                edges.add(new TrieEdge(iss[0], Integer.parseInt(iss[1])));
            }
            eReader.close();

            BufferedReader nReader = new BufferedReader(new InputStreamReader(new FileInputStream(path + ".nodes.trie"), "utf-8"));
            line = null;
            while ((line = nReader.readLine()) != null)
            {
                // nodes.add(Utils.toNode(line));
                TrieNode node = new TrieNode();
                String[] iss = line.split("[\t]");
                // node.setIsWord(Boolean.parseBoolean(iss[0]));

                for (int i = 0; i < iss.length - 1; i = i + 2)
                {
                    node.addEdge(iss[0].charAt(0), Integer.parseInt(iss[1]));
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

    public void toFile(String path)
    {
        try
        {
            long t1 = System.currentTimeMillis();
            System.out.print("Writing...");
            BufferedWriter eWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path + ".edges.trie"), "utf-8"));
            for (TrieEdge edge : edges)
            {
                eWriter.append(edge.getLabel()).append("\t" + edge.getDest() + "\n");
            }
            eWriter.close();

            BufferedWriter nWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path + ".nodes.trie"), "utf-8"));
            for (TrieNode node : nodes)
            {
                // nWriter.append(node.isWord() + "\t");
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

    private void addWord(String word)
    {
        int nodeIdx = root;
        for (int j = 0; j < word.length(); j++)
        {
            int edgeIdx = node(nodeIdx).getEdge(word.charAt(j));
            if (edgeIdx == -1) // create edge
            {
                // int newNodeIdx = createNode();
                int newEdgeIdx = createEdge(word, -1);
                node(nodeIdx).addEdge(word.charAt(j), newEdgeIdx);
                return;
            }
            else
            // match through edge
            {
                String lable = edge(edgeIdx).getLabel();
                for (int i = 0; i < lable.length(); i++)
                {
                    if (j >= word.length()) // the end
                    {
                        // node(edge(edgeIdx).getDest()).setIsWord(true);
                        if (edge(edgeIdx).getDest() != -1)
                        {
                            System.out.println(edge(edgeIdx).toString());
                        }
                        return;
                    }
                    else
                    {
                        if (word.charAt(j) == lable.charAt(i)) // next character
                        {
                            j++;
                        }
                        else
                        // split edge
                        {
                            split(word, nodeIdx, edgeIdx, i, j);
                            return;
                        }
                    }
                }
                
                nodeIdx = edge(edgeIdx).getDest(); // go to next node
                if (nodeIdx == -1)
                {
                    nodeIdx = createNode();
                }
            }
        }
    }

    private void split(String word, int nodeIdx, int edgeIdx, int posInLabe, int posInWord)
    {

        String orgLable = edge(edgeIdx).getLabel();

        String newLable = orgLable.substring(0, posInLabe);

        String lastLable = orgLable.substring(posInLabe);

        String lastWord = orgLable.substring(posInWord);

        // set top cut edge
        int midNode = createNode();

        int topCutEdge = createEdge(newLable, midNode);

        node(nodeIdx).addEdge(newLable.charAt(0), topCutEdge);

        // set bottom cut edge
        edge(edgeIdx).setLabel(lastLable);

        node(midNode).addEdge(lastLable.charAt(0), edgeIdx);

        // set branch edge
//        int branchEndNode = createNode(true);

        int branchEdge = createEdge(lastWord, -1);

        node(midNode).addEdge(lastWord.charAt(0), branchEdge);
    }


    public int getPrefix(String argString)
    {
        return getNode(argString.toCharArray());
    }

    private boolean contains(String argString, boolean argIsWord)
    {
        int node = getNode(argString);
        return (node != -1 && node(node).isWord() && argIsWord) || (!argIsWord && node != -1);
    }

    private String search(String word,StringBuffer matched)
    {
        StringBuffer result = new StringBuffer();
        int nodeIdx = root;
        for (int j = 0; j < word.length(); j++)
        {
            int edgeIdx = node(nodeIdx).getEdge(word.charAt(j));
            if (edgeIdx == -1)
            {
                return result.toString();
            }
            else
            // match through edge
            {
                String lable = edge(edgeIdx).getLabel();
                for (int i = 0; i < lable.length(); i++)
                {
                    if (j >= word.length()) // the end
                    {
                        while(i < lable.length())
                        {
                            
                        }
                    }
                    else
                    {
                        if (word.charAt(j) == lable.charAt(i)) // next character
                        {
                            result.append(lable.charAt(i));
                            j++;
                        }
                        else
                        {
                            return null;
                        }
                    }
                }
                
                
            }
            lastEdgeIdx = edgeIdx;
        }
    }
    
    private void goDown(int nodeIds, StringBuffer result)
    {
        if()
    }

    protected static TrieNode node(int index)
    {
        return nodes.get(index);
    }

    protected static TrieEdge edge(int index)
    {
        return edges.get(index);
    }

    protected static int createEdge(String lable, int dest)
    {
        edges.add(new TrieEdge(lable, dest));
        return edges.size() - 1;
    }

    protected static int createNode()
    {
        nodes.add(new TrieNode());
        return nodes.size() - 1;
    }

    // protected static int createNode(boolean isWord)
    // {
    // nodes.add(new TrieNode(isWord));
    // return nodes.size() - 1;
    // }

    public static TrieTree construct(String path)
    {
        long t1 = System.currentTimeMillis();
        TrieTree tree = new TrieTree();
        System.out.print("Loading words from " + path + " ...");
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "utf-8"));
            int index = 0;
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                tree.addWord(line);
                if (index++ % 1000 == 0)
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
        // TrieTree tree = new TrieTree();
        // tree.addWord("五道口");
        // tree.addWord("西直门");
        // tree.addWord("六道口");
        //
        // System.out.println(tree.containsPrefix("道口"));
        // System.out.println(tree.containsWord("五道口"));

        TrieTree tree = construct("data/poi2.txt");

        tree.toFile("data/poi2");

        TrieTree tree2 = new TrieTree("data/poi2");

        System.out.println("Nodes : " + TrieTree.nodes.size());

//        System.out.println(tree2.containsPrefix("五道口"));
    }
}
