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
import rx.observers.TestSubscriber;

public class ComposeExample {

	/**
	 * A custom operator for calculating a running average
	 * 
	 * @author Chris
	 *
	 */
	public static class RunningAverage implements Observable.Transformer<Integer, Double> {
	    private static class AverageAcc {
	        public final int sum;
	        public final int count;
	        public AverageAcc(int sum, int count) {
	            this.sum = sum;
	            this.count = count;
	        }
	    }

	    final int threshold;

	    public RunningAverage() {
	        this.threshold = Integer.MAX_VALUE;
	    }

	    public RunningAverage(int threshold) {
	        this.threshold = threshold;
	    }

	    @Override
	    public Observable<Double> call(Observable<Integer> source) {
	        return source
	            .filter(i -> i< this.threshold)
	            .scan(
	                new AverageAcc(0,0),
	                (acc, v) -> new AverageAcc(acc.sum + v, acc.count + 1))
	            .filter(acc -> acc.count > 0)
	            .map(acc -> acc.sum/(double)acc.count);
	    }
	}
	
	public void exampleComposeFromClass() {
		Observable.just(2, 3, 10, 12, 4)
			.compose(new RunningAverage())
			.subscribe(System.out::println);
		
		// 2.0
		// 2.5
		// 5.0
		// 6.75
		// 6.2
	}
	
	public void exampleComposeParameterised() {
		Observable.just(2, 3, 10, 12, 4)
			.compose(new RunningAverage(5))
			.subscribe(System.out::println);
		
		// 2.0
		// 2.5
		// 3.0
	}
	
	
	//
	// Test
	//
	
	@Test
	public void testComposeFromClass() {
		TestSubscriber<Double> tester = new TestSubscriber<>();
		
		Observable.just(2, 3, 10, 12, 4)
			.compose(new RunningAverage())
			.subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(2.0, 2.5, 5.0, 6.75, 6.2));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testComposeParameterised() {
		TestSubscriber<Double> tester = new TestSubscriber<>();
		
		Observable.just(2, 3, 10, 12, 4)
			.compose(new RunningAverage(5))
			.subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(2.0, 2.5, 3.0));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	

}
