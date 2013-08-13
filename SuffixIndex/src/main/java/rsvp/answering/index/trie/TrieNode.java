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

    private final Character ch;

    private final String value;

    private Map<Character, Integer> children = new HashMap<Character, Integer>();

    private boolean isValidWord;

    public TrieNode(char argChar, String argValue)
    {
        ch = argChar;
        value = argValue;
    }

//    public void addChild(int argChild)
//    {
////        if (children.containsKey(Character.toString(TrieTree.node(argChild).getChar())))
////        {
////            return false;
////        }
//
//        children.put(TrieTree.node(argChild).getChar(), argChild);
////        return true;
//    }
    
    public void putChild(char ch, int argChild)
    {
        children.put(ch, argChild);
    }

    public boolean containsChild(char c)
    {
        return children.containsKey(c);
    }

    public String getValue()
    {
        return value.toString();
    }

    public char getChar()
    {
        return ch;
    }
    
    public Map<Character, Integer> getChildrenMap()
    {
        return children;
    }

    public int getChild(char c)
    {
        Integer idx =  children.get(c);
        if(idx ==null)
        {
            return -1;
        }
        return idx.intValue();
    }

    public boolean isWord()
    {
        return isValidWord;
    }

    public void setIsWord(boolean argIsWord)
    {
        isValidWord = argIsWord;
    }

    public String toString()
    {
        return value;
    }

}