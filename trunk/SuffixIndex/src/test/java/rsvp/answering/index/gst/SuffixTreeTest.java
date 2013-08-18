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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import junit.framework.TestCase;

public class SuffixTreeTest extends TestCase
{

    public void testBasicTreeGeneration()
    {
        GSuffixTree in = new GSuffixTree();

        String word = "cacao";
        in.addWord(word, 0);

        /* test that every substring is contained within the tree */
        for (String s : getSubstrings(word))
        {
            assertTrue(in.flushAndSearch(s).contains(0));
        }
        assertNull(in.flushAndSearch("caco"));
        assertNull(in.flushAndSearch("cacaoo"));
        assertNull(in.flushAndSearch("ccacao"));

        in = new GSuffixTree();
        word = "bookkeeper";
        in.addWord(word, 0);

        for (String s : getSubstrings(word))
        {
            assertTrue(in.flushAndSearch(s).contains(0));
        }
        assertNull(in.flushAndSearch("books"));
        assertNull(in.flushAndSearch("boke"));
        assertNull(in.flushAndSearch("ookepr"));
    }

    public void testWeirdword()
    {
        GSuffixTree in = new GSuffixTree();

        String word = "cacacato";
        in.addWord(word, 0);

        /* test that every substring is contained within the tree */
        for (String s : getSubstrings(word))
        {
            assertTrue(in.flushAndSearch(s).contains(0));
        }
    }

    public void testDouble()
    {
        // test whether the tree can handle repetitions
        GSuffixTree in = new GSuffixTree();
        String word = "cacao";
        in.addWord(word, 0);
        in.addWord(word, 1);

        for (String s : getSubstrings(word))
        {
            assertTrue(in.flushAndSearch(s).contains(0));
            assertTrue(in.flushAndSearch(s).contains(1));
        }
    }

    public void testBananaAddition()
    {
        GSuffixTree in = new GSuffixTree();
        String[] words = new String[]
        { "banana", "bano", "ba" };
        for (int i = 0; i < words.length; ++i)
        {
            in.addWord(words[i], i);

            for (String s : getSubstrings(words[i]))
            {
                Collection<Integer> result = in.flushAndSearch(s);
                assertNotNull("result null for string " + s + " after adding " + words[i], result);
                assertTrue("substring " + s + " not found after adding " + words[i], result.contains(i));
            }

        }

        // verify post-addition
        for (int i = 0; i < words.length; ++i)
        {
            for (String s : getSubstrings(words[i]))
            {
                assertTrue(in.flushAndSearch(s).contains(i));
            }
        }

        // add again, to see if it's stable
        for (int i = 0; i < words.length; ++i)
        {
            in.addWord(words[i], i + words.length);

            for (String s : getSubstrings(words[i]))
            {
                assertTrue(in.flushAndSearch(s).contains(i + words.length));
            }
        }

    }

    public void testAddition()
    {
        GSuffixTree in = new GSuffixTree();
        String[] words = new String[]
        { "cacaor", "caricato", "cacato", "cacata", "caricata", "cacao", "banana" };
        for (int i = 0; i < words.length; ++i)
        {
            in.addWord(words[i], i);

            for (String s : getSubstrings(words[i]))
            {
                Collection<Integer> result = in.flushAndSearch(s);
                assertNotNull("result null for string " + s + " after adding " + words[i], result);
                assertTrue("substring " + s + " not found after adding " + words[i], result.contains(i));
            }
        }
        // verify post-addition
        for (int i = 0; i < words.length; ++i)
        {
            for (String s : getSubstrings(words[i]))
            {
                Collection<Integer> result = in.flushAndSearch(s);
                assertNotNull("result null for string " + s + " after adding " + words[i], result);
                assertTrue("substring " + s + " not found after adding " + words[i], result.contains(i));
            }
        }

        // add again, to see if it's stable
        for (int i = 0; i < words.length; ++i)
        {
            in.addWord(words[i], i + words.length);

            for (String s : getSubstrings(words[i]))
            {
                assertTrue(in.flushAndSearch(s).contains(i + words.length));
            }
        }

        // in.computeCount();
        // testResultsCount(in.getRoot());

        assertNull(in.flushAndSearch("aoca"));
    }

    public void testSampleAddition()
    {
        GSuffixTree in = new GSuffixTree();
        String[] words = new String[]
        { "libertypike", "franklintn", "carothersjohnhenryhouse", "carothersezealhouse",
                "acrossthetauntonriverfromdightonindightonrockstatepark", "dightonma", "dightonrock", "6mineoflowgaponlowgapfork",
                "lowgapky", "lemasterjohnjandellenhouse", "lemasterhouse", "70wilburblvd", "poughkeepsieny", "freerhouse", "701laurelst",
                "conwaysc", "hollidayjwjrhouse", "mainandappletonsts", "menomoneefallswi", "mainstreethistoricdistrict",
                "addressrestricted", "brownsmillsnj", "hanoverfurnace", "hanoverbogironfurnace", "sofsavannahatfergusonaveandbethesdard",
                "savannahga", "bethesdahomeforboys", "bethesda" };
        for (int i = 0; i < words.length; ++i)
        {
            in.addWord(words[i], i);

            for (String s : getSubstrings(words[i]))
            {
                Collection<Integer> result = in.flushAndSearch(s);
                assertNotNull("result null for string " + s + " after adding " + words[i], result);
                assertTrue("substring " + s + " not found after adding " + words[i], result.contains(i));
                assertEquals("substring " + s + " not match after adding " + words[i], s, in.match(s));
            }

        }
        // verify post-addition
        for (int i = 0; i < words.length; ++i)
        {
            for (String s : getSubstrings(words[i]))
            {
                assertTrue(in.flushAndSearch(s).contains(i));
            }
        }

        // add again, to see if it's stable
        for (int i = 0; i < words.length; ++i)
        {
            in.addWord(words[i], i + words.length);

            for (String s : getSubstrings(words[i]))
            {
                assertTrue(in.flushAndSearch(s).contains(i + words.length));
            }
        }

        // in.computeCount();
        // testResultsCount(in.getRoot());

        assertNull(in.flushAndSearch("aoca"));
    }

