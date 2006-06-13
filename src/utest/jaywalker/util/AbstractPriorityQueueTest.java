/**
 * Copyright 2005 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jaywalker.util;

import junit.framework.TestCase;

public abstract class AbstractPriorityQueueTest extends TestCase {
    public static class IntegerPriority implements Priority {
        private final int priority;

        public final static ListPriorityQueueTest.IntegerPriority SMALLEST = new ListPriorityQueueTest.IntegerPriority(10);

        public IntegerPriority(int priority) {
            this.priority = priority;
        }

        public boolean isHigherThan(Priority p) {
            ListPriorityQueueTest.IntegerPriority other = (ListPriorityQueueTest.IntegerPriority) p;
            return other.priority > priority;
        }

        public String toString() {
            return String.valueOf(priority);
        }
    }

    protected final static IntegerPriority PRIORITY_1 = new IntegerPriority(1);
    protected final static IntegerPriority PRIORITY_2 = new IntegerPriority(2);
    protected final static IntegerPriority PRIORITY_3 = new IntegerPriority(3);

    public abstract PriorityQueue createPriorityQueue();

    public void testEnqueueAndDequeueShouldReturnSameObject() {
        PriorityQueue queue = createPriorityQueue();
        Object expected = new Object();
        queue.init(IntegerPriority.SMALLEST);
        assertEquals(0, queue.size());
        assertNull(queue.peek());
        queue.enqueue(expected, PRIORITY_1);
        assertEquals(1, queue.size());
        assertSame(expected, queue.peek());
        assertSame(expected, queue.dequeue());
        assertEquals(0, queue.size());
        assertNull(queue.peek());
    }

    public void testEmptyQueueShouldReturnNull() {
        PriorityQueue queue = createPriorityQueue();
        queue.init(IntegerPriority.SMALLEST);
        assertEquals(0, queue.size());
        assertNull(queue.peek());
        assertNull(queue.dequeue());
        assertEquals(0, queue.size());
        assertNull(queue.peek());
    }

    public void testShouldReturnObjectsInOrder() {
        PriorityQueue queue = createPriorityQueue();
        queue.init(IntegerPriority.SMALLEST);

        String [] expects = {"1", "2", "3"};
        Priority [] priorities = {PRIORITY_1, PRIORITY_2, PRIORITY_3};

        for (int i = 0; i < 2; i++)
            for (int j = 0; j < 2; j++)
                if (i != j)
                    for (int k = 0; k < 2; k++)
                        if (i != k && j != k) {
                            assertEquals(0, queue.size());
                            assertNull(queue.peek());
                            queue.enqueue(expects[i], priorities[i]);
                            assertEquals(1, queue.size());
                            queue.enqueue(expects[j], priorities[j]);
                            assertEquals(2, queue.size());
                            queue.enqueue(expects[k], priorities[k]);
                            assertEquals(3, queue.size());
                            assertEquals("[ " + expects[0] + ", " + expects[1] + ", " + expects[2] + " ]", queue.toString());
                            assertEquals(3, queue.size());
                            assertSame(expects[0], queue.dequeue());
                            assertEquals(2, queue.size());
                            assertSame(expects[1], queue.dequeue());
                            assertEquals(1, queue.size());
                            assertSame(expects[2], queue.dequeue());
                            assertEquals(0, queue.size());
                            assertNull(queue.peek());
                        }

    }

    public void testRequeueOnEmptyQueueShouldReturnExpected() {
        PriorityQueue queue = createPriorityQueue();
        String expected = "1";
        queue.init(IntegerPriority.SMALLEST);
        assertEquals(0, queue.size());
        assertNull(queue.peek());
        queue.requeue(expected, PRIORITY_1);
        assertEquals(1, queue.size());
        assertSame(expected, queue.peek());
        assertSame(expected, queue.dequeue());
        assertEquals(0, queue.size());
        assertNull(queue.peek());
    }

    public void testEnqueueAndDequeueShouldReturnSameObjectAfterRequeue() {
        PriorityQueue queue = createPriorityQueue();
        Object expected = new Object();
        queue.init(IntegerPriority.SMALLEST);
        assertEquals(0, queue.size());
        assertNull(queue.peek());
        queue.enqueue(expected, PRIORITY_1);
        assertEquals(1, queue.size());
        assertSame(expected, queue.peek());
        queue.requeue(expected, PRIORITY_2);
        assertEquals(1, queue.size());
        assertSame(expected, queue.peek());
        assertSame(expected, queue.dequeue());
        assertEquals(0, queue.size());
        assertNull(queue.peek());
    }

    public void testEnqueueAndDequeueWithSamePrioritesShouldReturnInReverseOrder() {
        PriorityQueue queue = createPriorityQueue();
        String expected1 = "1";
        String expected2 = "2";
        String expected3 = "3";
        queue.init(IntegerPriority.SMALLEST);
        assertEquals(0, queue.size());
        assertNull(queue.peek());
        queue.enqueue(expected1, PRIORITY_1);
        assertEquals(1, queue.size());
        assertSame(expected1, queue.peek());
        queue.enqueue(expected2, PRIORITY_1);
        assertEquals(2, queue.size());
        assertSame(expected2, queue.peek());
        queue.enqueue(expected3, PRIORITY_1);
        assertEquals(3, queue.size());
        assertSame(expected3, queue.peek());
        assertSame(expected3, queue.dequeue());
        assertEquals(2, queue.size());
        assertSame(expected2, queue.peek());
        assertSame(expected2, queue.dequeue());
        assertEquals(1, queue.size());
        assertSame(expected1, queue.peek());
        assertSame(expected1, queue.dequeue());
        assertEquals(0, queue.size());
        assertNull(queue.peek());
    }

}
