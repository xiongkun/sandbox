/**
 *
 * Copyright 2013 University of Waterloo. All rights reserved.
 * TrieTreeTest.java
 *
 */
package rsvp.answering.index.trie;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import rsvp.answering.index.common.Utils;

/**
 * @author Kun Xiong (xiongkun04@gmail.com)
 * @date 2013-08-16
 */
public class TrieTreeTest
{

    @Test
    public void testContains()
    {
        String[] words = new String[]
        { "libertypike", "franklintn", "carothersjohnhenryhouse", "carothersezealhouse",
                "acrossthetauntonriverfromdightonindightonrockstatepark", "dightonma", "dightonrock", "6mineoflowgaponlowgapfork",
                "lowgapky", "lemasterjohnjandellenhouse", "lemasterhouse", "70wilburblvd", "poughkeepsieny", "freerhouse", "701laurelst",
                "conwaysc", "hollidayjwjrhouse", "mainandappletonsts", "menomoneefallswi", "mainstreethistoricdistrict",
                "addressrestricted", "brownsmillsnj", "hanoverfurnace", "hanoverbogironfurnace", "sofsavannahatfergusonaveandbethesdard",
                "savannahga", "bethesdahomeforboys", "bethesda" };

        TrieTree tree = new TrieTree();

        for (int i = 0; i < words.length; ++i)
        {
            tree.addWord(words[i]);
            for (String s : Utils.getPrefixes(words[i]))
            {
                assertTrue("Prefix " + s + " not found after adding " + words[i], tree.containsPrefix(s));
                assertEquals(s, tree.match(s));
                if (s.equals(words[i]))
                {
                    assertTrue("Word " + s + " not found after adding " + words[i], tree.containsWord(s));
                }
                else
                {
                    assertFalse("Prefix " + s + " found after adding " + words[i], tree.containsWord(s));
                }
            }
        }
    }

}
