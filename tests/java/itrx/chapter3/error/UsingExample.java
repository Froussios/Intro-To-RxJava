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
package itrx.chapter3.error;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

public class UsingExample {

	public void exampleUsing() {
		Observable<Character> values = Observable.using(
		    () -> {
		        String resource = "MyResource";
		        System.out.println("Leased: " + resource);
		        return resource;
		    },
		    (resource) -> {
		        return Observable.create(o -> {
		            for (Character c : resource.toCharArray())
		                o.onNext(c);
		            o.onCompleted();
		        });
		    },
		    (resource) -> System.out.println("Disposed: " + resource));

		values
		    .subscribe(
		        v -> System.out.println(v),
		        e -> System.out.println(e));
		
		// Leased: MyResource
		// M
		// y
		// R
		// e
		// s
		// o
		// u
		// r
		// c
		// e
		// Disposed: MyResource
	}
	
	
	//
	// Test
	//
	
	@Test
	public void testUsing() {
		TestSubscriber<Character> tester = new TestSubscriber<>();
		String[] leaseRelease = {"", ""};
		
		Observable<Character> values = Observable.using(
		    () -> {
		        String resource = "MyResource";
		        leaseRelease[0] = resource;
		        return resource;
		    },
		    (resource) -> {
		        return Observable.create(o -> {
		            for (Character c : resource.toCharArray())
		                o.onNext(c);
		            o.onCompleted();
		        });
		    },
		    (resource) -> leaseRelease[1] = resource);

		values
		    .subscribe(tester);
		
		assertEquals(leaseRelease[0], leaseRelease[1]);
		tester.assertReceivedOnNext(Arrays.asList('M','y','R','e','s','o','u','r','c','e'));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}

}
