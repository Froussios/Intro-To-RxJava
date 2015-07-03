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
package itrx.chapter3.sideeffects;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

public class MutablePipelineExample {

	private static class Data {
	    public int id;
	    public String name;
	    public Data(int id, String name) {
	        this.id = id;
	        this.name = name;
	    }
	}

	public void example() {
		Observable<Data> data = Observable.just(
			    new Data(1, "Microsoft"),
			    new Data(2, "Netflix")
			);

		data.subscribe(d -> d.name = "Garbage");
		data.subscribe(d -> System.out.println(d.id + ": " + d.name));
		
		// 1: Garbage
		// 2: Garbage
	}
	
	
	//
	// Test
	//
	
	@Test
	public void test() {
		TestSubscriber<String> tester = new TestSubscriber<>();
		
		Observable<Data> data = Observable.just(
			    new Data(1, "Microsoft"),
			    new Data(2, "Netflix")
			);

		data.subscribe(d -> d.name = "Garbage");
		data.map(d -> d.name)
			.subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList("Garbage", "Garbage"));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
}
