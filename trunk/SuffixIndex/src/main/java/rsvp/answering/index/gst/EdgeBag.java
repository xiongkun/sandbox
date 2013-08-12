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

class EdgeBag
{
    private char[] chars;

    private int[] values;

    private static final int BSEARCH_THRESHOLD = 6;

    void put(char c, int e)
    {
        // if (c != (char) (byte) c) {
        // throw new IllegalArgumentException("Illegal input character " + c +
        // ".");
        // }

        if (chars == null)
        {
            chars = new char[0];
            values = new int[0];
        }
        int idx = search(c);

        if (idx < 0)
        {
            int currsize = chars.length;
            char[] copy = new char[currsize + 1];
            System.arraycopy(chars, 0, copy, 0, currsize);
            chars = copy;
            int[] copy1 = new int[currsize + 1];
            System.arraycopy(values, 0, copy1, 0, currsize);
            values = copy1;
            chars[currsize] = (char) c;
            values[currsize] = e;
            currsize++;
            if (currsize > BSEARCH_THRESHOLD)
            {
                sortArrays();
            }
        }
        else
        {
            values[idx] = e;
        }
    }

    int get(char c)
    {
        // if (c != (char) (byte) c) {
        // throw new IllegalArgumentException("Illegal input character " + c +
        // ".");
        // }

        int idx = search(c);
        if (idx < 0)
        {
            return -1;
        }
        return values[idx];
    }

    private int search(char c)
    {
        if (chars == null)
            return -1;

        if (chars.length > BSEARCH_THRESHOLD)
        {
            return java.util.Arrays.binarySearch(chars, c);
        }

        for (int i = 0; i < chars.length; i++)
        {
            if (c == chars[i])
            {
                return i;
            }
        }
        return -1;
    }

    int[] values()
    {
        return values == null ? new int[0] : values;
    }

    /**
     * A trivial implementation of sort, used to sort chars[] and values[] according to the data in chars.
     * 
     * It was preferred to faster sorts (like qsort) because of the small sizes (<=36) of the collections involved.
     */
    private void sortArrays()
    {
        for (int i = 0; i < chars.length; i++)
        {
            for (int j = i; j > 0; j--)
            {
                if (chars[j - 1] > chars[j])
                {
                    char swap = chars[j];
                    chars[j] = chars[j - 1];
                    chars[j - 1] = swap;

                    int swapEdge = values[j];
                    values[j] = values[j - 1];
                    values[j - 1] = swapEdge;
                }
            }
        }
    }
}
