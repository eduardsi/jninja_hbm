package javaninja.locking;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@javax.persistence.Entity
class AggregateRoot {

    @Id
    @GeneratedValue
    private UUID uuid;

    @Version
    private long version;

    @ElementCollection
    @CollectionTable(name = "AGGREGATE_ELEMENTS", joinColumns = {@JoinColumn(name = "JK_ROOT_ID")})
    @OrderColumn(name = "INDEX")
    private List<Embed> embeds = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "ROOT_ID", updatable = false, nullable = false)
    private List<Unidirectional> unidirectionals = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "parent")
    private List<Bidirectional> bidirectionals = new ArrayList<>();

    void addRandomEmbed() {
        embeds.add(new Embed());
    }

    void addRandomUnidirectional() {
        unidirectionals.add(new Unidirectional());
    }

    void addRandomBidirectional() {
        bidirectionals.add(new Bidirectional(this));
    }


    public UUID id() {
        return uuid;
    }


}
