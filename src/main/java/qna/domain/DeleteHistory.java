package qna.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class DeleteHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private ContentType contentType;
    private Long contentId;
    private Long deletedById;
    private LocalDateTime createDate = LocalDateTime.now();

    protected DeleteHistory() {
    }

    private DeleteHistory(DeleteHistoryBuilder deleteHistoryBuilder) {
        this.contentType = deleteHistoryBuilder.contentType;
        this.contentId = deleteHistoryBuilder.contentId;
        this.deletedById = deleteHistoryBuilder.deletedById;
        this.createDate = deleteHistoryBuilder.createDate;
    }

    public static DeleteHistoryBuilder builder() {
        return new DeleteHistoryBuilder();
    }

    public static class DeleteHistoryBuilder {
        private ContentType contentType;
        private Long contentId;
        private Long deletedById;
        private LocalDateTime createDate = LocalDateTime.now();

        private DeleteHistoryBuilder() {
        }

        public DeleteHistoryBuilder contentType(ContentType contentType) {
            this.contentType = contentType;
            return this;
        }

        public DeleteHistoryBuilder contentId(Long contentId) {
            this.contentId = contentId;
            return this;
        }

        public DeleteHistoryBuilder deletedById(Long deletedById) {
            this.deletedById = deletedById;
            return this;
        }

        public DeleteHistoryBuilder createDate(LocalDateTime createDate) {
            this.createDate = createDate;
            return this;
        }

        public DeleteHistory build() {
            return new DeleteHistory(this);
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
        DeleteHistory that = (DeleteHistory) o;
        return Objects.equals(id, that.id) &&
                contentType == that.contentType &&
                Objects.equals(contentId, that.contentId) &&
                Objects.equals(deletedById, that.deletedById);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, contentType, contentId, deletedById);
    }

    @Override
    public String toString() {
        return "DeleteHistory{" +
                "id=" + id +
                ", contentType=" + contentType +
                ", contentId=" + contentId +
                ", deletedById=" + deletedById +
                ", createDate=" + createDate +
                '}';
    }
}
