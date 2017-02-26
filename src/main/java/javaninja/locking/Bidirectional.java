package javaninja.locking;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.UUID;

@Entity
class Bidirectional {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    AggregateRoot parent;

    public Bidirectional(AggregateRoot parent) {
        this.parent = parent;
    }
}
