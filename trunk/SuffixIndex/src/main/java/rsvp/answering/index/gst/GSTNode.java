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

    @Override
    public String toString()
    {
        return indices + ", " + edges;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((edges == null) ? 0 : edges.hashCode());
        result = prime * result + ((indices == null) ? 0 : indices.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GSTNode other = (GSTNode) obj;
        if (edges == null)
        {
            if (other.edges != null)
                return false;
        }
        else if (!equals(edges, other.edges))
        {
            return false;
        }
        if (indices == null)
        {
            if (other.indices != null)
                return false;
        }
        else if (!equals(indices, other.indices))
        {
            return false;
        }
        return true;
    }

    private boolean equals(HashMap<String, Integer> map1, HashMap<String, Integer> map2)
    {
        if (map1.size() != map2.size())
        {
            System.err.println("Expect edge map size : " + map1.size());
            System.err.println("Target edge map size : " + map2.size());
            return false;
        }
        for (String ch : map1.keySet())
        {
            if (!map2.containsKey(ch) || map2.get(ch).intValue() != map1.get(ch).intValue())
            {
                System.err.println("Expect : " + ch + "=" + map1.get(ch));
                System.err.println("Target : " + ch + "=" + map2.get(ch));
                return false;
            }
        }
        return true;
    }

    private boolean equals(HashSet<Integer> set1, HashSet<Integer> set2)
    {
        if (set1.size() != set2.size())
        {

            System.err.println("Expect indices size : " + set1.size());
            System.err.println("Target indices size : " + set2.size());
            return false;
        }
        for (int ch : set1)
        {
            if (!set2.contains(ch))
            {
                System.err.println("Expect : " + set1.contains(ch));
                System.err.println("Target : " + set2.contains(ch));
                return false;
            }
        }
        return true;
    }

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

    private HashMap<String, Integer> edges;

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
        edges = new HashMap<String, Integer>();
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
        edges.put(Character.toString(ch), e);
    }

    int getEdge(char ch)
    {
        Integer intg = edges.get(Character.toString(ch));
        if (intg == null)
        {
            return -1;
        }
        return intg.intValue();
    }

    HashMap<String, Integer> getEdges()
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

    public void addIndex(int index)
    {
        indices.add(index);
    }

    public int getResultCount()
    {
        return indices.size();
    }
}
