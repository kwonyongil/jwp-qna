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
import qna.NotFoundException;
import qna.UnAuthorizedException;

@Entity
public class Answer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Lob
    private String contents;
    @Column(nullable = false)
    private boolean deleted = false;
    @ManyToOne(optional = false, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "WRITER_ID", foreignKey = @ForeignKey(name = "fk_answer_writer"))
    private User writer;
    @ManyToOne(optional = false, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "QUESTION_ID", foreignKey = @ForeignKey(name = "fk_answer_to_question"))
    private Question question;

    protected Answer() {
    }

    private Answer(AnswerBuilder AnswerBuilder) {
        this.id = AnswerBuilder.id;
        this.writer = AnswerBuilder.writer;
        this.question = AnswerBuilder.question;
        this.contents = AnswerBuilder.contents;
    }

    public static AnswerBuilder builder(User writer, Question question) {
        return new AnswerBuilder(writer, question);
    }

    public static class AnswerBuilder {
        private Long id;
        private final User writer;
        private final Question question;
        private String contents;

        private AnswerBuilder(User writer, Question question) {
            validateWriterNotNull(writer);
            validateQuestionNotNull(question);
            this.writer = writer;
            this.question = question;
        }

        public AnswerBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AnswerBuilder contents(String contents) {
            this.contents = contents;
            return this;
        }

        private void validateQuestionNotNull(Question question) {
            if (Objects.isNull(question)) {
                throw new NotFoundException("질문 정보가 없습니다.");
            }
        }

        private void validateWriterNotNull(User writer) {
            if (Objects.isNull(writer)) {
                throw new UnAuthorizedException("작성자 정보가 없습니다.");
            }
        }

        public Answer build() {
            return new Answer(this);
        }
    }

    public DeleteHistory delete(User loginUser, LocalDateTime now) {
        validateOwnerSameUser(loginUser);
        this.setDeleted(true);
        return DeleteHistory.builder()
                .contentType(ContentType.ANSWER)
                .contentId(this.getId())
                .deletedBy(this.getWriter())
                .createDate(now)
                .build();
    }

    public boolean isOwner(User writer) {
        return this.writer.equals(writer);
    }

    public void toQuestion(Question question) {
        this.question = question;
    }

    public Long getId() {
        return id;
    }

    public User getWriter() {
        return writer;
    }

    public String getContents() {
        return contents;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    private void validateOwnerSameUser(User loginUser) {
        if (!this.isOwner(loginUser)) {
            throw new CannotDeleteException("다른 사람이 쓴 답변이 있어 삭제할 수 없습니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Answer answer = (Answer) o;
        return Objects.equals(getId(), answer.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", writer=" + writer +
                ", question=" + question +
                ", contents='" + contents + '\'' +
                ", deleted=" + deleted +
                '}';
    }
}
