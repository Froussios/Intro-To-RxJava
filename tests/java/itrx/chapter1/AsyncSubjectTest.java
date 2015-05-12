package itrx.chapter1;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import rx.observers.TestSubscriber;
import rx.subjects.AsyncSubject;

public class AsyncSubjectTest {

	public void exampleLastValue() {
		AsyncSubject<Integer> s = AsyncSubject.create();
		s.subscribe(v -> System.out.println(v));
		s.onNext(0);
		s.onNext(1);
		s.onNext(2);
		s.onCompleted();

		// 2
	}

	public void exampleNoCompletion() {
		AsyncSubject<Integer> s = AsyncSubject.create();
		s.subscribe(v -> System.out.println(v));
		s.onNext(0);
		s.onNext(1);
		s.onNext(2);
	}

	//
	// Tests
	//

	@Test
	public void testLastValue() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();

		AsyncSubject<Integer> s = AsyncSubject.create();
		s.subscribe(tester);
		s.onNext(0);
		s.onNext(1);
		s.onNext(2);
		s.onCompleted();

		tester.assertReceivedOnNext(Arrays.asList(2));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}

	@Test
	public void testNoCompletion() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();

		AsyncSubject<Integer> s = AsyncSubject.create();
		s.subscribe(tester);
		s.onNext(0);
		s.onNext(1);
		s.onNext(2);

		tester.assertReceivedOnNext(Arrays.asList());
		assertTrue(tester.getOnCompletedEvents().size() == 0);
		tester.assertNoErrors();
	}

}
