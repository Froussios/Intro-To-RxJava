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
package itrx.chapter2.transforming;

import java.util.Arrays;

import org.junit.Test;

import rx.Notification;
import rx.Observable;
import rx.Subscriber;
import rx.observers.TestSubscriber;

public class MaterializeExample {
	
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

	
	public void exampleMaterialize() {
		Observable<Integer> values = Observable.range(0,3);

		values.take(3)
		    .materialize()
		    .subscribe(new PrintSubscriber("Materialize"));
		
		// Materialize: [rx.Notification@a4c802e9 OnNext 0]
		// Materialize: [rx.Notification@a4c802ea OnNext 1]
		// Materialize: [rx.Notification@a4c802eb OnNext 2]
		// Materialize: [rx.Notification@18d48ace OnCompleted]
		// Materialize: Completed
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testMaterialize() {
		TestSubscriber<Notification<Integer>> tester = new TestSubscriber<>();
		
		Observable<Integer> values = Observable.range(0,3);

		values.take(3)
		    .materialize()
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(
				Notification.createOnNext(0),
				Notification.createOnNext(1),
				Notification.createOnNext(2),
				Notification.createOnCompleted()
		));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
}
