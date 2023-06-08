package com.example.board.entity;

import com.example.board.dto.CommentDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "comment_table")
@ToString
public class CommentEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String commentWriter;

    @Column(length = 200, nullable = false)
    private String commentContents;

//    @CreationTimestamp
//    @Column(nullable = false)
//    private LocalDateTime commentCreatedDate;

    @ManyToOne // 자식이 여러개니까 (N관계) ManyToOne를 써줌
    @JoinColumn(name="board_id") //실제 DB에서 생성되는 참조 컬럼의 이름 // board_id
    private BoardEntity boardEntity; //여기는 반드시 부모엔티티 타입이 온다 !!!!(!_!중요!_!)

    public static CommentEntity toSaveEntity(BoardEntity boardEntity, CommentDTO commentDTO){
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setCommentWriter(commentDTO.getCommentWriter());
        commentEntity.setCommentContents(commentDTO.getCommentContents());
        commentEntity.setBoardEntity(boardEntity);
        return commentEntity;
    }

}
