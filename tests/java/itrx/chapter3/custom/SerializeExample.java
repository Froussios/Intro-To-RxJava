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
package itrx.chapter3.custom;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.Subscriber;
import rx.observers.TestSubscriber;

public class SerializeExample {

	public void exampleSafeSubscribe() {
		Observable<Integer> source = Observable.create(o -> {
		    o.onNext(1);
		    o.onNext(2);
		    o.onCompleted();
		    o.onNext(3);
		    o.onCompleted();
		});

		source.doOnUnsubscribe(() -> System.out.println("Unsubscribed"))
			.subscribe(
		        System.out::println,
		        System.out::println,
		        () -> System.out.println("Completed"));
		
		// 1
		// 2
		// Completed
		// Unsubscribed
	}
	
	public void exampleUnsafeSubscribe() {
		Observable<Integer> source = Observable.create(o -> {
		    o.onNext(1);
		    o.onNext(2);
		    o.onCompleted();
		    o.onNext(3);
		    o.onCompleted();
		});

		source.doOnUnsubscribe(() -> System.out.println("Unsubscribed"))
		    .unsafeSubscribe(new Subscriber<Integer>() {
		        @Override
		        public void onCompleted() {
		            System.out.println("Completed");
		        }

		        @Override
		        public void onError(Throwable e) {
		            System.out.println(e);
		        }

		        @Override
		        public void onNext(Integer t) {
		            System.out.println(t);
		        }
		});
		
		// 1
		// 2
		// Completed
		// 3
		// Completed
	}
	
	public void exampleSerialize() {
		Observable<Integer> source = Observable.create(o -> {
		        o.onNext(1);
		        o.onNext(2);
		        o.onCompleted();
		        o.onNext(3);
		        o.onCompleted();
		    })
		    .cast(Integer.class)
		    .serialize();;


		source.doOnUnsubscribe(() -> System.out.println("Unsubscribed"))
		    .unsafeSubscribe(new Subscriber<Integer>() {
		        @Override
		        public void onCompleted() {
		            System.out.println("Completed");
		        }
	
		        @Override
		        public void onError(Throwable e) {
		            System.out.println(e);
		        }
	
		        @Override
		        public void onNext(Integer t) {
		            System.out.println(t);
		        }
		});
		
//		1
//		2
//		Completed
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testSafeSubscribe() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<Integer> source = Observable.create(o -> {
		    o.onNext(1);
		    o.onNext(2);
		    o.onCompleted();
		    o.onNext(3);
		    o.onCompleted();
		});

		source.subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(1, 2));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
		tester.assertUnsubscribed();
	}
	
	@Test
	public void testUnsafeSubscribe() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<Integer> source = Observable.create(o -> {
		    o.onNext(1);
		    o.onNext(2);
		    o.onCompleted();
		    o.onNext(3);
		    o.onCompleted();
		});

		source.doOnUnsubscribe(() -> System.out.println("Unsubscribed"))
		    .unsafeSubscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(1, 2, 3));
		assertEquals(2, tester.getOnCompletedEvents().size());
		tester.assertNoErrors();
		assertFalse(tester.isUnsubscribed());
	}
	
	@Test
	public void testSerialize() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<Integer> source = Observable.create(o -> {
		        o.onNext(1);
		        o.onNext(2);
		        o.onCompleted();
		        o.onNext(3);
		        o.onCompleted();
		    })
		    .cast(Integer.class)
		    .serialize();;


		source.doOnUnsubscribe(() -> System.out.println("Unsubscribed"))
		    .unsafeSubscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(1, 2));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
		assertFalse(tester.isUnsubscribed());
	}

}
