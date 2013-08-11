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
package com.abahgat.suffixtree;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.json.simple.JSONValue;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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

    public static GSuffixTree generateFromFile(String path)
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

    @SuppressWarnings("unchecked")
    public static String toJSONString(Edge edge)
    {
        JsonArray obj = new JsonArray();
        JsonElement ele = JsonElement()
        obj.add(JsonElement)
        return obj.toJSONString();
    }

    public static Edge toEdge(String s)
    {
        JsonArray obj = (JsonArray) JSONValue.parse(s);
        String label = (String) obj.get(0);
        int dest = (Integer) obj.get(1);
        Edge e = new Edge(label, dest);
        return e;
    }

    @SuppressWarnings("unchecked")
    public static String toJSONString(Node node)
    {
        JsonArray obj = new JsonArray();
        JsonArray data = new JsonArray();
        for (int ind : node.getData())
        {
            data.add(ind);
        }
        obj.add(data);
        JsonArray keys = new JsonArray();
        JsonArray values = new JsonArray();
        for (char key : node.getEdges().keySet())
        {
            keys.add(key);
            values.add(node.getEdges().get(key));
        }
        obj.add(keys);
        obj.add(values);
        return obj.toJSONString();
    }

    public static Node toNode(String s)
    {
        Node node = new Node();
        JsonArray obj = (JsonArray) JSONValue.parse(s);

        JsonArray data = (JsonArray) obj.get(0);
        JsonArray keys = (JsonArray) obj.get(1);
        JsonArray values = (JsonArray) obj.get(2);
        for (Object idx : data)
        {
            try
            {
                node.addIdx((Integer) idx);
            }
            catch (Exception e)
            {
                System.out.println("## "+idx);
            }
        }
        for (int i = 0; i < keys.size(); i++)
        {
            node.addEdge((Character) keys.get(i), (Integer) values.get(i));
        }
        return node;
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

        GSuffixTree tree = generateFromFile("data/poi2.txt");

        tree.toJSONFile("data/poi2");

        GSuffixTree tree2 = new GSuffixTree("data/poi2", true);

        System.out.println(GSuffixTree.nodes.size());

        System.out.println(GSuffixTree.edges.size());

        System.out.println(tree2.search("岗"));
        
    }
}
