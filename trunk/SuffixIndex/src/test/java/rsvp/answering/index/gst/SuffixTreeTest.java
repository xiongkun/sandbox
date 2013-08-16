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

import static rsvp.answering.index.common.Utils.getSubstrings;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import junit.framework.TestCase;

public class SuffixTreeTest extends TestCase {

    public void testBasicTreeGeneration() {
        GSuffixTree in = new GSuffixTree();

        String word = "cacao";
        in.put(word, 0);

        /* test that every substring is contained within the tree */
        for (String s : getSubstrings(word)) {
            assertTrue(in.flushAndSearch(s).contains(0));
        }
        assertNull(in.flushAndSearch("caco"));
        assertNull(in.flushAndSearch("cacaoo"));
        assertNull(in.flushAndSearch("ccacao"));

        in = new GSuffixTree();
        word = "bookkeeper";
        in.put(word, 0);
        
        for (String s : getSubstrings(word)) {
            assertTrue(in.flushAndSearch(s).contains(0));
        }
        assertNull(in.flushAndSearch("books"));
        assertNull(in.flushAndSearch("boke"));
        assertNull(in.flushAndSearch("ookepr"));
    }

    public void testWeirdword() {
        GSuffixTree in = new GSuffixTree();

        String word = "cacacato";
        in.put(word, 0);

        /* test that every substring is contained within the tree */
        for (String s : getSubstrings(word)) {
            assertTrue(in.flushAndSearch(s).contains(0));
        }
    }

    public void testDouble() {
        // test whether the tree can handle repetitions
        GSuffixTree in = new GSuffixTree();
        String word = "cacao";
        in.put(word, 0);
        in.put(word, 1);

        for (String s : getSubstrings(word)) {
            assertTrue(in.flushAndSearch(s).contains(0));
            assertTrue(in.flushAndSearch(s).contains(1));
        }
    }

    public void testBananaAddition() {
        GSuffixTree in = new GSuffixTree();
        String[] words = new String[] {"banana", "bano", "ba"};
        for (int i = 0; i < words.length; ++i) {
            in.put(words[i], i);
            
            for (String s : getSubstrings(words[i])) {
                Collection<Integer> result = in.flushAndSearch(s);
                assertNotNull("result null for string " + s + " after adding " + words[i], result);
                assertTrue("substring " + s + " not found after adding " + words[i], result.contains(i));
            }

        }

        // verify post-addition
        for (int i = 0; i < words.length; ++i) {
            for (String s : getSubstrings(words[i])) {
                assertTrue(in.flushAndSearch(s).contains(i));
            }
        }

        // add again, to see if it's stable
        for (int i = 0; i < words.length; ++i) {
            in.put(words[i], i + words.length);

            for (String s : getSubstrings(words[i])) {
                assertTrue(in.flushAndSearch(s).contains(i + words.length));
            }
        }

    }

    public void testAddition() {
        GSuffixTree in = new GSuffixTree();
        String[] words = new String[] {"cacaor" , "caricato", "cacato", "cacata", "caricata", "cacao", "banana"};
        for (int i = 0; i < words.length; ++i) {
            in.put(words[i], i);

            for (String s : getSubstrings(words[i])) {
                Collection<Integer> result = in.flushAndSearch(s);
                assertNotNull("result null for string " + s + " after adding " + words[i], result);
                assertTrue("substring " + s + " not found after adding " + words[i], result.contains(i));
            }
        }
        // verify post-addition
        for (int i = 0; i < words.length; ++i) {
            for (String s : getSubstrings(words[i])) {
                Collection<Integer> result = in.flushAndSearch(s);
                assertNotNull("result null for string " + s + " after adding " + words[i], result);
                assertTrue("substring " + s + " not found after adding " + words[i], result.contains(i));
            }
        }

        // add again, to see if it's stable
        for (int i = 0; i < words.length; ++i) {
            in.put(words[i], i + words.length);

            for (String s : getSubstrings(words[i])) {
                assertTrue(in.flushAndSearch(s).contains(i + words.length));
            }
        }
        
//        in.computeCount();
//        testResultsCount(in.getRoot());

        assertNull(in.flushAndSearch("aoca"));
    }

    public void testSampleAddition() {
        GSuffixTree in = new GSuffixTree();
        String[] words = new String[] {"libertypike",
            "franklintn",
            "carothersjohnhenryhouse",
            "carothersezealhouse",
            "acrossthetauntonriverfromdightonindightonrockstatepark",
            "dightonma",
            "dightonrock",
            "6mineoflowgaponlowgapfork",
            "lowgapky",
            "lemasterjohnjandellenhouse",
            "lemasterhouse",
            "70wilburblvd",
            "poughkeepsieny",
            "freerhouse",
            "701laurelst",
            "conwaysc",
            "hollidayjwjrhouse",
            "mainandappletonsts",
            "menomoneefallswi",
            "mainstreethistoricdistrict",
            "addressrestricted",
            "brownsmillsnj",
            "hanoverfurnace",
            "hanoverbogironfurnace",
            "sofsavannahatfergusonaveandbethesdard",
            "savannahga",
            "bethesdahomeforboys",
            "bethesda"};
        for (int i = 0; i < words.length; ++i) {
            in.put(words[i], i);
            
            for (String s : getSubstrings(words[i])) {
                Collection<Integer> result = in.flushAndSearch(s);
                assertNotNull("result null for string " + s + " after adding " + words[i], result);
                assertTrue("substring " + s + " not found after adding " + words[i], result.contains(i));
            }


        }
        // verify post-addition
        for (int i = 0; i < words.length; ++i) {
            for (String s : getSubstrings(words[i])) {
                assertTrue(in.flushAndSearch(s).contains(i));
            }
        }

        // add again, to see if it's stable
        for (int i = 0; i < words.length; ++i) {
            in.put(words[i], i + words.length);
            
            for (String s : getSubstrings(words[i])) {
                assertTrue(in.flushAndSearch(s).contains(i + words.length));
            }
        }

//        in.computeCount();
//        testResultsCount(in.getRoot());

        assertNull(in.flushAndSearch("aoca"));
    }

//    private void testResultsCount(GSTNode n) {
//        for (int e : n.getEdges().values()) {
//            assertEquals(n.getNodeIndices().size(), n.getResultCount());
//            testResultsCount(GSuffixTree.node(GSuffixTree.edge(e).getDest()));
//        }
//    }

    /* testing a test method :) */
    public void testGetSubstrings() {
        Collection<String> exp = new HashSet<String>();
        exp.addAll(Arrays.asList(new String[] {"w", "r", "d", "wr", "rd", "wrd"}));
        Collection<String> ret = getSubstrings("wrd");
        assertTrue(ret.equals(exp));
    }

}
