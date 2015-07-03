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
package itrx.chapter3.combining;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import rx.Observable;
import rx.Subscriber;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;

public class RepeatExample {

	private static class PrintSubscriber extends Subscriber<Object>{
	    private final String name;
	    public PrintSubscriber(String name) {
	        this.name = name;
	    }
	    @Override
	    public void onCompleted() {
	        System.out.println(name + ": Completed");
	    }
	    @Override
	    public void onError(Throwable e) {
	        System.out.println(name + ": Error: " + e);
	    }
	    @Override
	    public void onNext(Object v) {
	        System.out.println(name + ": " + v);
	    }
	}
	
	public void exampleRepeat() {
		Observable<Integer> words = Observable.range(0,2);

		words.repeat()
			.take(4)
		    .subscribe(System.out::println);
		
		// 0
		// 1
		// 0
		// 1
	}
	
	public void exampleRepeat2() {
		Observable<Integer> words = Observable.range(0,2);

		words.repeat(2)
		    .subscribe(System.out::println);
		
		// 0
		// 1
		// 0
		// 1
	}
	
	public void exampleRepeatWhen2() {
		Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

		values
		    .take(2)
		    .repeatWhen(ob -> {
		        return ob.take(2);
		    })
		    .subscribe(new PrintSubscriber("repeatWhen"));
		
		// repeatWhen: 0
		// repeatWhen: 1
		// repeatWhen: 0
		// repeatWhen: 1
		// repeatWhen: Completed
	}
	
	public void exampleRepeatWithInterval() {
		Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS);

		values
		    .take(5) // Numbers 0 to 4
		    .repeatWhen((ob)-> {
		        ob.subscribe();
		        return Observable.interval(2, TimeUnit.SECONDS);
		    }) // Repeat 0 to 4 every 2s, forever
		    .take(2) // Stop after second repetition 
		    .subscribe(new PrintSubscriber("repeatWhen"));
		
		// repeatWhen: 0
		// repeatWhen: 1
		// repeatWhen: 2
		// repeatWhen: 3
		// repeatWhen: 4
		// repeatWhen: 0
		// repeatWhen: 1
		// repeatWhen: 2
		// repeatWhen: 3
		// repeatWhen: 4
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testRepeat() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<Integer> words = Observable.range(0,2);

		words.repeat()
			.take(4)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(0,1,0,1));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testRepeat2() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<Integer> words = Observable.range(0,2);

		words.repeat(2)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(0,1,0,1));
		tester.assertTerminalEvent();
		tester.assertNoErrors();

	}
	
	@Test
	public void testRepeatWhen2() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<Integer> values = Observable.range(0, 2);

		values
		    .repeatWhen(ob -> {
		        return ob.take(2);
		    })
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(0,1,0,1));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testRepeatWithInterval() {
		TestSubscriber<Long> tester = new TestSubscriber<>();
		TestScheduler scheduler = Schedulers.test();
		
		Observable<Long> values = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler);

		values
		    .take(5) // Numbers 0 to 4
		    .repeatWhen((ob)-> {
		        ob.subscribe();
		        return Observable.interval(2, TimeUnit.SECONDS, scheduler);
		    }) // Repeat 0 to 4 every 2s, forever
		    .subscribe(tester);
		
		scheduler.advanceTimeBy(4, TimeUnit.SECONDS);
		
		tester.assertReceivedOnNext(Arrays.asList(0L,1L,2L,3L,4L,0L,1L,2L,3L,4L));
		tester.assertNoErrors();
	}

}
