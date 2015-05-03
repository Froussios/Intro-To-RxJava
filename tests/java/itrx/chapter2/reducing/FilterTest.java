package itrx.chapter2.reducing;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import rx.Observable;
import rx.Subscription;
import rx.observers.TestSubscriber;

public class FilterTest {

	@Test
	public void test() {
		TestSubscriber<Integer> tester = new TestSubscriber<Integer>();
		
		Observable<Integer> values = Observable.range(0,10);
		Subscription oddNumbers = values
		    .filter(v -> v % 2 == 0)
		    .subscribe(tester);
		
		tester.assertReceivedOnNext(Arrays.asList(0,2,4,6,8));
		tester.assertTerminalEvent();
		tester.assertNoErrors();
	}

}
