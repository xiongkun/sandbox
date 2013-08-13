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

            BufferedReader nReader = new BufferedReader(new InputStreamReader(new FileInputStream(path + ".trie"), "utf-8"));
            String line = null;
            while ((line = nReader.readLine()) != null)
            {
                String[] iss = line.split("[\t]");
                TrieNode node = new TrieNode(iss[0].charAt(0), iss[1]);
                node.setIsWord(Boolean.parseBoolean(iss[2]));

                line = nReader.readLine();
                iss = line.split("[\t]");
                for (int i = 0; i < iss.length - 1; i = i + 2)
                {
                    node.putChild(iss[0].charAt(0), Integer.parseInt(iss[1]));
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

            BufferedWriter nWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path + ".trie"), "utf-8"));
            for (TrieNode node : nodes)
            {
                nWriter.append(node.getChar() + "\t").append(node.getValue()).append("\t").append(node.isWord() + "\n");

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

    // public TrieTree(List<String> argInitialWords)
    // {
    // for (String word : argInitialWords)
    // {
    // addWord(word);
    // }
    // }

    // public void addWord(String argWord)
    // {
    // if (argWord != null && !argWord.trim().isEmpty())
    // addWord(argWord.toCharArray());
    // }

    private void addWord(char[] argWord)
    {

        if (!node(root).containsEdge(argWord[0]))
        {
            int newNodeIdx = TrieTree.createNode(argWord[0], "" + argWord[0]);
            node(root).putChild(argWord[0], newNodeIdx);
        }

        int currentNodeIdx = node(root).getEdge(argWord[0]);

        // int currentNodeIdx = node(root).getChild(argWord[0]);
        //
        // if (currentNodeIdx == -1)
        // {
        // int newNodeIdx = TrieTree.createNode(argWord[0], Character.toString(argWord[0]));
        // node(root).putChild(argWord[0], newNodeIdx);
        // }

        for (int i = 1; i < argWord.length; i++)
        {
            if (node(currentNodeIdx).getEdge(argWord[i]) == -1)
            {
                int newNodeIdx = TrieTree.createNode(argWord[i], node(currentNodeIdx).getValue() + argWord[i]);
                node(currentNodeIdx).putChild(argWord[i], newNodeIdx);
            }

            currentNodeIdx = node(currentNodeIdx).getEdge(argWord[i]);
        }

        node(currentNodeIdx).setIsWord(true);
    }

    private void addWord(String word)
    {
        // char firstChar = word.charAt(0);
        // if (!node(root).containsChild(firstChar))
        // {
        // int newNodeIdx = TrieTree.createNode(firstChar, word);
        // node(root).putChild(firstChar, newNodeIdx);
        // return;
        // }
        //
        // int currentNodeIdx = node(root).getChild(firstChar);
        //
        // // int currentNodeIdx = node(root).getChild(argWord[0]);
        // //
        // // if (currentNodeIdx == -1)
        // // {
        // // int newNodeIdx = TrieTree.createNode(argWord[0], Character.toString(argWord[0]));
        // // node(root).putChild(argWord[0], newNodeIdx);
        // // }
        // int i = 1;
        // while (i < word.length())
        // {
        // if (node(currentNodeIdx).getChild(word.charAt(i)) == -1)
        // {
        // int newNodeIdx = TrieTree.createNode(word[i], node(currentNodeIdx).getValue() + word[i]);
        // node(currentNodeIdx).putChild(word[i], newNodeIdx);
        // }
        //
        // currentNodeIdx = node(currentNodeIdx).getChild(word[i]);
        // }
        //
        // node(currentNodeIdx).setIsWord(true);
    }

    private void addWord(String word, int nodeIdx)
    {
        // char firstChar = word.charAt(0);
        // if (!node(nodeIdx).containsEdge(firstChar))
        // {
        // int newNodeIdx = createNode();
        // int newEdgeIdx = createEdge(word, newNodeIdx);
        // node(nodeIdx).addEdge(firstChar, newEdgeIdx);
        // return;
        // }

        int edgeIdx = node(nodeIdx).getEdge(word.charAt(i));
        if (edgeIdx == -1)
        {
            // create -> (edge -> node) for last whole string
            int newNodeIdx = createNode();
            int newEdgeIdx = createEdge(word, newNodeIdx);
            node(nodeIdx).addEdge(word.charAt(i), newEdgeIdx);
            return;
        }
        String lable = edge(edgeIdx).getLabel();
        for (int i = 0; i < lable.length(); i++)
        {
            if (i >= word.length()) // the end
            {
                node(edge(edgeIdx).getDest()).setIsWord(true);
                return;
            }
            else
            {
                if (word.charAt(i) == lable.charAt(i))
                {
                    i++;
                    continue;
                }
                else
                {
                    split(i,nodeIdx);
                }
            }


            if (node(currentNodeIdx).getEdge(word.charAt(i)) == -1)
            {

                // int newNodeIdx = TrieTree.createNode(word[i], node(currentNodeIdx).getValue() + word[i]);
                // node(currentNodeIdx).putChild(word[i], newNodeIdx);
            }

            currentNodeIdx = node(currentNodeIdx).getEdge(word[i]);
        }

        node(currentNodeIdx).setIsWord(true);
    }

    private void split(int nodeIdx, int cutPos)
    {
        String orgLable = node(nodeIdx).getLable();

        String newLable = orgLable.substring(0, cutPos);

        node(nodeIdx).setLable(newLable);

        String lastLable = orgLable.substring(cutPos);

        addWord(lastLable, nodeIdx);
    }

    public boolean containsPrefix(String argPrefix)
    {
        return contains(argPrefix.toCharArray(), false);
    }

    public boolean containsWord(String argWord)
    {
        return contains(argWord.toCharArray(), true);
    }

    public int getWord(String argString)
    {
        int node = getNode(argString.toCharArray());
        return node != -1 && node(node).isWord() ? node : null;
    }

    public int getPrefix(String argString)
    {
        return getNode(argString.toCharArray());
    }

    private boolean contains(char[] argString, boolean argIsWord)
    {
        int node = getNode(argString);
        return (node != -1 && node(node).isWord() && argIsWord) || (!argIsWord && node != -1);
    }

    private int getNode(char[] argString)
    {
        int currentNode = node(root).getEdge(argString[0]);

        for (int i = 1; i < argString.length && currentNode != -1; i++)
        {
            currentNode = node(currentNode).getEdge(argString[i]);

            if (currentNode == -1)
            {
                return -1;
            }
        }

        return currentNode;
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

        TrieTree tree = construct("data/poi.txt");

        tree.toFile("data/poi");

        TrieTree tree2 = new TrieTree("data/poi");

        System.out.println("Nodes : " + TrieTree.nodes.size());

        System.out.println(tree2.containsPrefix("五道口"));
    }
}
