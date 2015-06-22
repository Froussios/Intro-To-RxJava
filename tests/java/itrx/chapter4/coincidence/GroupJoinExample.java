/*******************************************************************************
 * Copyright (c) 2015 Christos Froussios
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *******************************************************************************/
package itrx.chapter4.coincidence;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class GroupJoinExample {

	private static class Tuple<T1, T2> {
		public final T1 item1;
		public final T2 item2;
		
		public Tuple(T1 item1, T2 item2) {
			this.item1 = item1;
			this.item2 = item2;
		}
		
		public static <T1,T2> Tuple<T1,T2> create(T1 item1, T2 item2) {
			return new Tuple<T1,T2>(item1, item2);
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Tuple<?,?>) {
				Tuple<?,?> other = (Tuple<?,?>) obj;
				return this.item1.equals(other.item1) &&
						this.item2.equals(other.item2);
			}
			return false;				
		}
		
		@Override
		public String toString() {
			return "(" + item1 + ", " + item2 + ")";
		}
	}
	
	public void example() {
		Observable<String> left = 
		        Observable.interval(100, TimeUnit.MILLISECONDS)
		            .map(i -> "L" + i)
		            .take(6);
		Observable<String> right = 
		        Observable.interval(200, TimeUnit.MILLISECONDS)
		            .map(i -> "R" + i)
		            .take(3);

		left
		    .groupJoin(
		        right,
		        i -> Observable.never(),
		        i -> Observable.timer(0, TimeUnit.MILLISECONDS),
		        (l, rs) -> rs.toList().subscribe(list -> System.out.println(l + ": " + list))
		    )
		    .subscribe();
		
		// L0: [R0, R1, R2]
		// L1: [R0, R1, R2]
		// L2: [R1, R2]
		// L3: [R1, R2]
		// L4: [R2]
		// L5: [R2]
	}
	
	
	//
	// Test
	//
	
	@Test
	public void test() {
		TestSubscriber<Object> tester = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable<Long> left = 
		        Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
		            .take(6);
		Observable<Long> right = 
		        Observable.interval(200, TimeUnit.MILLISECONDS, scheduler)
		            .take(3);

		left
		    .groupJoin(
		        right,
		        i -> Observable.never(),
		        i -> Observable.timer(0, TimeUnit.MILLISECONDS, scheduler),
		        (l, rs) -> rs.toList().map(rl -> Tuple.create(l, rl))
		    )
		    .flatMap(i -> i)
		    .subscribe(tester);
		
		scheduler.advanceTimeTo(600, TimeUnit.MILLISECONDS);
		tester.assertReceivedOnNext(Arrays.asList(
			Tuple.create(0L, Arrays.asList(0L, 1L, 2L)),
			Tuple.create(1L, Arrays.asList(0L, 1L, 2L)),
			Tuple.create(2L, Arrays.asList(1L, 2L)),
			Tuple.create(3L, Arrays.asList(1L, 2L)),
			Tuple.create(4L, Arrays.asList(2L)),
			Tuple.create(5L, Arrays.asList(2L))
		));
	}
	
	@Test
	public void exampleEquivalence() {
		TestScheduler scheduler = Schedulers.test();
		TestSubscriber<Object> testerJoin = new TestSubscriber<>();
		TestSubscriber<Object> testerGroupJoin = new TestSubscriber<>();
		
		Observable<Long> left = 
		        Observable.interval(100, TimeUnit.MILLISECONDS, scheduler);
		Observable<Long> right = 
		        Observable.interval(100, TimeUnit.MILLISECONDS, scheduler);

	    left.join(
		        right,
		        i -> Observable.timer(150, TimeUnit.MILLISECONDS, scheduler),
		        i -> Observable.timer(0, TimeUnit.MILLISECONDS, scheduler),
		        (l,r) -> Tuple.create(l, r)
		    )
		    .take(10)
		    .subscribe(testerJoin);
		
	    left.groupJoin(
		        right,
		        i -> Observable.timer(150, TimeUnit.MILLISECONDS, scheduler),
		        i -> Observable.timer(0, TimeUnit.MILLISECONDS, scheduler),
		        (l, rs) -> rs.map(r -> Tuple.create(l, r))
		    )
		    .flatMap(i -> i)
		    .take(10)
		    .subscribe(testerGroupJoin);
	    
	    scheduler.advanceTimeTo(600, TimeUnit.MILLISECONDS);
	    testerJoin.assertReceivedOnNext(testerGroupJoin.getOnNextEvents());
	}
}
