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
package rsvp.answering.index.common;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import rsvp.answering.index.gst.GSuffixTree;
import rsvp.answering.index.trie.TrieTree;

public class Utils
{

    /**
     * Normalize an input string
     * 
     * @return <tt>in</tt> all lower-case, without any non alphanumeric character
     */
    public static String normalize(String in)
    {
        StringBuilder out = new StringBuilder();
        String l = in.toLowerCase();
        for (int i = 0; i < l.length(); ++i)
        {
            char c = l.charAt(i);
            if (c >= 'a' && c <= 'z' || c >= '0' && c <= '9')
            {
                out.append(c);
            }
        }
        return out.toString();
    }

    /**
     * Computes the set of all the substrings contained within the <tt>str</tt>
     * 
     * It is fairly inefficient, but it is used just in tests ;)
     */
    public static Set<String> getSubstrings(String str)
    {
        Set<String> ret = new HashSet<String>();
        // compute all substrings
        for (int len = 1; len <= str.length(); ++len)
        {
            for (int start = 0; start + len <= str.length(); ++start)
            {
                String itstr = str.substring(start, start + len);
                ret.add(itstr);
            }
        }

        return ret;
    }

    public static Set<String> getPrefixes(String str)
    {
        Set<String> ret = new HashSet<String>();
        for (int i = 1; i <= str.length(); ++i)
        {
            String itstr = str.substring(0, i);
            ret.add(itstr);
        }
        return ret;
    }

    public static Set<String> getSuffixes(String str)
    {
        Set<String> ret = new HashSet<String>();
        for (int i = 0; i < str.length() - 1; ++i)
        {
            String itstr = str.substring(i);
            ret.add(itstr);
        }
        return ret;
    }

    public static HashMap<String, Integer> generateHashMap(String path)
    {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "utf-8"));
            int index = 0;
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                map.put(line, index++);
            }
            System.out.println("Load " + index + " to hashmap!");
            return map;
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

    // @SuppressWarnings("unchecked")
    // public static String toJSONString(GSTEdge edge)
    // {
    // JSONArray obj = new JSONArray();
    // obj.add(edge.getLabel());
    // obj.add(edge.getDest());
    // return obj.toJSONString();
    // }
    //
    // public static GSTEdge toEdge(String s)
    // {
    // JSONArray obj = (JSONArray) JSONValue.parse(s);
    // String label = (String) obj.get(0);
    // int dest = (Integer) obj.get(1);
    // GSTEdge e = new GSTEdge(label, dest);
    // return e;
    // }
    //
    // @SuppressWarnings("unchecked")
    // public static String toJSONString(GSTNode node)
    // {
    // JSONArray obj = new JSONArray();
    // JSONArray data = new JSONArray();
    // for (int ind : node.getData())
    // {
    // data.add(ind);
    // }
    // obj.add(data);
    // JSONArray keys = new JSONArray();
    // JSONArray values = new JSONArray();
    // for (char key : node.getEdges().keySet())
    // {
    // keys.add(key);
    // values.add(node.getEdges().get(key));
    // }
    // obj.add(keys);
    // obj.add(values);
    // return obj.toJSONString();
    // }
    //
    // public static GSTNode toNode(String s)
    // {
    // GSTNode node = new GSTNode();
    // JSONArray obj = (JSONArray) JSONValue.parse(s);
    //
    // JSONArray data = (JSONArray) obj.get(0);
    // JSONArray keys = (JSONArray) obj.get(1);
    // JSONArray values = (JSONArray) obj.get(2);
    // for (Object idx : data)
    // {
    // node.addIdx((Integer) idx);
    // }
    // for (int i = 0; i < keys.size(); i++)
    // {
    // node.addEdge((Character) keys.get(i), (Integer) values.get(i));
    // }
    // return node;
    // }

    public static String findLongestSubstring(GSuffixTree tree, String word)
    {
        String longestSubstr = "";
        for (int i = 0; i < word.length() - 1; i++)
        {
            String suffix = word.substring(i);
            String matched = tree.match(suffix);
            if (matched.length() > longestSubstr.length())
            {
                longestSubstr = matched;
            }
        }
        return longestSubstr;
    }

    public static String findLongestSubstring(TrieTree tree, String word)
    {
        String longestSubstr = "";
        for (int i = 0; i < word.length() - 1; i++)
        {
            String suffix = word.substring(i);
            String matched = tree.match(suffix);
            if (matched.length() > longestSubstr.length())
            {
                longestSubstr = matched;
            }
        }
        return longestSubstr;
    }

    public static byte[] toBytes(int intg)
    {
        byte[] ret = new byte[4];
        ret[0] = (byte) ((intg >> 24) & 0xff);
        ret[1] = (byte) ((intg >> 16) & 0xff);
        ret[2] = (byte) ((intg >> 8) & 0xff);
        ret[3] = (byte) (intg & 0xff);

        return ret;
    }

    public static int toInteger(byte[] bytes)
    {
        return ((bytes[0] & 0xff) << 24) | ((bytes[1] & 0xff) << 16) | ((bytes[2] & 0xff) << 8) | (bytes[3] & 0xff);
    }

    public static void main(String[] args)
    {
        // GSuffixTree.buildTree("data/poi2.txt");
        //
        // GSuffixTree tree = new GSuffixTree("data/poi2.txt");
        //
        // System.out.println(findLongestSubstring(tree, "北京五道口城铁附近"));

        TrieTree tree2 = new TrieTree("data/poi.txt");

        System.out.println(findLongestSubstring(tree2, "北京五道口城铁附近"));
    }
}
