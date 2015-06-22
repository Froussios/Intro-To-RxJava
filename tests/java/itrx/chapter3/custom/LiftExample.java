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

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.observers.TestSubscriber;

public class LiftExample {

	public static class MyMap<T,R> implements Observable.Operator<R, T> {
	    private Func1<T,R> transformer;

	    public MyMap(Func1<T,R> transformer) {
	        this.transformer = transformer;
	    }

	    @Override
	    public Subscriber<? super T> call(Subscriber<? super R> subscriber) {
	        return new Subscriber<T>() {

	            @Override
	            public void onCompleted() {
	                if (!subscriber.isUnsubscribed())
	                    subscriber.onCompleted();
	            }

	            @Override
	            public void onError(Throwable e) {
	                if (!subscriber.isUnsubscribed())
	                    subscriber.onError(e);
	            }

	            @Override
	            public void onNext(T t) {
	                if (!subscriber.isUnsubscribed())
	                    subscriber.onNext(transformer.call(t));
	            }

	        };
	    }
	    
	    public static <T,R> MyMap<T,R> create(Func1<T,R> transformer) {
	    	return new MyMap<T, R>(transformer);
	    }
	}
	
	public void exampleLift() {
		Observable.range(0, 5)
		    .lift(MyMap.create(i -> i + "!"))
		    .subscribe(System.out::println);
		
		// 0!
		// 1!
		// 2!
		// 3!
		// 4!
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testLift() {
		TestSubscriber<String> tester = new TestSubscriber<>();
		
		Observable.range(0, 5)
		    .lift(MyMap.create(i -> i + "!"))
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList("0!", "1!", "2!", "3!", "4!"));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
}
