package io.smileyjoe.putio.tv.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Async {

    public interface BackgroundOnly{
        void running();
    }

    public interface Background<T>{
        T running();
    }

    public interface Main<T>{
        void complete(T result);
    }

    private final Executor mBackgroundThread;
    private final Executor mMainThread;

    public static <T> void run(BackgroundOnly background){
        new Async().onBackground().execute(background::running);
    }

    public static <T> void run(Background<T> background, Main<T> main){
        Async async = new Async();
        async.onBackground().execute(() -> {
            T result = background.running();
            async.onMain().execute(() -> main.complete(result));
        });
    }

    public abstract static class Runner<T>{
        protected abstract T onBackground();
        protected abstract void onMain(T result);

        public void run(){
            Async.run(this::onBackground, this::onMain);
        }
    }

    public Executor onBackground() {
        return mBackgroundThread;
    }

    public Executor onMain() {
        return mMainThread;
    }

    public Async() {
        mBackgroundThread = Executors.newSingleThreadExecutor();
        mMainThread = new MainThreadExecutor();
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mMainThreadHandler = new Handler(
                Looper.getMainLooper()
        );

        @Override
        public void execute(Runnable command) {
            mMainThreadHandler.post(command);
        }
    }
}
