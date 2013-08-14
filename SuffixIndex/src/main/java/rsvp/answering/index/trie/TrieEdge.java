/**
 *
 * Copyright 2013 University of Waterloo. All rights reserved.
 * Node.java
 *
 */
package rsvp.answering.index.trie;

/**
 * @author Kun Xiong (xiongkun04@gmail.com)
 * @date 2013-08-12
 */
public class TrieEdge
{

    private String label;

    private int dest;

    public TrieEdge(String label, int dest)
    {
        this.label = label;
        this.dest = dest;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public int getDest()
    {
        return dest;
    }

    public void setDest(int dest)
    {
        this.dest = dest;
    }

    @Override
    public String toString()
    {
        return "TrieEdge [label=" + label + ", dest=" + dest + "]";
    }

}