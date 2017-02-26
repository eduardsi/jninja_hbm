package javaninja.locking;

import com.google.common.base.Throwables;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import static com.google.common.util.concurrent.MoreExecutors.listeningDecorator;

class Concurrently {

        private final ListeningExecutorService threads;
        private final int times;
        private final CyclicBarrier runAtTheSameTime;

        Concurrently(int times) {
            this.times = times;
            this.threads = listeningDecorator(Executors.newFixedThreadPool(times));
            this.runAtTheSameTime = new CyclicBarrier(times);
        }

        void run(Scenario scenario) {
            Collection<ListenableFuture<?>> futures = new ArrayList<>();
            for (int i = 0; i < times; i++) {
                ListenableFuture<?> future = runInAThread(atTheSameTime(scenario));
                futures.add(future);
            }

            ListenableFuture<List<Object>> future = Futures.allAsList(futures);
            try {
                future.get();
            } catch (ExecutionException e) {
                propagate(e.getCause());
            } catch (InterruptedException e) {
                propagate(e);
            }
        }

        private Scenario atTheSameTime(Scenario scenario) {
            return () -> {
                scenario.executeUnchecked();
                runAtTheSameTime.await();
            };
        }


        private ListenableFuture<?> runInAThread(Scenario scenario) {
            return threads.submit(scenario::executeUnchecked);
        }

        private void propagate(Throwable e) {
            Throwables.throwIfUnchecked(e);
            throw new RuntimeException(e);
        }
    }