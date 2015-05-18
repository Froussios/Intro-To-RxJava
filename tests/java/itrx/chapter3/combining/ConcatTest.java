package itrx.chapter3.combining;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;

public class ConcatTest {

	public void exampleConcat() {
		Observable<Integer> seq1 = Observable.range(0, 3);
		Observable<Integer> seq2 = Observable.range(10, 3);

		Observable.concat(seq1, seq2)
		    .subscribe(System.out::println);
		
		// 0
		// 1
		// 2
		// 10
		// 11
		// 12
	}
	
	public void exampleConcatDynamic() {
		Observable<String> words = Observable.just(
			    "First",
			    "Second",
			    "Third",
			    "Fourth",
			    "Fifth",
			    "Sixth"
			);

		Observable.concat(words.groupBy(v -> v.charAt(0)))
		    .subscribe(System.out::println);
		
		// First
		// Fourth
		// Fifth
		// Second
		// Sixth
		// Third
	}
	
	public void exampleConcatWith() {
		Observable<Integer> seq1 = Observable.range(0, 3);
		Observable<Integer> seq2 = Observable.range(10, 3);
		Observable<Integer> seq3 = Observable.just(20);
		
		seq1.concatWith(seq2)
			.concatWith(seq3)
			.subscribe(System.out::println);
		
		// 0
		// 1
		// 2
		// 10
		// 11
		// 12
		// 20
	}
	
	
	//
	// Tests
	//
	
	@Test
	public void testConcat() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<Integer> seq1 = Observable.range(0, 3);
		Observable<Integer> seq2 = Observable.range(10, 3);

		Observable.concat(seq1, seq2)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(0,1,2,10,11,12));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testConcatDynamic() {
		TestSubscriber<String> tester = new TestSubscriber<>();
		
		Observable<String> words = Observable.just(
			    "First",
			    "Second",
			    "Third",
			    "Fourth",
			    "Fifth",
			    "Sixth"
			);

		Observable.concat(words.groupBy(v -> v.charAt(0)))
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(
				"First",
				"Fourth",
				"Fifth",
				"Second",
				"Sixth",
				"Third"));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
	
	@Test
	public void testConcatWith() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		Observable<Integer> seq1 = Observable.range(0, 3);
		Observable<Integer> seq2 = Observable.range(10, 3);
		Observable<Integer> seq3 = Observable.just(20);
		
		seq1.concatWith(seq2)
			.concatWith(seq3)
			.subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(0,1,2,10,11,12,20));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}
}
