package net.fahd.book_network.history;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.fahd.book_network.book.Book;
import net.fahd.book_network.commun.BaseEntity;
import net.fahd.book_network.user.User;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BookTransactionHistory extends BaseEntity {

     //user relationship
//    @ManyToOne
//    @JoinColumn
//    private User user;

    @Column(name = "user_id")
    private String userId;

    //book relationship
    @ManyToOne
    @JoinColumn
    private Book book;

    private boolean returned;
    private boolean returnApproved;


}
