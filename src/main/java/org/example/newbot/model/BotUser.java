package org.example.newbot.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "bot_users")
public class BotUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstname;
    private String lastname;
    private String nickname;
    private String username;
    @Column(name = "chatid1")
    private Long chatId;
    private String lang;
    private Long chatIdHelp;
    private String phone;
    private String helperPhone;
    private String role;
    private Integer page=0;
    private String eventCode;
    private String helperValue;
    @ManyToMany(mappedBy = "users",fetch = FetchType.EAGER)
    private List<BotInfo> bots;
    private Long botUserId;
    private Long categoryId;
    private Long productId;
    private Long productVariantId;

    private String deliveryType;
}