    // private void testResultsCount(GSTNode n) {
    // for (int e : n.getEdges().values()) {
    // assertEquals(n.getNodeIndices().size(), n.getResultCount());
    // testResultsCount(GSuffixTree.node(GSuffixTree.edge(e).getDest()));
    // }
    // }

    public void testChinese()
    {
        GSuffixTree in = new GSuffixTree();
        String[] words = new String[]
        { "五道口","五道口城铁","蓝贝电动车头蓬专营店", "话机世界新市店", " 世纪华联超市天宁路加盟店", "金丹药房", "嘉缘商务宾馆", "牧羊人新建路店", "金衣皇童装专营店", "七厨房超级快餐NO.001", "兰溪水大吉奥汽车特约维修服务站", "阿飞手机大卖场",
                "世芳酒行专卖法国CASTEL名酒庄", "国强建设集团有限公司", "爱婴宝主题馆", "洪硕手机连锁祥符店", "富阳市万市中学", "洞桥大桥", "富阳市永顺工贸有限公司", "商辂丝绸公司叶家绢纺厂", "华联超市威坪加盟店",
                "中国电信威坪营业厅", "春江街道社区卫生中心", "绍兴希望包装有限公司", "杭州永波鞋业", "嘉兴市国鸿汽车运输有限公司汽车维修分公司", "杭州蓝达工艺制品公司", "双喜童车制造公司", "罗麦斯", "天一阁博物馆",
                "杭州富瑞司纺织有限公司", "浩明金属制品公司", "马·拉丁", "桐庐分水汽车站", "海力电子设备公司", "桐乡市中策纺织有限公司", "世纪华联超市NO.977", "地球村网吧文文店", "乐荣电线电器公司", "清园宾馆",
                "金华市新时代彩印厂", "定海区金伟针织机械厂", "成达汽车轮胎快修", "南雷网吧", "宁波丽欣针织服饰公司", "鼎盛物资租赁", "余姚市三七市镇幸福村村民委员会", "明国口腔诊所", "E-WORLD",
                "余姚市慈城镇三勤村村民委员会", "铭月轩美容生活馆NO.003", "银隆百货", "宁波市海曙区西门街道柳庄社区居民委员会", "乐山农贸市场" };
        for (int i = 0; i < words.length; ++i)
        {
            in.addWord(words[i], i);

            for (String s : getSubstrings(words[i]))
            {
                Collection<Integer> result = in.flushAndSearch(s);
                assertNotNull("result null for string " + s + " after adding " + words[i], result);
                assertTrue("substring " + s + " not found after adding " + words[i], result.contains(i));
                assertEquals("substring " + s + " not match after adding " + words[i], s, in.match(s));
            }
        }
        // verify post-addition
        for (int i = 0; i < words.length; ++i)
        {
            for (String s : getSubstrings(words[i]))
            {
                assertTrue(in.flushAndSearch(s).contains(i));
            }
        }

        // add again, to see if it's stable
        for (int i = 0; i < words.length; ++i)
        {
            in.addWord(words[i], i + words.length);

            for (String s : getSubstrings(words[i]))
            {
                assertTrue(in.flushAndSearch(s).contains(i + words.length));
            }
        }

        // in.computeCount();
        // testResultsCount(in.getRoot());

        assertNull(in.flushAndSearch("aoca"));
        
        String file = "data/test";
        try
        {
            in.toBinaryFile(file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        GSuffixTree fromFile = new GSuffixTree(file);
        for (int i = 0; i < words.length; ++i)
        {
            for (String s : getSubstrings(words[i]))
            {
                Collection<Integer> result = fromFile.search(s);
                assertNotNull("result null for string " + s + " after adding " + words[i], result);
                assertTrue("substring " + s + " not found after adding " + words[i], result.contains(i));
                assertEquals("substring " + s + " not match after adding " + words[i], s, fromFile.match(s));
            }
        }
        
    }

    /* testing a test method :) */
    public void testGetSubstrings()
    {
        Collection<String> exp = new HashSet<String>();
        exp.addAll(Arrays.asList(new String[]
        { "w", "r", "d", "wr", "rd", "wrd" }));
        Collection<String> ret = getSubstrings("wrd");
        assertTrue(ret.equals(exp));
    }

}
