package javaninja.locking;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
class Unidirectional {

    @Id
    @GeneratedValue
    private UUID id;


}
