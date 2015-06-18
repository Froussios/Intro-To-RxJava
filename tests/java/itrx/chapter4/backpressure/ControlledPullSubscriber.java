package itrx.chapter4.backpressure;

import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * An Rx Subscriber that does not accept any items unless manually requested to.
 * 
 * @author Chris
 *
 * @param <T>
 */
public class ControlledPullSubscriber<T> extends Subscriber<T> {
	
	private final Action1<T> onNextAction;
	private final Action1<Throwable> onErrorAction;
	private final Action0 onCompletedAction;
	
	public ControlledPullSubscriber(
			Action1<T> onNextAction,
			Action1<Throwable> onErrorAction,
			Action0 onCompletedAction) {
		this.onNextAction = onNextAction;
		this.onErrorAction = onErrorAction;
		this.onCompletedAction = onCompletedAction;
	}
	
	public ControlledPullSubscriber(
			Action1<T> onNextAction,
			Action1<Throwable> onErrorAction) {
		this(onNextAction, onErrorAction, () -> {});
	}
	
	public ControlledPullSubscriber(Action1<T> onNextAction) {
		this(onNextAction, e -> {}, () -> {});
	}
	
    @Override
    public void onStart() {
      request(0);
    }

    @Override
    public void onCompleted() {
    	onCompletedAction.call();
    }

    @Override
    public void onError(Throwable e) {
    	onErrorAction.call(e);
    }

    @Override
    public void onNext(T t) {
    	onNextAction.call(t);
    }
    
    public void requestMore(int n) {
    	request(n);
    }
}
