/* 
 * pLinguaCore: A JAVA library for Membrane Computing
 *              http://www.p-lingua.org
 *
 * Copyright (C) 2009  Research Group on Natural Computing
 *                     http://www.gcn.us.es
 *                      
 * This file is part of pLinguaCore.
 *
 * pLinguaCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * pLinguaCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with pLinguaCore.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gcn.plinguacore.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

/**
 * An indexed priority queue consists of:
 * <ul>
 * <li>A tree structure of ordered pairs of the form (i,t_i). This tree
 * structure has the property that each parent has a lower t_i than either of
 * its children</li>
 * <li>An index structure whose i-th element points to the position in the tree
 * that contains element (i,t_i)</li>
 * </ul>
 * 
 *  @author Research Group on Natural Computing (http://www.gcn.us.es)
 * 
 * @param <E>
 *            the type of elements held in this collection. It must implement
 *            Comparable
 */
public class IndexedPriorityQueue<E extends Comparable<E>> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7262509096684905459L;
	private Node<E>[] indexes; // index structure
	private Node<E> root;

	/**
	 * Constructor. It builds the tree structure and the index structure
	 * 
	 * @param c
	 *            Collection containing the elements for the priority queue. It
	 *            has to be ordered.
	 */
	public IndexedPriorityQueue(Collection<? extends E> c) {
		indexes = new Node[c.size()];
		int i = 0;
		int last = 0;
		Iterator<? extends E> it = c.iterator();
		while (it.hasNext()) {
			E elem = it.next();
			Node<E> n = new Node<E>(i, elem);
			indexes[i] = n;
			if (i == 0)
				root = n;

			else {
				Node<E> nodeLast = indexes[last];
				if (nodeLast.getLeftChild() == null)
					nodeLast.setLeftChild(n);

				else {
					nodeLast.setRightChild(n);
					last++; // because the last node is full of children :D
				}
				n.setParent(nodeLast);

			}
			i++;
		}
	}

	/**
	 * Swaps the tree nodes a and b and updates the index structure properly
	 * 
	 * @param a
	 * @param b
	 */
	public void swap(Node<E> a, Node<E> b) {
		if (a.equals(b))
			return;

		a = indexes[a.i()];
		b = indexes[b.i()];

		Node<E> leftChildOfB = b.getLeftChild();
		Node<E> rightChildOfB = b.getRightChild();
		Node<E> leftChildOfA = a.getLeftChild();
		Node<E> rightChildOfA = a.getRightChild();
		Node<E> parentOfA = a.parent();
		Node<E> parentOfB = b.parent();

		// caso general
		b.setLeftChild(leftChildOfA);
		b.setRightChild(rightChildOfA);
		a.setParent(parentOfB);
		a.setLeftChild(leftChildOfB);
		a.setRightChild(rightChildOfB);
		b.setParent(parentOfA);

		if (leftChildOfA != null && leftChildOfA.equals(b)) {
			b.setLeftChild(a);
			a.setParent(b);
		} else if (rightChildOfA != null && rightChildOfA.equals(b)) {
			b.setRightChild(a);
			a.setParent(b);
		} else if (leftChildOfB != null && leftChildOfB.equals(a)) {
			a.setLeftChild(b);
			b.setParent(a);
		} else if (rightChildOfB != null && rightChildOfB.equals(a)) {
			a.setRightChild(b);
			b.setParent(a);
		}

		// update parents in new children
		if (a.getLeftChild() != null)
			a.getLeftChild().setParent(a);
		if (a.getRightChild() != null)
			a.getRightChild().setParent(a);
		if (b.getLeftChild() != null)
			b.getLeftChild().setParent(b);
		if (b.getRightChild() != null)
			b.getRightChild().setParent(b);

		if (a.equals(root))
			root = b;
		else if (b.equals(root))
			root = a;

		if (parentOfA != null && !parentOfA.equals(b)) {
			if (a.equals(parentOfA.getLeftChild()))
				parentOfA.setLeftChild(b);
			else if (a.equals(parentOfA.getRightChild()))
				parentOfA.setRightChild(b);
		}

		if (parentOfB != null && !parentOfB.equals(a)) {
			if (b.equals(parentOfB.getLeftChild()))
				parentOfB.setLeftChild(a);
			else if (b.equals(parentOfB.getRightChild()))
				parentOfB.setRightChild(a);
		}

	}

	/**
	 * Updates a given node with a new value and moves entries in the tree
	 * structure until the tree has the property that each parent is less than
	 * its children
	 * 
	 * @param n
	 *            The node to be updated
	 * @param new_value
	 *            The new value for this node
	 */
	public void update(Node<E> n, E new_value) {
		if (n != null) {
			n.setValue(new_value);
			update_aux(n);
		}
	}

	private void update_aux(Node<E> n) {
		if (n != null) {
			Node<E> parentN = n.parent();
			// if value(n) < value(parent(n))
			if (parentN != null && n.value().compareTo(parentN.value()) < 0) {
				// swap n and parent(n)
				swap(n, parentN);
				// update_aux(parent(n))
				update_aux(parentN);
			}
			// else if value(n) > minimum value(children(n))
			else if (n.minimumValue() != null
					&& n.value().compareTo(n.minimumValue()) > 0) {
				// swap n and minimum_child(n)
				swap(n, n.minimumChild());
				// update_aux(minimum_child(n))
				update_aux(n.minimumChild());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String res = "Tree:\n+" + root.treeRepresentation(0);
		res += "\nIndex Structure:\n";

		for (int i = 0; i < indexes.length; i++)
			res += i + ". " + indexes[i].toString() + " -- "
					+ indexes[i].parent() + "\n";

		return res;

	}

	/**
	 * @return Number of elements in the priority queue
	 */
	public int size() {
		return indexes.length;
	}

	/**
	 * @return The minimum element stored in this priority queue
	 */
	public E findMinimum() {
		return root.value();
	}

	/**
	 * @param i
	 * @return the node
	 */

	public Node<E> getNode(int i) {
		return indexes[i];
	}

	public class Node<T extends E> implements Comparable<Node<T>>, Serializable {
		private int i;
		private T value;
		private Node<T> parent = null;
		private Node<T> rightChild = null;
		private Node<T> leftChild = null;

		/**
		 * @param i
		 * @param value
		 */
		public Node(int i, T value) {
			super();
			this.i = i;
			this.value = value;
		}

		@Override
		/*
		 * @throws ClassCastException if E does not implement Comparable and no
		 * Comparator is provided
		 */
		public int compareTo(Node<T> o) {
			return this.value().compareTo(o.value());
		}

		/**
		 * @return The parent of this node, null in the case of the root
		 */
		public Node<T> parent() {
			return parent;
		}

		/**
		 * @param parent
		 *            the parent to set
		 */
		public void setParent(Node<T> parent) {
			this.parent = parent;
		}

		/**
		 * @return the i
		 */
		public int i() {
			return i;
		}

		/**
		 * @param i
		 *            the i to set
		 */
		public void setI(int i) {
			this.i = i;
		}

		/**
		 * @return the t_i held by this node (i,t_i)
		 */
		public E value() {
			return value;
		}

		/**
		 * @param value
		 *            Value for this node, t_i
		 */
		public void setValue(T value) {
			this.value = value;
		}

		/**
		 * @param rightChild
		 *            the rightChild to set
		 */
		public void setRightChild(Node<T> rightChild) {
			this.rightChild = rightChild;
		}

		/**
		 * @param leftChild
		 *            the leftChild to set
		 */
		public void setLeftChild(Node<T> leftChild) {
			this.leftChild = leftChild;
		}

		/**
		 * @return the right child of this node
		 */
		public Node<T> getRightChild() {
			return rightChild;
		}

		/**
		 * @return the left child of this node
		 */
		public Node<T> getLeftChild() {
			return leftChild;
		}

		/**
		 * @return Minimum value held by the children of this node
		 */
		public E minimumValue() {

			E minimum = null;

			if (leftChild != null && rightChild != null) {
				// Compare both
				int cmp = leftChild.value().compareTo(rightChild.value());
				minimum = (cmp < 0 ? leftChild.value() : rightChild.value());
			} else if (rightChild != null)
				minimum = rightChild.value();
			else if (leftChild != null)
				minimum = leftChild.value();

			return minimum;
		}

		/**
		 * @return The child that holds the minimum value
		 */
		public Node<T> minimumChild() {
			Node<T> res = null;
			if (leftChild != null && rightChild != null) {
				// Compare both
				int cmp = leftChild.value().compareTo(rightChild.value());
				res = (cmp < 0 ? leftChild : rightChild);
			} else if (rightChild != null)
				res = rightChild;
			else if (leftChild != null)
				res = leftChild;

			return res;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;

			return this.i == ((Node<? extends E>) obj).i()
					&& this.value.equals(((Node<? extends E>) obj).value());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "(" + i + "," + value + ")";
		}

		public String treeRepresentation(int deep) {
			String res = "";
			for (int i = 0; i < deep; i++)
				res += "\t";

			res += "+" + toString();

			if (leftChild != null)
				res += "\n" + leftChild.treeRepresentation(deep + 1);

			if (rightChild != null)
				res += "\n" + rightChild.treeRepresentation(deep + 1);

			return res;

		}

	}
}
