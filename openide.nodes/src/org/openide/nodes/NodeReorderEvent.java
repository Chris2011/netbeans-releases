/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.openide.nodes;


/** Event describing change in the list of a node's children.
*
* @author Jaroslav Tulach
*/
public final class NodeReorderEvent extends NodeEvent {
    static final long serialVersionUID = 4479234495493767448L;

    /** list of new nodes indexes on the original positions */
    private int[] newIndices;

    /** Package private constructor to allow construction only
    * @param node the node that has changed
    * @param newIndices new indexes of the nodes
    */
    NodeReorderEvent(Node n, int[] newIndices) {
        super(n);
        this.newIndices = newIndices;
    }

    /** Get the new position of the child that had been at a given position.
    * @param i the original position of the child
    * @return the new position of the child
    */
    public int newIndexOf(int i) {
        return newIndices[i];
    }

    /** Get the permutation used for reordering.
    * @return array of integers used for reordering
    */
    public int[] getPermutation() {
        return newIndices;
    }

    /** Get the number of children reordered.
     * @return size of the permutation array */
    public int getPermutationSize() {
        return newIndices.length;
    }

    /** Human presentable information about the event */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getName());
        sb.append("[node="); // NOI18N
        sb.append(getSource());
        sb.append(", permutation = ("); // NOI18N

        int[] perm = getPermutation();

        for (int i = 0; i < perm.length;) {
            sb.append(perm[i]);

            if (++i < perm.length) {
                sb.append(", "); // NOI18N
            }
        }

        sb.append(")]"); // NOI18N

        return sb.toString();
    }
}
