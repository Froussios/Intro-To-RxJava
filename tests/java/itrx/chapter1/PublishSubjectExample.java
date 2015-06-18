package itrx.chapter1;

import java.util.Arrays;

import org.junit.Test;

import rx.observers.TestSubscriber;
import rx.subjects.PublishSubject;

public class PublishSubjectExample {

	public void example() {
		PublishSubject<Integer> subject = PublishSubject.create();
		subject.onNext(1);
		subject.subscribe(System.out::println);
		subject.onNext(2);
		subject.onNext(3);
		subject.onNext(4);
		
		// 2
		// 3
		// 4
	}
	
	
	//
	// Test
	//
	
	@Test
	public void test() {
		TestSubscriber<Integer> tester = new TestSubscriber<>();
		
		PublishSubject<Integer> subject = PublishSubject.create();
		subject.onNext(1);
		subject.subscribe(tester);
		subject.onNext(2);
		subject.onNext(3);
		subject.onNext(4);
		
		tester.assertReceivedOnNext(Arrays.asList(2,3,4));
	}

}
