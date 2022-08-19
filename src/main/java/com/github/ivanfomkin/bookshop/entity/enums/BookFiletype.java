package com.github.ivanfomkin.bookshop.entity.enums;

public enum BookFiletype {
    PDF(".pdf", 1),
    EPUB(".epub", 2),
    FB2(".fb2", 3);
    private final String fileExtension;
    private final int id;

    BookFiletype(String fileExtension, int id) {
        this.fileExtension = fileExtension;
        this.id = id;
    }

    public static String getFileExtensionById(int id) {
        return switch (id) {
            case 1 -> PDF.fileExtension;
            case 2 -> EPUB.fileExtension;
            case 3 -> FB2.fileExtension;
            default -> "";
        };
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public int getFileTypeId() {
        return id;
    }
}
