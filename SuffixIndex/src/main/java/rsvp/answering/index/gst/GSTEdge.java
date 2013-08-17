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

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + dest;
        result = prime * result + ((label == null) ? 0 : label.hashCode());
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
        GSTEdge other = (GSTEdge) obj;
        if (dest != other.dest)
            return false;
        if (label == null)
        {
            if (other.label != null)
                return false;
        }
        else if (!label.equals(other.label))
            return false;
        return true;
    }

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
