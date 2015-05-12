package itrx.chapter4.backpressure;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import rx.Observable;

public class ReactivePullTest {
	
	public void example() {
		ControlledPullSubscriber<Integer> tester = new ControlledPullSubscriber<Integer>(
				i -> System.out.println("Consumed " + i));
		
		Observable.range(0, 100)
			.subscribe(tester);
		
		System.out.println("Requesting 2 more");
		tester.requestMore(2);
		System.out.println("Requesting 3 more");
		tester.requestMore(3);
		
		// Requesting 2 more
		// Consumed 0
		// Consumed 1
		// Requesting 3 more
		// Consumed 2
		// Consumed 3
		// Consumed 4
	}
	
	
	//
	// Test
	//
	
	@Test
	public void test() {
		List<Integer> received = new ArrayList<>();
		ControlledPullSubscriber<Integer> tester = new ControlledPullSubscriber<Integer>(received::add);
		
		Observable.range(0, 100)
			.subscribe(tester);
		
		assertEquals(Arrays.asList(), received);
		tester.requestMore(2);
		assertEquals(Arrays.asList(0, 1), received);
		tester.requestMore(3);
		assertEquals(Arrays.asList(0, 1, 2, 3, 4), received);
	}
}
