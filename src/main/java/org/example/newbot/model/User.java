package org.example.newbot.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstname;
    private String lastname;
    private String nickname;
    private String username;
    @Column(unique = true)
    private Long chatId;
    private Integer page;
    private String phone;
    private String role;
    private String eventCode;
    private Long userId;
    private Long channelId;
    private String lang;
    private String helperPhone;
    private String findUsername;
    private String findNickname;
    private Integer messageId;
    private String helperRole;
    private String helperBotType;
    private Boolean forBotActive;
    private Double balance;
    private Long botPriceId;

    private Boolean isNew;
    /*
        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
        private List<Load> loads;
    */
    private Long loadId;
    private Long botId;
    private String queryText;

    private Double helperBalance = 0.0;

}
