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

/**
 * Represents an Edge in the Suffix Tree. It has a label and a destination Node
 */
class GSTEdge
{

    private String label;

    private int dest;

    public GSTEdge(String label, int dest)
    {
        this.label = label;
        this.dest = dest;
    }

    public String getLabel()
    {
        return label;
    }

    @Override
    public String toString()
    {
        return label + "\t" + dest;
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

}
