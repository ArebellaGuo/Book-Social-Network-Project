package com.qianwen.Booknetworkproject.entities.book.bookDTO;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class BookRequest{
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @NotNull(message = "100")
        @NotEmpty(message = "100")
        private String title;

        @NotNull(message = "101")
        @NotEmpty(message = "101")
        private String authorName;

        @NotNull(message = "102")
        @NotEmpty(message = "102")
        private String isbn;

        @NotNull(message = "103")
        @NotEmpty(message = "103")
        private String synopsis;

        private boolean shareable;


        public Integer getId() {
                return id;
        }

        public void setId(Integer id) {
                this.id = id;
        }

        public String getTitle() {
                return title;
        }

        public void setTitle(String title) {
                this.title = title;
        }

        public String getAuthorName() {
                return authorName;
        }

        public void setAuthorName(String authorName) {
                this.authorName = authorName;
        }

        public String getIsbn() {
                return isbn;
        }

        public void setIsbn(String isbn) {
                this.isbn = isbn;
        }

        public String getSynopsis() {
                return synopsis;
        }

        public void setSynopsis(String synopsis) {
                this.synopsis = synopsis;
        }

        public boolean isShareable() {
                return shareable;
        }

        public void setShareable(boolean shareable) {
                this.shareable = shareable;
        }
}