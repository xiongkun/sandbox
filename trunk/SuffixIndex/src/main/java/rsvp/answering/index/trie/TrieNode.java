/**
 *
 * Copyright 2013 University of Waterloo. All rights reserved.
 * Node.java
 *
 */
package rsvp.answering.index.trie;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kun Xiong (xiongkun04@gmail.com)
 * @date 2013-08-12
 */
public class TrieNode
{

    private Map<Character, Integer> edges = new HashMap<Character, Integer>();
    
    private boolean isWord = false;

    public TrieNode()
    {
    }
    
    public TrieNode(boolean isWord)
    {
        this.isWord = isWord;
    }
    
    public void addEdge(char ch, int argChild)
    {
        edges.put(ch, argChild);
    }

    public boolean containsEdge(char c)
    {
        return edges.containsKey(c);
    }

    public Map<Character, Integer> getEdges()
    {
        return edges;
    }

    public int getEdge(char c)
    {
        Integer idx = edges.get(c);
        if (idx == null)
        {
            return -1;
        }
        return idx.intValue();
    }

    
    @Override
    public String toString()
    {
        return edges.toString();
    }

    public boolean isWord()
    {
        return isWord;
    }

    public void setIsWord(boolean isWord)
    {
        this.isWord = isWord;
    }

    public void removeEdge(char key)
    {
        this.edges.remove(key);
    }
}