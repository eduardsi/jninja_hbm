package javaninja.locking;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import java.util.UUID;


@RunWith(SpringRunner.class)
@SpringBootTest
public class AggregateRootShould {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private final AggregateRoot aggregateRoot = new AggregateRoot();

    private UUID aggregateRootId;

    @Before
    public void persistAggregateRoot() {
        run(inTx(() -> {
            entityManager.persist(aggregateRoot);
        }));

        this.aggregateRootId = aggregateRoot.id();
    }

    private void run(Scenario scenario) {
        scenario.executeUnchecked();
    }

    @Test
    public void permitBidirectionalAggregateConcurrentModificationInDifferentTransactions() {
        Scenario modifyBidirectionalAggregate = () -> {
            AggregateRoot aggregateRoot = fetchAggregateRootBy(aggregateRootId);
            aggregateRoot.addRandomBidirectional();
        };
        runConcurrentlyInSeparateTransactions(modifyBidirectionalAggregate);
    }

    @Test(expected = ObjectOptimisticLockingFailureException.class)
    public void failWhenUnidirectionalAggregateIsConcurrentlyModifiedInDifferentTransactions() {
        Scenario modifyUnidirectionalAggregate = () -> {
            AggregateRoot aggregateRoot = fetchAggregateRootBy(aggregateRootId);
            aggregateRoot.addRandomUnidirectional();
        };
        runConcurrentlyInSeparateTransactions(modifyUnidirectionalAggregate);
    }


    @Test(expected = ObjectOptimisticLockingFailureException.class)
    public void failWhenEmbeddedAggregateIsConcurrentlyModifiedInDifferentTransactions() {
        Scenario modifyEmbeddedAggregate = () -> {
            AggregateRoot aggregateRoot = fetchAggregateRootBy(aggregateRootId);
            aggregateRoot.addRandomEmbed();

        };
        runConcurrentlyInSeparateTransactions(modifyEmbeddedAggregate);
    }


    private void runConcurrentlyInSeparateTransactions(Scenario scenario) {
        int threads = 2;
        Concurrently concurrently = new Concurrently(threads);
        concurrently.run(inTx(scenario));
    }

    private Scenario inTx(Scenario scenario) {
        return () -> {
            TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
            txTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    scenario.executeUnchecked();
                }
            });
        };
    }

    private AggregateRoot fetchAggregateRootBy(UUID uuid) {
        return entityManager.find(AggregateRoot.class, uuid);
    }


}
