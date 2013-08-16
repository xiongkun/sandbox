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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Represents a node of the generalized suffix tree graph
 * 
 * @see GSuffixTree
 */
class GSTNode
{

    /**
     * The payload array used to store the data (indexes) associated with this node. In this case, it is used to store
     * all property indexes.
     * 
     * As it is handled, it resembles an ArrayList: when it becomes full it is copied to another bigger array (whose
     * size is equals to data.length + INCREMENT).
     * 
     * Originally it was a List<Integer> but it took too much memory, changing it to int[] take less memory because
     * indexes are stored using native types.
     */
    private HashSet<Integer> indices;

    /**
     * The set of edges starting from this node
     */

    private HashMap<Character, Integer> edges;

    /**
     * The suffix link as described in Ukkonen's paper. if str is the string denoted by the path from the root to this,
     * this.suffix is the node denoted by the path that corresponds to str without the first char.
     */
    public int suffix;

    /**
     * Creates a new Node
     */
    GSTNode()
    {
        edges = new HashMap<Character, Integer>();
        suffix = -1;
        indices = new HashSet<Integer>();
    }

    public void setNodeIndices(Collection<Integer> all)
    {
        this.indices.clear();
        this.indices.addAll(all);
    }
    
    public void addIndices(Collection<Integer> indices)
    {
        this.indices.addAll(indices);
    }

    public HashSet<Integer> getNodeIndices()
    {
        return indices;
    }

    /**
     * Tests whether a node contains a reference to the given index.
     * 
     * <b>IMPORTANT</b>: it works because the array is sorted by construction
     * 
     * @param index the index to look for
     * @return true <tt>this</tt> contains a reference to index
     */
    public boolean containsIndex(int index)
    {
        return indices.contains(index);
    }

    void addEdge(char ch, int e)
    {
        edges.put(ch, e);
    }

    int getEdge(char ch)
    {
        Integer intg = edges.get(ch);
        if (intg == null)
        {
            return -1;
        }
        return intg.intValue();
    }

    HashMap<Character, Integer> getEdges()
    {
        return edges;
    }

    int getSuffix()
    {
        return suffix;
    }

    void setSuffix(int suffix)
    {
        this.suffix = suffix;
    }

    public void addIdx(int index)
    {
        // if (lastIdx == data.length)
        // {
        // int[] copy = new int[data.length + INCREMENT];
        // System.arraycopy(data, 0, copy, 0, data.length);
        // data = copy;
        // }
        // data[lastIdx++] = index;
        indices.add(index);
    }

    public int getResultCount()
    {
        return indices.size();
    }
}
