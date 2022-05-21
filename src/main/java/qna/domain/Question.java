package qna.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import qna.CannotDeleteException;

@Entity
public class Question extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100, nullable = false)
    private String title;
    @Lob
    private String contents;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "WRITER_ID", foreignKey = @ForeignKey(name = "fk_question_writer"))
    private User writer;
    @Column(nullable = false)
    private boolean deleted = false;

    protected Question() {
    }

    private Question(QuestionBuilder questionBuilder) {
        this.id = questionBuilder.id;
        this.title = questionBuilder.title;
        this.contents = questionBuilder.contents;
    }

    public static QuestionBuilder builder(String title) {
        return new QuestionBuilder(title);
    }

    public static class QuestionBuilder {
        private Long id;
        private final String title;
        private String contents;

        private QuestionBuilder(String title) {
            this.title = title;
        }

        public QuestionBuilder id(long id) {
            this.id = id;
            return this;
        }

        public QuestionBuilder contents(String contents) {
            this.contents = contents;
            return this;
        }

        public Question build() {
            return new Question(this);
        }
    }

    public DeleteHistory delete(User loginUser) throws CannotDeleteException {
        validateOwnerSameUser(loginUser);
        this.setDeleted(true);
        return DeleteHistory.builder()
                .contentType(ContentType.QUESTION)
                .contentId(this.id)
                .deletedBy(this.getWriter())
                .createDate(LocalDateTime.now())
                .build();
    }

    private void validateOwnerSameUser(User loginUser) throws CannotDeleteException {
        if (!this.isOwner(loginUser)) {
            throw new CannotDeleteException("질문을 삭제할 권한이 없습니다.");
        }
    }

    public Question writeBy(User writer) {
        this.writer = writer;
        return this;
    }

    public boolean isOwner(User writer) {
        return this.writer.equals(writer);
    }

    public void addAnswer(Answer answer) {
        answer.toQuestion(this);
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContents() {
        return contents;
    }

    public User getWriter() {
        return writer;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Question question = (Question) o;
        return Objects.equals(getId(), question.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", writer=" + writer +
                ", deleted=" + deleted +
                '}';
    }
}
