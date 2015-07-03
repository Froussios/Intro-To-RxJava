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
