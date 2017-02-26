package javaninja.locking;

import javax.persistence.Embeddable;
import java.util.UUID;

@Embeddable
class Embed {

    private String someState = UUID.randomUUID().toString();

}

