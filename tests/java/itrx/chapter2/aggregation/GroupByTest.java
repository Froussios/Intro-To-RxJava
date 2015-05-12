package itrx.chapter2.aggregation;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

public class GroupByTest {
	
	public void exampleGroupBy() {
		Observable<String> values = Observable.just(
		        "first",
		        "second",
		        "third",
		        "forth",
		        "fifth",
		        "sixth"
		);

		values.groupBy(word -> word.charAt(0))
		    .flatMap(group -> 
		        group.last().map(v -> group.getKey() + ": " + v)
		    )
		    .subscribe(v -> System.out.println(v));
		
		// s: sixth
		// t: third
		// f: fifth
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testGroupBy() {
		TestSubscriber<Object> tester = new TestSubscriber<>();
		
		Observable<String> values = Observable.just(
		        "first",
		        "second",
		        "third",
		        "forth",
		        "fifth",
		        "sixth"
		);

		values.groupBy(word -> word.charAt(0))
		    .flatMap(group -> 
		        group.last().map(v -> group.getKey() + ": " + v)
		    )
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList("s: sixth", "t: third", "f: fifth"));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
}


