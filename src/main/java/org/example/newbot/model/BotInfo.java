package org.example.newbot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "bot_info")
@Getter
@Setter
public class BotInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String botUsername;
    @Column(unique = true)
    private String botToken;
    @Column(name = "type1e")
    private String type;
    private boolean active;
    @ElementCollection(fetch = FetchType.EAGER)
//    @CollectionTable(name = "bot_user_mapping", joinColumns = @JoinColumn(name = "bot_id"))
    private List<Long> adminChatIds = new ArrayList<>();
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "bot_user",
            joinColumns = @JoinColumn(name = "bot_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<BotUser> users;
    private Boolean isFree;
    private Date createdAt ;
    private Date updatedAt ;
    public BotInfo() {

    }

    public BotInfo(Long id, String botUsername, String botToken, boolean active) {
        this.id = id;
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.active = active;
    }
}