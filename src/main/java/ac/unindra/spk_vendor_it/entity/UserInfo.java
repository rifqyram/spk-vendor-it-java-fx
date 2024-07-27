package ac.unindra.spk_vendor_it.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "m_user_info")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserInfo extends BaseEntity {
    @Column(name = "nip", unique = true, nullable = false, length = 20)
    private String nip;

    @Column(name = "name", nullable = false, length = 48)
    private String name;

    @Column(name = "position", nullable = false, length = 50)
    private String position;

    @Column(name = "email", nullable = false, length = 50, unique = true)
    private String email;

    @Column(name = "mobile_phone_no", nullable = false, length = 15, unique = true)
    private String mobilePhoneNo;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private UserCredential user;
}
