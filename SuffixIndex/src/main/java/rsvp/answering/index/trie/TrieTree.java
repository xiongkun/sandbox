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

    // private Map<Character, Integer> roots = new HashMap<Character, Integer>();

    private int root = 0;

    public TrieTree()
    {
        root = createNode('\0', "");
    }

    public TrieTree(String path)
    {
        try
        {
            System.out.print("Loading...");
            long t1 = System.currentTimeMillis();

            BufferedReader nReader = new BufferedReader(new InputStreamReader(new FileInputStream(path + ".nodes.json"), "utf-8"));
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

            BufferedWriter nWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path + ".nodes.json"), "utf-8"));
            for (TrieNode node : nodes)
            {
                nWriter.append(node.getChar() + "\t").append(node.getLable()).append("\t").append(node.isWord() + "\n");

                for (char ch : node.getChildrenMap().keySet())
                {
                    nWriter.append(ch + "\t").append(node.getChildrenMap().get(ch) + "\t");
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

    public void addWord(String argWord)
    {
        if (argWord != null && !argWord.trim().isEmpty())
            addWord(argWord.toCharArray());
    }

    private void addWord(char[] argWord)
    {

        if (!node(root).containsChild(argWord[0]))
        {
            int newNodeIdx = TrieTree.createNode(argWord[0], "" + argWord[0]);
            node(root).putChild(argWord[0], newNodeIdx);
        }

        int  currentNodeIdx = node(root).getChild(argWord[0]);

        // int currentNodeIdx = node(root).getChild(argWord[0]);
        //
        // if (currentNodeIdx == -1)
        // {
        // int newNodeIdx = TrieTree.createNode(argWord[0], Character.toString(argWord[0]));
        // node(root).putChild(argWord[0], newNodeIdx);
        // }

        for (int i = 1; i < argWord.length; i++)
        {
            if (node(currentNodeIdx).getChild(argWord[i]) == -1)
            {
                int newNodeIdx = TrieTree.createNode(argWord[i], node(currentNodeIdx).getLable() + argWord[i]);
                node(currentNodeIdx).putChild(argWord[i], newNodeIdx);
            }

            currentNodeIdx = node(currentNodeIdx).getChild(argWord[i]);
        }

        node(currentNodeIdx).setIsWord(true);
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

    @Override
    public String toString()
    {
        return node(root).toString();
    }

    private boolean contains(char[] argString, boolean argIsWord)
    {
        int node = getNode(argString);
        return (node != -1 && node(node).isWord() && argIsWord) || (!argIsWord && node != -1);
    }

    private int getNode(char[] argString)
    {
        int currentNode = node(root).getChild(argString[0]);

        for (int i = 1; i < argString.length && currentNode != -1; i++)
        {
            currentNode = node(currentNode).getChild(argString[i]);

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

    protected static int createNode(char argChar, String argValue)
    {
        nodes.add(new TrieNode(argChar, argValue));
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
