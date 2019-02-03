package dark.webb.spider.common.data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

/**
 * Created by Alistair Oxley on 16/10/2017.
 */
@MappedSuperclass
public class BaseModel implements Serializable {

    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Version
    @Column(nullable = false)
    private Integer version;

    @Column(nullable = false)
    private Instant created;

    @Column(nullable = false)
    private Instant updated;

    public BaseModel() {}

    @PrePersist
    public void onCreate() {
        created = Instant.now();
        updated = created;
    }

    @PreUpdate
    public void onUpdate() {
        updated = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return updated;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        BaseModel otherBaseModel = (BaseModel) obj;
        if (this.getId() == null || ((BaseModel) obj).getId() == null) {
            //log.info("Comparing BaseModel object that hasn't got an ID. Will attempt equals on objects having same creation and update dates.");
            return this.getCreated().equals(((BaseModel) obj).getCreated()) && this.getUpdated().equals(((BaseModel) obj).getUpdated());
        }
        return this.getId().equals(otherBaseModel.getId());
    }
}
