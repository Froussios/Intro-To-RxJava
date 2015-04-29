package itrx;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import rx.observers.TestSubscriber;
import rx.subjects.ReplaySubject;
import rx.subjects.Subject;

public class RxContractTest {

	@Test
	public void test() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		Subject<Integer, Integer> s = ReplaySubject.create();
		s.subscribe(tester);
		s.onNext(0);
		s.onCompleted();
		s.onNext(1);
		s.onNext(2);
		
		tester.assertReceivedOnNext(Arrays.asList(0));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}

}
